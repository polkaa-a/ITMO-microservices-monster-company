package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.ElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.service.ElectricBalloonService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/electric-balloons")
public class ElectricBalloonController {

    private final ElectricBalloonService electricBalloonService;
    private final ElectricBalloonMapper electricBalloonMapper;

    private static final int BUFFER_SIZE = 40;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<ElectricBalloonDTO> addElectricBalloon(@RequestBody Mono<ElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonMapper.mapEntityToDto(electricBalloonService.save(electricBalloonDTOMono));
    }

    @GetMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<ElectricBalloonDTO> getElectricBalloon(@PathVariable(name = "electricBalloonId") Mono<UUID> electricBalloonIdMono) {
        return electricBalloonMapper.mapEntityToDto(electricBalloonService.findById(electricBalloonIdMono));
    }

    @GetMapping("/{date}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT') or hasAuthority('RECRUITER')")
    public Mono<ResponseEntity<Flux<ElectricBalloonDTO>>> findAllFilledByDateAndCity(@PathVariable(name = "date") @DateTimeFormat(fallbackPatterns = "dd-MM-yyyy") Mono<Date> dateMono,
                                                                                     @RequestParam(required = false, name = "cityId") Mono<UUID> cityIdMono,
                                                                                     @RequestParam(defaultValue = "0", name = "page")
                                                                                             Mono<@Min(value = 0, message = "must not be less than zero") Integer> pageMono,
                                                                                     @RequestParam(defaultValue = "5", name = "size")
                                                                                             Mono<@Max(value = 50, message = "must not be more than 50 characters") Integer> sizeMono) {

        var cityNotNullFlux = electricBalloonService.findAllFilledByDateAndCity(dateMono, cityIdMono, pageMono, sizeMono);
        var cityIsNullFlux = electricBalloonService.findAllFilledByDate(dateMono, pageMono, sizeMono);
        var electricBalloonEntityFlux = cityIdMono.hasElement().flux().flatMap(hasElements -> hasElements ? cityNotNullFlux : cityIsNullFlux);

        var electricBalloonDTOFlux = electricBalloonEntityFlux.buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .map(electricBalloonEntity -> electricBalloonMapper.mapEntityToDto(Mono.just(electricBalloonEntity)))
                        .subscribeOn(Schedulers.parallel()))
                .flatMap(Mono::flux);

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<ElectricBalloonDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(electricBalloonDTOFlux, HttpStatus.OK));

        return electricBalloonEntityFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public void deleteElectricBalloon(@PathVariable(name = "electricBalloonId") Mono<UUID> electricBalloonIdMono) {
        electricBalloonService.delete(electricBalloonIdMono);
    }

    @PutMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARER') or hasAuthority('SCARE ASSISTANT')")
    public Mono<ElectricBalloonDTO> putElectricBalloon(@PathVariable(name = "electricBalloonId") Mono<UUID> electricBalloonIdMono, @RequestBody Mono<@Valid ElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonMapper.mapEntityToDto(electricBalloonService.updateById(electricBalloonIdMono, electricBalloonDTOMono));
    }
}
