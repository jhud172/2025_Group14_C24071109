package uk.ac.cf._5.group14.One_To_One.WorkoutsTests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationService;
import uk.ac.cf._5.group14.One_To_One.Notifications.NotificationType;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutPerformanceService;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetLog;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetLogRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorkoutPerformanceServiceTest {

    @Test
    void notifiesWhenPrHit() {
        WorkoutSetLogRepository repository = Mockito.mock(WorkoutSetLogRepository.class);
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        Clock clock = Clock.fixed(Instant.parse("2026-02-06T10:00:00Z"), ZoneOffset.UTC);
        WorkoutPerformanceService service = new WorkoutPerformanceService(repository, notificationService, clock);

        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        user.setId(1L);

        WorkoutSession session = new WorkoutSession();
        session.setId(42L);
        session.setStartedAt(LocalDateTime.of(2026, 2, 6, 9, 0));

        WorkoutSetLog log = new WorkoutSetLog();
        log.setExerciseName("Bench Press");
        log.setWeight(100.0);
        log.setReps(8);
        log.setCompleted(true);

        when(repository.findBestWeightBefore(user, "Bench Press", session.getStartedAt())).thenReturn(90.0);
        when(repository.findBestRepsAtWeightBefore(user, "Bench Press", 100.0, session.getStartedAt())).thenReturn(7);
        when(repository.findBestVolumeBefore(user, "Bench Press", session.getStartedAt())).thenReturn(700.0);
        when(notificationService.existsRecent(eq(user), eq(NotificationType.PR_HIT), any(), any())).thenReturn(false);

        service.maybeNotifyPr(user, session, log, true);

        verify(notificationService).create(eq(user), eq(NotificationType.PR_HIT), eq("Personal best"), contains("Bench Press"), eq("/workouts/studio/42"));
    }

    @Test
    void skipsNotificationWhenNoPr() {
        WorkoutSetLogRepository repository = Mockito.mock(WorkoutSetLogRepository.class);
        NotificationService notificationService = Mockito.mock(NotificationService.class);
        Clock clock = Clock.fixed(Instant.parse("2026-02-06T10:00:00Z"), ZoneOffset.UTC);
        WorkoutPerformanceService service = new WorkoutPerformanceService(repository, notificationService, clock);

        User user = new User("test@example.com", "Test", "User", "testuser", "password123");
        user.setId(1L);

        WorkoutSession session = new WorkoutSession();
        session.setId(42L);
        session.setStartedAt(LocalDateTime.of(2026, 2, 6, 9, 0));

        WorkoutSetLog log = new WorkoutSetLog();
        log.setExerciseName("Bench Press");
        log.setWeight(80.0);
        log.setReps(6);
        log.setCompleted(true);

        when(repository.findBestWeightBefore(user, "Bench Press", session.getStartedAt())).thenReturn(100.0);
        when(repository.findBestRepsAtWeightBefore(user, "Bench Press", 80.0, session.getStartedAt())).thenReturn(8);
        when(repository.findBestVolumeBefore(user, "Bench Press", session.getStartedAt())).thenReturn(900.0);

        service.maybeNotifyPr(user, session, log, true);

        verify(notificationService, never()).create(eq(user), eq(NotificationType.PR_HIT), any(), any(), any());
    }
}
