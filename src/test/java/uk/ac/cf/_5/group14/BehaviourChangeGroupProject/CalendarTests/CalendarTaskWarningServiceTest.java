package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarning;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskWarningTriggerType;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalendarTaskWarningServiceTest {

    @Mock
    private CalendarTaskWarningRepository warningRepository;

    @Test
    void addTimeWarning_ShouldReturnNull_WhenInputsInvalid() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        assertNull(service.addTimeWarning(null, LocalTime.NOON));
        assertNull(service.addTimeWarning(new CalendarTask(), LocalTime.NOON));
        assertNull(service.addTimeWarning(taskWithId(1L), null));

        verify(warningRepository, never()).save(any());
    }

    @Test
    void addTimeWarning_ShouldSaveWarning_WhenValid() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        CalendarTask task = taskWithId(1L);
        LocalTime time = LocalTime.of(10, 0);

        when(warningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CalendarTaskWarning saved = service.addTimeWarning(task, time);
        assertNotNull(saved);
        assertEquals(CalendarTaskWarningTriggerType.TIME, saved.getTriggerType());
        assertEquals(time, saved.getTriggerTime());
        assertEquals(task, saved.getTask());

        verify(warningRepository, times(1)).save(any());
    }

    @Test
    void addOnTaskCompleteWarning_ShouldValidateAndSave() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        User user = new User();
        user.setId(7L);

        CalendarTask task = taskWithId(1L);
        task.setUser(user);

        CalendarTask triggerTask = taskWithId(2L);
        triggerTask.setUser(user);

        when(warningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CalendarTaskWarning saved = service.addOnTaskCompleteWarning(task, triggerTask);
        assertNotNull(saved);
        assertEquals(CalendarTaskWarningTriggerType.ON_TASK_COMPLETE, saved.getTriggerType());
        assertEquals(task, saved.getTask());
        assertEquals(triggerTask, saved.getTriggerTask());

        assertNull(service.addOnTaskCompleteWarning(task, task));
        verify(warningRepository, times(1)).save(any());
    }

    @Test
    void onTaskCompleted_ShouldMarkWarningsTriggeredAndPersist() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        CalendarTask completedTask = taskWithId(99L);
        Instant now = Instant.now();

        CalendarTaskWarning w1 = new CalendarTaskWarning();
        w1.setTriggerType(CalendarTaskWarningTriggerType.ON_TASK_COMPLETE);
        w1.setTriggeredAt(null);

        CalendarTaskWarning w2 = new CalendarTaskWarning();
        w2.setTriggerType(CalendarTaskWarningTriggerType.ON_TASK_COMPLETE);
        w2.setTriggeredAt(null);

        when(warningRepository.findByTriggerTaskIdAndTriggeredAtIsNull(99L)).thenReturn(List.of(w1, w2));

        service.onTaskCompleted(completedTask, now);

        assertEquals(now, w1.getTriggeredAt());
        assertEquals(now, w2.getTriggeredAt());
        verify(warningRepository, times(1)).saveAll(anyList());
    }

    @Test
    void applyWarningStates_ShouldAutoTriggerTimeWarning_AndSetInGrace() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        CalendarTask task = taskWithId(1L);
        task.setCompleted(false);
        task.setGracePeriodMinutes(10);
        task.setDate(LocalDate.of(2023, 9, 25));

        CalendarTaskWarning warning = new CalendarTaskWarning();
        warning.setTask(task);
        warning.setTriggerType(CalendarTaskWarningTriggerType.TIME);
        warning.setTriggerTime(LocalTime.of(10, 0));
        warning.setTriggeredAt(null);

        when(warningRepository.findByTaskIdIn(List.of(1L))).thenReturn(List.of(warning));

        Instant scheduled = LocalDateTime.of(task.getDate(), warning.getTriggerTime())
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Instant now = scheduled.plusSeconds(5 * 60);

        service.applyWarningStates(List.of(task), now);

        assertTrue(task.isInGrace());
        assertFalse(task.isLate());
        assertEquals(scheduled, warning.getTriggeredAt());

        ArgumentCaptor<List<CalendarTaskWarning>> captor = ArgumentCaptor.forClass(List.class);
        verify(warningRepository, times(1)).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    void applyWarningStates_ShouldSetLate_AfterGraceEnds() {
        CalendarTaskWarningService service = new CalendarTaskWarningService(warningRepository);

        CalendarTask task = taskWithId(1L);
        task.setCompleted(false);
        task.setGracePeriodMinutes(10);
        task.setDate(LocalDate.of(2023, 9, 25));

        CalendarTaskWarning warning = new CalendarTaskWarning();
        warning.setTask(task);
        warning.setTriggerType(CalendarTaskWarningTriggerType.TIME);
        warning.setTriggerTime(LocalTime.of(10, 0));
        warning.setTriggeredAt(null);

        when(warningRepository.findByTaskIdIn(List.of(1L))).thenReturn(List.of(warning));

        Instant scheduled = LocalDateTime.of(task.getDate(), warning.getTriggerTime())
                .atZone(ZoneId.systemDefault())
                .toInstant();
        Instant now = scheduled.plusSeconds(15 * 60);

        service.applyWarningStates(List.of(task), now);

        assertFalse(task.isInGrace());
        assertTrue(task.isLate());
        assertEquals(scheduled, warning.getTriggeredAt());
    }

    private static CalendarTask taskWithId(Long id) {
        CalendarTask task = new CalendarTask();
        task.setId(id);
        return task;
    }
}
