package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CreateTaskActionHandler implements CoachActionHandler<CreateTaskActionPayload> {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.UK);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm", Locale.UK);

    private final CalendarTaskService calendarTaskService;

    public CreateTaskActionHandler(CalendarTaskService calendarTaskService) {
        this.calendarTaskService = calendarTaskService;
    }

    @Override
    public CoachActionType type() {
        return CoachActionType.CREATE_TASK;
    }

    @Override
    public List<String> validate(CreateTaskActionPayload payload, User user) {
        List<String> errors = new ArrayList<>();
        if (user == null || user.getId() == null) {
            errors.add("User is required.");
        }
        if (payload == null) {
            errors.add("Missing task details.");
            return errors;
        }
        if (payload.date() == null) {
            errors.add("Task date is required.");
        }
        if (payload.title() == null || payload.title().isBlank()) {
            errors.add("Task title is required.");
        }
        return errors;
    }

    @Override
    public CoachActionExecution execute(CreateTaskActionPayload payload, User user) {
        LocalDate date = payload.date();
        LocalTime time = payload.time();
        String title = payload.title().trim();

        String notes = buildNotes(payload.description(), payload.requirements());
        calendarTaskService.createTask(user, date, time, title, notes, false, false);

        String when = date.format(DATE_FMT);
        StringBuilder sb = new StringBuilder();
        sb.append("Task created on ").append(when);
        if (time != null) {
            sb.append(" at ").append(time.format(TIME_FMT));
        }
        sb.append(" — ").append(title).append(". Want to adjust anything?");
        return new CoachActionExecution(true, sb.toString(), null);
    }

    private String buildNotes(String description, String requirements) {
        StringBuilder sb = new StringBuilder();
        if (description != null && !description.isBlank()) {
            sb.append("Description: ").append(description.trim());
        }
        if (requirements != null && !requirements.isBlank()) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("Requirements: ").append(requirements.trim());
        }
        String out = sb.toString().trim();
        return out.isBlank() ? null : out;
    }
}
