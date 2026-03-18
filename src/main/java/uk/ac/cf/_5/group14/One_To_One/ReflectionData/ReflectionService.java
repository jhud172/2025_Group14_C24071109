package uk.ac.cf._5.group14.One_To_One.ReflectionData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.One_To_One.BehaviourMemoryData.BehaviourMemoryService;
import uk.ac.cf._5.group14.One_To_One.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReflectionService {

    private final ReflectionAiService reflectionAiService;
    private final BehaviourMemoryService behaviourMemoryService;

    public ReflectionService(ReflectionAiService reflectionAiService,
                            BehaviourMemoryService behaviourMemoryService) {
        this.reflectionAiService = reflectionAiService;
        this.behaviourMemoryService = behaviourMemoryService;
    }

    public ReflectionResult generate(User user,
                                    LocalDate date,
                                    String dailyFocus,
                                    List<CalendarTask> tasks,
                                    int completedWorkouts,
                                    int totalWorkouts,
                                    String reflection,
                                    String notes) {

        String dayData = buildDayData(dailyFocus, tasks, completedWorkouts, totalWorkouts);

        if (behaviourMemoryService != null) {
            var maybeMem = behaviourMemoryService.maybeGetAiContext(user);
            if (maybeMem.isPresent()) {
                dayData = dayData + "\n\n" + maybeMem.get();
            }
        }

        ReflectionResult ai = reflectionAiService.generateReflection(date, dayData, reflection, notes);
        if (ai != null) {
            return ai;
        }

        return fallback(dayData);
    }

    private static String buildDayData(String dailyFocus,
                                      List<CalendarTask> tasks,
                                      int completedWorkouts,
                                      int totalWorkouts) {
        int totalTasks = tasks == null ? 0 : tasks.size();
        int completedTasks = (tasks == null) ? 0 : (int) tasks.stream().filter(t -> Boolean.TRUE.equals(t.getCompleted())).count();

        StringBuilder sb = new StringBuilder();
        if (dailyFocus != null && !dailyFocus.isBlank()) {
            sb.append("Daily focus: ").append(dailyFocus.trim()).append("\n");
        }

        sb.append("Tasks: ").append(completedTasks).append("/").append(totalTasks).append(" completed\n");
        sb.append("Workouts: ").append(Math.max(0, completedWorkouts)).append("/").append(Math.max(0, totalWorkouts)).append(" completed\n");

        if (tasks != null && !tasks.isEmpty()) {
            sb.append("Task details:\n");
            int count = 0;
            for (CalendarTask t : tasks) {
                if (t == null) continue;
                String title = t.getTitle() == null ? "" : t.getTitle().trim();
                if (title.isBlank()) continue;
                sb.append(Boolean.TRUE.equals(t.getCompleted()) ? "- [x] " : "- [ ] ");
                sb.append(title);
                sb.append("\n");
                count++;
                if (count >= 12) break;
            }
        }

        return sb.toString().trim();
    }

    private static ReflectionResult fallback(String dayData) {
        String summary = "Nice work showing up today. Take a moment to notice what went well, then pick one small thing to improve tomorrow.";

        StringBuilder suggestions = new StringBuilder();
        suggestions.append("Try one small improvement next time:\n");
        suggestions.append("- Choose 1 priority and start it first\n");
        suggestions.append("- Keep the plan realistic (fewer items, better follow-through)\n");
        suggestions.append("- End the day with a quick reset and prep for tomorrow");

        if (dayData != null && !dayData.isBlank()) {
            return new ReflectionResult(summary, suggestions.toString());
        }
        return new ReflectionResult(summary, suggestions.toString());
    }
}
