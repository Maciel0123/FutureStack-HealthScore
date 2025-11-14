package futureStack.futureStack.recommendation;

import futureStack.futureStack.checkIn.CheckInService;
import futureStack.futureStack.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/latest")
    @Cacheable(value = "recommendations", key = "#score")
    public ResponseEntity<?> getLatestRecommendation(@AuthenticationPrincipal User user) {
        var checkIns = checkInService.getUserCheckIns(user.getId());
        if (checkIns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        var last = checkIns.get(0);
        String message = recommendationService.getRecommendation(last.getScore());

        var response = new RecommendationResponseDTO(
                message,
                last.getScore(),
                last.getDate().toString()
        );

        return ResponseEntity.ok(response);
    }
}
