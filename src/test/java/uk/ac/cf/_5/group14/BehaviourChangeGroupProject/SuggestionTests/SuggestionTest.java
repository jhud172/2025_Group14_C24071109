package uk.ac.cf._5.group14.BehaviourChangeGroupProject.SuggestionTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Exercise;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.ExerciseServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Tag;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SuggestionTest {

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    void SuggestExercises_ShouldReturnExercises_WhenPreferences() {
        // Create user
        User testUser = new User();
        testUser.setId(1L);

        // Create a tag
        Tag cardioTag = new Tag();
        cardioTag.setId(1L);
        cardioTag.setName("Cardio");

        // Add tag to preferences
        Set<Tag> preferredTags = new HashSet<>();
        preferredTags.add(cardioTag);

        // Creates the exercise and makes sure the tags match the preferences (So it should be returned)
        Exercise testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setTags(preferredTags);

        List<Exercise> expectedExercises = new ArrayList<>();
        expectedExercises.add(testExercise);

        Set<Tag> noBannedTags = new HashSet<>();

        when(userPreferenceRepository.getPreferredTags(testUser)).thenReturn(preferredTags);
        when(userPreferenceRepository.getBannedTags(testUser)).thenReturn(noBannedTags);
        when(exerciseRepository.getFilteredSuggestedExercises(preferredTags, noBannedTags)).thenReturn(expectedExercises);

        List<Exercise> actualResults = exerciseService.suggestExercises(testUser);

        Assertions.assertEquals(1, actualResults.size());
        Assertions.assertEquals(expectedExercises, actualResults);
    }

}
