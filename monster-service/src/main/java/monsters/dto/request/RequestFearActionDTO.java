package monsters.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFearActionDTO {

    private UUID id;

    @NotNull(message = "shouldn't be null")
    private UUID monsterId;

    @NotNull(message = "shouldn't be null")
    private UUID doorId;

    @NotNull(message = "shouldn't be null")
    private Date date;

}
