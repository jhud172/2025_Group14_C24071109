package uk.ac.cf._5.group14.One_To_One.CalendarTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.One_To_One.CalendarData.TaskAiGenerationService;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatResponse;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TaskAiGenerationServiceTest {

    @Mock
    private ChatService chatService;

    @Test
    void parsesValidJsonResponse() {
        given(chatService.chat(anyList()))
                .willReturn(new ChatResponse("{\"title\":\"Make bed\",\"notes\":\"Quick win\",\"exercise\":false,\"time\":\"07:30\"}"));

        TaskAiGenerationService svc = new TaskAiGenerationService(chatService, new ObjectMapper());
        TaskAiGenerationService.GeneratedTask task = svc.generateFromFreeText("I need to make my bed today");

        assertEquals("Make bed", task.title());
        assertEquals("Quick win", task.notes());
        assertFalse(task.exercise());
        assertEquals("07:30", task.time());
    }

    @Test
    void fallsBackToUserTextWhenResponseNotJson() {
        given(chatService.chat(anyList()))
                .willReturn(new ChatResponse("Sure â€” I'd suggest doing it soon."));

        TaskAiGenerationService svc = new TaskAiGenerationService(chatService, new ObjectMapper());
        TaskAiGenerationService.GeneratedTask task = svc.generateFromFreeText("Take out the bins");

        assertEquals("Take out the bins", task.title());
        assertEquals(null, task.notes());
        assertFalse(task.exercise());
        assertEquals(null, task.time());
    }

    @Test
    void extractsJsonFromCodeFencedReply() {
        given(chatService.chat(anyList()))
                .willReturn(new ChatResponse("```json\n{\"title\":\"Drink water\",\"notes\":null,\"exercise\":false,\"time\":null}\n```"));

        TaskAiGenerationService svc = new TaskAiGenerationService(chatService, new ObjectMapper());
        TaskAiGenerationService.GeneratedTask task = svc.generateFromFreeText("remind me to hydrate");

        assertEquals("Drink water", task.title());
        assertEquals(null, task.notes());
        assertFalse(task.exercise());
        assertEquals(null, task.time());
    }
}
