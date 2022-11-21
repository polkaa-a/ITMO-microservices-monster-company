package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerFearActionDTO;
import monsters.dto.request.RequestFearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.service.FearActionService;
import org.springframework.http.HttpStatus;
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
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<AnswerFearActionDTO> getFearAction(@PathVariable UUID fearActionId) {
        return fearActionMapper.mapEntityToDto(fearActionService.findById(fearActionId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<AnswerFearActionDTO> addFearAction(@RequestBody Mono<@Valid RequestFearActionDTO> fearActionDTOMono) {
        return fearActionMapper.mapEntityToDto(fearActionService.save(fearActionDTOMono));
    }

    @DeleteMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteFearAction(@PathVariable UUID fearActionId) {
        return fearActionService.delete(fearActionId);
    }

    @PutMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('ADMIN')")
    public Mono<AnswerFearActionDTO> putFearAction(@PathVariable UUID fearActionId, @RequestBody Mono<@Valid RequestFearActionDTO> fearActionMono) {
        return fearActionMapper.mapEntityToDto(fearActionService.updateById(fearActionId, fearActionMono));
    }

}

