package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.assertj.core.api.Assertions.assertThat;

class ExerciseTest {

    @Test
    void exerciseGettersAndSettersTest() {
        Exercise exercise = new Exercise();
        exercise.setName("Push Up");
        exercise.setDifficulty(3);
        exercise.setTags(new HashSet<>());

        assertThat(exercise.getName()).isEqualTo("Push Up");
        assertThat(exercise.getDifficulty()).isEqualTo(3);
        assertThat(exercise.getTags()).isNotNull();
    }
}
