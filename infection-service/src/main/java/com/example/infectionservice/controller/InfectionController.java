package com.example.infectionservice.controller;

import com.example.infectionservice.dto.InfectionDTO;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.repository.ChildRepository;
import com.example.infectionservice.service.InfectionService;
import lombok.RequiredArgsConstructor;
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
    private final ChildRepository childRepository; //TODO: убрать - добавила для тестирвоания
    private final InfectionService infectionService;
    //private final PageMapper<InfectionDTO> pageMapper;
    private final InfectionMapper infectionMapper;

    @GetMapping("/greeting")
    public String greeting() {
        return "hello infection";
    }

    @GetMapping("/test")
    public String test() {
        List<ChildEntity> childEntitiesList = childRepository.getChildren();
        String test = "";
        for (ChildEntity child: childEntitiesList) {
            test+= child.getName() + "\n";
        }
        return test;
    }

//    @PostMapping //todo: добавить
//    @ResponseStatus(HttpStatus.CREATED)
//    public InfectionDTO save(@Valid @RequestBody InfectionDTO infectionDTO) {
//        return infectionMapper.mapEntityToDto(infectionService.save(infectionDTO));
//    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionDTO findById(@PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.findById(id));
    }

    //todo
//    @GetMapping
//    public ResponseEntity<PageDTO<InfectionDTO>> findAll(@RequestParam(defaultValue = "0")
//                                                         @Min(value = 0, message = "must not be less than zero") int page,
//                                                         @RequestParam(defaultValue = "5")
//                                                         @Max(value = 50, message = "must not be more than 50 characters") int size,
//                                                         @RequestParam(required = false) UUID monsterId) {
//        Page<InfectionEntity> pageInfection = infectionService.findAll(page, size, monsterId);
//        if (pageInfection.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(pageMapper.mapToDto(pageInfection.map(infectionMapper::mapEntityToDto)), HttpStatus.OK);
//        }
//
//    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionDTO updateCureDate(@RequestBody Map<String, Date> cureDate, @PathVariable UUID id) {
        return infectionMapper.mapEntityToDto(infectionService.updateCureDate(id, cureDate));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectionService.delete(id);
    }
}
