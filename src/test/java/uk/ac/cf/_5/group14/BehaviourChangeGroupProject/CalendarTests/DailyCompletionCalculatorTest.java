package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionCalculator;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionStatus;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DailyCompletionCalculatorTest {

    @Test
    void partialCompletionShouldBeOrange() {
        LocalDate date = LocalDate.of(2026, 1, 14);
        LocalDate today = LocalDate.of(2026, 1, 15);

        DailyCompletionStatus status = DailyCompletionCalculator.computeStatus(date, 1, 3, today);

        assertThat(status).isEqualTo(DailyCompletionStatus.ORANGE);
    }

    @Test
    void fullCompletionShouldBeGreen() {
        LocalDate date = LocalDate.of(2026, 1, 14);
        LocalDate today = LocalDate.of(2026, 1, 15);

        DailyCompletionStatus status = DailyCompletionCalculator.computeStatus(date, 3, 3, today);

        assertThat(status).isEqualTo(DailyCompletionStatus.GREEN);
    }

    @Test
    void pastDayWithNoCompletionShouldBeRed() {
        LocalDate date = LocalDate.of(2026, 1, 14);
        LocalDate today = LocalDate.of(2026, 1, 15);

        DailyCompletionStatus status = DailyCompletionCalculator.computeStatus(date, 0, 3, today);

        assertThat(status).isEqualTo(DailyCompletionStatus.RED);
    }

    @Test
    void todayWithNoCompletionShouldBeGrey() {
        LocalDate today = LocalDate.of(2026, 1, 15);

        DailyCompletionStatus status = DailyCompletionCalculator.computeStatus(today, 0, 3, today);

        assertThat(status).isEqualTo(DailyCompletionStatus.GREY);
    }

    @Test
    void completionPercentageShouldCombineTasksAndWorkouts() {
        assertThat(DailyCompletionCalculator.computeCompletionPercentage(1, 3, 0, 0)).isEqualTo(33);
        assertThat(DailyCompletionCalculator.computeCompletionPercentage(1, 3, 1, 1)).isEqualTo(50);
        assertThat(DailyCompletionCalculator.computeCompletionPercentage(0, 0, 0, 0)).isEqualTo(0);
    }
}
