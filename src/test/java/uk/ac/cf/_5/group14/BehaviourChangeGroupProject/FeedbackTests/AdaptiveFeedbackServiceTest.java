package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackTests;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData.AdaptiveFeedback;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData.AdaptiveFeedbackAiService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData.AdaptiveFeedbackRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData.AdaptiveFeedbackService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData.AdaptiveFeedbackTone;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdaptiveFeedbackServiceTest {

    @Test
    void shouldReturnExistingFeedbackWithoutCallingAiOrSaving() {
        AdaptiveFeedbackRepository repository = mock(AdaptiveFeedbackRepository.class);
        AdaptiveFeedbackAiService aiService = mock(AdaptiveFeedbackAiService.class);
        AdaptiveFeedbackService service = new AdaptiveFeedbackService(repository, aiService);

        User user = new User();
        user.setId(42L);

        LocalDate today = LocalDate.of(2026, 1, 15);

        AdaptiveFeedback existing = new AdaptiveFeedback(42L, today, "Existing feedback", AdaptiveFeedbackTone.ENCOURAGING.name(), "hash");
        when(repository.findByUserIdAndDate(42L, today)).thenReturn(Optional.of(existing));

        String result = service.getOrGenerateForTodayHome(user, today,
                0L, 0L, 0, 0,
                0, 0, 0,
                false);

        assertThat(result).isEqualTo("Existing feedback");
        verifyNoInteractions(aiService);
        verify(repository, never()).save(any(AdaptiveFeedback.class));
    }

    @Test
    void shouldAvoidRepeatingRecentFeedback() {
        AdaptiveFeedbackRepository repository = mock(AdaptiveFeedbackRepository.class);
        AdaptiveFeedbackAiService aiService = mock(AdaptiveFeedbackAiService.class);
        AdaptiveFeedbackService service = new AdaptiveFeedbackService(repository, aiService);

        User user = new User();
        user.setId(7L);

        LocalDate today = LocalDate.of(2026, 1, 15);

        when(repository.findByUserIdAndDate(7L, today)).thenReturn(Optional.empty());
        when(repository.findTop7ByUserIdOrderByDateDesc(7L))
                .thenReturn(List.of(new AdaptiveFeedback(7L, today.minusDays(1), "Same message", AdaptiveFeedbackTone.COACHING.name(), "h")));

        when(aiService.suggestFeedback(eq(today), any(), anyString(), anyList()))
                .thenReturn("Same message", "Different message");

        service.getOrGenerateForTodayHome(user, today,
                1L, 1L, 1, 1,
                2, 1, 50,
                true);

        ArgumentCaptor<AdaptiveFeedback> savedCaptor = ArgumentCaptor.forClass(AdaptiveFeedback.class);
        verify(repository).save(savedCaptor.capture());
        verify(aiService, times(2)).suggestFeedback(eq(today), any(), anyString(), anyList());

        assertThat(savedCaptor.getValue().getFeedbackText()).isEqualTo("Different message");
        assertThat(savedCaptor.getValue().getFeedbackHash()).isNotBlank();
    }

    @Test
    void shouldRejectNonSupportiveFeedbackAndTryAgain() {
        AdaptiveFeedbackRepository repository = mock(AdaptiveFeedbackRepository.class);
        AdaptiveFeedbackAiService aiService = mock(AdaptiveFeedbackAiService.class);
        AdaptiveFeedbackService service = new AdaptiveFeedbackService(repository, aiService);

        User user = new User();
        user.setId(99L);

        LocalDate today = LocalDate.of(2026, 1, 15);

        when(repository.findByUserIdAndDate(eq(99L), eq(today))).thenReturn(Optional.empty());
        when(repository.findTop7ByUserIdOrderByDateDesc(99L)).thenReturn(List.of());

        when(aiService.suggestFeedback(eq(today), any(), anyString(), anyList()))
                .thenReturn("You are lazy.", "You’ve got this — keep going.");

        service.getOrGenerateForTodayHome(user, today,
                0L, 0L, 0, 0,
                0, 0, 0,
                false);

        ArgumentCaptor<AdaptiveFeedback> savedCaptor = ArgumentCaptor.forClass(AdaptiveFeedback.class);
        verify(repository).save(savedCaptor.capture());
        verify(aiService, times(2)).suggestFeedback(eq(today), any(), anyString(), anyList());

        assertThat(savedCaptor.getValue().getFeedbackText().toLowerCase()).doesNotContain("lazy");
    }

    @Test
    void shouldRotateToneAcrossDates() {
        AdaptiveFeedbackRepository repository = mock(AdaptiveFeedbackRepository.class);
        AdaptiveFeedbackAiService aiService = mock(AdaptiveFeedbackAiService.class);
        AdaptiveFeedbackService service = new AdaptiveFeedbackService(repository, aiService);

        User user = new User();
        user.setId(123L);

        when(repository.findByUserIdAndDate(eq(123L), any(LocalDate.class))).thenReturn(Optional.empty());
        when(repository.findTop7ByUserIdOrderByDateDesc(123L)).thenReturn(List.of());
        when(aiService.suggestFeedback(any(LocalDate.class), any(), anyString(), anyList())).thenReturn("Ok");

        LocalDate d1 = LocalDate.of(2026, 4, 10);
        LocalDate d2 = LocalDate.of(2026, 4, 11);

        service.getOrGenerateForTodayHome(user, d1,
                0L, 0L, 0, 0,
                0, 0, 0,
                false);
        service.getOrGenerateForTodayHome(user, d2,
                0L, 0L, 0, 0,
                0, 0, 0,
                false);

        ArgumentCaptor<AdaptiveFeedback> savedCaptor = ArgumentCaptor.forClass(AdaptiveFeedback.class);
        verify(repository, times(2)).save(savedCaptor.capture());

        List<AdaptiveFeedback> saved = savedCaptor.getAllValues();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getTone()).isNotNull();
        assertThat(saved.get(1).getTone()).isNotNull();
        assertThat(saved.get(0).getTone()).isNotEqualTo(saved.get(1).getTone());
    }
}
