package com.example.childservice.dto;

import com.example.childservice.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ChildResponseDTO {
    private UUID id;
    private String name;
    private Date dob;
    private Gender gender;
    private DoorDTO doorDTO;
}
