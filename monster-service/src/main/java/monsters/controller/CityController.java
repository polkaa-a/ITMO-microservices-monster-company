package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.CityDTO;
import monsters.mapper.CityMapper;
import monsters.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cities")
public class CityController {

    private static final int BUFFER_SIZE = 5;
    private final CityService cityService;
    private final CityMapper cityMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CityDTO> addCity(@RequestBody @Valid Mono<CityDTO> cityDTOMono) {
        return cityService.save(cityDTOMono).flatMap(cityEntity -> Mono.just(cityMapper.mapEntityToDto(cityEntity)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<CityDTO>>> findAll(@RequestParam(defaultValue = "0")
                                                       @Min(value = 0, message = "must not be less than zero") int page,
                                                       @RequestParam(defaultValue = "5")
                                                       @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var cityDTOFlux = cityService.findAll(page, size)
                .buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(cityEntity -> Mono.just(cityMapper.mapEntityToDto(cityEntity)))
                        .subscribeOn(Schedulers.parallel()))
                .flatMap(Mono::flux);

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<CityDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(cityDTOFlux, HttpStatus.OK));

        return cityDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{cityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCity(@PathVariable UUID cityId) {
        return cityService.delete(cityId);
    }

    @PutMapping("/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CityDTO> putCity(@PathVariable UUID cityId, @RequestBody @Valid Mono<CityDTO> cityDTOMono) {
        return cityService.updateById(cityId, cityDTOMono).flatMap(cityEntity -> Mono.just(cityMapper.mapEntityToDto(cityEntity)));
    }
}

