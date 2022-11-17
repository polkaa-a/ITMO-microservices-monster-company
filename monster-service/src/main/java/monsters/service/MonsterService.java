package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.monster.MonsterDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.CityEntity;
import monsters.model.MonsterEntity;
import monsters.repository.ElectricBalloonRepository;
import monsters.repository.MonsterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import javax.persistence.EntityExistsException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MonsterService {

    private static final String EXC_EXIST_EMAIL = "monster with this email already exists";
    private static final String EXC_EXIST_USER = "monster with this userId already exists";
    private static final String EXC_MES_ID = "none monster was found by id";
    private static final int BUFFER_SIZE = 40;
    private final MonsterRepository monsterRepository;
    private final ElectricBalloonRepository electricBalloonRepository;
    private final MonsterMapper monsterMapper;

    public Mono<MonsterEntity> save(Mono<MonsterDTO> monsterDTOMono) {
        Flux<MonsterEntity> monsterFlux = Flux.empty();

        var savedEntityMono = monsterMapper.mapDtoToEntity(monsterDTOMono)
                .flatMap(monsterEntity -> Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                        .subscribeOn(Schedulers.boundedElastic()));

        Mono<MonsterEntity> emailErrorMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.error(new EntityExistsException(EXC_EXIST_EMAIL + ": " + monsterDTO.getEmail())));

        Mono<MonsterEntity> idErrorMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.error(new EntityExistsException(EXC_EXIST_USER + ": " + monsterDTO.getUserId())));

        Mono<MonsterEntity> monsterEntityIdMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.fromCallable(() -> monsterRepository.findByUserId(monsterDTO.getUserId()))
                .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(optional -> {
                            optional.map(monsterEntity -> monsterFlux.mergeWith(Mono.just(monsterEntity)));
                            return monsterFlux.hasElements().flatMap(hasElements -> hasElements ? idErrorMono : savedEntityMono);
                        }));

        Mono<MonsterEntity> monsterEntityEmailMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.fromCallable(() -> monsterRepository.findByEmail(monsterDTO.getEmail()))
                        .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(optional -> {
                            optional.map(monsterEntity -> monsterFlux.mergeWith(Mono.just(monsterEntity)));
                            return monsterFlux.hasElements().flatMap(hasElements -> hasElements ? emailErrorMono : monsterEntityIdMono);
                        });

        return monsterEntityEmailMono
                .hasElement()
                .flatMap(hasElements -> hasElements ? emailErrorMono : monsterEntityIdMono);
    }

    public Flux<MonsterEntity> findAll(int page, int size) {
        var monstersMonoPage = Mono.fromCallable(() -> monsterRepository.findAll(PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> monstersFlux = Flux.empty();

        return monstersMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(monsterEntity -> monstersFlux.mergeWith(Mono.just(monsterEntity)));
                    return monstersFlux;
                });
    }

    public Mono<MonsterEntity> findById(UUID monsterId) {
        var monstersMonoOptional = Mono.fromCallable(() ->
                monsterRepository.findById(monsterId)).subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> monstersFlux = Flux.empty();

        return monstersMonoOptional
                .flatMap(optional -> {
                    optional.map(monsterEntity -> monstersFlux.mergeWith(Mono.just(monsterEntity)));
                    return monstersFlux.single();
                })
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)));
    }

    public Mono<MonsterEntity> updateJobById(Job job, UUID monsterId) {
        Mono<MonsterEntity> monsterEntityMono = Mono.empty();

        Mono<Optional<MonsterEntity>> optionalMono = Mono.fromCallable(() -> monsterRepository.findById(monsterId))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Optional<MonsterEntity>> idErrorMono = Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId));

        return optionalMono
                .switchIfEmpty(idErrorMono)
                .flatMap(optional -> {
                    optional.map(monsterEntity -> monsterEntityMono.mergeWith(Mono.just(monsterEntity)));
                    return monsterEntityMono;
                })
                .flatMap(monsterEntity -> {
                    monsterEntity.setJob(job);
                    return Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Flux<Tuple2<MonsterEntity, Long>> getRating(int page, int size) {
        Flux<Tuple2<MonsterEntity, Long>> ratingFlux = Flux.empty();
        var monsterEntityListMono = Mono.fromCallable(monsterRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic());

        return monsterEntityListMono
                .flux()
                .flatMap(Flux::fromIterable)
                .buffer(BUFFER_SIZE)
                .flatMap(list -> Flux.fromIterable(list)
                        .flatMap(monsterEntity -> {
                            var balloons = Mono.fromCallable(() ->
                                            electricBalloonRepository.findAllByMonsterId(monsterEntity.getId(), PageRequest.of(page, size)))
                                    .subscribeOn(Schedulers.boundedElastic());
                            return balloons.flux()
                                    .flatMap(p -> ratingFlux
                                            .mergeWith(Flux.zip(Mono.just(monsterEntity), Mono.just(p.stream().count()))));
                        }).subscribeOn(Schedulers.parallel()));
    }

    public Flux<MonsterEntity> findAllByJob(Job job, int page, int size) {
        var monstersMonoPage = Mono.fromCallable(() ->
                        monsterRepository.findAllByJob(job, PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> monstersFlux = Flux.empty();

        return monstersMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(monsterEntity -> monstersFlux.mergeWith(Mono.just(monsterEntity)));
                    return monstersFlux;
                });
    }

    public Flux<MonsterEntity> findAllByDateOfFearAction(Date date, int page, int size) {
        var monstersMonoPage = Mono.fromCallable(() ->
                        monsterRepository.findAllByDateOfFearAction(date, PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> monstersFlux = Flux.empty();

        return monstersMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(monsterEntity -> monstersFlux.mergeWith(Mono.just(monsterEntity)));
                    return monstersFlux;
                });
    }

    public Flux<MonsterEntity> findAllByInfectionDate(Date date, int page, int size) {

        var monstersMonoPage = Mono.fromCallable(() ->
                        monsterRepository.findAllByInfectionDate(date, PageRequest.of(page, size)))
                .subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> monstersFlux = Flux.empty();

        return monstersMonoPage
                .flux()
                .flatMap(p -> {
                    p.map(monsterEntity -> monstersFlux.mergeWith(Mono.just(monsterEntity)));
                    return monstersFlux;
                });
    }

    public Mono<MonsterEntity> updateById(UUID monsterId, Mono<MonsterDTO> monsterDTOMono) {
        return Mono.fromCallable(() -> monsterRepository.findById(monsterId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .then(monsterMapper.mapDtoToEntity(monsterDTOMono).flatMap(monsterEntity -> {
                    monsterEntity.setId(monsterId);
                    return Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                }));
    }

    public Mono<Void> delete(UUID monsterId) {
        return Mono.fromCallable(() -> monsterRepository.findById(monsterId))
                .subscribeOn(Schedulers.boundedElastic())
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .then(Mono.fromRunnable(() -> monsterRepository.deleteById(monsterId))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }
}
