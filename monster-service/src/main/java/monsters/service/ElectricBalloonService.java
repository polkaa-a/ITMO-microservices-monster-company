package monsters.service;

import monsters.controller.exception.NotFoundException;
import monsters.dto.request.RequestElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.model.ElectricBalloonEntity;
import monsters.repository.ElectricBalloonRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.UUID;

@Service
public class ElectricBalloonService {

    private static final String EXC_MES_ID = "none electric balloon was found by id";
    private final ElectricBalloonRepository electricBalloonRepository;
    private final ElectricBalloonMapper electricBalloonMapper;

    private final FearActionService fearActionService;
    private final CityService cityService;

    public ElectricBalloonService(@Lazy FearActionService fearActionService, CityService cityService, ElectricBalloonMapper electricBalloonMapper, ElectricBalloonRepository electricBalloonRepository) {
        this.electricBalloonRepository = electricBalloonRepository;
        this.electricBalloonMapper = electricBalloonMapper;
        this.cityService = cityService;
        this.fearActionService = fearActionService;
    }

    public Mono<ElectricBalloonEntity> save(Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonDTOMono
                .flatMap(electricBalloonDTO ->
                        Mono.zip(Mono.just(electricBalloonDTO), fearActionService.findById(electricBalloonDTO.getFearActionId()), cityService.findById(electricBalloonDTO.getCityId())))
                .flatMap(tuple -> Mono.just(electricBalloonMapper.mapDtoToEntity(tuple.getT1(), tuple.getT2(), tuple.getT3())))
                .flatMap(electricBalloonEntity -> Mono.fromCallable(() -> electricBalloonRepository.save(electricBalloonEntity))
                        .subscribeOn(Schedulers.boundedElastic()))
                .doOnError(Throwable::printStackTrace);
    }

    public Mono<ElectricBalloonEntity> findById(UUID electricBalloonId) {
        return Mono.fromCallable(() -> electricBalloonRepository.findById(electricBalloonId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)));
    }


    public Flux<ElectricBalloonEntity> findAllFilledByDate(Date date, int page, int size) {
        return Mono.fromCallable(() -> electricBalloonRepository.findAllFilledByDate(date, PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);
    }

    public Flux<ElectricBalloonEntity> findAllByMonsterId(UUID monsterId, int page, int size) {
        return Mono.fromCallable(() -> electricBalloonRepository.findAllByMonsterId(monsterId, PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);
    }

    public Flux<ElectricBalloonEntity> findAllFilledByDateAndCity(Date date, UUID citiId, int page, int size) {
        return Mono.fromCallable(() -> electricBalloonRepository.findAllFilledByDateAndCity(date, citiId, PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);
    }

    public Mono<ElectricBalloonEntity> updateById(UUID electricBalloonId, Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonDTOMono
                .flatMap(electricBalloonDTO ->
                        Mono.zip(electricBalloonDTOMono, fearActionService.findById(electricBalloonDTO.getFearActionId()), cityService.findById(electricBalloonDTO.getCityId())))
                .flatMap(tuple -> Mono.just(electricBalloonMapper.mapDtoToEntity(tuple.getT1(), tuple.getT2(), tuple.getT3())))
                .flatMap(electricBalloonEntity -> {
                    electricBalloonEntity.setId(electricBalloonId);
                    return Mono.fromCallable(() -> electricBalloonRepository.findById(electricBalloonEntity.getId()).stream())
                            .subscribeOn(Schedulers.boundedElastic())
                            .flux()
                            .flatMap(Flux::fromStream)
                            .singleOrEmpty()
                            .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                            .then(Mono.fromCallable(() -> electricBalloonRepository.save(electricBalloonEntity))
                                    .subscribeOn(Schedulers.boundedElastic()));
                });
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
