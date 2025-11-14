package futureStack.futureStack.checkIn;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequestDTO {

    @Min(0) @Max(10)
    private int mood;

    @Min(0) @Max(10)
    private int energy;

    @Min(0) @Max(24)
    private int sleep;

    @Min(0) @Max(10)
    private int focus;

    @Min(0) @Max(16)
    private int hoursWorked;
}
