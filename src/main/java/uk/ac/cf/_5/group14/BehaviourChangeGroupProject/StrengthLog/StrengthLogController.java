package uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.ExerciseSessionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.SetLogRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.StrengthLog.Repository.WorkoutSessionRepository;

import java.time.LocalDate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StrengthLogController {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private ExerciseSessionRepository exerciseSessionRepository;

    @Autowired
    private SetLogRepository setLogRepository;

    @GetMapping("/workout-session/{id}")
    public String viewSession(@PathVariable Long id, @SessionAttribute("user") User user, Model model) {
        var sessionOpt = workoutSessionRepository.findById(id);
        if (sessionOpt.isEmpty()) return "redirect:/calendar";
        var session = sessionOpt.get();
        if (!session.getUser().getId().equals(user.getId())) return "redirect:/calendar";

        model.addAttribute("workoutSession", session);

        List<ExerciseSession> exerciseSessions = session.getExerciseSessions()
                .stream()
                .sorted(Comparator.comparingInt(ExerciseSession::getOrderIndex))
                .toList();
        model.addAttribute("exerciseSessions", exerciseSessions);

        Map<Long, String> exerciseStateById = new HashMap<>();
        for (ExerciseSession es : exerciseSessions) {
            exerciseStateById.put(es.getId(), computeExerciseState(es));
        }
        model.addAttribute("exerciseStateById", exerciseStateById);

        return "strengthlog/workout-session";
    }

    @GetMapping("/exercise-session/{id}")
    public String viewExercise(@PathVariable Long id, @SessionAttribute("user") User user, Model model) {
        var esOpt = exerciseSessionRepository.findById(id);
        if (esOpt.isEmpty()) return "redirect:/calendar";
        var es = esOpt.get();
        if (!es.getWorkoutSession().getUser().getId().equals(user.getId())) return "redirect:/calendar";

        model.addAttribute("exerciseSession", es);

        List<SetLog> setLogs = es.getSetLogs().stream()
                .sorted(Comparator.comparingInt(SetLog::getSetNumber))
                .toList();
        model.addAttribute("setLogs", setLogs);
        model.addAttribute("exerciseState", computeExerciseState(es));

        return "strengthlog/exercise-session";
    }

    @GetMapping("/workout-session/{id}/completion")
    public String completion(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam(name = "day", required = false) String day,
            Model model
    ) {
        var sessionOpt = workoutSessionRepository.findById(id);
        if (sessionOpt.isEmpty()) return "redirect:/calendar";
        var session = sessionOpt.get();
        if (!session.getUser().getId().equals(user.getId())) return "redirect:/calendar";

        LocalDate dayDate = session.getDate();
        if (day != null && !day.isBlank()) {
            dayDate = LocalDate.parse(day);
        }

        model.addAttribute("workoutSession", session);
        model.addAttribute("day", dayDate);
        return "strengthlog/completion";
    }

    @PostMapping("/exercise-session/{id}/add-set")
    public String addSet(@PathVariable Long id, @SessionAttribute("user") User user) {
        var esOpt = exerciseSessionRepository.findById(id);
        if (esOpt.isEmpty()) return "redirect:/calendar";
        var es = esOpt.get();
        if (!es.getWorkoutSession().getUser().getId().equals(user.getId())) return "redirect:/calendar";

        int next = es.getSetLogs().stream().mapToInt(s -> s.getSetNumber()).max().orElse(0) + 1;
        SetLog s = new SetLog();
        s.setExerciseSession(es);
        s.setSetNumber(next);
        es.getSetLogs().add(s);
        workoutSessionRepository.save(es.getWorkoutSession());

        return "redirect:/exercise-session/" + id;
    }

    @PostMapping("/set-log/{id}/toggle")
    public String toggleSet(@PathVariable Long id, @SessionAttribute("user") User user) {
        var opt = setLogRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/calendar";
        var s = opt.get();
        if (!s.getExerciseSession().getWorkoutSession().getUser().getId().equals(user.getId())) return "redirect:/calendar";
        s.setCompleted(!s.isCompleted());
        setLogRepository.save(s);

        var es = s.getExerciseSession();
        rollUpCompletion(es);

        return "redirect:/exercise-session/" + es.getId();
    }

    @PostMapping("/set-log/{id}/save")
    public String saveSet(
            @PathVariable Long id,
            @SessionAttribute("user") User user,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) Integer reps,
            @RequestParam(required = false) String notes,
            @RequestParam(name = "completed", defaultValue = "false") boolean completed
    ) {
        var opt = setLogRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/calendar";
        var s = opt.get();
        if (!s.getExerciseSession().getWorkoutSession().getUser().getId().equals(user.getId())) return "redirect:/calendar";

        s.setWeight(weight);
        s.setReps(reps);
        s.setNotes(notes);
        s.setCompleted(completed);
        setLogRepository.save(s);

        rollUpCompletion(s.getExerciseSession());
        return "redirect:/exercise-session/" + s.getExerciseSession().getId();
    }

    @PostMapping("/set-log/{id}/delete")
    public String deleteSet(
            @PathVariable Long id,
            @SessionAttribute("user") User user
    ) {
        var opt = setLogRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/calendar";
        var s = opt.get();
        var es = s.getExerciseSession();
        if (!es.getWorkoutSession().getUser().getId().equals(user.getId())) return "redirect:/calendar";

        es.getSetLogs().removeIf(sl -> sl.getId().equals(id));
        exerciseSessionRepository.save(es);
        setLogRepository.delete(s);

        renumberSets(es);
        rollUpCompletion(es);

        return "redirect:/exercise-session/" + es.getId();
    }

    private void rollUpCompletion(ExerciseSession es) {
        renumberSets(es);

        boolean allSets = !es.getSetLogs().isEmpty() && es.getSetLogs().stream().allMatch(SetLog::isCompleted);
        es.setCompleted(allSets);
        exerciseSessionRepository.save(es);

        var ws = es.getWorkoutSession();
        boolean allExercises = !ws.getExerciseSessions().isEmpty() && ws.getExerciseSessions().stream().allMatch(ExerciseSession::isCompleted);
        ws.setCompleted(allExercises);
        workoutSessionRepository.save(ws);
    }

    private void renumberSets(ExerciseSession es) {
        List<SetLog> ordered = es.getSetLogs().stream()
                .sorted(Comparator.comparingInt(SetLog::getSetNumber))
                .toList();

        int next = 1;
        boolean changed = false;
        for (SetLog setLog : ordered) {
            if (setLog.getSetNumber() != next) {
                setLog.setSetNumber(next);
                changed = true;
            }
            next++;
        }
        if (changed) {
            exerciseSessionRepository.save(es);
        }
    }

    private String computeExerciseState(ExerciseSession es) {
        if (es.getSetLogs() == null || es.getSetLogs().isEmpty()) return "NOT_STARTED";
        boolean all = es.getSetLogs().stream().allMatch(SetLog::isCompleted);
        if (all) return "COMPLETED";
        boolean any = es.getSetLogs().stream().anyMatch(SetLog::isCompleted);
        return any ? "IN_PROGRESS" : "NOT_STARTED";
    }
}
