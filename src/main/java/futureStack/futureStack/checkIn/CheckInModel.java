package futureStack.futureStack.checkIn;

import futureStack.futureStack.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "check_ins")
public class CheckInModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private LocalDate date;

    @Min(0) @Max(10)
    @NotNull
    private int mood;

    @Min(0) @Max(10)
    @NotNull
    private int energy;

    @Min(0) @Max(24)
    @NotNull
    private int sleep;

    @Min(0) @Max(10)
    @NotNull
    private int focus;

    @Min(0) @Max(16)
    @NotNull
    private int hoursWorked;

    private int score;
}
