package monsters.mapper;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerElectricBalloonDTO;
import monsters.dto.request.RequestElectricBalloonDTO;
import monsters.model.CityEntity;
import monsters.model.ElectricBalloonEntity;
import monsters.model.FearActionEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ElectricBalloonMapper {

    private final FearActionMapper fearActionMapper;

    public Mono<AnswerElectricBalloonDTO> mapEntityToDto(Mono<ElectricBalloonEntity> electricBalloonEntityMono) {
        var fearActionDTOMono = electricBalloonEntityMono.flatMap(
                electricBalloonEntity -> fearActionMapper.mapEntityToDto(Mono.just(
                        electricBalloonEntity.getFearActionEntity())));

        return electricBalloonEntityMono.zipWith(fearActionDTOMono)
                .flatMap(tuple -> Mono.just(
                        AnswerElectricBalloonDTO.builder()
                                .id(tuple.getT1().getId())
                                .fearAction(tuple.getT2())
                                .city(tuple.getT1().getCityEntity())
                                .build()));
    }

    public Mono<ElectricBalloonEntity> mapDtoToEntity(Mono<RequestElectricBalloonDTO> electricBalloonDTOMono, Mono<CityEntity> cityEntityMono, Mono<FearActionEntity> fearActionEntityMono) {
        return Mono.zip(electricBalloonDTOMono, fearActionEntityMono, cityEntityMono)
                .flatMap(tuple -> Mono.just(
                        ElectricBalloonEntity.builder()
                                .id(tuple.getT1().getId())
                                .fearActionEntity(tuple.getT2())
                                .cityEntity(tuple.getT3())
                                .build()));
    }
}
