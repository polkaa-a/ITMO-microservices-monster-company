package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.request.RequestElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.model.CityEntity;
import monsters.model.ElectricBalloonEntity;
import monsters.model.FearActionEntity;
import monsters.repository.ElectricBalloonRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ElectricBalloonService {

    private static final String EXC_MES_ID = "none electric balloon was found by id";
    private final ElectricBalloonRepository electricBalloonRepository;
    private final ElectricBalloonMapper electricBalloonMapper;

    private final FearActionService fearActionService;
    private final CityService cityService;

    private Mono<FearActionEntity> getFearActionEntityMono(Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonDTOMono.flatMap(electricBalloonDTO ->
                fearActionService.findById(electricBalloonDTO.getFearActionId()));
    }

    private Mono<CityEntity> getCityEntityMono(Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonDTOMono.flatMap(electricBalloonEntity ->
                cityService.findByName(electricBalloonEntity.getCityName()));
    }

    public Mono<ElectricBalloonEntity> save(Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonMapper
                .mapDtoToEntity(electricBalloonDTOMono, getCityEntityMono(electricBalloonDTOMono), getFearActionEntityMono(electricBalloonDTOMono))
                .flatMap(electricBalloonEntity -> Mono.fromCallable(() -> electricBalloonRepository.save(electricBalloonEntity))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<ElectricBalloonEntity> findById(UUID electricBalloonId) {
        var balloonEntityFlux = Mono.fromCallable(() ->
                        electricBalloonRepository.findById(electricBalloonId).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());

        return balloonEntityFlux
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)));
    }


    public Flux<ElectricBalloonEntity> findAllFilledByDate(Date date, int page, int size) {
        return Mono.fromCallable(() ->
                        electricBalloonRepository.findAllFilledByDate(date, PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<ElectricBalloonEntity> findAllByMonsterId(UUID monsterId, int page, int size) {
        return Mono.fromCallable(() ->
                        electricBalloonRepository.findAllByMonsterId(monsterId, PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<ElectricBalloonEntity> findAllFilledByDateAndCity(Date date, UUID citiId, int page, int size) {
        return Mono.fromCallable(() ->
                        electricBalloonRepository.findAllFilledByDateAndCity(date, citiId, PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ElectricBalloonEntity> updateById(UUID electricBalloonId, Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return Mono.fromCallable(() -> electricBalloonRepository.findById(electricBalloonId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                .then(electricBalloonMapper.mapDtoToEntity(electricBalloonDTOMono, getCityEntityMono(electricBalloonDTOMono), getFearActionEntityMono(electricBalloonDTOMono))
                        .flatMap(electricBalloonEntity -> {
                            electricBalloonEntity.setId(electricBalloonId);
                            return Mono.fromCallable(() -> electricBalloonRepository.save(electricBalloonEntity))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }));
    }

    public Mono<Void> delete(UUID electricBalloonId) {
        return Mono.fromCallable(() -> electricBalloonRepository.findById(electricBalloonId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                .then(Mono.fromRunnable(() -> electricBalloonRepository.deleteById(electricBalloonId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
