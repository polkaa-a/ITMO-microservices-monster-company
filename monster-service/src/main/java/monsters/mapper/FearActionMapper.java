package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerFearActionDTO;
import monsters.dto.request.RequestFearActionDTO;
import monsters.model.FearActionEntity;
import monsters.model.MonsterEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FearActionMapper {

    private final MonsterMapper monsterMapper;

    public Mono<AnswerFearActionDTO> mapEntityToDto(Mono<FearActionEntity> fearActionEntityMono) {
        var monsterEntityMono = fearActionEntityMono.flatMap(
                fearActionEntity -> Mono.just(fearActionEntity.getMonsterEntity()));

        var rewardsEntityFlux = monsterEntityMono
                .flux()
                .flatMap(monsterEntity -> Flux.fromIterable(monsterEntity.getRewards()));

        var monsterDTOMono = fearActionEntityMono.flatMap(
                fearActionEntity -> monsterMapper.mapEntityToDto(monsterEntityMono, rewardsEntityFlux));

        return fearActionEntityMono.zipWith(monsterDTOMono)
                .flatMap(tuple -> Mono.just(
                        AnswerFearActionDTO.builder()
                                .id(tuple.getT1().getId())
                                .monster(tuple.getT2())
                                .date(tuple.getT1().getDate())
                                .doorId(tuple.getT1().getDoorId())
                                .build()));
    }

    public Mono<FearActionEntity> mapDtoToEntity(Mono<RequestFearActionDTO> fearActionDTOMono, Mono<MonsterEntity> monsterEntityMono) {
        return fearActionDTOMono.zipWith(monsterEntityMono)
                .flatMap(tuple -> Mono.just(
                        FearActionEntity.builder()
                                .id(tuple.getT1().getId())
                                .monsterEntity(tuple.getT2())
                                .date(tuple.getT1().getDate())
                                .doorId(tuple.getT1().getDoorId())
                                .build()));
    }

}
