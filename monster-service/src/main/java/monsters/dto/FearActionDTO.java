package monsters.dto;

import lombok.*;
import monsters.dto.monster.MonsterDTO;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FearActionDTO {

    private UUID id;

    @NotNull(message = "shouldn't be null")
    private MonsterDTO monster;

    @NotNull(message = "shouldn't be null")
    private UUID doorId;

    @NotNull(message = "shouldn't be null")
    private Date date;

}
