package com.example.childservice.controller;

import com.example.childservice.dto.ChildRequestDTO;
import com.example.childservice.dto.ChildResponseDTO;
import com.example.childservice.dto.PageDTO;
import com.example.childservice.mapper.ChildMapper;
import com.example.childservice.mapper.DoorMapper;
import com.example.childservice.mapper.PageMapper;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/child")
@Validated
public class ChildController {

    private final ChildService childService;
    private final DoorMapper doorMapper;
    private final ChildMapper childMapper;
    private final PageMapper<ChildResponseDTO> pageMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResponseDTO addChild(@Valid @RequestBody ChildRequestDTO childDTO) {
        return childMapper.mapEntityToDto(childService.save(childDTO), doorMapper);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChildResponseDTO findById(@PathVariable UUID id) {
        return childMapper.mapEntityToDto(childService.findById(id), doorMapper);
    }

    @GetMapping
    public ResponseEntity<PageDTO<ChildResponseDTO>> getChildren(@RequestParam(defaultValue = "0")
                                                                 @Min(value = 0, message = "must not be less than zero")
                                                                 int page,
                                                                 @RequestParam(defaultValue = "5")
                                                                 @Max(value = 50, message = "must not be more than 50 characters")
                                                                 int size) {
        Page<ChildEntity> pageChild = childService.getAll(page, size);
        if (pageChild.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageChild.map(childEntity ->
                    childMapper.mapEntityToDto(childEntity, doorMapper))), HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        childService.delete(id);
    }
}
