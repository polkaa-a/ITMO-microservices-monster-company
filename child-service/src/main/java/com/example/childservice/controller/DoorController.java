package com.example.childservice.controller;

import com.example.childservice.dto.DoorDTO;
import com.example.childservice.dto.PageDTO;
import com.example.childservice.mapper.DoorMapper;
import com.example.childservice.mapper.PageMapper;
import com.example.childservice.model.DoorEntity;
import com.example.childservice.service.DoorService;
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
@RestController
@RequestMapping("/doors")
@Validated
public class DoorController {
    private final DoorService doorService;
    private final DoorMapper doorMapper;
    private final PageMapper<DoorDTO> pageMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO save(@Valid @RequestBody DoorDTO doorDTO) {
        return doorMapper.mapEntityToDto(doorService.save(doorMapper.mapDtoToEntity(doorDTO)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO findById(@PathVariable UUID id) {
        return doorMapper.mapEntityToDto(doorService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageDTO<DoorDTO>> findAll(@RequestParam(defaultValue = "0")
                                                    @Min(value = 0, message = "must not be less than zero")
                                                    int page,
                                                    @RequestParam(defaultValue = "5")
                                                    @Max(value = 50, message = "must not be more than 50 characters")
                                                    int size,
                                                    @RequestParam(required = false) Boolean status) {
        Page<DoorEntity> pageChild = doorService.findAll(PageRequest.of(page, size), status);
        if (pageChild.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageChild.map(doorMapper::mapEntityToDto)), HttpStatus.OK);
        }
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO changeDoor(@PathVariable UUID id) {
        return doorMapper.mapEntityToDto(doorService.changeActive(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDoor(@PathVariable UUID id) {
        doorService.deleteDoor(id);
    }
}
