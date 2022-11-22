package com.example.infectionservice.mapper;

import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.model.DoorEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoorMapper implements RowMapper<DoorEntity> {

    @Override
    public DoorEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        return doorEntity;
    }
}
