package com.example.childservice.repository;

import com.example.childservice.mapper.ChildMapper;
import com.example.childservice.model.ChildEntity;
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
public class ChildRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<ChildEntity> findById(UUID id) {
        return jdbcTemplate.query("SELECT * FROM child WHERE id = ?", new ChildMapper(), id)
                .stream().findAny();
    }

    public void delete(ChildEntity childEntity) {
        jdbcTemplate.update("DELETE FROM child WHERE id = ?", childEntity.getId());
    }

    public ChildEntity save(ChildEntity childEntity) {
        jdbcTemplate.update("INSERT INTO child VALUES (?, ?, ?, ?, ?)",
                childEntity.getId(),
                childEntity.getName(),
                childEntity.getGender().toString(),
                childEntity.getDateOfBirth(),
                childEntity.getDoor().getId());
        return childEntity;
    }

    public Page<ChildEntity> findAll(Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from child", Integer.class);
        List<ChildEntity> infections = jdbcTemplate.query("SELECT * FROM child " +
                        "JOIN door ON child.door_id = door.id " +
                        "limit ? offset ?",
                new ChildMapper(),
                pageable.getPageSize(),
                pageable.getOffset());
        return new PageImpl<>(infections, pageable, count);
    }
}
