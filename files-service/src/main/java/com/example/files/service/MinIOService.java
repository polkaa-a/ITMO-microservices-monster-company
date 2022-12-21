package com.example.files.service;

import com.example.files.controller.exception.object.DeleteObjectException;
import com.example.files.controller.exception.object.DownloadObjectException;
import com.example.files.controller.exception.object.GetObjectException;
import com.example.files.controller.exception.object.UploadObjectException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinIOService {

    //@Value("$(minio.bucket-name)")
    private final String bucketName = "storage";

    private static final int BUFFER_SIZE = 5;
    private static final String EXC_OBJ= "Bad object";
    private static final String EXC_DELETE= "Error occurred while deleting object";
    private static final String EXC_DOWNLOAD= "Error occurred while downloading object";
    private static final String EXC_UPLOAD= "Error occurred while uploading object";
    private static final String EXC_GET= "Error occurred while getting objects";

    private final MinioClient minioClient;

    public Flux<String> getListOfObjects(String prefix){
        return Mono.fromCallable(() -> minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build())
                ).flux()
                .flatMap(Flux::fromIterable)
                .buffer(BUFFER_SIZE)
                .flatMap(list -> Flux.fromIterable(list)
                        .flatMap(result -> {
                            try {
                                return Mono.just(result.get().objectName());
                            } catch (Exception e){
                                return Mono.error(
                                        new GetObjectException(EXC_GET + " from " + bucketName + ": " + e.getMessage())
                                );
                            }
                        })
                        .subscribeOn(Schedulers.parallel()));
    }

    public Flux<String> getListOfObjectsByUser(String userName){
        return getListOfObjects(userName);
    }

    public Mono<ObjectWriteResponse> uploadObject(String objectName, Mono<InputStream> inputStreamMono) {
        return inputStreamMono.flatMap(inputStream ->
                Mono.fromCallable(inputStream::available).flatMap(objectSize ->
                        Mono.fromCallable(() -> {
                                    try {
                                        return minioClient.putObject(PutObjectArgs.builder()
                                                .bucket(bucketName)
                                                .object(objectName)
                                                .stream(inputStream, objectSize, -1)
                                                .build());
                                    } catch (Exception e) {
                                        throw new UploadObjectException(EXC_UPLOAD + " " + objectName + ": "
                                                + e.getMessage());
                                    }
                                }
                        )));
    }

    public Mono<Void> downloadObject(String objectName, String fileName) {
        return Mono.fromRunnable(() -> {
            try {
                minioClient.downloadObject(DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
            } catch (Exception e) {
                throw new DownloadObjectException(EXC_DOWNLOAD + " " + objectName + ": " + e.getMessage());
            }
        });
    }

    public Mono<Void> deleteObject(String objectName){
        return Mono.fromRunnable(() -> {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
            } catch (Exception e) {
                throw new DeleteObjectException(EXC_DELETE + " " + objectName + ": " + e.getMessage());
            }
        });
    }
}
