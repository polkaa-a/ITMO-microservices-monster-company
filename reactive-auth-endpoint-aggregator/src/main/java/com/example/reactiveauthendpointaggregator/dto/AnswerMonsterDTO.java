package com.example.reactiveauthendpointaggregator.dto;

import com.example.reactiveauthendpointaggregator.enums.Gender;
import com.example.reactiveauthendpointaggregator.enums.Job;
import lombok.*;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerMonsterDTO {

    private UUID id;
    private UserResponseDTO userResponseDTO;
    private Job job;
    private String name;
    private Date dateOfBirth;
    private Gender gender;
    private String email;
    private Integer salary;
    private List<RewardDTO> rewards;
}
