package com.example.infectionservice.controller;

import com.example.infectionservice.dto.request.InfectedThingRequestDTO;
import com.example.infectionservice.dto.response.InfectedThingResponseDTO;
import com.example.infectionservice.dto.response.PageDTO;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.mapper.PageMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.service.InfectedThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/infected-things")
public class InfectedThingController {

    private final InfectedThingService infectedThingService;
    private final PageMapper<InfectedThingResponseDTO> pageMapper;
    private final InfectedThingMapper infectedThingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfectedThingResponseDTO save(@Valid @RequestBody InfectedThingRequestDTO infectionDTO) {
        return infectedThingService.save(infectionDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectedThingResponseDTO findById(@PathVariable UUID id) {
        InfectedThingEntity infectedThingEntity = infectedThingService.findById(id);
        return infectedThingMapper.mapEntityToDto(infectedThingEntity);
    }


    @GetMapping
    public ResponseEntity<PageDTO<InfectedThingResponseDTO>> findAll(@RequestParam(defaultValue = "0")
                                                                     @Min(value = 0, message = "must not be less than zero")
                                                                     int page,
                                                                     @RequestParam(defaultValue = "5")
                                                                     @Max(value = 50, message = "must not be more than 50 characters")
                                                                     int size,
                                                                     @RequestParam(required = false) UUID doorId) {
        Page<InfectedThingEntity> pageThings = infectedThingService.findAll(PageRequest.of(page, size), doorId);
        if (pageThings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageThings.map(
                    infectedThingMapper::mapEntityToDto
            )), HttpStatus.OK);
        }

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectedThingService.delete(id);
    }

}
