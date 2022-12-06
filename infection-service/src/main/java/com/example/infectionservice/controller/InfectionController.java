package com.example.infectionservice.controller;

import com.example.infectionservice.dto.request.InfectionRequestDTO;
import com.example.infectionservice.dto.response.InfectionResponseDTO;
import com.example.infectionservice.dto.response.PageDTO;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.mapper.PageMapper;
import com.example.infectionservice.model.InfectionEntity;
import com.example.infectionservice.service.InfectionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/infections")
public class InfectionController {
    private final InfectionService infectionService;
    private final PageMapper<InfectionResponseDTO> pageMapper;
    private final InfectionMapper infectionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfectionResponseDTO save(@Valid @RequestBody InfectionRequestDTO infectionRequestDTO) {
        return infectionMapper.mapEntityToDto(infectionService.save(infectionRequestDTO));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionResponseDTO findById(@PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.findById(id));
    }


    @GetMapping
    public ResponseEntity<PageDTO<InfectionResponseDTO>> findAll(@RequestParam(defaultValue = "0")
                                                                 @Min(value = 0, message = "must not be less than zero") int page,
                                                                 @RequestParam(defaultValue = "5")
                                                                 @Max(value = 50, message = "must not be more than 50 characters") int size,
                                                                 @RequestParam(required = false) UUID monsterId,
                                                                 @RequestParam(required = false) Date date) {
        Page<InfectionEntity> pageInfection = infectionService.findAll(PageRequest.of(page, size), monsterId, date);
        if (pageInfection.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageInfection.map(infectionMapper::mapEntityToDto)), HttpStatus.OK);
        }

    }

    @PatchMapping("/{id}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ResponseStatus(HttpStatus.OK)
    public InfectionResponseDTO updateCureDate(@RequestBody Map<String, Date> cureDate, @PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.updateCureDate(id, cureDate));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectionService.delete(id);
    }
}
