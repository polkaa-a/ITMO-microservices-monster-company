package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.AnswerElectricBalloonDTO;
import com.example.reactiveauthendpointaggregator.dto.RequestElectricBalloonDTO;
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
@RequestMapping("/electric-balloons")
public class ElectricBalloonController {
    private final MonsterServiceFeignClient balloonClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<AnswerElectricBalloonDTO> addElectricBalloon(@RequestBody Mono<RequestElectricBalloonDTO>
                                                                     electricBalloonDTOMono) {
        return balloonClient.addElectricBalloon(electricBalloonDTOMono);
    }

    @GetMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<AnswerElectricBalloonDTO> getElectricBalloon(@PathVariable UUID electricBalloonId) {
        return balloonClient.getElectricBalloon(electricBalloonId);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<ResponseEntity<Flux<AnswerElectricBalloonDTO>>> findAllFilledByDateAndCity(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
            @RequestParam(required = false) UUID cityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        var electricBalloonDTOFlux = balloonClient
                .findAllFilledByDateAndCity(date, cityId, page, size);

        var emptyResponseMono = Mono.just(
                new ResponseEntity<Flux<AnswerElectricBalloonDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(
                new ResponseEntity<>(electricBalloonDTOFlux, HttpStatus.OK));

        return electricBalloonDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<Void> deleteElectricBalloon(@PathVariable UUID electricBalloonId) {
        return balloonClient.deleteElectricBalloon(electricBalloonId);
    }

    @PutMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<AnswerElectricBalloonDTO> putElectricBalloon(@PathVariable UUID electricBalloonId,
                                                             @RequestBody Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return balloonClient.putElectricBalloon(electricBalloonId, electricBalloonDTOMono);
    }
}
