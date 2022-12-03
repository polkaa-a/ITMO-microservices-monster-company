package com.example.reactiveauthendpointaggregator.controller;

import com.example.reactiveauthendpointaggregator.dto.PageDTO;
import com.example.reactiveauthendpointaggregator.dto.RoleDTO;
import com.example.reactiveauthendpointaggregator.feignclient.UserServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RestController
@RequestMapping("/roles")
public class RoleController {
    private final UserServiceFeignClient roleClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDTO> save(@RequestBody Mono<RoleDTO> roleDTOMono) {
        return roleClient.saveRole(roleDTOMono);
    }


    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RoleDTO> findByName(@PathVariable String name) {
        return roleClient.findRoleByName(name);
    }

    @GetMapping
    public Mono<ResponseEntity<PageDTO<RoleDTO>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size) {
        return roleClient.findAllRoles(page, size)
                .map(pageRoles -> new ResponseEntity<>(
                        pageRoles,
                        HttpStatus.OK))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String name) {
        return roleClient.deleteRole(name);
    }

}
