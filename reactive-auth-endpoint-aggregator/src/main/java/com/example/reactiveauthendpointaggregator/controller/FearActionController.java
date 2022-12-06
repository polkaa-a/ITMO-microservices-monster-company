package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.AnswerFearActionDTO;
import com.example.reactiveauthendpointaggregator.dto.RequestFearActionDTO;
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
@RequestMapping("/fear-actions")
public class FearActionController {
    private final MonsterServiceFeignClient fearActionClient;

    @GetMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<AnswerFearActionDTO> getFearAction(@PathVariable UUID fearActionId) {
        return fearActionClient.getFearAction(fearActionId);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<ResponseEntity<Flux<AnswerFearActionDTO>>> findAllByDate(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        var fearActionDTOFlux = fearActionClient.findAllByDate(date, page, size);
        var emptyResponseMono = Mono.just(
                new ResponseEntity<Flux<AnswerFearActionDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(
                new ResponseEntity<>(fearActionDTOFlux, HttpStatus.OK));

        return fearActionDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<AnswerFearActionDTO> addFearAction(@RequestBody Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionClient.addFearAction(fearActionDTOMono);
    }

    @DeleteMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> deleteFearAction(@PathVariable UUID fearActionId) {
        return fearActionClient.deleteFearAction(fearActionId);
    }

    @PutMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<AnswerFearActionDTO> putFearAction(@PathVariable UUID fearActionId,
                                                   @RequestBody Mono<RequestFearActionDTO> fearActionMono) {
        return fearActionClient.putFearAction(fearActionId, fearActionMono);
    }

}

