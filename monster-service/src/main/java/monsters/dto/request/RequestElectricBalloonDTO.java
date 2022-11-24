package monsters.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestElectricBalloonDTO {

    private UUID id;

    @NotNull(message = "shouldn't be null")
    private UUID fearActionId;

    @NotNull(message = "shouldn't be null")
    private UUID cityId;
}
