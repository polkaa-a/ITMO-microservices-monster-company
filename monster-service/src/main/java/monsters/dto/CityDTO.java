package monsters.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityDTO {

    private UUID id;

    @NotBlank(message = "shouldn't be empty")
    @Size(max = 20, message = "shouldn't exceed 20 characters")
    private String name;
}
