package futureStack.futureStack.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class ChatBotService {

    private final ChatClient chatClient;
    private final MessageSource messageSource;

    public ChatBotService(ChatClient chatClient, MessageSource messageSource) {
        this.chatClient = chatClient;
        this.messageSource = messageSource;
    }

    public String chat(String userMessage) {

        String basePrompt = messageSource.getMessage(
                "chatbot.basePrompt",
                null,
                LocaleContextHolder.getLocale()
        );

        String finalPrompt = basePrompt + "\n\n" +
                messageSource.getMessage("chatbot.userQuestion", new Object[]{userMessage}, LocaleContextHolder.getLocale());

        return chatClient
                .prompt()
                .user(finalPrompt)
                .options(OpenAiChatOptions.builder()
                        .model("openai/gpt-oss-20b")
                        .build())
                .call()
                .content();
    }
}
