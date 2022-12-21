package com.example.files.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MinIOConfig {

    @Value("$(minio.url)")
    private String minioUrl;
    @Value("$(minio.accessKey)")
    private String accessKey;
    @Value("$(minio.secretKey)")
    private String secretKey;

    @Bean
    public MinioClient MinIOClient(){
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();

        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000/")
                .httpClient(httpClient)
                .credentials("minioadmin", "minioadmin")
                .build();
    }
}
