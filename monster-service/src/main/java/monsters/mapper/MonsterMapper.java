package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.MonsterRatingDTO;
import monsters.dto.RewardDTO;
import monsters.dto.answer.AnswerMonsterDTO;
import monsters.dto.answer.UserResponseDTO;
import monsters.dto.request.RequestMonsterDTO;
import monsters.model.MonsterEntity;
import monsters.model.RewardEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MonsterMapper {

    private final RewardMapper rewardMapper;

    public AnswerMonsterDTO mapEntityToDto(MonsterEntity monsterEntity, UserResponseDTO userResponseDTO) {
        List<RewardEntity> rewards = monsterEntity.getRewards();
        List<RewardDTO> rewardsDTOs = new ArrayList<>();
        for (RewardEntity rewardEntity : rewards) {
            rewardsDTOs.add(rewardMapper.mapEntityToDto(rewardEntity));
        }
        return AnswerMonsterDTO.builder()
                .userResponseDTO(userResponseDTO)
                .salary(monsterEntity.getSalary())
                .name(monsterEntity.getName())
                .job(monsterEntity.getJob())
                .gender(monsterEntity.getGender())
                .email(monsterEntity.getEmail())
                .dateOfBirth(monsterEntity.getDateOfBirth())
                .id(monsterEntity.getId())
                .rewards(rewardsDTOs)
                .build();
    }

    public MonsterEntity mapDtoToEntity(RequestMonsterDTO monsterDTO) {
        return MonsterEntity.builder()
                .dateOfBirth(monsterDTO.getDateOfBirth())
                .email(monsterDTO.getEmail())
                .gender(monsterDTO.getGender())
                .id(monsterDTO.getId())
                .job(monsterDTO.getJob())
                .name(monsterDTO.getName())
                .salary(monsterDTO.getSalary())
                .userId(monsterDTO.getUserId())
                .rewards(new ArrayList<>())
                .build();
    }

    public MonsterRatingDTO mapEntityToRatingDTO(MonsterEntity monsterEntity, long countBalloons) {
        return MonsterRatingDTO.builder()
                .monsterID(monsterEntity.getId())
                .countBalloons(countBalloons)
                .build();
    }

}
