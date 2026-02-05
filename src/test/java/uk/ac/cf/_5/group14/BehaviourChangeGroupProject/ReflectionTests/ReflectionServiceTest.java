package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionTests;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.BehaviourMemoryData.BehaviourMemoryService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionAiService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionResult;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ReflectionData.ReflectionService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReflectionServiceTest {

    @Test
    void shouldFallbackWhenAiReturnsNull() {
        ReflectionAiService ai = mock(ReflectionAiService.class);
        when(ai.generateReflection(any(), anyString(), anyString(), any())).thenReturn(null);

        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);
        when(behaviourMemoryService.maybeGetAiContext(any(User.class))).thenReturn(Optional.empty());

        ReflectionService service = new ReflectionService(ai, behaviourMemoryService);

        User user = new User();
        user.setId(1L);

        CalendarTask t = new CalendarTask();
        t.setTitle("Test");
        t.setCompleted(true);

        ReflectionResult result = service.generate(
            user,
                LocalDate.of(2026, 1, 15),
                "General",
                List.of(t),
                0,
                0,
                "Felt good",
                ""
        );

        assertNotNull(result);
        assertNotNull(result.performanceSummary());
        assertFalse(result.performanceSummary().isBlank());
        assertNotNull(result.improvementSuggestions());
        assertFalse(result.improvementSuggestions().isBlank());
    }

    @Test
    void shouldReturnAiResultWhenAvailable() {
        ReflectionAiService ai = mock(ReflectionAiService.class);
        when(ai.generateReflection(any(), anyString(), anyString(), any()))
                .thenReturn(new ReflectionResult("Summary", "Suggestions"));

        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);
        when(behaviourMemoryService.maybeGetAiContext(any(User.class))).thenReturn(Optional.empty());

        ReflectionService service = new ReflectionService(ai, behaviourMemoryService);

        User user = new User();
        user.setId(2L);

        ReflectionResult result = service.generate(
            user,
                LocalDate.of(2026, 1, 15),
                null,
                List.of(),
                0,
                0,
                "Reflection",
                "Notes"
        );

        assertEquals("Summary", result.performanceSummary());
        assertEquals("Suggestions", result.improvementSuggestions());
    }

    @Test
    void shouldAppendBehaviourMemoryToDayDataWhenAvailable() {
        ReflectionAiService ai = mock(ReflectionAiService.class);
        when(ai.generateReflection(any(), anyString(), anyString(), any()))
                .thenReturn(new ReflectionResult("Summary", "Suggestions"));

        BehaviourMemoryService behaviourMemoryService = mock(BehaviourMemoryService.class);
        when(behaviourMemoryService.maybeGetAiContext(any(User.class)))
                .thenReturn(Optional.of("Behaviour memory (last 14 days, aggregates):\n- Success rate: avg 50% completion\nUse these only as gentle context."));

        ReflectionService service = new ReflectionService(ai, behaviourMemoryService);

        User user = new User();
        user.setId(3L);

        service.generate(
                user,
                LocalDate.of(2026, 1, 15),
                "Focus",
                List.of(),
                0,
                0,
                "Reflection",
                "Notes"
        );

        ArgumentCaptor<String> dayDataCaptor = ArgumentCaptor.forClass(String.class);
        verify(ai).generateReflection(any(), dayDataCaptor.capture(), anyString(), any());
        assertTrue(dayDataCaptor.getValue().contains("Behaviour memory"));
    }
}
