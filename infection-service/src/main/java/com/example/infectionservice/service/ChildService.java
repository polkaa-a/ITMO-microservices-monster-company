package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.ChildDTO;
import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChildService {

    private static final String EXC_MES_ID = "none child was found by id";
    private final DoorService doorService;
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    @Transactional
    public ChildEntity save(ChildDTO childDTO) {
        DoorEntity doorEntity;
        try {
            doorEntity = doorService.findById(childDTO.getDoorId());
        } catch (NotFoundException exception) {
            doorEntity = doorService.save(new DoorEntity());
        }
        ChildEntity childEntity = childMapper.mapDtoToEntity(childDTO, doorEntity);
        return childRepository.save(childEntity);
    }

    //TODO: PAgeable1
//    public Page<ChildEntity> getAll(int page, int size) {
//        return childRepository.findAll(PageRequest.of(page, size));
//    }

    //TODO: PAgeable2
//    public Page<ChildEntity> getScaredChildrenByDate(int page, int size, Date date) {
//        Pageable pageable = PageRequest.of(page, size);
//        return childRepository.findAllScaredChildrenByDate(date, pageable);
//    }

    public void delete(UUID id) {
        childRepository.delete(
                childRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + ": " + id)
                )
        );
    }
}
