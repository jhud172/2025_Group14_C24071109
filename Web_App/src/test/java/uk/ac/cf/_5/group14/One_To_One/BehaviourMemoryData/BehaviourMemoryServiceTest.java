package uk.ac.cf._5.group14.One_To_One.BehaviourMemoryData;

import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletion;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionRepository;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BehaviourMemoryServiceTest {

    @Test
    void shouldComputeAndPersistAggregatePatterns() {
        BehaviourMemoryRepository memoryRepo = mock(BehaviourMemoryRepository.class);
        DailyCompletionRepository completionRepo = mock(DailyCompletionRepository.class);
        CalendarTaskRepository taskRepo = mock(CalendarTaskRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        BehaviourMemoryService service = new BehaviourMemoryService(memoryRepo, completionRepo, taskRepo, entityManager);

        User user = new User();
        user.setId(7L);

        LocalDate asOf = LocalDate.of(2026, 1, 15);
        LocalDate d1 = LocalDate.of(2026, 1, 12);
        LocalDate d2 = LocalDate.of(2026, 1, 13);
        LocalDate d4 = LocalDate.of(2026, 1, 15);

        DailyCompletion c1 = new DailyCompletion();
        c1.setUser(user);
        c1.setDate(d1);
        c1.setCompletionStatus(DailyCompletionStatus.GREEN);
        c1.setCompletionPercentage(100);

        DailyCompletion c2 = new DailyCompletion();
        c2.setUser(user);
        c2.setDate(d2);
        c2.setCompletionStatus(DailyCompletionStatus.ORANGE);
        c2.setCompletionPercentage(50);

        DailyCompletion c4 = new DailyCompletion();
        c4.setUser(user);
        c4.setDate(d4);
        c4.setCompletionStatus(DailyCompletionStatus.RED);
        c4.setCompletionPercentage(0);

        when(completionRepo.findByUserAndDateBetween(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(c1, c2, c4));

        CalendarTask t1 = new CalendarTask();
        t1.setDate(d2);
        CalendarTask t2 = new CalendarTask();
        t2.setDate(d2);
        CalendarTask t3 = new CalendarTask();
        t3.setDate(d2);
        CalendarTask t4 = new CalendarTask();
        t4.setDate(d2);
        CalendarTask t5 = new CalendarTask();
        t5.setDate(d2);
        CalendarTask t6 = new CalendarTask();
        t6.setDate(d2);

        CalendarTask t7 = new CalendarTask();
        t7.setDate(LocalDate.of(2026, 1, 14));
        CalendarTask t8 = new CalendarTask();
        t8.setDate(LocalDate.of(2026, 1, 14));

        when(taskRepo.findByUserAndDateBetween(eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(t1, t2, t3, t4, t5, t6, t7, t8));

        when(memoryRepo.findById(eq(user.getId()))).thenReturn(Optional.empty());
        when(memoryRepo.save(any(BehaviourMemory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(entityManager.getReference(eq(User.class), eq(user.getId()))).thenReturn(user);

        BehaviourMemory memory = service.getOrUpdateMemory(user, asOf, 4);

        assertThat(memory.getAsOfDate()).isEqualTo(asOf);
        assertThat(memory.getWindowDays()).isEqualTo(4);

        assertThat(memory.getGreenDays()).isEqualTo(1);
        assertThat(memory.getOrangeDays()).isEqualTo(1);
        assertThat(memory.getRedDays()).isEqualTo(1);
        assertThat(memory.getGreyDays()).isEqualTo(1);

        assertThat(memory.getAvgCompletionPercentage()).isEqualTo(38);
        assertThat(memory.getAvgTasksPerDay()).isEqualTo(2.0);
        assertThat(memory.getHighLoadDays()).isEqualTo(1);
        assertThat(memory.getTimePressureScore()).isEqualTo(16);
    }

    @Test
    void shouldEnforceAiCooldown() {
        BehaviourMemoryRepository memoryRepo = mock(BehaviourMemoryRepository.class);
        DailyCompletionRepository completionRepo = mock(DailyCompletionRepository.class);
        CalendarTaskRepository taskRepo = mock(CalendarTaskRepository.class);
        EntityManager entityManager = mock(EntityManager.class);

        BehaviourMemoryService service = new BehaviourMemoryService(memoryRepo, completionRepo, taskRepo, entityManager);

        User user = new User();
        user.setId(9L);

        LocalDate asOf = LocalDate.of(2026, 1, 15);
        Instant now = Instant.parse("2026-01-15T10:00:00Z");

        BehaviourMemory existing = new BehaviourMemory();
        existing.setUser(user);
        existing.setAsOfDate(asOf);
        existing.setWindowDays(BehaviourMemoryService.DEFAULT_WINDOW_DAYS);
        existing.setLastAiReferenceAt(now.minusSeconds(60));

        when(memoryRepo.findById(eq(user.getId()))).thenReturn(Optional.of(existing));

        Optional<String> denied = service.maybeGetAiContext(user, asOf, now);
        assertThat(denied).isEmpty();
        verify(memoryRepo, never()).save(any());

        Instant later = now.plus(BehaviourMemoryService.AI_REFERENCE_COOLDOWN).plusSeconds(1);
        when(memoryRepo.save(any(BehaviourMemory.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<String> allowed = service.maybeGetAiContext(user, asOf, later);
        assertThat(allowed).isPresent();
        verify(memoryRepo, times(1)).save(any(BehaviourMemory.class));
    }
}
