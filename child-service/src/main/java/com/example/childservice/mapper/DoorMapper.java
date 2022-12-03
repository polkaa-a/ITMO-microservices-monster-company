package com.example.childservice.mapper;

import com.example.childservice.dto.DoorDTO;
import com.example.childservice.model.DoorEntity;
import org.springframework.stereotype.Component;

@Component
public class DoorMapper {

    public DoorDTO mapEntityToDto(DoorEntity doorEntity) {
        return DoorDTO.builder()
                .id(doorEntity.getId())
                .isActive(doorEntity.isActive())
                .build();
    }

    public DoorEntity mapDtoToEntity(DoorDTO doorDTO) {
        return DoorEntity.builder()
                .id(doorDTO.getId())
                .isActive(doorDTO.getIsActive())
                .build();
    }

}
