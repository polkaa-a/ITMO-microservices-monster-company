package monsters.service;

import lombok.RequiredArgsConstructor;
import monsters.controller.exception.NotFoundException;
import monsters.dto.request.RequestMonsterDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.MonsterEntity;
import monsters.repository.MonsterRepository;
import monsters.service.feign.clients.InfectionServiceFeignClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import javax.persistence.EntityExistsException;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MonsterService {

    private static final String EXC_EXIST_EMAIL = "monster with this email already exists";
    private static final String EXC_EXIST_USER = "monster with this userId already exists";
    private static final String EXC_MES_ID = "none monster was found by id";

    private static final int BUFFER_SIZE = 40;

    private final MonsterRepository monsterRepository;
    private final ElectricBalloonService electricBalloonService;
    private final FearActionService fearActionService;
    private final MonsterMapper monsterMapper;
    private final InfectionServiceFeignClient infectionServiceFeignClient;

    public Mono<MonsterEntity> save(Mono<RequestMonsterDTO> monsterDTOMono) {
        var savedEntityMono = monsterMapper.mapDtoToEntity(monsterDTOMono)
                .flatMap(monsterEntity -> Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                        .subscribeOn(Schedulers.boundedElastic()));

        Mono<MonsterEntity> emailErrorMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.error(new EntityExistsException(EXC_EXIST_EMAIL + ": " + monsterDTO.getEmail())));

        Mono<MonsterEntity> idErrorMono = monsterDTOMono
                .flatMap(monsterDTO -> Mono.error(new EntityExistsException(EXC_EXIST_USER + ": " + monsterDTO.getUserId())));

        Mono<MonsterEntity> monsterEntityIdMono = monsterDTOMono
                .flatMap(monsterDTO ->
                        Mono.from(Mono.fromCallable(() -> monsterRepository.findByUserId(monsterDTO.getUserId()).stream())
                                .subscribeOn(Schedulers.boundedElastic())
                                .flux()
                                .flatMap(Flux::fromStream))

                ).hasElement()
                .flatMap(hasElements -> hasElements ? idErrorMono : savedEntityMono);


        return monsterDTOMono
                .flatMap(monsterDTO ->
                        Mono.from(Mono.fromCallable(() -> monsterRepository.findByEmail(monsterDTO.getEmail()).stream())
                                .subscribeOn(Schedulers.boundedElastic())
                                .flux()
                                .flatMap(Flux::fromStream))
                ).hasElement()
                .flatMap(hasElements -> hasElements ? emailErrorMono : monsterEntityIdMono);
    }

    public Flux<MonsterEntity> findAllByDateOfFearAction(Date date, int page, int size) {
        var fearActionsEntityFlux = fearActionService.findAllByDate(date, page, size);

        return fearActionsEntityFlux
                .flatMap(fearActionEntity -> Mono.just(fearActionEntity.getMonsterEntity()))
                .distinct();
    }

    public Flux<MonsterEntity> findAll(int page, int size) {
        return Mono.fromCallable(() -> monsterRepository.findAll(PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<MonsterEntity> findById(UUID monsterId) {
        var monstersMonoStream = Mono.fromCallable(() ->
                monsterRepository.findById(monsterId).stream()).subscribeOn(Schedulers.boundedElastic());

        return Mono.from(monstersMonoStream.flux().flatMap(Flux::fromStream)
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId))));
    }

    public Mono<MonsterEntity> updateJobById(Job job, UUID monsterId) {
        var monstersMonoStream = Mono.fromCallable(() -> monsterRepository.findById(monsterId).stream())
                .subscribeOn(Schedulers.boundedElastic());

        Flux<MonsterEntity> idErrorMono = Flux.error(new NotFoundException(EXC_MES_ID + ": " + monsterId));

        return Mono.from(monstersMonoStream
                .flux()
                .flatMap(Flux::fromStream)
                .switchIfEmpty(idErrorMono)
                .flatMap(monsterEntity -> {
                    monsterEntity.setJob(job);
                    return Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                            .flux()
                            .subscribeOn(Schedulers.boundedElastic());
                }));
    }

    public Flux<Tuple2<MonsterEntity, Long>> getRating(int page, int size) {
        Flux<Tuple2<MonsterEntity, Long>> ratingFlux = Flux.empty();
        var monstersMonoStream = Mono.fromCallable(() -> monsterRepository.findAll(PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic());

        return monstersMonoStream
                .flux()
                .flatMap(Flux::fromStream)
                .buffer(BUFFER_SIZE)
                .flatMap(list -> Flux.fromIterable(list)
                        .flatMap(monsterEntity -> {
                            var balloons = electricBalloonService
                                    .findAllByMonsterId(monsterEntity.getId(), page, size);

                            return ratingFlux.mergeWith(Flux.zip(Mono.just(monsterEntity), balloons.count()));
                        }).subscribeOn(Schedulers.parallel()));
    }

    public Flux<MonsterEntity> findAllByJob(Job job, int page, int size) {
        return Mono.fromCallable(() ->
                        monsterRepository.findAllByJob(job, PageRequest.of(page, size)).stream())
                .flux()
                .flatMap(Flux::fromStream)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<MonsterEntity> findAllByInfectionDate(Date date) {

        var infectionsDtoFlux = Mono.fromCallable(() ->
                        infectionServiceFeignClient.findAllByDate(date).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);

        return infectionsDtoFlux
                .flatMap(infectionDTO -> Mono.fromCallable(() ->
                                monsterRepository.findById(infectionDTO.getMonsterId()).stream())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flux()
                        .flatMap(Flux::fromStream))
                .distinct();
    }

    public Mono<MonsterEntity> updateById(UUID monsterId, Mono<RequestMonsterDTO> monsterDTOMono) {
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
