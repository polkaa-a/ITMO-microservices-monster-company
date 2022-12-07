package monsters.service;

import io.micrometer.core.instrument.MeterRegistry;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
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
        return monsterDTOMono.flatMap(monsterDTO ->
                Mono.fromCallable(() -> monsterRepository.findByEmail(monsterDTO.getEmail()).stream())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flux()
                        .flatMap(Flux::fromStream)
                        .hasElements()
                        .flatMap(hasElements -> hasElements ?
                                Mono.error(new EntityExistsException(EXC_EXIST_EMAIL + ": " + monsterDTO.getEmail())) :
                                Mono.fromCallable(() -> monsterRepository.findByUserId(monsterDTO.getUserId()).stream())
                                        .subscribeOn(Schedulers.boundedElastic()))
                        .flux()
                        .flatMap(Flux::fromStream)
                        .hasElements()
                        .flatMap(hasElements -> hasElements ?
                                Mono.error(new EntityExistsException(EXC_EXIST_USER + ": " + monsterDTO.getUserId())) :
                                Mono.just(monsterMapper.mapDtoToEntity(monsterDTO))
                                        .flatMap(monsterEntity -> Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                                                .subscribeOn(Schedulers.boundedElastic()))));
    }

    public Flux<MonsterEntity> findAllByDateOfFearAction(java.util.Date date, int page, int size) {
        return fearActionService.findAllByDate(date, page, size)
                .flatMap(fearActionEntity -> Mono.just(fearActionEntity.getMonsterEntity()))
                .distinct()
                .doOnError(Throwable::printStackTrace);
    }

    public Flux<MonsterEntity> findAll(int page, int size) {
        return Mono.fromCallable(() -> monsterRepository.findAll(PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);
    }

    public Mono<MonsterEntity> findById(UUID monsterId) {
        return Mono.fromCallable(() -> monsterRepository.findById(monsterId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)));
    }

    public Mono<MonsterEntity> updateJobById(Job job, UUID monsterId) {
        return Mono.fromCallable(() -> monsterRepository.findById(monsterId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .flatMap(monsterEntity -> {
                    monsterEntity.setJob(job);
                    return Mono.fromCallable(() -> monsterRepository.save(monsterEntity))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Flux<Tuple2<MonsterEntity, Long>> getRating(int page, int size) {
        Flux<Tuple2<MonsterEntity, Long>> ratingFlux = Flux.empty();

        return Mono.fromCallable(() -> monsterRepository.findAll(PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
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
        return Mono.fromCallable(() -> monsterRepository.findAllByJob(job, PageRequest.of(page, size)).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream);
    }

    public Flux<MonsterEntity> findAllByInfectionDate(Date date) {
        return Mono.fromCallable(() -> infectionServiceFeignClient.findAllByDate(date).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .flatMap(infectionDTO -> Mono.fromCallable(() ->
                                monsterRepository.findById(infectionDTO.getMonsterId()).stream())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flux()
                        .flatMap(Flux::fromStream))
                .distinct();
    }

    public Mono<MonsterEntity> updateById(UUID monsterId, Mono<RequestMonsterDTO> monsterDTOMono) {
        return Mono.fromCallable(() -> monsterRepository.findById(monsterId).stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(Flux::fromStream)
                .singleOrEmpty()
                .switchIfEmpty(Mono.error(new NotFoundException(EXC_MES_ID + ": " + monsterId)))
                .then(monsterDTOMono.flatMap(monsterDTO -> Mono.just(monsterMapper.mapDtoToEntity(monsterDTO)))
                        .flatMap(monsterEntity -> {
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
