package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.ElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.model.ElectricBalloonEntity;
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

    public Mono<ElectricBalloonEntity> save(Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonMapper.mapDtoToEntity(electricBalloonDTOMono).flatMap(electricBalloonEntity ->
                Mono.fromCallable(() -> electricBalloonRepository.save(electricBalloonEntity))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<ElectricBalloonEntity> findById(UUID electricBalloonId) {
        var balloonsMonoOptional = Mono.fromCallable(() ->
                        electricBalloonRepository.findById(electricBalloonId))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<ElectricBalloonEntity> balloonsFlux = Flux.empty();

        return balloonsMonoOptional
                .flatMap(optional -> {
                    optional.map(electricBalloonEntity -> balloonsFlux.mergeWith(Mono.just(electricBalloonEntity)));
                    return balloonsFlux.single();
                })
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)));
    }


    public Flux<ElectricBalloonEntity> findAllFilledByDate(Date date, int page, int size) {
        var balloonsMonoPage = Mono.fromCallable(() ->
                        electricBalloonRepository.findAllFilledByDate(date, PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<ElectricBalloonEntity> balloonsFlux = Flux.empty();

        return balloonsMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(electricBalloonEntity -> balloonsFlux.mergeWith(Mono.just(electricBalloonEntity)));
                    return balloonsFlux;
                });
    }

    public Flux<ElectricBalloonEntity> findAllFilledByDateAndCity(Date date, UUID citiId, int page, int size) {

        var balloonsMonoPage = Mono.fromCallable(() ->
                        electricBalloonRepository.findAllFilledByDateAndCity(date, citiId, PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<ElectricBalloonEntity> balloonsFlux = Flux.empty();

        return balloonsMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(electricBalloonEntity -> balloonsFlux.mergeWith(Mono.just(electricBalloonEntity)));
                    return balloonsFlux;
                });
    }

    public Mono<ElectricBalloonEntity> updateById(UUID electricBalloonId, Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        return Mono.fromCallable(() -> electricBalloonRepository.findById(electricBalloonId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                .then(electricBalloonMapper.mapDtoToEntity(electricBalloonDTOMono).flatMap(electricBalloonEntity -> {
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
