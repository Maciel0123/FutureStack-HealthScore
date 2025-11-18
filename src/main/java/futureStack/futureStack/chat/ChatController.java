package futureStack.futureStack.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired
    private ChatBotService chatBotService;

    @Autowired
    private ChatBotWebSocketService chatBotWebSocketService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {

        String response = chatBotService.chat(request.message);

        chatBotWebSocketService.sendChatMessage(response);

        return ResponseEntity.ok(new ChatResponseDTO(response));
    }
}
