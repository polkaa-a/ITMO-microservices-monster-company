package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.CityDTO;
import monsters.mapper.CityMapper;
import monsters.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cities")
@PreAuthorize("hasAuthority('ADMIN')")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;

    private static final int BUFFER_SIZE = 5;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CityDTO> addCity(@RequestBody Mono<@Valid CityDTO> cityDTOMono) {
        return cityMapper.mapEntityToDto(cityService.save(cityDTOMono));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<CityDTO>>> findAll(@RequestParam(defaultValue = "0", name = "pageMono")
                                                               Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                       @RequestParam(defaultValue = "5", name = "sizeMono")
                                                               Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var cityEntityFlux = cityService.findAll(pageMono, sizeMono);
        var cityDTOFlux = cityEntityFlux.buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(cityEntity -> cityMapper.mapEntityToDto(Mono.just(cityEntity))))
                .flatMap(Mono::flux);

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<CityDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(cityDTOFlux, HttpStatus.OK));

        return cityEntityFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{cityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable(name = "cityId") Mono<UUID> cityIdMono) {
        cityService.delete(cityIdMono);
    }

    @PutMapping("/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CityDTO> putCity(@PathVariable(name = "cityId") Mono<UUID> cityIdMono, @RequestBody Mono<@Valid CityDTO> cityDTOMono) {
        return cityMapper.mapEntityToDto(cityService.updateById(cityIdMono, cityDTOMono));
    }
}

