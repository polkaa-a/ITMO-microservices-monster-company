package com.example.reactiveauthendpointaggregator.dto;

import com.example.reactiveauthendpointaggregator.enums.Gender;
import com.example.reactiveauthendpointaggregator.enums.Job;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestMonsterDTO {

    private UUID id;
    private UUID userId;
    private Job job;
    private String name;
    private Date dateOfBirth;
    private Gender gender;
    private String email;
    private Integer salary;

}
