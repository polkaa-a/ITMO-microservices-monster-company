package monsters.dto.answer;

import lombok.*;

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
    private UUID doorId;

    @NotNull(message = "shouldn't be null")
    private Date date;

}
