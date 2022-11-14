package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.RewardDTO;
import monsters.model.RewardEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class RewardMapper {

    private final ModelMapper modelMapper;

    public Mono<RewardDTO> mapEntityToDto(Mono<RewardEntity> rewardEntityMono) {
        return rewardEntityMono.flatMap(rewardEntity -> Mono.just(
                modelMapper.map(rewardEntity, RewardDTO.class)
        ));
    }

    public Mono<RewardEntity> mapDtoToEntity(Mono<RewardDTO> rewardDTOMono) {
        return rewardDTOMono.flatMap(rewardDTO -> Mono.just(
                modelMapper.map(rewardDTO, RewardEntity.class)
        ));
    }

}
