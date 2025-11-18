package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checkins")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private WScoreCalculator calculator;

    @Autowired
    private CheckInProducer producer;

    @PostMapping
    public ResponseEntity<CheckInResponseDTO> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckInRequestDTO dto
    ) {
        var saved = checkInService.createCheckIn(user, dto);

        producer.sendCheckInEvent(
                new CheckInEventDTO(
                        saved.getUser().getId(),
                        saved.getId(),
                        saved.getScore(),
                        saved.getDate().toString()
                )
        );

        var message = calculator.getScoreMessage(saved.getScore());

        var response = new CheckInResponseDTO(
                saved.getId(),
                saved.getDate().toString(),
                saved.getMood(),
                saved.getEnergy(),
                saved.getSleep(),
                saved.getFocus(),
                saved.getHoursWorked(),
                saved.getScore(),
                message
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/today/exist")
    public ResponseEntity<Boolean> hasToday(@AuthenticationPrincipal User user) {
        boolean exists = checkInService.hasCheckInToday(user.getId());
        return ResponseEntity.ok(exists);
    }

    @GetMapping
    public ResponseEntity<Page<CheckInResponseDTO>> list(
            @AuthenticationPrincipal User user,
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CheckInModel> checkIns =
                checkInService.getUserCheckInsPaginated(user.getId(), pageable);

        return ResponseEntity.ok(
                checkIns.map(c -> new CheckInResponseDTO(
                        c.getId(),
                        c.getDate().toString(),
                        c.getMood(),
                        c.getEnergy(),
                        c.getSleep(),
                        c.getFocus(),
                        c.getHoursWorked(),
                        c.getScore(),
                        calculator.getScoreMessage(c.getScore())
                ))
        );
    }

    @GetMapping("/score/today")
    public ResponseEntity<?> getTodayScore(@AuthenticationPrincipal User user) {
        var checkin = checkInService.getTodayCheckIn(user.getId());
        if (checkin == null) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(
                new CheckInResponseDTO(
                        checkin.getId(),
                        checkin.getDate().toString(),
                        checkin.getMood(),
                        checkin.getEnergy(),
                        checkin.getSleep(),
                        checkin.getFocus(),
                        checkin.getHoursWorked(),
                        checkin.getScore(),
                        calculator.getScoreMessage(checkin.getScore())
                )
        );
    }

    @GetMapping("/score/history")
    public ResponseEntity<List<CheckInResponseDTO>> getHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "7") int days
    ) {
        var checkins = checkInService.getHistory(user.getId(), days);
        var response = checkins.stream()
                .map(c -> new CheckInResponseDTO(
                        c.getId(),
                        c.getDate().toString(),
                        c.getMood(),
                        c.getEnergy(),
                        c.getSleep(),
                        c.getFocus(),
                        c.getHoursWorked(),
                        c.getScore(),
                        calculator.getScoreMessage(c.getScore())
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-average")
    public ResponseEntity<Double> monthlyAverage(@AuthenticationPrincipal User user) {
        Double avg = checkInService.getMonthlyAverage(user.getId());
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    @GetMapping("/weekly-average")
    public ResponseEntity<Double> weeklyAverage(@AuthenticationPrincipal User user) {
        Double avg = checkInService.getWeeklyAverage(user.getId());
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    @GetMapping("/dashboard/humor-distribution")
    public ResponseEntity<?> getHumorDistribution(@AuthenticationPrincipal User user) {
        var stats = checkInService.getHumorDistribution(user.getId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/dashboard/energy")
    public ResponseEntity<List<Integer>> getEnergyHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                checkInService.getLastEnergyValues(user.getId(), 7)
        );
    }

    @GetMapping("/dashboard/focus")
    public ResponseEntity<List<Integer>> getFocusHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                checkInService.getLastFocusValues(user.getId(), 7)
        );
    }

    @GetMapping("/dashboard/sleep")
    public ResponseEntity<List<Integer>> getSleepHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                checkInService.getLastSleepValues(user.getId(), 7)
        );
    }
    @GetMapping("/dates")
    public ResponseEntity<List<String>> getCheckInDates(@AuthenticationPrincipal User user) {
        var list = checkInService.getUserCheckIns(user.getId());
        var dates = list.stream()
                .map(c -> c.getDate().toString())
                .toList();
        return ResponseEntity.ok(dates);
    }


    @GetMapping("/last")
    public ResponseEntity<CheckInResponseDTO> last(@AuthenticationPrincipal User user) {
        var checkIns = checkInService.getUserCheckIns(user.getId());

        if (checkIns.isEmpty()) return ResponseEntity.noContent().build();

        var last = checkIns.get(0);

        return ResponseEntity.ok(
                new CheckInResponseDTO(
                        last.getId(),
                        last.getDate().toString(),
                        last.getMood(),
                        last.getEnergy(),
                        last.getSleep(),
                        last.getFocus(),
                        last.getHoursWorked(),
                        last.getScore(),
                        calculator.getScoreMessage(last.getScore())
                )
        );
    }
}
