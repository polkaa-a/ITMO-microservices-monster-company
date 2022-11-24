package monsters.mapper;

import monsters.dto.CityDTO;
import monsters.model.CityEntity;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    public CityDTO mapEntityToDto(CityEntity cityEntity) {
        return CityDTO.builder()
                .id(cityEntity.getId())
                .name(cityEntity.getName())
                .build();
    }

    public CityEntity mapDtoToEntity(CityDTO cityDTO) {
        return CityEntity.builder()
                .id(cityDTO.getId())
                .name(cityDTO.getName())
                .build();
    }
}
