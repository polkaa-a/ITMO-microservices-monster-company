package com.example.files.config;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MinIOConfig {

    private final String url = "http://127.0.0.1:9000/";
    private final String accessKey = "minioadmin";
    private final String secretKey = "minioadmin";

    @Bean
    public MinioClient MinIOClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();

        return MinioClient.builder()
                .endpoint(url)
                .httpClient(httpClient)
                .credentials(accessKey, secretKey)
                .build();
    }
}
