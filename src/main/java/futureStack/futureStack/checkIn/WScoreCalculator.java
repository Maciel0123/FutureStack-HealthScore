package futureStack.futureStack.checkIn;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WScoreCalculator {

    private final MessageSource messageSource;

    public WScoreCalculator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public int calculateScore(int mood, int energy, int sleep, int focus, int hoursWorked) {
        double moodWeight = 0.25;
        double energyWeight = 0.20;
        double sleepWeight = 0.20;
        double focusWeight = 0.20;
        double workloadWeight = 0.15;

        double moodScore = (mood / 10.0) * 1000 * moodWeight;
        double energyScore = (energy / 10.0) * 1000 * energyWeight;
        double focusScore = (focus / 10.0) * 1000 * focusWeight;
        double sleepScore = calculateSleepScore(sleep) * 1000 * sleepWeight;
        double workloadScore = calculateWorkloadScore(hoursWorked) * 1000 * workloadWeight;

        int totalScore = (int) Math.round(moodScore + energyScore + sleepScore + focusScore + workloadScore);
        return Math.min(1000, Math.max(0, totalScore));
    }

    private double calculateSleepScore(int sleepHours) {
        if (sleepHours >= 7 && sleepHours <= 9) return 1.0;
        if (sleepHours == 6 || sleepHours == 10) return 0.8;
        if (sleepHours == 5 || sleepHours == 11) return 0.6;
        if (sleepHours == 4 || sleepHours == 12) return 0.4;
        return 0.2;
    }

    private double calculateWorkloadScore(int hoursWorked) {
        if (hoursWorked >= 6 && hoursWorked <= 8) return 1.0;
        if (hoursWorked == 5 || hoursWorked == 9) return 0.8;
        if (hoursWorked == 4 || hoursWorked == 10) return 0.6;
        if (hoursWorked == 3 || hoursWorked == 11) return 0.4;
        return 0.2;
    }

    public String getScoreMessage(int score) {
        String key;

        if (score >= 800) key = "score.excellent";
        else if (score >= 600) key = "score.good";
        else if (score >= 400) key = "score.attention";
        else if (score >= 200) key = "score.warning";
        else key = "score.critical";

        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
