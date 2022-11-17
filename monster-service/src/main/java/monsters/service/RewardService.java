package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.RewardDTO;
import monsters.mapper.RewardMapper;
import monsters.model.RewardEntity;
import monsters.repository.RewardRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final String EXC_EXIST = "reward with such amount of money already exists";
    private static final String EXC_MES_ID = "none reward was found by id";

    private final RewardMapper rewardMapper;
    private final RewardRepository rewardRepository;

    public Mono<RewardEntity> save(Mono<RewardDTO> rewardDTOMono) {
        return rewardMapper.mapDtoToEntity(rewardDTOMono).flatMap(rewardEntity ->
                Mono.fromCallable(() -> rewardRepository.save(rewardEntity))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<RewardEntity> findById(UUID rewardId) {

        var rewardsMonoOptional = Mono.fromCallable(() ->
                rewardRepository.findById(rewardId)).subscribeOn(Schedulers.boundedElastic());

        Flux<RewardEntity> rewardsFlux = Flux.empty();

        return rewardsMonoOptional
                .flatMap(optional -> {
                    optional.map(rewardEntity -> rewardsFlux.mergeWith(Mono.just(rewardEntity)));
                    return rewardsFlux.single();
                })
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)));
    }

    public Mono<RewardEntity> updateById(UUID rewardId, Mono<RewardDTO> rewardDTOMono) {
        return Mono.fromCallable(() -> rewardRepository.findById(rewardId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)))
                .then(rewardMapper.mapDtoToEntity(rewardDTOMono).flatMap(rewardEntity -> {
                    rewardEntity.setId(rewardId);
                    return Mono.fromCallable(() -> rewardRepository.save(rewardEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                }));
    }

    public Mono<Void> delete(UUID rewardId) {
        return Mono.fromCallable(() -> rewardRepository.findById(rewardId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)))
                .then(Mono.fromRunnable(() -> rewardRepository.deleteById(rewardId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
