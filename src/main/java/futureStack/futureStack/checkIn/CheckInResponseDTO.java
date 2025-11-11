package futureStack.futureStack.checkIn;

public record CheckInResponseDTO(
        Long id,
        String date,
        int mood,
        int energy,
        int sleep,
        int focus,
        int hoursWorked,
        int score,
        String message
) {}
