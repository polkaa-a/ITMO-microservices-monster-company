package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.model.InfectionEntity;
import lombok.RequiredArgsConstructor;
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
public class InfectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<InfectionEntity> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM infection " +
                        "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                        "WHERE infection.id = ?",
                new InfectionMapper(), id)
                .stream().findAny();
    }

    public InfectionEntity save(InfectionEntity infectionEntity) {
        jdbcTemplate.update("INSERT INTO infection VALUES (?, ?, ?, ?, ?)",
                infectionEntity.getId(),
                infectionEntity.getMonster(),
                infectionEntity.getInfectedThing(),
                infectionEntity.getInfectionDate(),
                infectionEntity.getCureDate());
        return infectionEntity;
    }

    public void update(InfectionEntity infectionEntity) {
        jdbcTemplate.update("update infection set cure_date = ? where id = ?",
                infectionEntity.getCureDate(),
                infectionEntity.getId());
    }

    public Page<InfectionEntity> findAll(Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from infection", Integer.class);
            List<InfectionEntity> infections = jdbcTemplate.query("SELECT * FROM infection " +
                            "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                            "limit ? offset ?",
                new InfectionMapper(),
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }

    public Page<InfectionEntity> findAllByMonsterId(UUID monsterId, Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from infection", Integer.class);
        List<InfectionEntity> infections = jdbcTemplate.query("SELECT * FROM infection " +
                        "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                "WHERE monster_id = ? " +
                        "limit ? offset ?",
                new InfectionMapper(),
                monsterId,
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }

    public void delete(InfectionEntity infectionEntity) {
        jdbcTemplate.update("DELETE FROM infection WHERE id = ?", infectionEntity.getId());
    }
}
