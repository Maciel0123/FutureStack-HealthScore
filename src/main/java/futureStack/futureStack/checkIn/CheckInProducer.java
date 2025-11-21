package futureStack.futureStack.checkIn;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static futureStack.futureStack.config.RabbitConfig.*;

@Profile("dev")
@Component
public class CheckInProducer {

    private final RabbitTemplate rabbitTemplate;

    public CheckInProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCheckInEvent(CheckInEventDTO dto) {
        rabbitTemplate.convertAndSend(
                CHECKIN_EXCHANGE,
                CHECKIN_ROUTING_KEY,
                dto
        );
    }
}
