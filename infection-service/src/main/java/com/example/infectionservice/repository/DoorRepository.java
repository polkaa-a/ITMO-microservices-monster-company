package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.mapper.DoorMapper;
import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.model.DoorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DoorRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DoorRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

//    public DoorEntity findById(UUID id) {
//        return jdbcTemplate.query("select * from door where id = ?", new Object[]{id}, new DoorMapper())
//                .stream().findAny().orElse(null);
//    }

    public Optional<DoorEntity> findById(UUID id) {
        return jdbcTemplate.query("select * from door where id = ?", new Object[]{id}, new DoorMapper())
                .stream().findAny();
    }

    public DoorEntity save(DoorEntity doorEntity) {
        jdbcTemplate.update("insert into door values(?, ?)", doorEntity.getId(), doorEntity.isActive());
        return doorEntity;
    }

    public List<DoorEntity> findAllByActive() {
        return jdbcTemplate.query("select * from door where status = 'true'", new DoorMapper());
    }

    public void delete(DoorEntity doorEntity) {
        jdbcTemplate.update("delete from door where id = ?", doorEntity.getId());
    }
}
