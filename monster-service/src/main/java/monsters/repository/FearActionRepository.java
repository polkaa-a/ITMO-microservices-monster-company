package monsters.repository;

import monsters.model.FearActionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface FearActionRepository extends JpaRepository<FearActionEntity, UUID> {
    Page<FearActionEntity> findAllByDate(Date date, Pageable pageable);
}
