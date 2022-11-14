package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.monster.MonsterDTO;
import monsters.dto.monster.MonsterRatingDTO;
import monsters.model.MonsterEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MonsterMapper {

    // TODO: 14.11.2022 connection with User

    private final ModelMapper modelMapper;

    public Mono<MonsterDTO> mapEntityToDto(Mono<MonsterEntity> monsterEntityMono) {
        return monsterEntityMono.flatMap(monsterEntity -> Mono.just(
                        modelMapper.map(monsterEntity, MonsterDTO.class)))
                .flatMap(monsterDTO -> {
                    //monsterDTO.setUserId();
                    return Mono.just(monsterDTO);
                });
    }

    public Mono<MonsterEntity> mapDtoToEntity(Mono<MonsterDTO> monsterDTOMono) {
        return monsterDTOMono.flatMap(monsterDTO -> Mono.just(
                        modelMapper.map(monsterDTO, MonsterEntity.class)))
                .flatMap(monsterEntity -> {
                    //monsterEntity.setUserEntity();
                    return Mono.just(monsterEntity);
                });
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
