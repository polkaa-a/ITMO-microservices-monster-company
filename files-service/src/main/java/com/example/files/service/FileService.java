package com.example.files.service;

import com.example.files.dto.FileRequestDTO;
import com.example.files.dto.FileResponseDTO;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final MinIOService minIOService;

    public Mono<FileResponseDTO> uploadFile(Mono<FileRequestDTO> fileDTOMono){
        return fileDTOMono.flatMap(fileDTO ->
                minIOService.uploadObject(fileDTO.getObjectName(),
                        Mono.fromCallable(() -> new FileInputStream(fileDTO.getAbsolutePath()))
                ).flatMap(objectWriteResponse ->
                        Mono.fromCallable(() -> new FileResponseDTO(objectWriteResponse.object()))));
    }

    public Flux<FileResponseDTO> getAllUploadedFiles(){
       return minIOService.getListOfObjects("")
               .flatMap(objectName -> Mono.fromCallable(() -> new FileResponseDTO(objectName)));
    }

    public Flux<FileResponseDTO> getAllUploadedFilesByUser(String userName){
        return minIOService.getListOfObjectsByUser(userName)
                .flatMap(objectName -> Mono.fromCallable(() -> new FileResponseDTO(objectName)));
    }

    public Mono<Void> downloadFile(Mono<FileRequestDTO> fileDTOMono){
        return fileDTOMono.flatMap(fileRequestDTO ->
                minIOService.downloadObject(fileRequestDTO.getObjectName(), fileRequestDTO.getAbsolutePath()));
    }

    public Mono<Void> deleteFile(Mono<FileRequestDTO> fileDTOMono){
        return fileDTOMono.flatMap(fileRequestDTO ->
                minIOService.deleteObject(fileRequestDTO.getObjectName()));
    }
}
