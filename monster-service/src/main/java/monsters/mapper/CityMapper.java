package monsters.mapper;

import monsters.dto.CityDTO;
import monsters.model.CityEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CityMapper {

    public Mono<CityDTO> mapEntityToDto(Mono<CityEntity> cityEntityMono) {
        return cityEntityMono.flatMap(cityEntity -> Mono.just(CityDTO.builder()
                .id(cityEntity.getId())
                .name(cityEntity.getName())
                .build()));
    }

    public Mono<CityEntity> mapDtoToEntity(Mono<CityDTO> cityDTOMono) {
        return cityDTOMono.flatMap(cityDTO -> Mono.just(CityEntity.builder()
                .id(cityDTO.getId())
                .name(cityDTO.getName())
                .build()));
    }
}
