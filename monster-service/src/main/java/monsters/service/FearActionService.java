package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.request.RequestFearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.model.FearActionEntity;
import monsters.model.MonsterEntity;
import monsters.repository.FearActionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FearActionService {

    private static final String EXC_MES_ID = "none fear action was found by id";
    private final FearActionRepository fearActionRepository;
    private final FearActionMapper fearActionMapper;

    private final MonsterService monsterService;

    private Mono<MonsterEntity> getMonsterEntityMono(Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionDTOMono.flatMap(fearActionDTO -> monsterService.findById(fearActionDTO.getMonsterId()));
    }

    public Mono<FearActionEntity> save(Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionMapper.mapDtoToEntity(fearActionDTOMono, getMonsterEntityMono(fearActionDTOMono))
                .flatMap(electricBalloonEntity ->
                        Mono.fromCallable(() -> fearActionRepository.save(electricBalloonEntity))
                                .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<FearActionEntity> findById(UUID fearActionId) {
        var actionEntityFlux = Mono.fromCallable(() ->
                        fearActionRepository.findById(fearActionId).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());

        return actionEntityFlux
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)));
    }

    public Mono<FearActionEntity> updateById(UUID fearActionId, Mono<RequestFearActionDTO> fearActionDTOMono) {
        return Mono.fromCallable(() -> fearActionRepository.findById(fearActionId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + fearActionId)))
                .then(fearActionMapper.mapDtoToEntity(fearActionDTOMono, getMonsterEntityMono(fearActionDTOMono))
                        .flatMap(fearActionEntity -> {
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

    public Flux<FearActionEntity> findAllByDate(Date date, int page, int size) {
        return Mono.fromCallable(() ->
                        fearActionRepository.findAllByDate(date, PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
