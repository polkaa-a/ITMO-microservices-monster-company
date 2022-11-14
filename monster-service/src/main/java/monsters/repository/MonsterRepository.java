package monsters.repository;

import monsters.enums.Job;
import monsters.model.MonsterEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.UUID;

@Repository
public interface MonsterRepository extends ReactiveCrudRepository<MonsterEntity, UUID> {

    Flux<MonsterEntity> findAll(Pageable pageable);

    Flux<MonsterEntity> findAllByJob(Job job, Pageable pageable);

    Flux<MonsterEntity> findByEmail(String email);

    // TODO: 14.11.2022 connection with User
//    @Query("select m from MonsterEntity m " +
//            "join m.userEntity u " +
//            "where u.id=:userId")
    Flux<MonsterEntity> findByUserId(@Param("userId") UUID userId);

    @Query("select m from MonsterEntity m " +
            "join m.fearActions f " +
            "where f.date=:date")
    Flux<MonsterEntity> findAllByDateOfFearAction(@Param("date") Date date, Pageable pageable);

    // TODO: 14.11.2022 connection with Infection
//    @Query("select m from MonsterEntity m " +
//            "join m.infections i " +
//            "where i.infectionDate<=:date " +
//            "and i.cureDate>:date")
    Flux<MonsterEntity> findAllByInfectionDate(@Param("date") Date date, Pageable pageable);

}
