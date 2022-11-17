package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.CityDTO;
import monsters.mapper.CityMapper;
import monsters.model.CityEntity;
import monsters.repository.CityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.persistence.EntityExistsException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CityService {

    private static final String EXC_MES_ID = "none city was found by id";
    private static final String EXC_MES_NAME = "none city was found by name";
    private static final String EXC_EXIST = "city with this name already exists";
    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public Mono<CityEntity> save(Mono<CityDTO> cityDTOMono) {
        Mono<CityEntity> errorMono = cityDTOMono
                .flatMap(cityDTO -> Mono.error(new EntityExistsException(EXC_EXIST + ": " + cityDTO.getName())));

        var savedEntityMono = cityMapper.mapDtoToEntity(cityDTOMono)
                .flatMap(cityEntity -> Mono.fromCallable(() -> cityRepository.save(cityEntity))
                        .subscribeOn(Schedulers.boundedElastic()));

        Flux<CityEntity> cityFlux = Flux.empty();

        return cityDTOMono.flatMap(cityDTO ->
                Mono.fromCallable(() -> cityRepository.findByName(cityDTO.getName()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(optional -> {
                            optional.map(cityEntity -> cityFlux.mergeWith(Mono.just(cityEntity)));
                            return cityFlux.hasElements().flatMap(hasElements -> hasElements ? errorMono : savedEntityMono);
                        }));
    }

    public Mono<CityEntity> findByName(String cityName) {

        var citiesMonoOptional = Mono.fromCallable(() ->
                cityRepository.findByName(cityName)).subscribeOn(Schedulers.boundedElastic());

        Flux<CityEntity> citiesFlux = Flux.empty();

        return citiesMonoOptional
                .flatMap(optional -> {
                    optional.map(cityEntity -> citiesFlux.mergeWith(Mono.just(cityEntity)));
                    return citiesFlux.single();
                })
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_NAME + ": " + cityName)));
    }

    public Mono<CityEntity> updateById(UUID cityId, Mono<CityDTO> cityDTOMono) {
        return Mono.fromCallable(() -> cityRepository.findById(cityId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)))
                .then(cityMapper.mapDtoToEntity(cityDTOMono).flatMap(cityEntity -> {
                    cityEntity.setId(cityId);
                    return Mono.fromCallable(() -> cityRepository.save(cityEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                }));
    }

    public Flux<CityEntity> findAll(int page, int size) {
        var citiesMonoPage = Mono.fromCallable(() -> cityRepository.findAll(PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<CityEntity> citiesFlux = Flux.empty();

        return citiesMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(cityEntity -> citiesFlux.mergeWith(Mono.just(cityEntity)));
                    return citiesFlux;
                });
    }

    public Mono<Void> delete(UUID cityId) {
        return Mono.fromCallable(() -> cityRepository.findById(cityId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)))
                .then(Mono.fromRunnable(() -> cityRepository.deleteById(cityId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
