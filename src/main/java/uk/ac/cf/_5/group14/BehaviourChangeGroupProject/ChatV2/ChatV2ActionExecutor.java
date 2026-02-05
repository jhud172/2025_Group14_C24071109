package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatV2;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.Note;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolderRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteFolderService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes.NoteService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ScheduleData.ScheduleOccurrenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatV2ActionExecutor {

    private final CalendarTaskService calendarTaskService;
    private final NoteService noteService;
    private final NoteFolderRepository noteFolderRepository;
    private final NoteFolderService noteFolderService;
    private final ScheduleOccurrenceRepository scheduleOccurrenceRepository;

    public ChatV2ActionExecutor(CalendarTaskService calendarTaskService,
                                NoteService noteService,
                                NoteFolderRepository noteFolderRepository,
                                NoteFolderService noteFolderService,
                                ScheduleOccurrenceRepository scheduleOccurrenceRepository) {
        this.calendarTaskService = calendarTaskService;
        this.noteService = noteService;
        this.noteFolderRepository = noteFolderRepository;
        this.noteFolderService = noteFolderService;
        this.scheduleOccurrenceRepository = scheduleOccurrenceRepository;
    }

    public ChatV2ActionResult execute(User user, String type, Map<String, Object> payload) {
        if (type == null) return new ChatV2ActionResult("UNKNOWN", payload, "ERROR", "Unknown action");

        return switch (type) {
            case "TASK_CREATE" -> handleTaskCreate(user, payload);
            case "TASK_COMPLETE" -> handleTaskComplete(user, payload);
            case "NOTE_CREATE" -> handleNoteCreate(user, payload);
            case "SCHEDULE_APPLY" -> new ChatV2ActionResult(type, payload, "ERROR", "Schedule apply not available yet.");
            default -> new ChatV2ActionResult(type, payload, "ERROR", "Unknown action");
        };
    }

    private ChatV2ActionResult handleTaskCreate(User user, Map<String, Object> payload) {
        try {
            String title = value(payload, "title", "New task");
            String dateStr = value(payload, "date", LocalDate.now().toString());
            String timeStr = value(payload, "time", null);
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = timeStr != null && !timeStr.isBlank() ? LocalTime.parse(timeStr) : null;
            calendarTaskService.createTask(user, date, time, title, null, false, false);
            return new ChatV2ActionResult("TASK_CREATE", payload, "OK", "Task created");
        } catch (Exception e) {
            return new ChatV2ActionResult("TASK_CREATE", payload, "ERROR", "Unable to create task");
        }
    }

    private ChatV2ActionResult handleTaskComplete(User user, Map<String, Object> payload) {
        try {
            String idStr = value(payload, "taskId", null);
            Long taskId = idStr != null ? Long.parseLong(idStr) : null;
            if (taskId == null) {
                return new ChatV2ActionResult("TASK_COMPLETE", payload, "ERROR", "Missing task id");
            }
            calendarTaskService.toggleCompleted(taskId, user);
            return new ChatV2ActionResult("TASK_COMPLETE", payload, "OK", "Task marked complete");
        } catch (Exception e) {
            return new ChatV2ActionResult("TASK_COMPLETE", payload, "ERROR", "Unable to update task");
        }
    }

    private ChatV2ActionResult handleNoteCreate(User user, Map<String, Object> payload) {
        try {
            String title = value(payload, "title", "New note");
            String content = value(payload, "content", "");
            noteFolderService.ensureDefaults(user);
            Long folderId = noteFolderRepository.findByUserAndNameIgnoreCase(user, "Unsorted")
                .map(f -> f.getId())
                .orElse(null);
            Note note = noteService.create(user, folderId, title, content, "slate");
            Map<String, Object> out = new HashMap<>(payload);
            out.put("noteId", note.getId());
            return new ChatV2ActionResult("NOTE_CREATE", out, "OK", "Note created");
        } catch (Exception e) {
            return new ChatV2ActionResult("NOTE_CREATE", payload, "ERROR", "Unable to create note");
        }
    }

    private String value(Map<String, Object> payload, String key, String fallback) {
        if (payload == null) return fallback;
        Object v = payload.get(key);
        return v != null ? v.toString() : fallback;
    }

    public List<CalendarTask> listTodaysTasks(User user) {
        return calendarTaskService.getTasks(user, LocalDate.now());
    }

    public List<?> listUpcomingSchedules(User user) {
        return scheduleOccurrenceRepository.findByUserAndDateBetween(user, LocalDate.now(), LocalDate.now().plusDays(7));
    }
}
