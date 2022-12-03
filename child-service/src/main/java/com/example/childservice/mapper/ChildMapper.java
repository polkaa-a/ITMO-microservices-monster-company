package com.example.childservice.mapper;

import com.example.childservice.dto.ChildRequestDTO;
import com.example.childservice.dto.ChildResponseDTO;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.model.DoorEntity;
import org.springframework.stereotype.Component;

@Component
public class ChildMapper {

    public ChildResponseDTO mapEntityToDto(ChildEntity childEntity, DoorMapper doorMapper) {
        return ChildResponseDTO.builder()
                .id(childEntity.getId())
                .name(childEntity.getName())
                .dob(childEntity.getDateOfBirth())
                .gender(childEntity.getGender())
                .doorDTO(doorMapper.mapEntityToDto(childEntity.getDoor()))
                .build();
    }

    public ChildEntity mapDtoToEntity(ChildRequestDTO childDTO, DoorEntity doorEntity) {
        return ChildEntity.builder()
                .name(childDTO.getName())
                .gender(childDTO.getGender())
                .dateOfBirth(childDTO.getDob())
                .door(doorEntity)
                .build();
    }
}
