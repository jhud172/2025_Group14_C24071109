package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class WorkoutController {

    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;
    private final AuthHelper authHelper;

    @GetMapping("/workout")
    public ModelAndView createWorkout() {
        ModelAndView mav = new ModelAndView("schedule/workout");
        User user = authHelper.getAuthenticatedUser();
        
        List<Exercise> suggestedExercises = exerciseService.suggestExercises(user);
        mav.addObject("suggestedExercises", suggestedExercises);

        List<Workout> usersWorkouts = workoutService.getWorkouts();
        mav.addObject("usersWorkouts", usersWorkouts);

        return mav;
    }

    @GetMapping("/workout/create")
    public String getCreateFragment() {
        return "fragments/workout/workout-frags :: createWorkout";
    }

    @GetMapping("/workout/edit/{id}")
    public String editWorkout(@PathVariable Long id, Model model) {
        Workout workout = workoutService.getWorkoutToEdit(id);
        model.addAttribute("workout", workout);

        return "fragments/workout/workout-frags.html :: editWorkout";
    }

    @PostMapping("/save-workout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveWorkout(
            @RequestBody SaveWorkoutDTO dto
    )
    {
        workoutService.saveWorkout(dto);
        return ResponseEntity.ok(Map.of("message", "Workout saved successfully"));
    }

    @PostMapping("/delete-workout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteWorkout(
            @RequestBody Map <String, Long> payload
    )
    {
        Long id = payload.get("id");
        workoutService.deleteWorkout(id);
        return ResponseEntity.ok(Map.of("message", "Workout deleted successfully"));
    }
}
