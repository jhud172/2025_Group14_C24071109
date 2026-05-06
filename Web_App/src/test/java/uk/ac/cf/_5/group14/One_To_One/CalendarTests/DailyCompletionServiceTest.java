package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionRepository;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DailyCompletionServiceTest {

    @Mock
    private DailyCompletionRepository dailyCompletionRepository;

    @InjectMocks
    private DailyCompletionService dailyCompletionService;

    @Test
    void missingRecordShouldReturnGrey() {
        User user = new User();
        user.setId(123L);
        LocalDate date = LocalDate.of(2026, 1, 15);

        given(dailyCompletionRepository.findById(any())).willReturn(Optional.empty());

        DailyCompletionStatus status = dailyCompletionService.getCompletionStatus(user, date);

        assertThat(status).isEqualTo(DailyCompletionStatus.GREY);
        then(dailyCompletionRepository).should().findById(any());
    }

    @Test
    void nullUserOrDateShouldReturnGreyWithoutQuerying() {
        DailyCompletionStatus a = dailyCompletionService.getCompletionStatus(null, LocalDate.of(2026, 1, 15));
        DailyCompletionStatus b = dailyCompletionService.getCompletionStatus(new User(), LocalDate.of(2026, 1, 15));
        DailyCompletionStatus c = dailyCompletionService.getCompletionStatus(new User(), null);

        assertThat(a).isEqualTo(DailyCompletionStatus.GREY);
        assertThat(b).isEqualTo(DailyCompletionStatus.GREY);
        assertThat(c).isEqualTo(DailyCompletionStatus.GREY);
        then(dailyCompletionRepository).shouldHaveNoInteractions();
    }
}
