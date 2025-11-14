package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@CacheConfig(cacheNames = {
        "checkins_user",
        "checkins_history",
        "monthly_avg",
        "weekly_avg",
        "last_checkin"
})
public class CheckInService {

    @Autowired
    private CheckInRepository repository;

    @Autowired
    private WScoreCalculator calculator;

    @CacheEvict(allEntries = true)
    public CheckInModel createCheckIn(User user, CheckInRequestDTO dto) {
        if (repository.findByUser_IdAndDate(user.getId(), LocalDate.now()).isPresent()) {
            throw new RuntimeException("Check-in já realizado hoje");
        }

        CheckInModel checkIn = new CheckInModel();
        checkIn.setUser(user);
        checkIn.setDate(LocalDate.now());
        checkIn.setMood(dto.getMood());
        checkIn.setEnergy(dto.getEnergy());
        checkIn.setSleep(dto.getSleep());
        checkIn.setFocus(dto.getFocus());
        checkIn.setHoursWorked(dto.getHoursWorked());

        int score = calculator.calculateScore(
                dto.getMood(),
                dto.getEnergy(),
                dto.getSleep(),
                dto.getFocus(),
                dto.getHoursWorked()
        );
        checkIn.setScore(score);

        return repository.save(checkIn);
    }

    @Cacheable(value = "checkins_user", key = "#userId")
    public List<CheckInModel> getUserCheckIns(Long userId) {
        return repository.findByUser_IdOrderByDateDesc(userId);
    }

    public Page<CheckInModel> getUserCheckInsPaginated(Long userId, Pageable pageable) {
        return repository.findByUser_IdOrderByDateDesc(userId, pageable);
    }

    @Cacheable(value = "weekly_avg", key = "#userId")
    public Double getWeeklyAverage(Long userId) {
        return repository.findAverageScoreSince(userId, LocalDate.now().minusDays(7));
    }

    @Cacheable(value = "last_checkin", key = "#userId")
    public CheckInModel getTodayCheckIn(Long userId) {
        return repository.findByUser_IdAndDate(userId, LocalDate.now()).orElse(null);
    }

    @Cacheable(value = "checkins_history", key = "#userId + '-' + #days")
    public List<CheckInModel> getHistory(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return repository.findByUser_IdOrderByDateDesc(userId)
                .stream()
                .filter(c -> !c.getDate().isBefore(startDate))
                .toList();
    }

    public Double getMonthlyAverage(Long userId) {
    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
    return repository.findAverageScoreSince(userId, startOfMonth);
}

}
