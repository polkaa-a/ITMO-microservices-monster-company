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

    private final MonsterService monsterService;
    private final MonsterMapper monsterMapper;

    private static final int BUFFER_SIZE = 40;

    @GetMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<MonsterDTO> getMonster(@PathVariable(name = "monsterId") Mono<UUID> monsterIdMono) {
        return monsterMapper.mapEntityToDto(monsterService.findById(monsterIdMono));
    }

    @GetMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Flux<MonsterRatingDTO> getRating(Mono<Integer> pageMono, Mono<Integer> sizeMono) {
        Flux<MonsterRatingDTO> ratingDTO = Flux.empty();
        Flux<Tuple2<MonsterEntity, Long>> rating = monsterService.getRating(pageMono, sizeMono);

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
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAll(@RequestParam(defaultValue = "0", name = "page")
                                                                  Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                          @RequestParam(defaultValue = "5", name = "size")
                                                                  Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var monsterEntityFlux = monsterService.findAll(pageMono, sizeMono);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/job")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByJob(@RequestParam(name = "job") Mono<Job> jobMono,
                                                               @RequestParam(defaultValue = "0", name = "page")
                                                                       Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                               @RequestParam(defaultValue = "5", name = "size")
                                                                       Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var monsterEntityFlux = monsterService.findAllByJob(jobMono, pageMono, sizeMono);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/fear-action/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByFearActionDate(@PathVariable(name = "date") @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Mono<Date> dateMono, @RequestParam(defaultValue = "0", name = "page")
            Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                                          @RequestParam(defaultValue = "5", name = "size")
                                                                                  Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var monsterEntityFlux = monsterService.findAllByDateOfFearAction(dateMono, pageMono, sizeMono);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @GetMapping("/infection/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<MonsterDTO>>> findAllByInfectionDate(@PathVariable(name = "date") @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Mono<Date> dateMono, @RequestParam(defaultValue = "0", name = "page")
            Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                                         @RequestParam(defaultValue = "5", name = "sizeMono")
                                                                                 Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var monsterEntityFlux = monsterService.findAllByInfectionDate(dateMono, pageMono, sizeMono);
        return getMonoResponseEntity(monsterEntityFlux);
    }

    @PatchMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<MonsterDTO> updateJobById(@PathVariable(name = "monsterId") Mono<UUID> monsterIdMono, @RequestParam(name = "job") Mono<Job> jobMono) {
        return monsterMapper.mapEntityToDto(monsterService.updateJobById(jobMono, monsterIdMono));
    }

    @DeleteMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteMonster(@PathVariable(name = "monsterId") Mono<UUID> monsterIdMono) {
        monsterService.delete(monsterIdMono);
    }

    @PutMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<MonsterDTO> putMonster(@PathVariable(name = "monsterId") Mono<UUID> monsterIdMono, @RequestBody Mono<@Valid MonsterDTO> monsterDTOMono) {
        return monsterMapper.mapEntityToDto(monsterService.updateById(monsterIdMono, monsterDTOMono));
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
