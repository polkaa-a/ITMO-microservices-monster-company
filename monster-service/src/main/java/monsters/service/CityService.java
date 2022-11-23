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
        return cityDTOMono.flatMap(cityDTO ->
                Mono.fromCallable(() -> cityRepository.findByName(cityDTO.getName()).stream())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flux()
                        .flatMap(Flux::fromStream)
                        .hasElements()
                        .flatMap(hasElements -> hasElements ?
                                Mono.error(new EntityExistsException(EXC_EXIST + ": " + cityDTO.getName())) :
                                Mono.just(cityMapper.mapDtoToEntity(cityDTO))
                                        .flatMap(cityEntity -> Mono.fromCallable(() -> cityRepository.save(cityEntity))
                                                .subscribeOn(Schedulers.boundedElastic()))));

    }

    public Mono<CityEntity> findById(UUID cityId) {
        return Mono.fromCallable(() -> cityRepository.findById(cityId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)));
    }

    public Mono<CityEntity> findByName(String cityName) {
        return Mono.fromCallable(() -> cityRepository.findByName(cityName).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_NAME + ": " + cityName)));
    }

    public Mono<CityEntity> updateById(UUID cityId, Mono<CityDTO> cityDTOMono) {
        return Mono.fromCallable(() -> cityRepository.findById(cityId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + cityId)))
                .then(cityDTOMono.flatMap(cityDTO -> Mono.just(cityMapper.mapDtoToEntity(cityDTO)))
                        .flatMap(cityEntity -> {
                            cityEntity.setId(cityId);
                            return Mono.fromCallable(() -> cityRepository.save(cityEntity))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }));
    }

    public Flux<CityEntity> findAll(int page, int size) {
        return Mono.fromCallable(()
                        -> cityRepository.findAll(PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
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
