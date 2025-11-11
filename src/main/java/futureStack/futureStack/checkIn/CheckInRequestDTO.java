package futureStack.futureStack.checkIn;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CheckInRequestDTO(
        @Min(0) @Max(10) int mood,
        @Min(0) @Max(10) int energy,
        @Min(0) @Max(24) int sleep,
        @Min(0) @Max(10) int focus,
        @Min(0) @Max(16) int hoursWorked
) {}

