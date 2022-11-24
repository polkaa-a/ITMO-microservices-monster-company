package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.InfectedThingDTO;
import com.example.infectionservice.model.InfectedThingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class InfectedThingMapper implements RowMapper<InfectedThingEntity> {

    @Override
    public InfectedThingEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        InfectedThingEntity infectedThingEntity = new InfectedThingEntity();
        infectedThingEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        infectedThingEntity.setName(resultSet.getString("name"));
        infectedThingEntity.setDoor(resultSet.getObject("door_id", java.util.UUID.class));
        return infectedThingEntity;
    }

    public InfectedThingDTO mapEntityToDto(InfectedThingEntity infectedThingEntity) {
        return InfectedThingDTO.builder()
                .id(infectedThingEntity.getId())
                .name(infectedThingEntity.getName())
                .doorId(infectedThingEntity.getDoor())
                .build();
    }

    public InfectedThingEntity mapDtoToEntity(InfectedThingDTO infectedThingDTO) {
        return InfectedThingEntity.builder()
                .id(infectedThingDTO.getId())
                .name(infectedThingDTO.getName())
                .door(infectedThingDTO.getDoorId())
                .build();
    }
}
