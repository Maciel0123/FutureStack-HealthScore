package futureStack.futureStack.recommendation;

import futureStack.futureStack.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Long checkInId;

    private LocalDate date;

    private int score;

    @Column(columnDefinition = "TEXT")
    private String text;

    @PrePersist
    public void setDateIfNull() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
}
