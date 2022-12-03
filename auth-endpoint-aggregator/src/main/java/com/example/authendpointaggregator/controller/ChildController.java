package com.example.authendpointaggregator.controller;

import com.example.authendpointaggregator.dto.ChildRequestDTO;
import com.example.authendpointaggregator.dto.ChildResponseDTO;
import com.example.authendpointaggregator.dto.PageDTO;
import com.example.authendpointaggregator.feignclient.ChildServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/child")
public class ChildController {
    private final ChildServiceFeignClient childClient;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResponseDTO addChild(@RequestBody ChildRequestDTO childDTO) {
        return childClient.addChild(childDTO);
    }

    @PreAuthorize("hasAuthority('SCARE') or hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChildResponseDTO findById(@PathVariable UUID id) {
        return childClient.findChildById(id);
    }

    @PreAuthorize("hasAuthority('SCARE') or hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PageDTO<ChildResponseDTO>> getChildren(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size) {
        return childClient.getChildren(page, size);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        childClient.deleteChild(id);
    }
}
