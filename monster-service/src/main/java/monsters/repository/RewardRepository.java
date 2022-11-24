package monsters.repository;

import monsters.model.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, UUID> {
    Optional<RewardEntity> findByMoney(int money);
}
