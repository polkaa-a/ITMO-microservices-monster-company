package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.FearActionDTO;
import monsters.model.FearActionEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FearActionMapper {

    // TODO: 14.11.2022 connection with Door

    private final MonsterMapper monsterMapper;

    public Mono<FearActionDTO> mapEntityToDto(Mono<FearActionEntity> fearActionEntityMono) {
        var monsterDTOMono = fearActionEntityMono.flatMap(
                fearActionEntity -> monsterMapper.mapEntityToDto(Mono.just(
                        fearActionEntity.getMonsterEntity())));

        return fearActionEntityMono.zipWith(monsterDTOMono)
                .flatMap(tuple -> Mono.just(
                        FearActionDTO.builder()
                                .id(tuple.getT1().getId())
                                .monster(tuple.getT2())
                                .date(tuple.getT1().getDate())
                                //.doorId()
                                .build()));
    }

    public Mono<FearActionEntity> mapDtoToEntity(Mono<FearActionDTO> fearActionDTOMono) {

        var monsterEntityMono = fearActionDTOMono.flatMap(
                fearActionDTO -> monsterMapper.mapDtoToEntity(Mono.just(
                        fearActionDTO.getMonster())));

        return fearActionDTOMono.zipWith(monsterEntityMono)
                .flatMap(tuple -> Mono.just(
                        FearActionEntity.builder()
                                .id(tuple.getT1().getId())
                                .monsterEntity(tuple.getT2())
                                .date(tuple.getT1().getDate())
                                //.doorId()
                                .build()));
    }

}
