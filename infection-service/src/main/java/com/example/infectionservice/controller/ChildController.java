package com.example.infectionservice.controller;

import com.example.infectionservice.dto.ChildDTO;
import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/child")
public class ChildController {

    private final ChildService childService;
    private final ChildMapper childMapper;
    //TODO: Pageable
//    private final PageMapper<ChildDTO> pageMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildDTO addChild(@Valid @RequestBody ChildDTO childDTO) {
        return childMapper.mapEntityToDto(childService.save(childDTO));
    }

    //TODO: Pageable
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<PageDTO<ChildDTO>> getChildren(@RequestParam(defaultValue = "0")
//                                                         @Min(value = 0, message = "must not be less than zero") int page,
//                                                         @RequestParam(defaultValue = "5")
//                                                         @Max(value = 50, message = "must not be more than 50 characters") int size) {
//        Page<ChildEntity> pageChild = childService.getAll(page, size);
//        if (pageChild.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(pageMapper.mapToDto(pageChild.map(childMapper::mapEntityToDto)), HttpStatus.OK);
//        }
//    }

    //TODO: Pageable
//    @GetMapping("/scared")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<PageDTO<ChildDTO>> getScaredChildrenByDate(@RequestParam(defaultValue = "0")
//                                                                     @Min(value = 0, message = "must not be less than zero") int page,
//                                                                     @RequestParam(defaultValue = "5")
//                                                                     @Max(value = 50, message = "must not be more than 50 characters") int size,
//                                                                     @RequestParam(required = false) Date date) {
//        Page<ChildEntity> pageChild = childService.getScaredChildrenByDate(page, size, date);
//
//        if (pageChild.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(pageMapper.mapToDto(pageChild.map(childMapper::mapEntityToDto)), HttpStatus.OK);
//        }
//    }
}
