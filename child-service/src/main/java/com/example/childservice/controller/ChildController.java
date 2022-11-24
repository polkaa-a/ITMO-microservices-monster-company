package com.example.childservice.controller;

import com.example.childservice.dto.ChildDTO;
import com.example.childservice.dto.PageDTO;
import com.example.childservice.mapper.ChildMapper;
import com.example.childservice.mapper.PageMapper;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RequiredArgsConstructor
@RestController
@RequestMapping("/child")
public class ChildController {

    private final ChildService childService;
    private final ChildMapper childMapper;
    private final PageMapper<ChildDTO> pageMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildDTO addChild(@Valid @RequestBody ChildDTO childDTO) {
        return childMapper.mapEntityToDto(childService.save(childDTO));
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PageDTO<ChildDTO>> getChildren(@RequestParam(defaultValue = "0")
                                                         @Min(value = 0, message = "must not be less than zero") int page,
                                                         @RequestParam(defaultValue = "5")
                                                         @Max(value = 50, message = "must not be more than 50 characters") int size) {
        Page<ChildEntity> pageChild = childService.getAll(page, size);
        if (pageChild.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(pageMapper.mapToDto(pageChild.map(childMapper::mapEntityToDto)), HttpStatus.OK);
        }
    }
}
