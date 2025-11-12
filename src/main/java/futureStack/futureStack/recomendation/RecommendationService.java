package futureStack.futureStack.recomendation;

import futureStack.futureStack.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationRepository repository;

    public String getRecommendation(int score) {
        if (score >= 800) return "Continue assim! Você está com um ótimo equilíbrio.";
        if (score >= 600) return "Muito bom! Que tal uma pausa curta hoje?";
        if (score >= 400) return "Cuidado com a rotina intensa, tente dormir mais cedo.";
        if (score >= 200) return "Seu corpo está pedindo descanso. Faça uma caminhada leve.";
        return "Situação crítica! Converse com seu gestor e busque apoio profissional.";
    }

    public RecommendationModel saveRecommendation(User user, int score, String message) {
        RecommendationModel recommendation = RecommendationModel.builder()
                .user(user)
                .score(score)
                .message(message)
                .build();

        return repository.save(recommendation);
    }
}
