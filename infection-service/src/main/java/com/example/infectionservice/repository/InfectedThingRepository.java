package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Slf4j
public class InfectedThingRepository {
    private final JdbcTemplate jdbcTemplate;

    public InfectedThingEntity save(InfectedThingEntity infectedThingEntity) {
        jdbcTemplate.update("INSERT INTO infected_thing VALUES (?, ?, ?)",
                infectedThingEntity.getId(),
                infectedThingEntity.getName(),
                infectedThingEntity.getDoor());
        return infectedThingEntity;
    }

    public Optional<InfectedThingEntity> findById(UUID id) {
           return jdbcTemplate.query("SELECT * FROM infected_thing JOIN door " +
                "ON infected_thing.door_id = door.id WHERE infected_thing.id = ?",
                new InfectedThingMapper(),
                id).stream().findAny();
    }

    public Page<InfectedThingEntity> findAll(Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from infection", Integer.class);
        List<InfectedThingEntity> infections = jdbcTemplate.query("SELECT * FROM infected_thing " +
                        "JOIN door ON infected_thing.door_id = door.id limit ? offset ?",
                new InfectedThingMapper(),
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }

    public Page<InfectedThingEntity> findAllByDoor(UUID doorId, Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from infection", Integer.class);
        List<InfectedThingEntity> infections = jdbcTemplate.query("SELECT * FROM infected_thing " +
                        "JOIN door ON infected_thing.door_id = door.id where door_id = ? limit ? offset ?",
                new InfectedThingMapper(),
                doorId,
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }

    public void delete(InfectedThingEntity infectedThingEntity) {
        jdbcTemplate.update("delete from infected_thing where id = ?", infectedThingEntity.getId());
    }
}
