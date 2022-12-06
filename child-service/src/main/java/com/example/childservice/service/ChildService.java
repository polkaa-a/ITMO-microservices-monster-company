package com.example.childservice.service;

import com.example.childservice.controller.exception.NotFoundException;
import com.example.childservice.dto.ChildRequestDTO;
import com.example.childservice.mapper.ChildMapper;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.model.DoorEntity;
import com.example.childservice.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChildService {

    private static final String EXC_MES_ID = "none child was found by id";
    private final DoorService doorService;
    private final ChildRepository childRepository;
    private final ChildMapper childMapper;

    @Transactional
    public ChildEntity save(ChildRequestDTO childDTO) {
        DoorEntity doorEntity;
        if (childDTO.getDoorId() != null) {
            doorEntity = doorService.findById(childDTO.getDoorId());
        } else doorEntity = doorService.save(new DoorEntity());
        ChildEntity childEntity = childMapper.mapDtoToEntity(childDTO, doorEntity);
        childEntity.setId(UUID.randomUUID());
        return childRepository.save(childEntity);
    }


    public Page<ChildEntity> getAll(int page, int size) {
        return childRepository.findAll(PageRequest.of(page, size));
    }

    public void delete(UUID id) {
        childRepository.delete(
                childRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + ": " + id)
                )
        );
    }

    public ChildEntity findById(UUID id) {
        return childRepository.findById(id).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + ": " + id)
        );
    }
}
