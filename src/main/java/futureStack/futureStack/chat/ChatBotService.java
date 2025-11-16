package futureStack.futureStack.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

    private final ChatClient chatClient;

    private final String basePrompt = """
    Você é o assistente oficial do aplicativo FeatureStack HealthScore.
    Seu papel é tirar dúvidas sobre: check-ins, score, humor, energia, foco, carga de trabalho, histórico, recomendações, e funcionamento geral do app.
    
    REGRAS:
    - Responda sempre de forma curta, simples e amigável.
    - Não use listas, markdown, negrito ou bullets.
    - Fale como um guia amigável, sem linguagem técnica demais.
    - Se o usuário pedir algo que o app não faz, responda com gentileza explicando o que é possível.
    """;

    public ChatBotService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String userMessage) {

        String finalPrompt = basePrompt + "\n\nPergunta do usuário: " + userMessage;

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
