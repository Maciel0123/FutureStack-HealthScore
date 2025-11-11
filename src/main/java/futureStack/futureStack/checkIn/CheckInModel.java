package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "check_ins")
public class CheckInModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDate date;

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

    private int score;

    @PrePersist
    public void calculateScore() {
        WScoreCalculator calculator = new WScoreCalculator();
        this.score = calculator.calculateScore(mood, energy, sleep, focus, hoursWorked);
    }
}
