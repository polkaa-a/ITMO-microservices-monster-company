package monsters.repository;

import monsters.model.FearActionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FearActionRepository extends ReactiveCrudRepository<FearActionEntity, UUID> {
}
