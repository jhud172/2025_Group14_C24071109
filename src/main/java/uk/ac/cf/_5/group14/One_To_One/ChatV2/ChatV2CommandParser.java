package uk.ac.cf._5.group14.One_To_One.ChatV2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ChatV2CommandParser {

    private ChatV2CommandParser() {
    }

    public static ParsedCommand parse(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (!trimmed.startsWith("/")) return null;

        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (lower.startsWith("/task add")) {
            Map<String, Object> payload = new HashMap<>();
            String rest = trimmed.substring(9).trim();
            String title = rest;
            LocalDate date = LocalDate.now();
            LocalTime time = null;

            if (rest.contains("@")) {
                String[] parts = rest.split("@", 2);
                title = parts[0].trim();
                String when = parts[1].trim();
                try {
                    if (when.length() >= 10) {
                        date = LocalDate.parse(when.substring(0, 10));
                    }
                } catch (Exception ignored) {
                }
                try {
                    if (when.length() >= 16) {
                        time = LocalTime.parse(when.substring(11, 16));
                    }
                } catch (Exception ignored) {
                }
            }

            payload.put("title", title.isBlank() ? "New task" : title);
            payload.put("date", date.format(DateTimeFormatter.ISO_DATE));
            if (time != null) payload.put("time", time.format(DateTimeFormatter.ofPattern("HH:mm")));
            return new ParsedCommand(ChatV2ActionType.TASK_CREATE.name(), payload, "Task created.");
        }

        if (lower.startsWith("/task done")) {
            String rest = trimmed.substring(10).trim();
            Map<String, Object> payload = new HashMap<>();
            payload.put("taskId", rest);
            return new ParsedCommand(ChatV2ActionType.TASK_COMPLETE.name(), payload, "Task updated.");
        }

        if (lower.startsWith("/task list")) {
            return new ParsedCommand("TASK_LIST", Map.of(), "Here are your tasks for today.");
        }

        if (lower.startsWith("/note add")) {
            String rest = trimmed.substring(9).trim();
            Map<String, Object> payload = new HashMap<>();
            String[] parts = rest.split("::", 2);
            if (parts.length == 2) {
                payload.put("title", parts[0].trim());
                payload.put("content", parts[1].trim());
            } else {
                payload.put("title", "New note");
                payload.put("content", rest);
            }
            return new ParsedCommand(ChatV2ActionType.NOTE_CREATE.name(), payload, "Note created.");
        }

        if (lower.startsWith("/note search")) {
            String rest = trimmed.substring(12).trim();
            return new ParsedCommand("NOTE_SEARCH", Map.of("query", rest), "Searching notesâ€¦");
        }

        if (lower.startsWith("/schedule list")) {
            return new ParsedCommand("SCHEDULE_LIST", Map.of(), "Here are your upcoming schedules.");
        }

        return new ParsedCommand("UNKNOWN", Map.of("raw", trimmed), "I don't recognize that command.");
    }

    public record ParsedCommand(String type, Map<String, Object> payload, String assistantText) {}
}
