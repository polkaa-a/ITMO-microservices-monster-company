package monsters.dto;

import lombok.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardDTO {

    private UUID id;

    @Min(value = 0, message = "shouldn't be less than 0")
    private Integer balloonCount;

    @Min(value = 0, message = "shouldn't be less than 0")
    private Integer money;

}
