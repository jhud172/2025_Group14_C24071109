package uk.ac.cf._5.group14.One_To_One.DayHealthTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;
import uk.ac.cf._5.group14.One_To_One.DayHealthData.DayHealthAiService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DayHealthAiServiceTest {

    @Test
    void shouldParseJsonAnalysisField() {
        ChatService chatService = mock(ChatService.class);
        ObjectMapper mapper = new ObjectMapper();
        DayHealthAiService service = new DayHealthAiService(chatService, mapper);

        when(chatService.chat(anyList())).thenReturn(new ChatResponse("{\"analysis\":\"Keep it steady.\"}"));

        String out = service.suggestDayHealth(LocalDate.of(2026, 1, 15), "ctx");

        assertThat(out).isEqualTo("Keep it steady.");
        verify(chatService, times(1)).chat(anyList());
    }

    @Test
    void shouldReturnNullWhenUnparseable() {
        ChatService chatService = mock(ChatService.class);
        ObjectMapper mapper = new ObjectMapper();
        DayHealthAiService service = new DayHealthAiService(chatService, mapper);

        when(chatService.chat(anyList())).thenReturn(new ChatResponse("not json"));

        String out = service.suggestDayHealth(LocalDate.of(2026, 1, 15), "ctx");

        assertThat(out).isNull();
    }
}
