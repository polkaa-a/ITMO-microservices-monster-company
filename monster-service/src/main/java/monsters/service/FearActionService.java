package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.FearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.model.FearActionEntity;
import monsters.repository.FearActionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FearActionService {

    private static final String EXC_MES_ID = "none fear action was found by id";
    private final FearActionRepository fearActionRepository;
    private final FearActionMapper fearActionMapper;

    public Mono<FearActionEntity> save(Mono<FearActionDTO> fearActionDTOMono) {
        return fearActionMapper.mapDtoToEntity(fearActionDTOMono).flatMap(electricBalloonEntity ->
                Mono.fromCallable(() -> fearActionRepository.save(electricBalloonEntity))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<FearActionEntity> findById(UUID fearActionId) {
        var actionsMonoOptional = Mono.fromCallable(() ->
                        fearActionRepository.findById(fearActionId))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<FearActionEntity> actionsFlux = Flux.empty();

        return actionsMonoOptional
                .flatMap(optional -> {
                    optional.map(fearActionEntity -> actionsFlux.mergeWith(Mono.just(fearActionEntity)));
                    return actionsFlux.single();
                })
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)));
    }

    public Mono<FearActionEntity> updateById(UUID fearActionId, Mono<FearActionDTO> fearActionDTOMono) {
        return Mono.fromCallable(() -> fearActionRepository.findById(fearActionId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                .then(fearActionMapper.mapDtoToEntity(fearActionDTOMono).flatMap(fearActionEntity -> {
                    fearActionEntity.setId(fearActionId);
                    return Mono.fromCallable(() -> fearActionRepository.save(fearActionEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                }));
    }

    public Mono<Void> delete(UUID fearActionId) {
        return Mono.fromCallable(() -> fearActionRepository.findById(fearActionId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                .then(Mono.fromRunnable(() -> fearActionRepository.deleteById(fearActionId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }

}
