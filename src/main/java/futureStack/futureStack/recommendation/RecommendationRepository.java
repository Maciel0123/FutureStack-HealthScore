package futureStack.futureStack.recommendation;

import futureStack.futureStack.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationModel, Long> {
    List<RecommendationModel> findByUserId(Long userId);

    RecommendationModel findByCheckInId(Long checkInId);
}
