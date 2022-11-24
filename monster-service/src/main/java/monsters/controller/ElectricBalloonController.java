package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerElectricBalloonDTO;
import monsters.dto.request.RequestElectricBalloonDTO;
import monsters.mapper.ElectricBalloonMapper;
import monsters.service.ElectricBalloonService;
import monsters.service.feign.clients.ChildServiceFeignClient;
import monsters.service.feign.clients.UserServiceFeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final int BUFFER_SIZE = 40;
    private final ElectricBalloonService electricBalloonService;
    private final UserServiceFeignClient userServiceFeignClient;
    private final ChildServiceFeignClient childServiceFeignClient;
    private final ElectricBalloonMapper electricBalloonMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AnswerElectricBalloonDTO> addElectricBalloon(@RequestBody @Valid Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonService.save(electricBalloonDTOMono)
                .flatMap(electricBalloonEntity -> userServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(electricBalloonMapper.mapEntityToDto(electricBalloonEntity, tuple.getT1(), tuple.getT2()))));
    }

    @GetMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerElectricBalloonDTO> getElectricBalloon(@PathVariable UUID electricBalloonId) {
        return electricBalloonService.findById(electricBalloonId)
                .flatMap(electricBalloonEntity -> userServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(electricBalloonMapper.mapEntityToDto(electricBalloonEntity, tuple.getT1(), tuple.getT2()))));
    }

    @GetMapping("/date/{date}")
    public Mono<ResponseEntity<Flux<AnswerElectricBalloonDTO>>> findAllFilledByDateAndCity(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
                                                                                           @RequestParam(required = false) UUID cityId,
                                                                                           @RequestParam(defaultValue = "0")
                                                                                           @Min(value = 0, message = "must not be less than zero") int page,
                                                                                           @RequestParam(defaultValue = "5")
                                                                                           @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var cityNotNullFlux = electricBalloonService.findAllFilledByDateAndCity(date, cityId, page, size);
        var cityIsNullFlux = electricBalloonService.findAllFilledByDate(date, page, size);
        var electricBalloonEntityFlux = Mono.just(cityId != null).flux().flatMap(notNull -> notNull ? cityNotNullFlux : cityIsNullFlux);

        var electricBalloonDTOFlux = electricBalloonEntityFlux
                .buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .flatMap(electricBalloonEntity -> userServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getMonsterEntity().getUserId())
                                .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getDoorId()))
                                        .subscribeOn(Schedulers.boundedElastic()))
                                .flatMap(tuple -> Mono.just(electricBalloonMapper.mapEntityToDto(electricBalloonEntity, tuple.getT1(), tuple.getT2()))))
                        .subscribeOn(Schedulers.parallel()));

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<AnswerElectricBalloonDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(electricBalloonDTOFlux, HttpStatus.OK));

        return electricBalloonEntityFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @DeleteMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteElectricBalloon(@PathVariable UUID electricBalloonId) {
        return electricBalloonService.delete(electricBalloonId);
    }

    @PutMapping("/{electricBalloonId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerElectricBalloonDTO> putElectricBalloon(@PathVariable UUID electricBalloonId, @RequestBody @Valid Mono<RequestElectricBalloonDTO> electricBalloonDTOMono) {
        return electricBalloonService.updateById(electricBalloonId, electricBalloonDTOMono)
                .flatMap(electricBalloonEntity -> userServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(electricBalloonEntity.getFearActionEntity().getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(electricBalloonMapper.mapEntityToDto(electricBalloonEntity, tuple.getT1(), tuple.getT2()))));
    }
}
