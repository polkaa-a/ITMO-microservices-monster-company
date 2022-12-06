package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.RewardDTO;
import com.example.reactiveauthendpointaggregator.feignclient.MonsterServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rewards")
public class RewardController {

    private final MonsterServiceFeignClient rewardClient;

    @GetMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RewardDTO> getReward(@PathVariable UUID rewardId) {
        return rewardClient.getReward(rewardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RewardDTO> addReward(@RequestBody Mono<RewardDTO> rewardDTOMono) {
        return rewardClient.addReward(rewardDTOMono);
    }

    @DeleteMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteReward(@PathVariable UUID rewardId) {
        return rewardClient.deleteReward(rewardId);
    }

    @PutMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RewardDTO> putReward(@PathVariable UUID rewardId, @RequestBody Mono<RewardDTO> rewardDTOMono) {
        return rewardClient.putReward(rewardId, rewardDTOMono);
    }

}
