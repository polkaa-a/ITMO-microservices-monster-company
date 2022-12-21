package com.example.files.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileRequestDTO {
    private final String SEPARATOR = "/";
    @NotNull
    private String userName;
    @NotNull
    private String absolutePath;
    @NotNull
    private String fileName;

    public String getObjectName(){
        return userName + SEPARATOR + fileName;
    }
}
