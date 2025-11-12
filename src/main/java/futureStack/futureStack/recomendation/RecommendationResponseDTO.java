package futureStack.futureStack.recomendation;

public record RecommendationResponseDTO(
        String message,
        int score,
        String date
) {}