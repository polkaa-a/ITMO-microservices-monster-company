package com.example.childservice.repository;

import com.example.childservice.mapper.DoorMapper;
import com.example.childservice.model.DoorEntity;
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
public class DoorRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<DoorEntity> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM door WHERE id = ?", new DoorMapper(), id)
                .stream().findAny();
    }

    public DoorEntity save(DoorEntity doorEntity) {
        jdbcTemplate.update("INSERT INTO door VALUES (?, ?)",
                doorEntity.getId(),
                doorEntity.isActive());
        return doorEntity;
    }

    public void update(DoorEntity doorEntity) {
        jdbcTemplate.update("UPDATE door SET status = ? WHERE id = ?",
                doorEntity.isActive(),
                doorEntity.getId());
    }

    public Page<DoorEntity> findAll(Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from door", Integer.class);
        List<DoorEntity> infections = jdbcTemplate.query("SELECT * FROM door " +
                        "limit ? offset ?",
                new DoorMapper(),
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }

    public void delete(DoorEntity doorEntity) {
        jdbcTemplate.update("DELETE FROM door WHERE id = ?", doorEntity.getId());
    }
}
