package monsters.dto.answer;

import lombok.*;
import monsters.dto.CityDTO;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerElectricBalloonDTO {

    private UUID id;

    @NotNull(message = "shouldn't be null")
    private AnswerFearActionDTO fearAction;

    @NotNull(message = "shouldn't be null")
    private CityDTO city;
}
