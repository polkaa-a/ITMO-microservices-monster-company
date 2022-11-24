package monsters.repository;

import monsters.enums.Job;
import monsters.model.MonsterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonsterRepository extends JpaRepository<MonsterEntity, UUID> {

    Page<MonsterEntity> findAll(Pageable pageable);

    Page<MonsterEntity> findAllByJob(Job job, Pageable pageable);

    Optional<MonsterEntity> findByEmail(String email);

    @Query("select m from MonsterEntity m " +
            "where m.userId=:userId")
    Optional<MonsterEntity> findByUserId(@Param("userId") UUID userId);

}
