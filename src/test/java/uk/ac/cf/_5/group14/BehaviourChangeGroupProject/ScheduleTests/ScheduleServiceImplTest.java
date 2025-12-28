package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Test
    void save_ShouldCallRepositoryAndReturnSavedSchedule() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        Schedule result = scheduleService.save(schedule);
        verify(scheduleRepository).save(schedule);
        assertEquals(schedule, result);
    }

    @Test
    void findById_ShouldReturnSchedule_WhenPresent() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(schedule));
        Schedule result = scheduleService.findById(1L);
        assertEquals(schedule, result);
    }

    @Test
    void findById_ShouldReturnNull_WhenNotPresent() {
        when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        Schedule result = scheduleService.findById(1L);
        assertNull(result);
    }

    @Test
    void findByUser_ShouldReturnListOfSchedulesForUser() {
        User user = new User();
        user.setId(1L);
        Schedule s1 = new Schedule();
        s1.setId(1L);
        s1.setUser(user);
        Schedule s2 = new Schedule();
        s2.setId(2L);
        s2.setUser(user);
        List<Schedule> schedules = Arrays.asList(s1, s2);
        when(scheduleRepository.findByUser(user)).thenReturn(schedules);
        List<Schedule> result = scheduleService.findByUser(user);
        assertEquals(schedules, result);
    }

    @Test
    void findByUser_ShouldReturnEmptyList_WhenRepositoryReturnsEmpty() {
        User user = new User();
        user.setId(1L);
        when(scheduleRepository.findByUser(user)).thenReturn(List.of());
        List<Schedule> result = scheduleService.findByUser(user);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}