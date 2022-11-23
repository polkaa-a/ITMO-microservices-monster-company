package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.RewardDTO;
import monsters.model.RewardEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RewardMapper {

    private final ModelMapper modelMapper;

    public RewardDTO mapEntityToDto(RewardEntity rewardEntity) {
        return modelMapper.map(rewardEntity, RewardDTO.class);
    }

    public RewardEntity mapDtoToEntity(RewardDTO rewardDTO) {
        return modelMapper.map(rewardDTO, RewardEntity.class);
    }

}
