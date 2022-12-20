package com.example.files.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    @Value("$(minio.url)")
    private String minioUrl;
    @Value("$(minio.accessKey)")
    private String accessKey;
    @Value("$(minio.secretKey)")
    private String secretKey;
    @Value("$(minio.secure)")
    private boolean minioSecure;

    @Bean
    public MinioClient MinIOClient(){
        return MinioClient.builder()
                .endpoint(minioUrl, 9000, minioSecure)
                .credentials(accessKey, secretKey)
                .build();
    }
}
