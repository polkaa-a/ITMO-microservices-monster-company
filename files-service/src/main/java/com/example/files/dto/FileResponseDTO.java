package com.example.files.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDTO {

    private final String SEPARATOR = "/";
    @NotNull
    private String userName;
    @NotNull
    private String fileName;

    public String getObjectName(){
        return userName + SEPARATOR + fileName;
    }

    public FileResponseDTO(String objectName){
        int separator = objectName.indexOf(SEPARATOR);
        this.userName = objectName.substring(0, separator);
        this.fileName = objectName.substring(separator + 1);
    }
}