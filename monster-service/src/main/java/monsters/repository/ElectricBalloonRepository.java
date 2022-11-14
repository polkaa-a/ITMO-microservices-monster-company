package monsters.repository;

import monsters.model.ElectricBalloonEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.UUID;

@Repository
public interface ElectricBalloonRepository extends ReactiveCrudRepository<ElectricBalloonEntity, UUID> {

    @Query("select e from ElectricBalloonEntity e " +
            "join e.fearActionEntity f " +
            "where f.date=:date")
    Flux<ElectricBalloonEntity> findAllFilledByDate(@Param("date") Date date, Pageable pageable);

    @Query("select e from ElectricBalloonEntity e " +
            "join e.fearActionEntity f " +
            "where f.date=:date " +
            "and e.cityEntity.id=:cityId")
    Flux<ElectricBalloonEntity> findAllFilledByDateAndCity(@Param("date") Date date, @Param("cityId") UUID cityId, Pageable pageable);


    @Query("select e from ElectricBalloonEntity e " +
            "join e.fearActionEntity f " +
            "where f.monsterEntity.id=:monsterId")
    Flux<ElectricBalloonEntity> findAllByMonsterId(@Param("monsterId") UUID monsterId, Pageable pageable);

}

