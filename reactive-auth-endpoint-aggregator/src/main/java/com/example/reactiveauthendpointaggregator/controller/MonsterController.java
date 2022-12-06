package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.AnswerMonsterDTO;
import com.example.reactiveauthendpointaggregator.dto.MonsterRatingDTO;
import com.example.reactiveauthendpointaggregator.dto.RequestMonsterDTO;
import com.example.reactiveauthendpointaggregator.enums.Job;
import com.example.reactiveauthendpointaggregator.feignclient.MonsterServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monsters")
public class MonsterController {
    private final MonsterServiceFeignClient monsterClient;


    @GetMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<AnswerMonsterDTO> getMonster(@PathVariable UUID monsterId) {
        return monsterClient.getMonster(monsterId);
    }

    @GetMapping("/rating")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Flux<MonsterRatingDTO> getRating(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
        return monsterClient.getRating(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<AnswerMonsterDTO> addMonster(@RequestBody Mono<RequestMonsterDTO> monsterDTOMono) {
        return monsterClient.addMonster(monsterDTOMono);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size) {
        return getMonoResponseEntity(monsterClient.findAllMonsters(page, size));
    }

    @GetMapping("/job")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByJob(@RequestParam Job job,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5", name = "size") int size) {

        return getMonoResponseEntity(monsterClient.findAllByJob(job, page, size));
    }

    @GetMapping("/fear-action/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByFearActionDate(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return getMonoResponseEntity(monsterClient.findAllByFearActionDate(date, page, size));
    }

    @GetMapping("/infection/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER') or hasAuthority('DISINFECTOR')")
    public Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> findAllByInfectionDate(@PathVariable
                                                                               @DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
        return getMonoResponseEntity(monsterClient.findAllByInfectionDate(date));
    }

    @PatchMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RECRUITER')")
    public Mono<AnswerMonsterDTO> updateJobById(@PathVariable UUID monsterId, @RequestParam Job job) {
        return monsterClient.updateJobById(monsterId, job);
    }

    @DeleteMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteMonster(@PathVariable UUID monsterId) {
        return monsterClient.deleteMonster(monsterId);
    }

    @PutMapping("/{monsterId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<AnswerMonsterDTO> putMonster(@PathVariable UUID monsterId,
                                             @RequestBody Mono<RequestMonsterDTO> monsterDTOMono) {
        return monsterClient.putMonster(monsterId, monsterDTOMono);
    }

    private Mono<ResponseEntity<Flux<AnswerMonsterDTO>>> getMonoResponseEntity(Flux<AnswerMonsterDTO> monsterDTOFlux) {
        var emptyResponseMono = Mono.just(
                new ResponseEntity<Flux<AnswerMonsterDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(monsterDTOFlux, HttpStatus.OK));

        return monsterDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }
}
