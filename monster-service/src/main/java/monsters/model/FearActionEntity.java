package monsters.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "fear_action")
public class FearActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @NotNull(message = "shouldn't be null")
    @ManyToOne
    @JoinColumn(name = "monster_id")
    private MonsterEntity monsterEntity;

    @NotNull(message = "shouldn't be null")
    @Column(name = "door_id")
    private UUID doorId;

    @NotNull(message = "shouldn't be null")
    @Column(name = "date")
    private Date date;

}
