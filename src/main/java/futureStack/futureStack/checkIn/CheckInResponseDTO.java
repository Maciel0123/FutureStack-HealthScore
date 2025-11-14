package futureStack.futureStack.checkIn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponseDTO {
    public Long id;
    public String date;
    public int mood;
    public int energy;
    public int sleep;
    public int focus;
    public int hoursWorked;
    public int score;
    public String message;
}
