package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.model.ChildEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ChildRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<ChildEntity> findById(UUID id) {
        return jdbcTemplate.query("select * from child where id = ?", new Object[]{id}, new ChildMapper())
                .stream().findAny();
    }

    public void delete(ChildEntity childEntity) {
        jdbcTemplate.update("delete from child where id = ?", childEntity.getId());
    }

    public ChildEntity save(ChildEntity childEntity) {
        childEntity.setId(UUID.randomUUID());
        jdbcTemplate.update("insert into child values(?, ?, ?, ?, ?)", childEntity.getId(),
                                                                            childEntity.getName(),
                                                                            childEntity.getGender(),
                                                                            childEntity.getDateOfBirth(),
                                                                            childEntity.getDoor().getId());
        return childEntity;
    }
}
