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
    public Mono<AnswerFearActionDTO> getFearAction(@PathVariable UUID fearActionId) {
        return fearActionService.findById(fearActionId)
                .flatMap(fearActionEntity -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AnswerFearActionDTO> addFearAction(@RequestBody @Valid Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionService.save(fearActionDTOMono)
                .flatMap(fearActionEntity -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity)));
    }

    @DeleteMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFearAction(@PathVariable UUID fearActionId) {
        return fearActionService.delete(fearActionId);
    }

    @PutMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerFearActionDTO> putFearAction(@PathVariable UUID fearActionId, @RequestBody @Valid Mono<RequestFearActionDTO> fearActionMono) {
        return fearActionService.updateById(fearActionId, fearActionMono)
                .flatMap(fearActionEntity -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity)));
    }

}

