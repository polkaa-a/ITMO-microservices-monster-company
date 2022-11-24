package monsters.dto.answer;

import lombok.*;
import monsters.dto.RewardDTO;
import monsters.enums.Gender;
import monsters.enums.Job;

import javax.validation.constraints.*;
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

    @NotNull(message = "shouldn't be null")
    private UserResponseDTO userResponseDTO;

    @NotNull(message = "shouldn't be null")
    private Job job;

    @NotBlank(message = "shouldn't be empty")
    @Size(max = 16, message = "shouldn't exceed 16 characters")
    private String name;

    @NotNull(message = "shouldn't be null")
    private Date dateOfBirth;

    @NotNull(message = "shouldn't be null")
    private Gender gender;

    @NotBlank(message = "shouldn't be null")
    @Size(max = 30, message = "shouldn't exceed 30 characters")
    @Email
    private String email;

    @NotNull(message = "shouldn't be null")
    @Min(value = 0, message = "shouldn't be less than 0")
    private Integer salary;

    @NotNull(message = "can be empty, but shouldn't be null")
    private List<RewardDTO> rewards;
}
