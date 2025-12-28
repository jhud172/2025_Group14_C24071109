package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleEntry;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleEntryRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleEntryServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleEntryServiceImplTest {

    @Mock
    private ScheduleEntryRepository scheduleEntryRepository;

    @InjectMocks
    private ScheduleEntryServiceImpl scheduleEntryService;

    @Test
    void save_ShouldCallRepository() {
        ScheduleEntry entry = new ScheduleEntry();
        scheduleEntryService.save(entry);
        verify(scheduleEntryRepository).save(entry);
    }

    @Test
    void getEntries_ShouldReturnList() {
        Long scheduleId = 1L;
        ScheduleEntry e1 = new ScheduleEntry();
        ScheduleEntry e2 = new ScheduleEntry();
        List<ScheduleEntry> list = Arrays.asList(e1, e2);
        when(scheduleEntryRepository.findByScheduleIdOrderByOrderNumber(scheduleId)).thenReturn(list);
        List<ScheduleEntry> result = scheduleEntryService.getEntries(scheduleId);
        assertEquals(list, result);
    }

    @Test
    void getEntriesBySchedule_ShouldReturnEmptyList_WhenScheduleIsNull() {
        List<ScheduleEntry> result = scheduleEntryService.getEntriesBySchedule(null);
        assertTrue(result.isEmpty());
        verify(scheduleEntryRepository, never()).findBySchedule(any());
    }

    @Test
    void getEntriesBySchedule_ShouldReturnList_WhenScheduleNotNull() {
        Schedule schedule = new Schedule();
        ScheduleEntry e1 = new ScheduleEntry();
        List<ScheduleEntry> list = Collections.singletonList(e1);
        when(scheduleEntryRepository.findBySchedule(schedule)).thenReturn(list);
        List<ScheduleEntry> result = scheduleEntryService.getEntriesBySchedule(schedule);
        assertEquals(list, result);
    }
}
