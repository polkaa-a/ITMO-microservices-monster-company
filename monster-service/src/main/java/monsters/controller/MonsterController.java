package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.MonsterRatingDTO;
import monsters.dto.answer.AnswerMonsterDTO;
import monsters.dto.request.RequestMonsterDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.MonsterEntity;
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
    public Mono<AnswerMonsterDTO> getMonster(@PathVariable UUID monsterId) {
        return monsterService.findById(monsterId)
                .flatMap(monsterEntity -> Mono.just(monsterMapper.mapEntityToDto(monsterEntity)));
    }

    @GetMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MonsterRatingDTO> getRating(@RequestParam(defaultValue = "0")
                                            @Min(value = 0, message = "must not be less than zero") int page,
                                            @RequestParam(defaultValue = "5")
                                            @Max(value = 50, message = "must not be more than 50 characters") int size) {
        Flux<MonsterRatingDTO> ratingDTO = Flux.empty();
        Flux<Tuple2<MonsterEntity, Long>> rating = monsterService.getRating(page, size);

        return rating.flatMap(tuple -> ratingDTO.mergeWith(Mono.just(monsterMapper.mapEntityToRatingDTO(tuple.getT1(), tuple.getT2()))));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AnswerMonsterDTO> addMonster(@RequestBody @Valid Mono<RequestMonsterDTO> monsterDTOMono) {
        return monsterService.save(monsterDTOMono)
                .flatMap(monsterEntity -> Mono.just(monsterMapper.mapEntityToDto(monsterEntity)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAll(@RequestParam(defaultValue = "0")
                                                                @Min(value = 0, message = "must not be less than zero") int page,
                                                                @RequestParam(defaultValue = "5")
                                                                @Max(value = 50, message = "must not be more than 50 characters") int size) {

        return getMonoResponseEntity(monsterService.findAll(page, size));
    }

    @GetMapping("/job")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByJob(@RequestParam Job job,
                                                                     @RequestParam(defaultValue = "0")
                                                                     @Min(value = 0, message = "must not be less than zero") int page,
                                                                     @RequestParam(defaultValue = "5", name = "size")
                                                                     @Max(value = 50, message = "must not be more than 50 characters") int size) {

        return getMonoResponseEntity(monsterService.findAllByJob(job, page, size));
    }

    @GetMapping("/fear-action/{date}")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByFearActionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date,
                                                                                @RequestParam(defaultValue = "0")
                                                                                @Min(value = 0, message = "must not be less than zero") int page,
                                                                                @RequestParam(defaultValue = "5")
                                                                                @Max(value = 50, message = "must not be more than 50 characters") int size) {

        return getMonoResponseEntity(monsterService.findAllByDateOfFearAction(date, page, size));
    }

    @GetMapping("/infection/{date}")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByInfectionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date) {
        return getMonoResponseEntity(monsterService.findAllByInfectionDate(date));
    }

    @PatchMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerMonsterDTO> updateJobById(@PathVariable UUID monsterId, @RequestParam Job job) {
        return monsterService.updateJobById(job, monsterId)
                .flatMap(monsterEntity -> Mono.just(monsterMapper.mapEntityToDto(monsterEntity)));
    }

    @DeleteMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMonster(@PathVariable UUID monsterId) {
        return monsterService.delete(monsterId);
    }

    @PutMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerMonsterDTO> putMonster(@PathVariable UUID monsterId, @RequestBody @Valid Mono<RequestMonsterDTO> monsterDTOMono) {
        return monsterService.updateById(monsterId, monsterDTOMono)
                .flatMap(monsterEntity -> Mono.just(monsterMapper.mapEntityToDto(monsterEntity)));
    }

    private Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> getMonoResponseEntity(Flux<MonsterEntity> monsterEntityFlux) {
        var monsterDTOFlux = monsterEntityFlux.buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(monsterMapper::mapEntityToDto)
                        .subscribeOn(Schedulers.parallel()));

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<AnswerMonsterDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(monsterDTOFlux, HttpStatus.OK));

        return monsterDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }
}
