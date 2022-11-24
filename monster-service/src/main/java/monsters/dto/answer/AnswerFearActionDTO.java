package monsters.dto.answer;

import lombok.*;
import monsters.dto.DoorDTO;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerFearActionDTO {

    private UUID id;

    @NotNull(message = "shouldn't be null")
    private AnswerMonsterDTO monster;

    @NotNull(message = "shouldn't be null")
    private DoorDTO door;

    @NotNull(message = "shouldn't be null")
    private Date date;

}
