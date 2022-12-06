package com.example.reactiveauthendpointaggregator.feignclient;

import com.example.reactiveauthendpointaggregator.dto.*;
import com.example.reactiveauthendpointaggregator.enums.Job;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@ReactiveFeignClient(name = "monster-service")
public interface MonsterServiceFeignClient {
    @PostMapping("/cities")
    Mono<CityDTO> addCity(@RequestBody Mono<CityDTO> cityDTOMono);

    @GetMapping("/cities")
    Flux<CityDTO> findAllCities(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size);

    @DeleteMapping("/cities/{cityId}")
    Mono<Void> deleteCity(@PathVariable UUID cityId);

    @PutMapping("/cities/{cityId}")
    Mono<CityDTO> putCity(@PathVariable UUID cityId, @RequestBody Mono<CityDTO> cityDTOMono);

    @PostMapping("/electric-balloons")
    Mono<AnswerElectricBalloonDTO> addElectricBalloon(@RequestBody Mono<RequestElectricBalloonDTO> electricBalloonDTOMono);

    @GetMapping("/electric-balloons/{electricBalloonId}")
    Mono<AnswerElectricBalloonDTO> getElectricBalloon(@PathVariable UUID electricBalloonId);

    @GetMapping("/electric-balloons/date/{date}")
    Flux<AnswerElectricBalloonDTO> findAllFilledByDateAndCity(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(required = false) UUID cityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size);

    @DeleteMapping("/electric-balloons/{electricBalloonId}")
    Mono<Void> deleteElectricBalloon(@PathVariable UUID electricBalloonId);

    @PutMapping("/electric-balloons/{electricBalloonId}")
    Mono<AnswerElectricBalloonDTO> putElectricBalloon(@PathVariable UUID electricBalloonId,
                                                      @RequestBody Mono<RequestElectricBalloonDTO> electricBalloonDTOMono);

    @GetMapping("/fear-actions/{fearActionId}")
    Mono<AnswerFearActionDTO> getFearAction(@PathVariable UUID fearActionId);

    @GetMapping("/fear-actions/date/{date}")
    Flux<AnswerFearActionDTO> findAllByDate(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size);

    @PostMapping("/fear-actions")
    Mono<AnswerFearActionDTO> addFearAction(@RequestBody Mono<RequestFearActionDTO> fearActionDTOMono);

    @DeleteMapping("/fear-actions/{fearActionId}")
    Mono<Void> deleteFearAction(@PathVariable UUID fearActionId);

    @PutMapping("/fear-actions/{fearActionId}")
    Mono<AnswerFearActionDTO> putFearAction(@PathVariable UUID fearActionId,
                                            @RequestBody Mono<RequestFearActionDTO> fearActionMono);

    @GetMapping("/monsters/{monsterId}")
    Mono<AnswerMonsterDTO> getMonster(@PathVariable UUID monsterId);

    @GetMapping("/monsters/rating")
    Flux<MonsterRatingDTO> getRating(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size);

    @PostMapping("/monsters")
    Mono<AnswerMonsterDTO> addMonster(@RequestBody Mono<RequestMonsterDTO> monsterDTOMono);


    @GetMapping("/monsters")
    Flux<AnswerMonsterDTO> findAllMonsters(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size);

    @GetMapping("/monsters/job")
    Flux<AnswerMonsterDTO> findAllByJob(
            @RequestParam Job job,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5", name = "size") int size);

    @GetMapping("/monsters/fear-action/{date}")
    Flux<AnswerMonsterDTO> findAllByFearActionDate(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size);

    @GetMapping("/monsters/infection/{date}")
    Flux<AnswerMonsterDTO> findAllByInfectionDate(@PathVariable
                                                  @DateTimeFormat(pattern = "dd-MM-yyyy") Date date);

    @PatchMapping("/monsters/{monsterId}")
    Mono<AnswerMonsterDTO> updateJobById(@PathVariable UUID monsterId, @RequestParam Job job);

    @DeleteMapping("/monsters/{monsterId}")
    Mono<Void> deleteMonster(@PathVariable UUID monsterId);

    @PutMapping("/monsters/{monsterId}")
    Mono<AnswerMonsterDTO> putMonster(@PathVariable UUID monsterId,
                                      @RequestBody Mono<RequestMonsterDTO> monsterDTOMono);

    @GetMapping("/rewards/{rewardId}")
    Mono<RewardDTO> getReward(@PathVariable UUID rewardId);

    @PostMapping("/rewards")
    Mono<RewardDTO> addReward(@RequestBody Mono<RewardDTO> rewardDTOMono);

    @DeleteMapping("/rewards/{rewardId}")
    Mono<Void> deleteReward(@PathVariable UUID rewardId);

    @PutMapping("/rewards/{rewardId}")
    Mono<RewardDTO> putReward(@PathVariable UUID rewardId, @RequestBody Mono<RewardDTO> rewardDTOMono);
}
