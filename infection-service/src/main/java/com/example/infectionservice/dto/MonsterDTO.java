package com.example.infectionservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MonsterDTO {
    private UUID id;

    @NotNull(message = "shouldn't be null")
    private UUID userId;

    @NotNull(message = "shouldn't be null")
    private String job;

    @NotBlank(message = "shouldn't be empty")
    @Size(max = 16, message = "shouldn't exceed 16 characters")
    private String name;

    @NotNull(message = "shouldn't be null")
    private Date dateOfBirth;

    @NotNull(message = "shouldn't be null")
    private String gender;

    @NotBlank(message = "shouldn't be null")
    @Size(max = 30, message = "shouldn't exceed 30 characters")
    private String email;

    @NotNull(message = "shouldn't be null")
    @Min(value = 0, message = "shouldn't be less than 0")
    private Integer salary;
}
