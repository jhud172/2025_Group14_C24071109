package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Tag;
import java.util.List;
import java.util.Set;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByTags(Set<Tag> tags);

    // Gets exercises with the preferred tags and ensures that exercises with the banned tags are excluded.
    @Query("select e from Exercise e " +
            "join e.tags t " +
            "where t in :preferredTags " +
            "and e not in " +
            "(select e from Exercise e " +
            "join e.tags t where t in :bannedTags)")
    List<Exercise> getFilteredSuggestedExercises(@Param("preferredTags") Set<Tag> preferredTags, @Param("bannedTags") Set<Tag> bannedTags);
}
