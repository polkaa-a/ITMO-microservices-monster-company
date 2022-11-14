package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.FearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.service.FearActionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fear-actions")
public class FearActionController {

    private final FearActionService fearActionService;
    private final FearActionMapper fearActionMapper;

    @GetMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<FearActionDTO> getFearAction(@PathVariable(name = "fearActionId") Mono<UUID> fearActionIdMono) {
        return fearActionMapper.mapEntityToDto(fearActionService.findById(fearActionIdMono));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<FearActionDTO> addFearAction(@RequestBody Mono<@Valid FearActionDTO> fearActionDTOMono) {
        return fearActionMapper.mapEntityToDto(fearActionService.save(fearActionDTOMono));
    }

    @DeleteMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteFearAction(@PathVariable(name = "fearActionId") Mono<UUID> fearActionIdMono) {
        fearActionService.delete(fearActionIdMono);
    }

    @PutMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<FearActionDTO> putFearAction(@PathVariable(name = "fearActionId") Mono<UUID> fearActionIdMono, @RequestBody Mono<@Valid FearActionDTO> fearActionMono) {
        return fearActionMapper.mapEntityToDto(fearActionService.updateById(fearActionIdMono, fearActionMono));
    }

}

