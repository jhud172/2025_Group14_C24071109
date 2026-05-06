package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SimpleCoachActionParser implements CoachActionParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEEK_PATTERN = Pattern.compile("(\\d{1,2})\\s*(week|weeks)", Pattern.CASE_INSENSITIVE);

    private final Clock clock;

    public SimpleCoachActionParser(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Optional<CoachParsedAction> parse(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        String lower = message.toLowerCase();
        Optional<CoachParsedAction> schedule = parseSchedule(message, lower);
        if (schedule.isPresent()) {
            return schedule;
        }
        return parseCreateTask(message, lower);
    }

    private Optional<CoachParsedAction> parseSchedule(String message, String lower) {
        if (!(lower.contains("schedule") && (lower.contains("apply") || lower.contains("start") || lower.contains("use")))) {
            return Optional.empty();
        }
        String name = extractScheduleName(message);
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        LocalDate startDate = parseDate(lower);
        if (startDate == null) {
            startDate = LocalDate.now(clock);
        }
        int weeks = parseWeeks(lower);
        if (weeks <= 0) {
            weeks = 4;
        }
        return Optional.of(new CoachParsedAction(CoachActionType.APPLY_SCHEDULE,
                new ApplyScheduleActionPayload(name, startDate, weeks)));
    }

    private Optional<CoachParsedAction> parseCreateTask(String message, String lower) {
        if (!(lower.contains("task") || lower.contains("remind") || lower.contains("add"))) {
            return Optional.empty();
        }
        LocalDate date = parseDate(lower);
        if (date == null) {
            return Optional.empty();
        }
        LocalTime time = parseTime(message);
        String title = extractTaskTitle(message, lower);
        if (title == null || title.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new CoachParsedAction(CoachActionType.CREATE_TASK,
                new CreateTaskActionPayload(date, time, title, null, null)));
    }

    private LocalDate parseDate(String lower) {
        if (lower.contains("tomorrow")) {
            return LocalDate.now(clock).plusDays(1);
        }
        if (lower.contains("today")) {
            return LocalDate.now(clock);
        }
        return ChatDateParser.tryParse(lower).orElse(null);
    }

    private LocalTime parseTime(String message) {
        Matcher m = TIME_PATTERN.matcher(message);
        if (!m.find()) {
            return null;
        }
        int hour = Integer.parseInt(m.group(1));
        int minute = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
        String meridiem = m.group(3);
        if (meridiem != null) {
            String mer = meridiem.toLowerCase();
            if (mer.equals("pm") && hour < 12) hour += 12;
            if (mer.equals("am") && hour == 12) hour = 0;
        }
        if (hour > 23 || minute > 59) {
            return null;
        }
        return LocalTime.of(hour, minute);
    }

    private int parseWeeks(String lower) {
        Matcher m = WEEK_PATTERN.matcher(lower);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private String extractScheduleName(String message) {
        String lower = message.toLowerCase();
        int idx = lower.indexOf("schedule");
        if (idx < 0) return null;
        String after = message.substring(idx + "schedule".length()).trim();
        if (after.startsWith(":")) after = after.substring(1).trim();
        if (after.startsWith("\"") && after.contains("\"")) {
            int end = after.indexOf("\"", 1);
            return end > 1 ? after.substring(1, end).trim() : null;
        }
        if (after.startsWith("'") && after.contains("'")) {
            int end = after.indexOf("'", 1);
            return end > 1 ? after.substring(1, end).trim() : null;
        }
        int cut = after.toLowerCase().indexOf("starting");
        if (cut < 0) cut = after.toLowerCase().indexOf("from");
        if (cut < 0) cut = after.toLowerCase().indexOf("for");
        String name = cut > 0 ? after.substring(0, cut).trim() : after.trim();
        return name.isBlank() ? null : name;
    }

    private String extractTaskTitle(String message, String lower) {
        String title = message;
        int idx = lower.indexOf("remind me to");
        if (idx >= 0) {
            title = message.substring(idx + "remind me to".length()).trim();
        }
        idx = lower.indexOf("add task");
        if (idx >= 0) {
            title = message.substring(idx + "add task".length()).trim();
        }
        idx = lower.indexOf("task");
        if (idx >= 0 && title.equals(message)) {
            title = message.substring(idx + "task".length()).trim();
        }
        title = title.replaceAll("\\bon\\b.*", "").replaceAll("\\bat\\b.*", "").trim();
        return title.isBlank() ? null : title;
    }
}
