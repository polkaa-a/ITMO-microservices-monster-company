package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.monster.MonsterDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.MonsterEntity;
import monsters.repository.ElectricBalloonRepository;
import monsters.repository.MonsterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.persistence.EntityExistsException;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MonsterService {

    private final MonsterRepository monsterRepository;
    private final ElectricBalloonRepository electricBalloonRepository;
    private final MonsterMapper monsterMapper;

    private static final String EXC_EXIST_EMAIL = "monster with this email already exists";
    private static final String EXC_EXIST_USER = "monster with this userId already exists";
    private static final String EXC_MES_ID = "none monster was found by id";

    private static final int BUFFER_SIZE = 40;

    public Mono<MonsterEntity> save(Mono<MonsterDTO> monsterDTOMono) {
        return monsterDTOMono.flatMap(monsterDTO -> monsterRepository.findByEmail(monsterDTO.getEmail())
                .hasElements()
                .flatMap(hasElements -> hasElements ?
                        Mono.error(new EntityExistsException(EXC_EXIST_EMAIL + ": " + monsterDTO.getEmail())) :
                        monsterDTOMono)
                .then(monsterRepository.findByUserId(monsterDTO.getUserId()).hasElements()
                        .flatMap(hasElements_ -> hasElements_ ?
                                Mono.error(new EntityExistsException(EXC_EXIST_USER + ": " + monsterDTO.getUserId())) :
                                monsterMapper.mapDtoToEntity(monsterDTOMono).flatMap(monsterRepository::save))));
    }

    public Flux<MonsterEntity> findAll(Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> monsterRepository.findAll(PageRequest.of(tuple.getT1(), tuple.getT2())));
    }

    public Mono<MonsterEntity> findById(Mono<UUID> monsterIdMono) {
        return monsterIdMono.flatMap(monsterId -> monsterRepository.findById(monsterIdMono)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId))));
    }

    public Mono<MonsterEntity> updateJobById(Mono<Job> jobMono, Mono<UUID> monsterIdMono) {
        return monsterIdMono.flatMap(monsterId -> monsterRepository.findById(monsterId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .flatMap(monsterEntity -> jobMono.flatMap(job -> {
                    monsterEntity.setJob(job);
                    return monsterRepository.save(monsterEntity);
                })));
    }

    public Flux<Tuple2<MonsterEntity, Long>> getRating(Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        Flux<Tuple2<MonsterEntity, Long>> rating = Flux.empty();
        var monsterEntityFlux = monsterRepository.findAll();

        return monsterEntityFlux
                .buffer(BUFFER_SIZE)
                .flatMap(list -> Flux.fromIterable(list).flatMap(monsterEntity -> {
                    var countBalloons = Mono.zip(pageMono, sizeMono).flatMap(tuple ->
                            electricBalloonRepository.findAllByMonsterId(monsterEntity.getId(), PageRequest.of(tuple.getT1(), tuple.getT2())).count()
                    );
                    return rating.mergeWith(Flux.zip(Mono.just(monsterEntity), countBalloons));
                }));
    }

    public Flux<MonsterEntity> findAllByJob(Mono<Job> jobMono, Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(jobMono, pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> monsterRepository.findAllByJob(tuple.getT1(), PageRequest.of(tuple.getT2(), tuple.getT3())));
    }

    public Flux<MonsterEntity> findAllByDateOfFearAction(Mono<Date> dateMono, Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(dateMono, pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> monsterRepository.findAllByDateOfFearAction(tuple.getT1(), PageRequest.of(tuple.getT2(), tuple.getT3())));
    }

    public Flux<MonsterEntity> findAllByInfectionDate(Mono<Date> dateMono, Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        return Mono.zip(dateMono, pageMono, sizeMono)
                .flux()
                .flatMap(tuple -> monsterRepository.findAllByInfectionDate(tuple.getT1(), PageRequest.of(tuple.getT2(), tuple.getT3())));
    }

    public Mono<MonsterEntity> updateById(Mono<UUID> monsterIdMono, Mono<MonsterDTO> monsterDTOMono) {
        return monsterIdMono.flatMap(monsterId -> monsterRepository.findById(monsterId)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .then(monsterMapper.mapDtoToEntity(monsterDTOMono).flatMap(monsterEntity -> {
                    monsterEntity.setId(monsterId);
                    return monsterRepository.save(monsterEntity);
                })));
    }

    public void delete(Mono<UUID> monsterIdMono) {
        monsterIdMono.map(monsterId -> monsterRepository.findById(monsterId)
                        .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                        .then(monsterRepository.deleteById(monsterId)))
                .subscribe();
    }
}
