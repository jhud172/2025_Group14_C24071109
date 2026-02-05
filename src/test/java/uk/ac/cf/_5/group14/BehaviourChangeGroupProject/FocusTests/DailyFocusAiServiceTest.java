package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatResponse;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat.ChatService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.FocusData.DailyFocusAiService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

class DailyFocusAiServiceTest {

    @Test
    void usesAiJsonDailyFocusWhenValid() {
        ChatService chatService = Mockito.mock(ChatService.class);
        DailyFocusAiService svc = new DailyFocusAiService(chatService, new ObjectMapper());

        given(chatService.chat(anyList())).willReturn(new ChatResponse("{\"dailyFocus\":\"Hydrate\"}"));

        String focus = svc.suggestDailyFocus(LocalDate.of(2026, 1, 15), "Morning", 3, 1);
        assertThat(focus).isEqualTo("Hydrate");
    }

    @Test
    void fallsBackToTimedFocusWhenAiReplyNotJson() {
        ChatService chatService = Mockito.mock(ChatService.class);
        DailyFocusAiService svc = new DailyFocusAiService(chatService, new ObjectMapper());

        given(chatService.chat(anyList())).willReturn(new ChatResponse("not json"));

        String focus = svc.suggestDailyFocus(LocalDate.of(2026, 1, 15), "Morning", 3, 1);
        assertThat(focus).isEqualTo("Morning");
    }
}
