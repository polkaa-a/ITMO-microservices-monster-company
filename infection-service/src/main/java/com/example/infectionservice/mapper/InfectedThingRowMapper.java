package com.example.infectionservice.mapper;

import com.example.infectionservice.model.InfectedThingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class InfectedThingRowMapper implements RowMapper<InfectedThingEntity> {
    @Override
    public InfectedThingEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        InfectedThingEntity infectedThingEntity = new InfectedThingEntity();
        infectedThingEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        infectedThingEntity.setName(resultSet.getString("name"));
        infectedThingEntity.setDoor(resultSet.getObject("door_id", java.util.UUID.class));
        return infectedThingEntity;
    }
}
