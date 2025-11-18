package futureStack.futureStack.checkIn;

import futureStack.futureStack.recommendation.RecommendationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static futureStack.futureStack.config.RabbitConfig.CHECKIN_QUEUE;

@Component
public class CheckInConsumer {

    private final RecommendationService recommendationService;

    public CheckInConsumer(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @RabbitListener(queues = CHECKIN_QUEUE)
    public void processCheckIn(CheckInEventDTO event) {

        String recommendation = recommendationService.generateRecommendationFromScore(
                event.getScore()
        );

        recommendationService.saveRecommendation(
                event.getUserId(),
                event.getCheckInId(),
                event.getScore(),
                recommendation
        );

    }
}