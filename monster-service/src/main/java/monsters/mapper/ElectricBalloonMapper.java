package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.DoorDTO;
import monsters.dto.answer.AnswerElectricBalloonDTO;
import monsters.dto.answer.UserResponseDTO;
import monsters.dto.request.RequestElectricBalloonDTO;
import monsters.model.CityEntity;
import monsters.model.ElectricBalloonEntity;
import monsters.model.FearActionEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElectricBalloonMapper {

    private final FearActionMapper fearActionMapper;
    private final CityMapper cityMapper;

    public AnswerElectricBalloonDTO mapEntityToDto(ElectricBalloonEntity electricBalloonEntity, UserResponseDTO userResponseDTO, DoorDTO doorDTO) {
        return AnswerElectricBalloonDTO.builder()
                .id(electricBalloonEntity.getId())
                .fearAction(fearActionMapper.mapEntityToDto(electricBalloonEntity.getFearActionEntity(), userResponseDTO, doorDTO))
                .city(cityMapper.mapEntityToDto(electricBalloonEntity.getCityEntity()))
                .build();
    }

    public ElectricBalloonEntity mapDtoToEntity(RequestElectricBalloonDTO electricBalloonDTO, FearActionEntity fearActionEntity, CityEntity cityEntity) {
        return ElectricBalloonEntity.builder()
                .fearActionEntity(fearActionEntity)
                .cityEntity(cityEntity)
                .id(electricBalloonDTO.getId())
                .build();
    }
}
