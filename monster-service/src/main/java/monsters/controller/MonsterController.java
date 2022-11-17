package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.monster.MonsterDTO;
import monsters.dto.monster.MonsterRatingDTO;
import monsters.enums.Job;
import monsters.mapper.MonsterMapper;
import monsters.model.MonsterEntity;
import monsters.service.MonsterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<MonsterDTO> getMonster(@PathVariable UUID monsterId) {
        return monsterMapper.mapEntityToDto(monsterService.findById(monsterId));
    }

    @GetMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Flux<MonsterRatingDTO> getRating(int page, int size) {
        Flux<MonsterRatingDTO> ratingDTO = Flux.empty();
        Flux<Tuple2<MonsterEntity, Long>> rating = monsterService.getRating(page, size);

        return rating.flatMap(tuple -> ratingDTO.mergeWith(monsterMapper.mapEntityToRatingDTO(Mono.just(tuple.getT1()), tuple.getT2())));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<MonsterDTO> addMonster(@RequestBody Mono<@Valid MonsterDTO> monsterDTOMono) {
        return monsterMapper.mapEntityToDto(monsterService.save(monsterDTOMono));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAll(@RequestParam(defaultValue = "0")
                                                          @Min(value = 0, message = "must not be less than zero") int page,
                                                          @RequestParam(defaultValue = "5")
                                                          @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAll(page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/job")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByJob(@RequestParam Job job,
                                                               @RequestParam(defaultValue = "0")
                                                               @Min(value = 0, message = "must not be less than zero") int page,
                                                               @RequestParam(defaultValue = "5", name = "size")
                                                               @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAllByJob(job, page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/fear-action/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByFearActionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date,
                                                                          @RequestParam(defaultValue = "0")
                                                                          @Min(value = 0, message = "must not be less than zero") int page,
                                                                          @RequestParam(defaultValue = "5")
                                                                          @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAllByDateOfFearAction(date, page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/infection/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByInfectionDate(@PathVariable @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Date date, @RequestParam(defaultValue = "0")
    @Min(value = 0, message = "must not be less than zero") int page,
                                                                         @RequestParam(defaultValue = "5")
                                                                         @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var monsterEntityFlux = monsterService.findAllByInfectionDate(date, page, size);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @PatchMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<MonsterDTO> updateJobById(@PathVariable UUID monsterId, @RequestParam Job job) {
        return monsterMapper.mapEntityToDto(monsterService.updateJobById(job, monsterId));
    }

    @DeleteMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteMonster(@PathVariable UUID monsterId) {
        return monsterService.delete(monsterId);
    }

    @PutMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<MonsterDTO> putMonster(@PathVariable UUID monsterId, @RequestBody Mono<@Valid MonsterDTO> monsterDTOMono) {
        return monsterMapper.mapEntityToDto(monsterService.updateById(monsterId, monsterDTOMono));
    }

    private Mono<ResponseEntity<Flux<MonsterDTO>>> getMonoResponseEntity(Flux<MonsterEntity> monsterEntityFlux) {
        var monsterDTOFlux = monsterEntityFlux.buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(monsterEntity -> monsterMapper.mapEntityToDto(Mono.just(monsterEntity)))
                        .subscribeOn(Schedulers.parallel()))
                .flatMap(Mono::flux);

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<MonsterDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(monsterDTOFlux, HttpStatus.OK));

        return monsterEntityFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }
}
