package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.InfectedThingDTO;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.repository.InfectedThingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InfectedThingService {

    private final InfectedThingRepository infectedThingRepository;
    private final DoorService doorService;
    private final InfectedThingMapper mapper;

    private static final String EXC_MES_ID = "none infected thing was found by id ";

//    public InfectedThingEntity save(InfectedThingDTO infectedThingDTO) {
//        DoorEntity doorEntity;
//        try {
//            doorEntity = doorService.findById(infectedThingDTO.getDoorId());
//        } catch (NotFoundException exception) {
//            doorEntity = doorService.save(new DoorEntity());
//        }
//        return infectedThingRepository.save(mapper.mapDtoToEntity(infectedThingDTO, doorEntity));
//    }

    public InfectedThingEntity save(InfectedThingDTO infectedThingDTO) {
        DoorEntity doorEntity;
        try {
            doorEntity = doorService.findById(infectedThingDTO.getDoorId());
        } catch (NotFoundException exception) {
            throw exception;
        }
        return infectedThingRepository.save(mapper.mapDtoToEntity(infectedThingDTO, doorEntity));
    }

    public InfectedThingEntity findById(UUID infectedThingId) {
        return infectedThingRepository.findById(infectedThingId).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + infectedThingId)
        );
    }

    //TODO" Pageable
//    public Page<InfectedThingEntity> findAll(int page, int size, UUID doorId) {
//        Pageable pageable = PageRequest.of(page, size);
//        if (doorId != null) {
//            DoorEntity doorEntity = doorService.findById(doorId);
//            return infectedThingRepository.findAllByDoor(doorEntity, pageable);
//        } else return infectedThingRepository.findAll(pageable);
//    }

    public void delete(UUID infectedThingId) {
        infectedThingRepository.delete(
                infectedThingRepository.findById(infectedThingId).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + ": " + infectedThingId)
                )
        );
    }
}
