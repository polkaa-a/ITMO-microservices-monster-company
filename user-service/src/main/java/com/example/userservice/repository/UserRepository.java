package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {

    Mono<User> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
    Flux<User> findAllByRoleId(UUID id, Pageable pageable);

    Flux<User> findAllBy(Pageable pageable);
}