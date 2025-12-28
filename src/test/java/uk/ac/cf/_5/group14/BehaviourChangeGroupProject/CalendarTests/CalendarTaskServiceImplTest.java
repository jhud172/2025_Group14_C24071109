package uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalendarTaskServiceImplTest {

    @Mock
    private CalendarTaskRepository repo;

    @InjectMocks
    private CalendarTaskServiceImpl calendarTaskService;

    @Test
    void createTask_ShouldSaveTaskWithCorrectAttributes() {
        User user = new User();
        user.setId(1L);
        LocalDate date = LocalDate.of(2023, 9, 25);
        LocalTime time = LocalTime.of(10, 0);
        String title = "Test";
        String notes = "Notes";
        boolean exercise = true;
        boolean completed = false;
        calendarTaskService.createTask(user, date, time, title, notes, exercise, completed);
        ArgumentCaptor<CalendarTask> captor = ArgumentCaptor.forClass(CalendarTask.class);
        verify(repo).save(captor.capture());
        CalendarTask saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals(date, saved.getDate());
        assertEquals(time, saved.getTime());
        assertEquals(title, saved.getTitle());
        assertEquals(notes, saved.getNotes());
        assertEquals(exercise, saved.getExercise());
        assertEquals(completed, saved.getCompleted());
    }

    @Test
    void getTasks_ShouldReturnTasksForDate() {
        User user = new User();
        LocalDate date = LocalDate.now();
        List<CalendarTask> list = Arrays.asList(new CalendarTask(), new CalendarTask());
        when(repo.findByUserAndDateOrderByTime(user, date)).thenReturn(list);
        List<CalendarTask> result = calendarTaskService.getTasks(user, date);
        assertEquals(list, result);
    }

    @Test
    void getTasksGroupedByDate_ShouldGroupTasksByDate() {
        User user = new User();
        CalendarTask task1 = new CalendarTask();
        task1.setDate(LocalDate.of(2023, 9, 25));
        CalendarTask task2 = new CalendarTask();
        task2.setDate(LocalDate.of(2023, 9, 25));
        CalendarTask task3 = new CalendarTask();
        task3.setDate(LocalDate.of(2023, 9, 26));
        when(repo.findByUserOrderByDateAscTimeAsc(user)).thenReturn(Arrays.asList(task1, task2, task3));
        Map<LocalDate, List<CalendarTask>> result = calendarTaskService.getTasksGroupedByDate(user);
        assertEquals(2, result.size());
        assertEquals(2, result.get(task1.getDate()).size());
        assertEquals(1, result.get(task3.getDate()).size());
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenUserMatches() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(id);
        User otherUser = user;
        task.setUser(otherUser);
        task.setTitle("Old");
        task.setTime(LocalTime.of(9, 0));
        task.setNotes("Old");
        task.setExercise(false);
        when(repo.findById(id)).thenReturn(Optional.of(task));
        calendarTaskService.updateTask(id, user, "New", "10:30", "NewNotes", true);
        verify(repo).save(task);
        assertEquals("New", task.getTitle());
        assertEquals(LocalTime.of(10, 30), task.getTime());
        assertEquals("NewNotes", task.getNotes());
        assertTrue(task.getExercise());
    }

    @Test
    void updateTask_ShouldHandleBlankTime() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(id);
        task.setUser(user);
        task.setTime(LocalTime.of(9, 0));
        when(repo.findById(id)).thenReturn(Optional.of(task));
        calendarTaskService.updateTask(id, user, "Title", "", "Notes", false);
        verify(repo).save(task);
        assertNull(task.getTime());
    }

    @Test
    void updateTask_ShouldHandleNullTime() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(id);
        task.setUser(user);
        task.setTime(LocalTime.of(9, 0));
        when(repo.findById(id)).thenReturn(Optional.of(task));
        calendarTaskService.updateTask(id, user, "Title", null, "Notes", false);
        verify(repo).save(task);
        assertNull(task.getTime());
    }

    @Test
    void updateTask_ShouldDoNothing_WhenUserDoesNotMatch() {
        Long id = 1L;
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(id);
        User otherUser = new User();
        otherUser.setId(2L);
        task.setUser(otherUser);
        when(repo.findById(id)).thenReturn(Optional.of(task));
        calendarTaskService.updateTask(id, user, "Title", "10:00", "Notes", true);
        verify(repo, never()).save(any());
    }

    @Test
    void updateTask_ShouldDoNothing_WhenTaskNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        calendarTaskService.updateTask(1L, new User(), "Title", "10:00", "Notes", true);
        verify(repo, never()).save(any());
    }

    @Test
    void toggleCompleted_ShouldToggleAndSave_WhenUserMatches() {
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(1L);
        task.setUser(user);
        task.setCompleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(task));
        calendarTaskService.toggleCompleted(1L, user);
        verify(repo).save(task);
        assertTrue(task.getCompleted());
        calendarTaskService.toggleCompleted(1L, user);
        assertFalse(task.getCompleted());
    }

    @Test
    void toggleCompleted_ShouldDoNothing_WhenUserDoesNotMatch() {
        User user = new User();
        user.setId(1L);
        CalendarTask task = new CalendarTask();
        task.setId(1L);
        User other = new User();
        other.setId(2L);
        task.setUser(other);
        task.setCompleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(task));
        calendarTaskService.toggleCompleted(1L, user);
        verify(repo, never()).save(any());
        assertFalse(task.getCompleted());
    }

    @Test
    void toggleCompleted_ShouldDoNothing_WhenTaskNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        calendarTaskService.toggleCompleted(1L, new User());
        verify(repo, never()).save(any());
    }

    @Test
    void getTasksByRange_ShouldGroupTasksByDate() {
        User user = new User();
        LocalDate start = LocalDate.of(2023, 9, 25);
        LocalDate end = LocalDate.of(2023, 9, 27);
        CalendarTask t1 = new CalendarTask();
        t1.setDate(start);
        CalendarTask t2 = new CalendarTask();
        t2.setDate(start.plusDays(1));
        when(repo.findByUserAndDateBetween(user, start, end)).thenReturn(Arrays.asList(t1, t2));
        Map<LocalDate, List<CalendarTask>> result = calendarTaskService.getTasksByRange(user, start, end);
        assertEquals(2, result.size());
        assertEquals(1, result.get(t1.getDate()).size());
        assertEquals(1, result.get(t2.getDate()).size());
    }
}