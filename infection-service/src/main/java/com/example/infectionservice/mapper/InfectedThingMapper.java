package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.InfectedThingDTO;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.model.InfectedThingEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfectedThingMapper implements RowMapper<InfectedThingEntity> {

    @Override
    public InfectedThingEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        InfectedThingEntity infectedThingEntity = new InfectedThingEntity();
        infectedThingEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        infectedThingEntity.setName(resultSet.getString("name"));
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("door_id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        infectedThingEntity.setDoor(doorEntity);
        return infectedThingEntity;
    }

    public InfectedThingDTO mapEntityToDto(InfectedThingEntity infectedThingEntity) {
        return InfectedThingDTO.builder()
                .id(infectedThingEntity.getId())
                .name(infectedThingEntity.getName())
                .doorId(infectedThingEntity.getDoor().getId())
                .build();
    }

    public InfectedThingEntity mapDtoToEntity(InfectedThingDTO infectedThingDTO, DoorEntity doorEntity) {
        return InfectedThingEntity.builder()
                .id(infectedThingDTO.getId())
                .name(infectedThingDTO.getName())
                .door(doorEntity)
                .build();
    }
}
