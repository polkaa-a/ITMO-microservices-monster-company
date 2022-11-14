package monsters.repository;

import monsters.model.RewardEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RewardRepository extends ReactiveCrudRepository<RewardEntity, UUID> {
    Flux<RewardEntity> findByMoney(int money);
}
