package futureStack.futureStack.recommendation;

import futureStack.futureStack.checkIn.CheckInModel;
import futureStack.futureStack.checkIn.CheckInRepository;
import futureStack.futureStack.users.User;
import futureStack.futureStack.users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecommendationService {

    private final ChatClient chatClient;
    private final CheckInRepository checkInRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public RecommendationService(ChatClient chatClient, CheckInRepository repo, RecommendationRepository recommendationRepository, UserRepository userRepository, MessageSource messageSource) {
        this.chatClient = chatClient;
        this.checkInRepository = repo;
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public String generateDailyRecommendation(User user) {

        var lastCheckin = checkInRepository
                .findTopByUser_IdOrderByDateDesc(user.getId())
                .orElse(null);

        if (lastCheckin == null) {
            return messageSource.getMessage(
                    "recommendation.noCheckins",
                    null,
                    LocaleContextHolder.getLocale()
            );
        }

        String prompt = messageSource.getMessage(
                "ai.dailyPrompt",
                new Object[]{
                        lastCheckin.getMood(),
                        lastCheckin.getEnergy(),
                        lastCheckin.getSleep(),
                        lastCheckin.getFocus(),
                        lastCheckin.getHoursWorked(),
                        lastCheckin.getScore()
                },
                LocaleContextHolder.getLocale()
        );

        return chatClient
                .prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder().model("openai/gpt-oss-20b").build())
                .call()
                .content();

    }

    public String generateRecommendationFromScore(Integer score) {

        String prompt = messageSource.getMessage(
                "ai.scorePrompt",
                new Object[]{ score },
                LocaleContextHolder.getLocale()
        );

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

    public String generateMonthlySummary(User user) {

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        List<CheckInModel> list =
                checkInRepository.findByUser_IdAndDateAfter(user.getId(), thirtyDaysAgo);

        if (list.isEmpty()) {
            return messageSource.getMessage("monthly.noData", null, LocaleContextHolder.getLocale());
        }

        StringBuilder data = new StringBuilder();
        for (CheckInModel c : list) {
            data.append("""
                Dia %s:
                humor=%d, energia=%d, sono=%d, foco=%d, carga=%d, score=%d

                """.formatted(
                    c.getDate(),
                    c.getMood(),
                    c.getEnergy(),
                    c.getSleep(),
                    c.getFocus(),
                    c.getHoursWorked(),
                    c.getScore()
            ));
        }

        String prompt = messageSource.getMessage(
                "ai.monthlyPrompt",
                new Object[]{data.toString()},
                LocaleContextHolder.getLocale()
        );

        return chatClient
                .prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder()
                        .model("openai/gpt-oss-20b")
                        .build())
                .call()
                .content();
    }

}
