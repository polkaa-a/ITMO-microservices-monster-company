package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.ElectricBalloonDTO;
import monsters.model.ElectricBalloonEntity;
import monsters.service.CityService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ElectricBalloonMapper {

    private final CityService cityService;
    private final FearActionMapper fearActionMapper;

    public Mono<ElectricBalloonDTO> mapEntityToDto(Mono<ElectricBalloonEntity> electricBalloonEntityMono) {
        var fearActionDTOMono = electricBalloonEntityMono.flatMap(
                electricBalloonEntity -> fearActionMapper.mapEntityToDto(Mono.just(
                        electricBalloonEntity.getFearActionEntity())));

        return electricBalloonEntityMono.zipWith(fearActionDTOMono)
                .flatMap(tuple -> Mono.just(
                        ElectricBalloonDTO.builder()
                                .id(tuple.getT1().getId())
                                .fearAction(tuple.getT2())
                                .cityName(tuple.getT1().getCityEntity().getName())
                                .build()));
    }

    public Mono<ElectricBalloonEntity> mapDtoToEntity(Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        var fearActionEntityMono = electricBalloonDTOMono.flatMap(
                electricBalloonEntity -> fearActionMapper.mapDtoToEntity(Mono.just(
                        electricBalloonEntity.getFearAction())));

        var cityEntityMono = electricBalloonDTOMono.flatMap(
                electricBalloonEntity -> cityService.findByName(Mono.just(
                        electricBalloonEntity.getCityName())));

        return Mono.zip(electricBalloonDTOMono, fearActionEntityMono, cityEntityMono)
                .flatMap(tuple -> Mono.just(
                        ElectricBalloonEntity.builder()
                                .id(tuple.getT1().getId())
                                .fearActionEntity(tuple.getT2())
                                .cityEntity(tuple.getT3())
                                .build()));
    }
}
