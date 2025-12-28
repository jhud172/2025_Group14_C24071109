package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workout;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepositoryImplementation<Workout,Long> {
    List<Workout> findByUserId(Long userId);

    // used when a single workout is needed along with its exercises (better performance than fetching exercises eagerly all the time)
    @EntityGraph(attributePaths = {"exercises"})
    Optional<Workout> findByIdAndUserId(Long id, Long userId);
}
