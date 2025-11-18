package futureStack.futureStack.chat;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatBotWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatBotWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendChatMessage(String response) {
        messagingTemplate.convertAndSend(
                "/topic/chatbot",
                new ChatResponseDTO(response)
        );
    }
}
