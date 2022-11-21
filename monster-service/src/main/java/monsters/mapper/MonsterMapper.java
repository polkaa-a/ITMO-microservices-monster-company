package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.MonsterRatingDTO;
import monsters.dto.RewardDTO;
import monsters.dto.answer.AnswerMonsterDTO;
import monsters.dto.request.RequestMonsterDTO;
import monsters.model.MonsterEntity;
import monsters.model.RewardEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonsterMapper {

    private static final int BUFFER_SIZE = 10;
    private final ModelMapper modelMapper;
    private final RewardMapper rewardMapper;

    public Mono<AnswerMonsterDTO> mapEntityToDto(Mono<MonsterEntity> monsterEntityMono, Flux<RewardEntity> rewardEntityFlux) {
        var rewardDTOFlux = rewardEntityFlux
                .buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(rewardEntity -> rewardMapper.mapEntityToDto(Mono.just(rewardEntity)))
                        .flatMap(Mono::flux)
                        .subscribeOn(Schedulers.parallel()));

        return monsterEntityMono
                .zipWith(Mono.just(rewardDTOFlux))
                .flatMap(tuple -> Mono.just(modelMapper.map(tuple.getT1(), AnswerMonsterDTO.class))
                        .flatMap(monsterDTO -> {
                            monsterDTO.setUserId(tuple.getT1().getUserId());
                            return Mono.fromCallable(tuple.getT2()::toIterable).subscribeOn(Schedulers.boundedElastic())
                                    .flatMap(iterable -> {
                                        List<RewardDTO> rewards = new ArrayList<>();
                                        iterable.forEach(rewards::add);
                                        monsterDTO.setRewards(rewards);
                                        return Mono.just(monsterDTO);
                                    });
                        }));

    }

    public Mono<MonsterEntity> mapDtoToEntity(Mono<RequestMonsterDTO> monsterDTOMono) {
        return monsterDTOMono.flatMap(monsterDTO -> Mono.just(
                        modelMapper.map(monsterDTO, MonsterEntity.class))
                .flatMap(monsterEntity -> {
                    monsterEntity.setUserId(monsterDTO.getUserId());
                    return Mono.just(monsterEntity);
                }));
    }

    public Mono<MonsterRatingDTO> mapEntityToRatingDTO(Mono<MonsterEntity> monsterEntityMono, long countBalloons) {
        return monsterEntityMono.flatMap(monsterEntity -> Mono.just(
                MonsterRatingDTO.builder()
                        .monsterID(monsterEntity.getId())
                        .countBalloons(countBalloons)
                        .build()
        ));
    }

}
