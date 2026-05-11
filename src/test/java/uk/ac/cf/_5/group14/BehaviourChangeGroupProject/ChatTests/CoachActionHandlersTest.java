package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ApplyScheduleActionHandler;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ApplyScheduleActionPayload;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.CreateTaskActionHandler;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.CreateTaskActionPayload;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.CoachActionExecution;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.Schedule;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleAppliedRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachActionHandlersTest {

    @Mock
    private CalendarTaskService calendarTaskService;

    @InjectMocks
    private CreateTaskActionHandler createTaskActionHandler;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleOccurrenceService scheduleOccurrenceService;

    @Mock
    private ScheduleAppliedRepository scheduleAppliedRepository;

    @Mock
    private TrainerClientLinkRepository trainerClientLinkRepository;

    @InjectMocks
    private ApplyScheduleActionHandler applyScheduleActionHandler;

    @Test
    void createTaskValidationRejectsMissingTitle() {
        User user = new User();
        user.setId(5L);
        CreateTaskActionPayload payload = new CreateTaskActionPayload(LocalDate.now(), null, " ", null, null);

        List<String> errors = createTaskActionHandler.validate(payload, user);

        assertFalse(errors.isEmpty());
        verifyNoInteractions(calendarTaskService);
    }

    @Test
    void applyScheduleRejectsUnauthorizedTrainerSchedule() {
        User user = new User();
        user.setId(1L);
        User trainer = new User();
        trainer.setId(2L);

        Schedule trainerSchedule = new Schedule();
        trainerSchedule.setId(10L);
        trainerSchedule.setName("Pro Plan");
        trainerSchedule.setUser(trainer);

        when(scheduleRepository.findByUserAndNameIgnoreCase(eq(user), anyString())).thenReturn(Optional.empty());
        when(trainerClientLinkRepository.findFirstByClientUserIdAndStatusOrderByUpdatedAtDesc(1L, TrainerClientLinkStatus.ACTIVE))
                .thenReturn(Optional.empty());

        ApplyScheduleActionPayload payload = new ApplyScheduleActionPayload("Pro Plan", LocalDate.now(), 4);
        CoachActionExecution execution = applyScheduleActionHandler.execute(payload, user);

        assertFalse(execution.success());
        verify(scheduleOccurrenceService, never()).generateOccurrencesForSchedule(any(), any(), any(), any(), anyInt());
        verify(scheduleAppliedRepository, never()).save(any());
    }
}
