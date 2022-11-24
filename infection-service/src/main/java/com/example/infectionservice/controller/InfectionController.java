package com.example.infectionservice.controller;

import com.example.infectionservice.dto.InfectionDTO;
import com.example.infectionservice.dto.PageDTO;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.mapper.PageMapper;
import com.example.infectionservice.model.InfectionEntity;
import com.example.infectionservice.service.InfectionService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/infections")
public class InfectionController {
    private final InfectionService infectionService;
    private final PageMapper<InfectionDTO> pageMapper;
    private final InfectionMapper infectionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfectionDTO save(@Valid @RequestBody InfectionDTO infectionDTO) {
        return infectionMapper.mapEntityToDto(infectionService.save(infectionDTO));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionDTO findById(@PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.findById(id));
    }


    @GetMapping
    public ResponseEntity<PageDTO<InfectionDTO>> findAll(@RequestParam(defaultValue = "0")
                                                         @Min(value = 0, message = "must not be less than zero") int page,
                                                         @RequestParam(defaultValue = "5")
                                                         @Max(value = 50, message = "must not be more than 50 characters") int size,
                                                         @RequestParam(required = false) UUID monsterId) {
        Page<InfectionEntity> pageInfection = infectionService.findAll(page, size, monsterId);
        if (pageInfection.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageInfection.map(infectionMapper::mapEntityToDto)), HttpStatus.OK);
        }

    }

    @PatchMapping("/{id}")
    @JsonFormat(pattern="yyyy-MM-dd")
    @ResponseStatus(HttpStatus.OK)
    public InfectionDTO updateCureDate(@RequestBody Map<String, Date> cureDate, @PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.updateCureDate(id, cureDate));
    }

    @GetMapping("/infections/{date}")
    @ResponseStatus(HttpStatus.OK)
    List<InfectionDTO> findAllByDate(@PathVariable Date date) {
        return infectionService.findAllByDate(date).stream().map(infectionMapper::mapEntityToDto).toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectionService.delete(id);
    }
}
