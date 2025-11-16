package futureStack.futureStack.checkIn;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static futureStack.futureStack.config.RabbitConfig.*;

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