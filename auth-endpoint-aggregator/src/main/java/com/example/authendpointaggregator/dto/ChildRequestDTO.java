package com.example.authendpointaggregator.dto;


import com.example.authendpointaggregator.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ChildRequestDTO {
    private String name;
    private Date dob;
    private Gender gender;
    private UUID doorId;
}
