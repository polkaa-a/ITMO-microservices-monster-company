package com.example.childservice.mapper;

import com.example.childservice.dto.DoorDTO;
import com.example.childservice.model.DoorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class DoorMapper implements RowMapper<DoorEntity> {

    @Override
    public DoorEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        return doorEntity;
    }

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
