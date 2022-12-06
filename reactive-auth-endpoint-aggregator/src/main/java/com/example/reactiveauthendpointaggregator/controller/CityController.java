package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.CityDTO;
import com.example.reactiveauthendpointaggregator.feignclient.MonsterServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/cities")
public class CityController {

    private final MonsterServiceFeignClient cityClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CityDTO> addCity(@RequestBody Mono<CityDTO> cityDTOMono) {
        return cityClient.addCity(cityDTOMono);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<CityDTO>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {

        var cityDTOFlux = cityClient.findAllCities(page, size);

        var emptyResponseMono = Mono.just(
                new ResponseEntity<Flux<CityDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(
                new ResponseEntity<>(cityDTOFlux, HttpStatus.OK));

        return cityClient.findAllCities(page, size)
                .hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{cityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCity(@PathVariable UUID cityId) {
        return cityClient.deleteCity(cityId);
    }

    @PutMapping("/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CityDTO> putCity(@PathVariable UUID cityId, @RequestBody Mono<CityDTO> cityDTOMono) {
        return cityClient.putCity(cityId, cityDTOMono);
    }
}

