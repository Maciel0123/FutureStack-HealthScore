package futureStack.futureStack.recommendation;

import futureStack.futureStack.checkIn.CheckInRepository;
import futureStack.futureStack.users.User;
import futureStack.futureStack.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private final ChatClient chatClient;
    private final CheckInRepository checkInRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public RecommendationService(ChatClient chatClient, CheckInRepository repo, RecommendationRepository recommendationRepository, UserRepository userRepository) {
        this.chatClient = chatClient;
        this.checkInRepository = repo;
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }

    public String generateDailyRecommendation(User user) {

        var lastCheckin = checkInRepository
                .findTopByUser_IdOrderByDateDesc(user.getId())
                .orElse(null);

        if (lastCheckin == null) {
            return "Ainda não encontrei check-ins seus. Faça seu primeiro check-in para gerar uma recomendação personalizada.";
        }

        String prompt = """
        Você é um assistente especializado em bem-estar corporativo. Gere uma recomendação curta, clara e motivadora baseada nos dados:
        
        Humor: %d
        Energia: %d
        Sono: %d
        Foco: %d
        Carga de Trabalho: %d
        Score: %d
        
        INSTRUÇÕES OBRIGATÓRIAS:
        - Responda em texto corrido, com 3 a 5 frases.
        - Não use tabelas, listas, tópicos, negrito, títulos ou markdown.
        - Não repita os valores numéricos informados (como 7/10 ou 8/10).
        - Mantenha tom profissional, simples e acolhedor.
        - Dê apenas UMA recomendação prática no final.
        - Retorne somente o texto final, nada além disso.
        
        Agora gere a resposta.
        """.formatted(
                lastCheckin.getMood(),
                lastCheckin.getEnergy(),
                lastCheckin.getSleep(),
                lastCheckin.getFocus(),
                lastCheckin.getHoursWorked(),
                lastCheckin.getScore()
        );

        return chatClient
                .prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder().model("openai/gpt-oss-20b").build())
                .call()
                .content();

    }

    public String generateRecommendationFromScore(Integer score) {

        String prompt = """
        Gere uma recomendação curta, clara e motivadora baseada no score de bem-estar informado:
        
        Score: %d
        
        INSTRUÇÕES:
        - Responda em 3 a 5 frases.
        - Texto corrido.
        - Não use títulos, tópicos, listas ou markdown.
        - Não repita o valor numérico do score.
        - Dê apenas uma recomendação prática no final.
        
        Agora gere a resposta.
        """.formatted(score);

        return chatClient
                .prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder()
                        .model("openai/gpt-oss-20b")
                        .build())
                .call()
                .content();
    }

    @Transactional
    public void saveRecommendation(Long userId, Long checkInId, Integer score, String text) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RecommendationModel model = new RecommendationModel();
        model.setUser(user);
        model.setCheckInId(checkInId);
        model.setScore(score);
        model.setText(text);

        recommendationRepository.save(model);
    }

}
