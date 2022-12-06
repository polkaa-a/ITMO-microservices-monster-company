package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.PageDTO;
import com.example.reactiveauthendpointaggregator.dto.UserRequestDTO;
import com.example.reactiveauthendpointaggregator.dto.UserResponseDTO;
import com.example.reactiveauthendpointaggregator.feignclient.UserServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceFeignClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> save(@RequestBody Mono<UserRequestDTO> userRequestDTOMono) {
        return userClient.saveUser(userRequestDTOMono);
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDTO> findByUsername(@PathVariable String username) {
        return userClient.findUserByUsername(username);
    }

    @GetMapping
    public Mono<ResponseEntity<PageDTO<UserResponseDTO>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size,
                                                                  @RequestParam(required = false) String role) {
        return userClient.findAllUsers(page, size, role)
                .map(pageUsers -> new ResponseEntity<>(
                        pageUsers,
                        HttpStatus.OK))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));

    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDTO> updateById(@RequestBody Map<String, String> updates, @PathVariable UUID id) {
        return userClient.updateUserById(updates, id);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String username) {
        return userClient.deleteUser(username);
    }
}
