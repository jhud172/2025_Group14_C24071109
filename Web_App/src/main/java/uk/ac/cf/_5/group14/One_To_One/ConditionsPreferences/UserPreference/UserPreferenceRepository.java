package uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.ac.cf._5.group14.One_To_One.ExerciseData.Tag;
import uk.ac.cf._5.group14.One_To_One.Users.User;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> getByUser(User user);

        boolean existsByUser(User user);

    @Query("select preferenceTags from UserPreference up " +
            "join up.preferences p " +
            "join p.tags preferenceTags " +
            "where up.user = :user")
    Set<Tag> getPreferredTags(@Param("user") User user);

    @Query("select conditionTags from UserPreference up " +
            "join up.physicalConditions pc " +
            "join pc.tags conditionTags " +
            "where up.user = :user")
    Set<Tag> getBannedTags(@Param("user") User user);
}
