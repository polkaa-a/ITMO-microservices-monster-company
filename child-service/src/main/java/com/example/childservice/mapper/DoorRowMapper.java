package com.example.childservice.mapper;

import com.example.childservice.model.DoorEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DoorRowMapper implements RowMapper<DoorEntity> {
    @Override
    public DoorEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        return doorEntity;
    }
}
