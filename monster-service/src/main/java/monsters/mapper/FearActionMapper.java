package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerFearActionDTO;
import monsters.dto.request.RequestFearActionDTO;
import monsters.model.FearActionEntity;
import monsters.model.MonsterEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FearActionMapper {

    private MonsterMapper monsterMapper;

    public AnswerFearActionDTO mapEntityToDto(FearActionEntity fearActionEntity) {
        return AnswerFearActionDTO.builder()
                .id(fearActionEntity.getId())
                .monster(monsterMapper.mapEntityToDto(fearActionEntity.getMonsterEntity()))
                .doorId(fearActionEntity.getDoorId())
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
