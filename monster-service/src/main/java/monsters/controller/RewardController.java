package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.RewardDTO;
import monsters.mapper.RewardMapper;
import monsters.service.RewardService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<RewardDTO> getReward(@PathVariable(name = "rewardId") Mono<UUID> rewardIdMono) {
        return rewardMapper.mapEntityToDto(rewardService.findById(rewardIdMono));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<RewardDTO> addReward(@RequestBody Mono<@Valid RewardDTO> rewardDTOMono) {
        return rewardMapper.mapEntityToDto(rewardService.save(rewardDTOMono));
    }

    @DeleteMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteReward(@PathVariable(name = "rewardId") Mono<UUID> rewardIdMono) {
        rewardService.delete(rewardIdMono);
    }

    @PutMapping("/{rewardId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<RewardDTO> putReward(@PathVariable(name = "rewardId") Mono<UUID> rewardIdMono, @RequestBody Mono<@Valid RewardDTO> rewardDTOMono) {
        return rewardMapper.mapEntityToDto(rewardService.updateById(rewardIdMono, rewardDTOMono));
    }

}
