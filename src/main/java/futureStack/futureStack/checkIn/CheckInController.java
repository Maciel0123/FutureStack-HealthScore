package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import futureStack.futureStack.users.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private WScoreCalculator calculator;

    @PostMapping
    public ResponseEntity<CheckInResponseDTO> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckInRequestDTO dto
    ) {
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
    public ResponseEntity<Page<CheckInResponseDTO>> list(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Page<CheckInModel> checkIns = checkInService.getUserCheckInsPaginated(user.getId(), pageable);
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

    @GetMapping("/score/today")
    public ResponseEntity<?> getTodayScore(@AuthenticationPrincipal User user) {
        var checkin = checkInService.getTodayCheckIn(user.getId());
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

    @GetMapping("/weekly-average")
    public ResponseEntity<Double> weeklyAverage(@AuthenticationPrincipal User user) {
        Double avg = checkInService.getWeeklyAverage(user.getId());
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }

    @GetMapping("/last")
    public ResponseEntity<CheckInResponseDTO> last(@AuthenticationPrincipal User user) {
        var checkIns = checkInService.getUserCheckIns(user.getId());
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
