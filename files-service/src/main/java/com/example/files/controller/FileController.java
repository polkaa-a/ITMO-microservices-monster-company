package com.example.files.controller;

import com.example.files.dto.FileRequestDTO;
import com.example.files.dto.FileResponseDTO;
import com.example.files.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<FileResponseDTO> getAllUploadedFiles() {
        return fileService.getAllUploadedFiles();
    }

    @GetMapping("/{userName}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<FileResponseDTO> getAllUploadedFilesByUser(@PathVariable String userName) {
        return fileService.getAllUploadedFilesByUser(userName);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FileResponseDTO> uploadFile(@RequestBody @Valid Mono<FileRequestDTO> fileRequestDTOMono) {
        return fileService.uploadFile(fileRequestDTOMono);
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> downloadFile(@RequestBody @Valid Mono<FileRequestDTO> fileRequestDTOMono) {
        return fileService.downloadFile(fileRequestDTOMono);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(@RequestBody @Valid Mono<FileRequestDTO> fileRequestDTOMono) {
        return fileService.deleteFile(fileRequestDTOMono);
    }
}
