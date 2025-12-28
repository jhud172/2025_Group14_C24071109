package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;
import java.util.List;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

public interface ExerciseService {
    List<Exercise> getAllExercises();
    Exercise getExerciseById(Long id);
    void saveExercise(Exercise exercise);
    List<Exercise> suggestExercises(User user);
}
