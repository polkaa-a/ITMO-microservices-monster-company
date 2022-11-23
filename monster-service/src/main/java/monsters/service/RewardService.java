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

import javax.persistence.EntityExistsException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final String EXC_EXIST = "reward with such amount of money already exists";
    private static final String EXC_MES_ID = "none reward was found by id";

    private final RewardMapper rewardMapper;
    private final RewardRepository rewardRepository;

    public Mono<RewardEntity> save(Mono<RewardDTO> rewardDTOMono) {
        return rewardDTOMono.flatMap(rewardDTO ->
                Mono.fromCallable(() -> rewardRepository.findByMoney(rewardDTO.getMoney()).stream())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flux()
                        .flatMap(Flux::fromStream)
                        .hasElements()
                        .flatMap(hasElements -> hasElements ?
                                Mono.error(new EntityExistsException(EXC_EXIST + ": " + rewardDTO.getMoney())) :
                                Mono.just(rewardMapper.mapDtoToEntity(rewardDTO))
                                        .flatMap(rewardEntity -> Mono.fromCallable(() -> rewardRepository.save(rewardEntity))
                                                .subscribeOn(Schedulers.boundedElastic()))));
    }

    public Mono<RewardEntity> findById(UUID rewardId) {
        return Mono.fromCallable(() -> rewardRepository.findById(rewardId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)));
    }

    public Mono<RewardEntity> updateById(UUID rewardId, Mono<RewardDTO> rewardDTOMono) {
        return Mono.fromCallable(() -> rewardRepository.findById(rewardId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)))
                .then(rewardDTOMono.flatMap(rewardDTO -> Mono.just(rewardMapper.mapDtoToEntity(rewardDTO)))
                        .flatMap(rewardEntity -> {
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
