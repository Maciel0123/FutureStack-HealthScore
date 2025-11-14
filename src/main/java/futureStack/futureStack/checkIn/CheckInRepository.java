package futureStack.futureStack.checkIn;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckInModel, Long> {

    List<CheckInModel> findByUser_IdOrderByDateDesc(Long userId);

    Page<CheckInModel> findByUser_IdOrderByDateDesc(Long userId, Pageable pageable);

    Optional<CheckInModel> findByUser_IdAndDate(Long userId, LocalDate date);

    @Query("SELECT AVG(c.score) FROM CheckInModel c WHERE c.user.id = :userId AND c.date >= :startDate")
    Double findAverageScoreSince(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
}