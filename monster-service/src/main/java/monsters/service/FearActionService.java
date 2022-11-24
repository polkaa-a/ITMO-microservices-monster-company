package monsters.service;

import monsters.controller.exception.NotFoundException;
import monsters.dto.request.RequestFearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.model.FearActionEntity;
import monsters.repository.FearActionRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.UUID;

@Service
public class FearActionService {

    private static final String EXC_MES_ID = "none fear action was found by id";
    private final FearActionRepository fearActionRepository;
    private final FearActionMapper fearActionMapper;
    private final MonsterService monsterService;

    public FearActionService(@Lazy MonsterService monsterService, FearActionRepository fearActionRepository, FearActionMapper fearActionMapper) {
        this.fearActionRepository = fearActionRepository;
        this.monsterService = monsterService;
        this.fearActionMapper = fearActionMapper;
    }

    public Mono<FearActionEntity> save(Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionDTOMono
                .flatMap(fearActionDTO -> Mono.zip(Mono.just(fearActionDTO), monsterService.findById(fearActionDTO.getMonsterId())))
                .flatMap(tuple -> Mono.just(fearActionMapper.mapDtoToEntity(tuple.getT1(), tuple.getT2())))
                .flatMap(fearActionEntity ->
                        Mono.fromCallable(() -> fearActionRepository.save(fearActionEntity))
                                .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<FearActionEntity> findById(UUID fearActionId) {
        return Mono.fromCallable(() -> fearActionRepository.findById(fearActionId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)));
    }

    public Mono<FearActionEntity> updateById(UUID fearActionId, Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionDTOMono
                .flatMap(fearActionDTO ->
                        Mono.zip(fearActionDTOMono, monsterService.findById(fearActionDTO.getMonsterId())))
                .flatMap(tuple -> Mono.just(fearActionMapper.mapDtoToEntity(tuple.getT1(), tuple.getT2())))
                .flatMap(fearActionEntity -> {
                    fearActionEntity.setId(fearActionId);
                    return Mono.fromCallable(() -> fearActionRepository.findById(fearActionEntity.getId()).stream())
                            .subscribeOn(Schedulers.boundedElastic())
                            .flux()
                            .flatMap(Flux::fromStream)
                            .singleOrEmpty()
                            .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                            .then(Mono.fromCallable(() -> fearActionRepository.save(fearActionEntity))
                                    .subscribeOn(Schedulers.boundedElastic()));
                });
    }

    public Mono<Void> delete(UUID fearActionId) {
        return Mono.fromCallable(() -> fearActionRepository.findById(fearActionId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                .then(Mono.fromRunnable(() -> fearActionRepository.deleteById(fearActionId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }

    public Flux<FearActionEntity> findAllByDate(Date date, int page, int size) {
        return Mono.fromCallable(() -> fearActionRepository.findAllByDate(date, PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .doOnError(Throwable::printStackTrace);
    }
}
