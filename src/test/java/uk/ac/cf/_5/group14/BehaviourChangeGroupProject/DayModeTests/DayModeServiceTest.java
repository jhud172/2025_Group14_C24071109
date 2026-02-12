package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayModeTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayMode.DayMode;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.DayMode.DayModeService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutScheduleService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Service.WorkoutSessionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSchedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.WorkoutSession;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DayModeServiceTest {

    @Mock
    private WorkoutScheduleService workoutScheduleService;

    @Mock
    private WorkoutSessionService workoutSessionService;

    @InjectMocks
    private DayModeService dayModeService;

    @Test
    void trainingDayWhenScheduleExists() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        LocalDate date = LocalDate.of(2026, 2, 6);

        when(workoutScheduleService.findByUserAndDayOfWeek(user, date.getDayOfWeek().getValue()))
                .thenReturn(List.of(new WorkoutSchedule()));
        when(workoutSessionService.findByUserAndDate(user, date)).thenReturn(List.of());

        assertThat(dayModeService.determine(user, date)).isEqualTo(DayMode.TRAINING_DAY);
    }

    @Test
    void trainingDayWhenWorkoutCompleted() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        LocalDate date = LocalDate.of(2026, 2, 6);

        WorkoutSession completed = new WorkoutSession();
        completed.setCompleted(true);

        when(workoutScheduleService.findByUserAndDayOfWeek(user, date.getDayOfWeek().getValue()))
                .thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(user, date)).thenReturn(List.of(completed));

        assertThat(dayModeService.determine(user, date)).isEqualTo(DayMode.TRAINING_DAY);
    }

    @Test
    void restDayWhenNoScheduleOrCompletedWorkout() {
        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        LocalDate date = LocalDate.of(2026, 2, 6);

        when(workoutScheduleService.findByUserAndDayOfWeek(user, date.getDayOfWeek().getValue()))
                .thenReturn(List.of());
        when(workoutSessionService.findByUserAndDate(user, date)).thenReturn(List.of());

        assertThat(dayModeService.determine(user, date)).isEqualTo(DayMode.REST_DAY);
    }
}
