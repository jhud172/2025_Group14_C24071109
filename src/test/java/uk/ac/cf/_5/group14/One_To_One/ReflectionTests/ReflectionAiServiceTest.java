package uk.ac.cf._5.group14.One_To_One.ReflectionTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;
import uk.ac.cf._5.group14.One_To_One.ReflectionData.ReflectionAiService;
import uk.ac.cf._5.group14.One_To_One.ReflectionData.ReflectionResult;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ReflectionAiServiceTest {

    @Test
    void shouldParseJsonIntoReflectionResult() {
        ChatService chatService = mock(ChatService.class);
        ObjectMapper mapper = new ObjectMapper();

        when(chatService.chat(anyList())).thenReturn(new ChatResponse("{" +
                "\"performanceSummary\":\"You did well.\"," +
                "\"improvementSuggestions\":\"Try starting earlier.\"" +
                "}"));

        ReflectionAiService service = new ReflectionAiService(chatService, mapper);
        ReflectionResult result = service.generateReflection(
                LocalDate.of(2026, 1, 15),
                "Tasks: 1/1 completed",
                "Felt good.",
                "Keep it casual"
        );

        assertNotNull(result);
        assertEquals("You did well.", result.performanceSummary());
        assertEquals("Try starting earlier.", result.improvementSuggestions());
    }

    @Test
    void shouldReturnNullForUnparseableOutput() {
        ChatService chatService = mock(ChatService.class);
        ObjectMapper mapper = new ObjectMapper();

        when(chatService.chat(anyList())).thenReturn(new ChatResponse("AI integration is not configured yet."));

        ReflectionAiService service = new ReflectionAiService(chatService, mapper);
        ReflectionResult result = service.generateReflection(LocalDate.now(), "", "Test", "");

        assertNull(result);
    }

    @Test
    void shouldIncludeNotesInUserMessageForTonePreservation() {
        ChatService chatService = mock(ChatService.class);
        ObjectMapper mapper = new ObjectMapper();

        when(chatService.chat(anyList())).thenReturn(new ChatResponse("{" +
                "\"performanceSummary\":\"Ok\"," +
                "\"improvementSuggestions\":\"Ok\"" +
                "}"));

        ReflectionAiService service = new ReflectionAiService(chatService, mapper);

        String notes = "Please keep it upbeat and informal";
        service.generateReflection(LocalDate.now(), "Tasks: 1/1 completed", "It went fine", notes);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ChatService.Message>> captor = ArgumentCaptor.forClass(List.class);
        verify(chatService).chat(captor.capture());

        String userContent = captor.getValue().stream()
                .filter(m -> "user".equals(m.role()))
                .findFirst()
                .map(ChatService.Message::content)
                .orElse("");

        assertTrue(userContent.contains(notes));
    }
}
