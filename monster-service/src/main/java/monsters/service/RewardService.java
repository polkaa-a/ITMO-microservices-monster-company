package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.RewardDTO;
import monsters.mapper.RewardMapper;
import monsters.model.RewardEntity;
import monsters.repository.RewardRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
        return rewardDTOMono.flatMap(rewardDTO -> rewardRepository.findByMoney(rewardDTO.getMoney())
                .hasElements()
                .flatMap(hasElements -> hasElements ?
                        Mono.error(new EntityExistsException(EXC_EXIST + ": " + rewardDTO.getMoney())) :
                        rewardMapper.mapDtoToEntity(rewardDTOMono).flatMap(rewardRepository::save)));
    }

    public Mono<RewardEntity> findById(Mono<UUID> rewardIdMono) {
        return rewardIdMono.flatMap(rewardId -> rewardRepository.findById(rewardId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId))));
    }

    public Mono<RewardEntity> updateById(Mono<UUID> rewardIdMono, Mono<RewardDTO> rewardDTOMono) {
        return rewardIdMono.flatMap(rewardId -> rewardRepository.findById(rewardId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)))
                .then(rewardMapper.mapDtoToEntity(rewardDTOMono).flatMap(rewardEntity -> {
                    rewardEntity.setId(rewardId);
                    return rewardRepository.save(rewardEntity);
                })));
    }

    public void delete(Mono<UUID> rewardIdMono) {
        rewardIdMono.map(rewardId -> rewardRepository.findById(rewardId)
                        .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + rewardId)))
                        .then(rewardRepository.deleteById(rewardId)))
                .subscribe();
    }
}
