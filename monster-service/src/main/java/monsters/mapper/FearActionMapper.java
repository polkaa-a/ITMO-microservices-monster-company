package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.DoorDTO;
import monsters.dto.answer.AnswerFearActionDTO;
import monsters.dto.answer.UserResponseDTO;
import monsters.dto.request.RequestFearActionDTO;
import monsters.model.FearActionEntity;
import monsters.model.MonsterEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FearActionMapper {

    private final MonsterMapper monsterMapper;

    public AnswerFearActionDTO mapEntityToDto(FearActionEntity fearActionEntity, UserResponseDTO userResponseDTO, DoorDTO doorDTO) {
        return AnswerFearActionDTO.builder()
                .id(fearActionEntity.getId())
                .monster(monsterMapper.mapEntityToDto(fearActionEntity.getMonsterEntity(), userResponseDTO))
                .door(doorDTO)
                .date(fearActionEntity.getDate())
                .build();
    }

    public FearActionEntity mapDtoToEntity(RequestFearActionDTO fearActionDTO, MonsterEntity monsterEntity) {
        return FearActionEntity.builder()
                .id(fearActionDTO.getId())
                .monsterEntity(monsterEntity)
                .doorId(fearActionDTO.getDoorId())
                .date(fearActionDTO.getDate())
                .build();
    }

}
