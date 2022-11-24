package com.example.childservice.mapper;

import com.example.childservice.dto.ChildDTO;
import com.example.childservice.enums.Gender;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.model.DoorEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class ChildMapper implements RowMapper<ChildEntity> {

    @Override
    public ChildEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ChildEntity childEntity = new ChildEntity();
        childEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        childEntity.setName(resultSet.getString("name"));
        childEntity.setDateOfBirth(resultSet.getDate("date_of_birth"));
        childEntity.setGender(Gender.valueOf(resultSet.getString("gender")));
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("door_id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        childEntity.setDoor(doorEntity);
        return childEntity;
    }

    public ChildDTO mapEntityToDto(ChildEntity childEntity) {
        return ChildDTO.builder()
                .id(childEntity.getId())
                .name(childEntity.getName())
                .dob(childEntity.getDateOfBirth())
                .gender(childEntity.getGender())
                .doorId(childEntity.getDoor().getId())
                .build();
    }

    public ChildEntity mapDtoToEntity(ChildDTO childDTO, DoorEntity doorEntity) {
        return ChildEntity.builder()
                .id(childDTO.getId())
                .name(childDTO.getName())
                .gender(childDTO.getGender())
                .dateOfBirth(childDTO.getDob())
                .door(doorEntity)
                .build();
    }
}
