package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("/exercise/{id}")
    public ModelAndView getExercise(@PathVariable Long id, Model model) {
        ModelAndView modelAndView = new ModelAndView("/exercise-log/ExerciseTutorial");
        Exercise exercise = exerciseService.getExerciseById(id);
        modelAndView.addObject("exercise", exercise);
        return modelAndView;

      /*  if (exercise != null) {
            model.("exercise", exercise);
            return "exercise-log/ExerciseTutorial"; //
        } else {
            // Handle exercise not found
            return "error"; // to be written?
        } */
    }
}