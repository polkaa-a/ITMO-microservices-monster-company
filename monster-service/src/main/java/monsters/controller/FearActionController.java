package monsters.controller;

import lombok.RequiredArgsConstructor;
import monsters.dto.answer.AnswerFearActionDTO;
import monsters.dto.request.RequestFearActionDTO;
import monsters.mapper.FearActionMapper;
import monsters.service.FearActionService;
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
@RequestMapping("/fear-actions")
public class FearActionController {

    private static final int BUFFER_SIZE = 40;
    private final FearActionService fearActionService;
    private final UserServiceFeignClient userServiceFeignClient;
    private final ChildServiceFeignClient childServiceFeignClient;
    private final FearActionMapper fearActionMapper;

    @GetMapping("/{fearActionId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerFearActionDTO> getFearAction(@PathVariable UUID fearActionId) {
        return fearActionService.findById(fearActionId)
                .flatMap(fearActionEntity -> userServiceFeignClient.findById(fearActionEntity.getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(fearActionEntity.getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity, tuple.getT1(), tuple.getT2()))));
    }

    @GetMapping("/date/{date}")
    public Mono<ResponseEntity<Flux<AnswerFearActionDTO>>> findAllByDate(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") Date date,
                                                                         @RequestParam(defaultValue = "0")
                                                                         @Min(value = 0, message = "must not be less than zero") int page,
                                                                         @RequestParam(defaultValue = "5")
                                                                         @Max(value = 50, message = "must not be more than 50 characters") int size) {

        var fearActionDTOFlux = fearActionService.findAllByDate(date, page, size)
                .buffer(BUFFER_SIZE)
                .flatMap(it -> Flux.fromIterable(it)
                        .flatMap(fearActionEntity -> userServiceFeignClient.findById(fearActionEntity.getMonsterEntity().getUserId())
                                .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(fearActionEntity.getDoorId()))
                                        .subscribeOn(Schedulers.boundedElastic()))
                                .flatMap(tuple -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity, tuple.getT1(), tuple.getT2()))))
                        .subscribeOn(Schedulers.parallel()));

        var emptyResponseMono = Mono.just(new ResponseEntity<Flux<AnswerFearActionDTO>>(HttpStatus.NO_CONTENT));
        var responseMono = Mono.just(new ResponseEntity<>(fearActionDTOFlux, HttpStatus.OK));

        return fearActionDTOFlux.hasElements().flatMap(hasElements -> hasElements ? responseMono : emptyResponseMono);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AnswerFearActionDTO> addFearAction(@RequestBody @Valid Mono<RequestFearActionDTO> fearActionDTOMono) {
        return fearActionService.save(fearActionDTOMono)
                .flatMap(fearActionEntity -> userServiceFeignClient.findById(fearActionEntity.getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(fearActionEntity.getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity, tuple.getT1(), tuple.getT2()))));
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
                .flatMap(fearActionEntity -> userServiceFeignClient.findById(fearActionEntity.getMonsterEntity().getUserId())
                        .zipWith(Mono.fromCallable(() -> childServiceFeignClient.findById(fearActionEntity.getDoorId()))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(tuple -> Mono.just(fearActionMapper.mapEntityToDto(fearActionEntity, tuple.getT1(), tuple.getT2()))));
    }

}

