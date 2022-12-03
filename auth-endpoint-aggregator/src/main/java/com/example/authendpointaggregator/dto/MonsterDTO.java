package com.example.authendpointaggregator.dto;

import com.example.authendpointaggregator.enums.Gender;
import com.example.authendpointaggregator.enums.Job;
import lombok.*;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonsterDTO {

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
