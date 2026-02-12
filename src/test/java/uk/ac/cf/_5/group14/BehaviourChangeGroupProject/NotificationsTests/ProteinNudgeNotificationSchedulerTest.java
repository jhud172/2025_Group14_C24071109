package uk.ac.cf._5.group14.BehaviourChangeGroupProject.NotificationsTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationType;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.ProteinNudgeNotificationScheduler;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.DailyNutritionLog;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.DailyNutritionLogRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition.ProteinTargetService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.WorkoutSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProteinNudgeNotificationSchedulerTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private DailyNutritionLogRepository dailyNutritionLogRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProteinTargetService proteinTargetService;

    @Test
    void sendsNudgeWhenNoNutritionLog() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.of(2026, 2, 6);
        Clock clock = Clock.fixed(today.atStartOfDay(zone).toInstant(), zone);

        User user = new User();
        user.setId(10L);

        when(workoutSessionRepository.findDistinctUsersWithCompletedWorkouts(today)).thenReturn(List.of(user));
        when(notificationService.existsRecentByType(eq(user), eq(NotificationType.PROTEIN_NUDGE), any()))
                .thenReturn(false);
        when(dailyNutritionLogRepository.findByUserAndDate(user, today)).thenReturn(Optional.empty());
        when(proteinTargetService.resolveTargetGrams(user)).thenReturn(160);

        ProteinNudgeNotificationScheduler scheduler = new ProteinNudgeNotificationScheduler(
                workoutSessionRepository,
                dailyNutritionLogRepository,
                notificationService,
                proteinTargetService,
                clock
        );

        scheduler.sendProteinNudges();

        verify(notificationService).create(
                eq(user),
                eq(NotificationType.PROTEIN_NUDGE),
                anyString(),
                anyString(),
                eq("/nutrition?date=2026-02-06")
        );
    }

    @Test
    void skipsWhenProteinAtTarget() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.of(2026, 2, 6);
        Clock clock = Clock.fixed(today.atStartOfDay(zone).toInstant(), zone);

        User user = new User();
        user.setId(12L);

        DailyNutritionLog log = new DailyNutritionLog();
        log.setProteinGrams(180);

        when(workoutSessionRepository.findDistinctUsersWithCompletedWorkouts(today)).thenReturn(List.of(user));
        when(notificationService.existsRecentByType(eq(user), eq(NotificationType.PROTEIN_NUDGE), any()))
                .thenReturn(false);
        when(dailyNutritionLogRepository.findByUserAndDate(user, today)).thenReturn(Optional.of(log));
        when(proteinTargetService.resolveTargetGrams(user)).thenReturn(160);

        ProteinNudgeNotificationScheduler scheduler = new ProteinNudgeNotificationScheduler(
                workoutSessionRepository,
                dailyNutritionLogRepository,
                notificationService,
                proteinTargetService,
                clock
        );

        scheduler.sendProteinNudges();

        verify(notificationService, never()).create(
                eq(user),
                eq(NotificationType.PROTEIN_NUDGE),
                anyString(),
                anyString(),
                anyString()
        );
    }

    @Test
    void skipsWhenAlreadyNotifiedToday() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.of(2026, 2, 6);
        Clock clock = Clock.fixed(today.atStartOfDay(zone).toInstant(), zone);

        User user = new User();
        user.setId(14L);

        when(workoutSessionRepository.findDistinctUsersWithCompletedWorkouts(today)).thenReturn(List.of(user));
        when(notificationService.existsRecentByType(eq(user), eq(NotificationType.PROTEIN_NUDGE), any()))
                .thenReturn(true);

        ProteinNudgeNotificationScheduler scheduler = new ProteinNudgeNotificationScheduler(
                workoutSessionRepository,
                dailyNutritionLogRepository,
                notificationService,
                proteinTargetService,
                clock
        );

        scheduler.sendProteinNudges();

        verify(notificationService, never()).create(
                eq(user),
                eq(NotificationType.PROTEIN_NUDGE),
                anyString(),
                anyString(),
                anyString()
        );
    }
}
