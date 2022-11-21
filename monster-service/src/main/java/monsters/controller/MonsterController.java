package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.MonsterRatingDTO;
import monsters.dto.answer.AnswerMonsterDTO;
import monsters.dto.request.RequestMonsterDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.MonsterEntity;
import monsters.model.RewardEntity;
import monsters.service.MonsterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monsters")
public class MonsterController {

    private static final int BUFFER_SIZE = 40;
    private final MonsterService monsterService;
    private final MonsterMapper monsterMapper;

    @GetMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<AnswerMonsterDTO> getMonster(@PathVariable UUID monsterId) {
        Mono<MonsterEntity> monsterEntityMono = monsterService.findById(monsterId);
        return monsterMapper.mapEntityToDto(monsterEntityMono, getRewardsEntityFlux(monsterEntityMono));
    }

    @GetMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Flux<MonsterRatingDTO> getRating(@RequestParam(defaultValue = "0")
                                            @Min(value = 0, message = "must not be less than zero") int page,
                                            @RequestParam(defaultValue = "5")
                                            @Max(value = 50, message = "must not be more than 50 characters") int size) {
        Flux<MonsterRatingDTO> ratingDTO = Flux.empty();
        Flux<Tuple2<MonsterEntity, Long>> rating = monsterService.getRating(page, size);

        return rating.flatMap(tuple -> ratingDTO.mergeWith(monsterMapper.mapEntityToRatingDTO(Mono.just(tuple.getT1()), tuple.getT2())));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<AnswerMonsterDTO> addMonster(@RequestBody Mono<@Valid RequestMonsterDTO> monsterDTOMono) {
        Mono<MonsterEntity> monsterEntityMono = monsterService.save(monsterDTOMono);
        return monsterMapper.mapEntityToDto(monsterEntityMono, getRewardsEntityFlux(monsterEntityMono));
    }

    @GetMapping
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAll(@RequestParam(defaultValue = "0")
                                                                @Min(value = 0, message = "must not be less than zero") int page,
                                                                @RequestParam(defaultValue = "5")
                                                                @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAll(page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/job")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByJob(@RequestParam Job job,
                                                                     @RequestParam(defaultValue = "0")
                                                                     @Min(value = 0, message = "must not be less than zero") int page,
                                                                     @RequestParam(defaultValue = "5", name = "size")
                                                                     @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAllByJob(job, page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/fear-action/{date}")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByFearActionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date,
                                                                                @RequestParam(defaultValue = "0")
                                                                                @Min(value = 0, message = "must not be less than zero") int page,
                                                                                @RequestParam(defaultValue = "5")
                                                                                @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAllByDateOfFearAction(date, page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/infection/{date}")
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByInfectionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date) {
        var monsterEntityFlux = monsterService.findAllByInfectionDate(date);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @PatchMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<AnswerMonsterDTO> updateJobById(@PathVariable UUID monsterId, @RequestParam Job job) {
        Mono<MonsterEntity> monsterEntityMono = monsterService.updateJobById(job, monsterId);
        return monsterMapper.mapEntityToDto(monsterEntityMono, getRewardsEntityFlux(monsterEntityMono));
    }

    @DeleteMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteMonster(@PathVariable UUID monsterId) {
        return monsterService.delete(monsterId);
    }

    @PutMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('ADMIN')")
    public Mono<AnswerMonsterDTO> putMonster(@PathVariable UUID monsterId, @RequestBody Mono<@Valid RequestMonsterDTO> monsterDTOMono) {
        Mono<MonsterEntity> monsterEntityMono = monsterService.updateById(monsterId, monsterDTOMono);
        return monsterMapper.mapEntityToDto(monsterEntityMono, getRewardsEntityFlux(monsterEntityMono));
    }

    private Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> getMonoResponseEntity(Flux<MonsterEntity> monsterEntityFlux) {
        var monsterDTOFlux = monsterEntityFlux.buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(monsterEntity -> monsterMapper.mapEntityToDto(Mono.just(monsterEntity), getRewardsEntityFlux(Mono.just(monsterEntity))))
                        .subscribeOn(Schedulers.parallel()))
                .flatMap(Mono::flux);

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<AnswerMonsterDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(monsterDTOFlux, HttpStatus.OK));

        return monsterEntityFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    private Flux<RewardEntity> getRewardsEntityFlux(Mono<MonsterEntity> monsterEntityMono) {
        return monsterEntityMono
                .flux()
                .flatMap(monsterEntity -> Flux.fromIterable(monsterEntity.getRewards()));
    }
}
