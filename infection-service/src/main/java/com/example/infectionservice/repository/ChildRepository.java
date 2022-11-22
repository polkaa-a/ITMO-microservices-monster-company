package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.model.ChildEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ChildRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChildRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ChildEntity> getChildren() { //TODO: убрать
        return jdbcTemplate.query("select * from child join door on child.door_id = door.id", new ChildMapper());
    }

    public Optional<ChildEntity> findById(UUID id) {
        return jdbcTemplate.query("select * from child where id = ?", new Object[]{id}, new ChildMapper())
                .stream().findAny();
    }

    public void delete(ChildEntity childEntity) {
        jdbcTemplate.update("delete from child where id = ?", childEntity.getId());
    }

    public ChildEntity save(ChildEntity childEntity) {
        jdbcTemplate.update("insert into child values(?, ?, ?, ?, ?)", childEntity.getId(),
                                                                            childEntity.getName(),
                                                                            childEntity.getGender(),
                                                                            childEntity.getDateOfBirth(),
                                                                            childEntity.getDoor().getId());
        return childEntity;
    }
}
