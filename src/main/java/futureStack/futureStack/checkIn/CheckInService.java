package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private WScoreCalculator calculator;

    @CacheEvict(allEntries = true)
    public CheckInModel createCheckIn(User user, CheckInRequestDTO dto) {
        if (repository.findByUser_IdAndDate(user.getId(), LocalDate.now()).isPresent()) {
            throw new RuntimeException(
                    messageSource.getMessage(
                            "checkin.already.exists",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );

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

        var saved = repository.save(checkIn);

        Double monthlyAvg = getMonthlyAverage(user.getId());

        messagingTemplate.convertAndSend(
                "/topic/dashboard-update",
                Map.of(
                        "score", saved.getScore(),
                        "mood", saved.getMood(),
                        "energy", saved.getEnergy(),
                        "sleep", saved.getSleep(),
                        "focus", saved.getFocus(),
                        "date", saved.getDate().toString(),

                        "monthlyAverage", monthlyAvg != null ? monthlyAvg : 0.0
                )
        );
        return saved;
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

    public Map<String, Integer> getHumorDistribution(Long userId) {
        List<CheckInModel> list = repository.findByUser_IdOrderByDateDesc(userId);

        int low = (int) list.stream().filter(c -> c.getMood() <= 4).count();
        int medium = (int) list.stream().filter(c -> c.getMood() >= 5 && c.getMood() <= 7).count();
        int high = (int) list.stream().filter(c -> c.getMood() >= 8).count();

        return Map.of(
                "low", low,
                "medium", medium,
                "high", high
        );
    }

    public List<Integer> getLastEnergyValues(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days);

        return repository.findByUser_IdOrderByDateDesc(userId)
                .stream()
                .filter(c -> !c.getDate().isBefore(start))
                .map(CheckInModel::getEnergy)
                .toList();
    }

    public List<Integer> getLastFocusValues(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days);

        return repository.findByUser_IdOrderByDateDesc(userId)
                .stream()
                .filter(c -> !c.getDate().isBefore(start))
                .map(CheckInModel::getFocus)
                .toList();
    }

    public List<Integer> getLastSleepValues(Long userId, int days) {
        LocalDate start = LocalDate.now().minusDays(days);

        return repository.findByUser_IdOrderByDateDesc(userId)
                .stream()
                .filter(c -> !c.getDate().isBefore(start))
                .map(CheckInModel::getSleep)
                .toList();
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
