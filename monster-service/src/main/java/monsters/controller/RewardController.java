package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.RewardDTO;
import monsters.mapper.RewardMapper;
import monsters.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final RewardMapper rewardMapper;

    @GetMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RewardDTO> getReward(@PathVariable UUID rewardId) {
        return rewardService.findById(rewardId)
                .flatMap(rewardEntity -> Mono.just(rewardMapper.mapEntityToDto(rewardEntity)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RewardDTO> addReward(@RequestBody @Valid Mono<RewardDTO> rewardDTOMono) {
        return rewardService.save(rewardDTOMono)
                .flatMap(rewardEntity -> Mono.just(rewardMapper.mapEntityToDto(rewardEntity)));
    }

    @DeleteMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteReward(@PathVariable UUID rewardId) {
        return rewardService.delete(rewardId);
    }

    @PutMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RewardDTO> putReward(@PathVariable UUID rewardId, @RequestBody @Valid Mono<RewardDTO> rewardDTOMono) {
        return rewardService.updateById(rewardId, rewardDTOMono)
                .flatMap(rewardEntity -> Mono.just(rewardMapper.mapEntityToDto(rewardEntity)));
    }

}
