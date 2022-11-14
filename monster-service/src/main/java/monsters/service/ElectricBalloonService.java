package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.ElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.model.ElectricBalloonEntity;
import monsters.repository.ElectricBalloonRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ElectricBalloonService {

    private final ElectricBalloonRepository electricBalloonRepository;
    private final ElectricBalloonMapper electricBalloonMapper;

    private static final String EXC_MES_ID = "none electric balloon was found by id";

    public Mono<ElectricBalloonEntity> save(Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonMapper.mapDtoToEntity(electricBalloonDTOMono).flatMap(electricBalloonRepository::save);

    }

    public Mono<ElectricBalloonEntity> findById(Mono<UUID> electricBalloonIdMono) {
        return electricBalloonIdMono.flatMap(electricBalloonId -> electricBalloonRepository.findById(electricBalloonId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId))));
    }


    public Flux<ElectricBalloonEntity> findAllFilledByDate(Mono<Date> dateMono, Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(dateMono, pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> electricBalloonRepository.findAllFilledByDate(tuple.getT1(), PageRequest.of(tuple.getT2(), tuple.getT3())));
    }

    public Flux<ElectricBalloonEntity> findAllFilledByDateAndCity(Mono<Date> dateMono, Mono<UUID> citiIdMono, Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(dateMono, citiIdMono, pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> electricBalloonRepository.findAllFilledByDateAndCity(tuple.getT1(), tuple.getT2(), PageRequest.of(tuple.getT3(), tuple.getT4())));
    }

    public Mono<ElectricBalloonEntity> updateById(Mono<UUID> electricBalloonIdMono, Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonIdMono.flatMap(electricBalloonId -> electricBalloonRepository.findById(electricBalloonId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                .then(electricBalloonMapper.mapDtoToEntity(electricBalloonDTOMono).flatMap(electricBalloonEntity -> {
                    electricBalloonEntity.setId(electricBalloonId);
                    return electricBalloonRepository.save(electricBalloonEntity);
                })));
    }

    public void delete(Mono<UUID> electricBalloonIdMono) {
        electricBalloonIdMono.map(electricBalloonId -> electricBalloonRepository.findById(electricBalloonId)
                        .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + electricBalloonId)))
                        .then(electricBalloonRepository.deleteById(electricBalloonId)))
                .subscribe();
    }
}
