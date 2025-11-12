package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping
    public ResponseEntity<CheckInResponseDTO> create( @AuthenticationPrincipal User user, @Valid @RequestBody CheckInRequestDTO dto) {
        var saved = checkInService.createCheckIn(user, dto);
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
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CheckInResponseDTO>> list( @AuthenticationPrincipal User user, Pageable pageable) {
        Page<CheckInModel> checkIns = checkInService.getUserCheckInsPaginated(user.getIdUser(), pageable);

        return ResponseEntity.ok(checkIns.map(c ->
                new CheckInResponseDTO(
                        c.getId(),
                        c.getDate().toString(),
                        c.getMood(),
                        c.getEnergy(),
                        c.getSleep(),
                        c.getFocus(),
                        c.getHoursWorked(),
                        c.getScore(),
                        calculator.getScoreMessage(c.getScore())
                )
        ));
    }

    @GetMapping("/score/trend")
    public ResponseEntity<List<Integer>> getScoreTrend(@AuthenticationPrincipal User user) {
        var checkins = checkInService.getHistory(user.getIdUser(), 30);
        var trend = checkins.stream().map(CheckInModel::getScore).toList();
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/today/status")
    public ResponseEntity<Boolean> hasCheckInToday(@AuthenticationPrincipal User user) {
        boolean done = checkInService.getTodayCheckIn(user.getIdUser()) != null;
    return ResponseEntity.ok(done);
    }

    @GetMapping("/statistics")
        public ResponseEntity<CheckInStatisticsDTO> getStatistics(@AuthenticationPrincipal User user) {
        var stats = checkInService.getStatistics(user.getIdUser());
        return ResponseEntity.ok(stats);
        }

        @GetMapping("/calendar")
        public ResponseEntity<List<String>> getCheckInDates(@AuthenticationPrincipal User user) {
                var checkins = checkInService.getUserCheckIns(user.getIdUser());
                if (checkins.isEmpty()) {
                        return ResponseEntity.noContent().build();
                }

                var dates = checkins.stream().map(c -> c.getDate().toString()).toList();

                return ResponseEntity.ok(dates);
        }


     @GetMapping("/score/monthly")
     public ResponseEntity<Double> getMonthlyAverage(@AuthenticationPrincipal User user) {

        Double avg = checkInService.getMonthlyAverage(user.getIdUser());
        return ResponseEntity.ok(avg != null ? avg : 0.0);
     }

     @GetMapping("/export/json")
     public ResponseEntity<List<CheckInResponseDTO>> exportAll(@AuthenticationPrincipal User user) {

        var checkIns = checkInService.getUserCheckIns(user.getIdUser());
        if (checkIns.isEmpty()) {
                return ResponseEntity.noContent().build();
        }

        var response = checkIns.stream()
                .map(c -> new CheckInResponseDTO(
                        c.getId(),
                        c.getDate().toString(),
                        c.getMood(),
                        c.getEnergy(),
                        c.getSleep(),
                        c.getFocus(),
                        c.getHoursWorked(),
                        c.getScore(),
                        "Exportado com sucesso"
                ))
                .toList();

        return ResponseEntity.ok(response);
     }


    @GetMapping("/score/today")
    public ResponseEntity<?> getTodayScore(@AuthenticationPrincipal User user) {
        var checkin = checkInService.getTodayCheckIn(user.getIdUser());
        if (checkin == null) return ResponseEntity.noContent().build();

        var message = calculator.getScoreMessage(checkin.getScore());
        return ResponseEntity.ok(new CheckInResponseDTO(
                checkin.getId(),
                checkin.getDate().toString(),
                checkin.getMood(),
                checkin.getEnergy(),
                checkin.getSleep(),
                checkin.getFocus(),
                checkin.getHoursWorked(),
                checkin.getScore(),
                message
        ));
    }

    @GetMapping("/score/history")
    public ResponseEntity<List<CheckInResponseDTO>> getHistory( @AuthenticationPrincipal User user, @RequestParam(defaultValue = "7") int days) {
        var checkins = checkInService.getHistory(user.getIdUser(), days);
        var response = checkins.stream().map(c -> new CheckInResponseDTO(
                        c.getId(),
                        c.getDate().toString(),
                        c.getMood(),
                        c.getEnergy(),
                        c.getSleep(),
                        c.getFocus(),
                        c.getHoursWorked(),
                        c.getScore(),
                        calculator.getScoreMessage(c.getScore())
                )).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly-average")
    public ResponseEntity<Double> weeklyAverage(@AuthenticationPrincipal User user) {

        Double avg = checkInService.getWeeklyAverage(user.getIdUser());
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    @GetMapping("/last")
    public ResponseEntity<CheckInResponseDTO> last(@AuthenticationPrincipal User user) {

        var checkIns = checkInService.getUserCheckIns(user.getIdUser());
        if (checkIns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var last = checkIns.get(0);
        var response = new CheckInResponseDTO(
                last.getId(),
                last.getDate().toString(),
                last.getMood(),
                last.getEnergy(),
                last.getSleep(),
                last.getFocus(),
                last.getHoursWorked(),
                last.getScore(),
                calculator.getScoreMessage(last.getScore())
        );

        return ResponseEntity.ok(response);
    }
}
