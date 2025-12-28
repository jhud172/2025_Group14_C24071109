package uk.ac.cf._5.group14.BehaviourChangeGroupProject.UserPreferenceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceForm;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference.UserPreferenceServiceImpl;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserPreferenceServiceUnitTests {

    @Mock
    private HealthRecordService healthRecordService;
    @Mock
    private UserPreferenceRepository userPreferenceRepository;
    @Mock
    private PreferenceRepository preferenceRepository;
    @Mock
    private PhysicalConditionRepository physicalConditionRepository;

    @InjectMocks
    private UserPreferenceServiceImpl userPreferenceService;

    private User testUser;
    private UserPreference testUserPreference;


    @BeforeEach
    public void setup() {
        testUser = new User();
        testUserPreference = new UserPreference();
    }

    @Test
    public void shouldReturnEmptyListOfUsersPhysicalConditions() {
        // Given that it is the users first time selecting their preferences, and they do not have a health record.
        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.empty());

        // When the application retrieves the user's physical condition preferences.
        List<PhysicalCondition> result = userPreferenceService.getUsersPhysicalConditions(testUser);

        // Then an empty list of physical conditions is returned.
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnListOfUsersPhysicalConditions() {
        // Given that a user has previously selected their physical condition preferences, and they do not have a health record.

        PhysicalCondition previouslySelectedCondition = new PhysicalCondition();

        List<PhysicalCondition> expectedList = List.of(previouslySelectedCondition);
        testUserPreference.setPhysicalConditions(expectedList);

        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.of(testUserPreference));

        // When the application retrieves the user's physical condition preferences.
        List<PhysicalCondition> result = userPreferenceService.getUsersPhysicalConditions(testUser);

        // Then a list of the users selected physical conditions is returned.
        assertEquals(1, result.size());
        assertEquals(expectedList, result);

    }


    @Test
    public void shouldReturnEmptyListOfUsersPreferences() {
        // Given that it is the users first time selecting their preferences
        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.empty());

        // When the application retrieves the user's preferences.
        List<Preference> result = userPreferenceService.getUserPreferences(testUser);

        // Then an empty list of preferences is returned.
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnListOfUsersPreferences() {
        // Given that a user has previously selected their preferences.

        Preference previouslySelectedPreference = new Preference();

        List<Preference> expectedList = List.of(previouslySelectedPreference);
        testUserPreference.setPreferences(expectedList);

        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.of(testUserPreference));

        // When the application retrieves the user's preferences.
        List<Preference> result = userPreferenceService.getUserPreferences(testUser);


        // Then a list of the users selected preferences is returned.
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(expectedList, result);
    }

    @Test
    public void shouldGetSetOfPreviouslySelectedIDsForPhysicalConditionsAndPreferences() {
        // Given that a user has previously selected their preferences, physical condition preferences and has a health record containing no physical conditions.

        HealthRecord testHealthRecord = new HealthRecord();
        testHealthRecord.setUser(testUser);

        testUserPreference.setUser(testUser);

        Preference previouslySelectedPreference = new Preference();
        previouslySelectedPreference.setId(2L);
        List<Preference> preferenceList = List.of(previouslySelectedPreference);
        testUserPreference.setPreferences(preferenceList);

        PhysicalCondition previouslySelectedCondition = new PhysicalCondition();
        previouslySelectedCondition.setId(1L);
        List<PhysicalCondition> physicalConditionList = List.of(previouslySelectedCondition);
        testUserPreference.setPhysicalConditions(physicalConditionList);

        Set<Long> expectedConditionList = Set.of(previouslySelectedCondition.getId());
        Set<Long> expectedPreferenceList = Set.of(previouslySelectedPreference.getId());

        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.of(testUserPreference));
        when(healthRecordService.getMostRecentHealthRecord(testUser)).thenReturn(testHealthRecord);

        // When the application retrieves the preference and conditions form.
        UserPreferenceForm result = userPreferenceService.getUserPreferenceForm(testUser);

        // Then a DTO is returned containing a set of the users preference ID's and physicalCondition ID's.
        assertEquals(expectedPreferenceList, result.getSelectedPreferenceIds());
        assertEquals(expectedConditionList, result.getSelectedConditionIds());
    }

    @Test
    public void shouldGetEmptyPreferenceSetAndSetContainingUsersRecordedHealthConditions() {
        // Given that it is the users first time selecting their preferences, and has the physical condition arthritis recorded in their most recent health record.
        HealthRecord testHealthRecord = new HealthRecord();
        testHealthRecord.setUser(testUser);

        PhysicalCondition physicalCondition = new PhysicalCondition();
        physicalCondition.setId(1L);
        physicalCondition.setName("Arthritis");
        List<PhysicalCondition> recordPhysicalConditions = List.of(physicalCondition);
        testHealthRecord.setPhysicalConditions(recordPhysicalConditions);
        Set<Long> expectedConditionList = Set.of(physicalCondition.getId());

        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.empty());
        when(healthRecordService.getMostRecentHealthRecord(testUser)).thenReturn(testHealthRecord);

        // When the application retrieves the preference and conditions form.
        UserPreferenceForm result = userPreferenceService.getUserPreferenceForm(testUser);

        // Then a DTO is returned containing an empty set of the users preference ID's and a set containing the ID for arthritis.
        assertTrue(result.getSelectedPreferenceIds().isEmpty());
        assertEquals(expectedConditionList, result.getSelectedConditionIds());
    }


    @Test
    public void userPreferenceShouldContainSelectedPreferences() {
        // Given that it is the users first time selecting their preferences, and they do not have a health record
        Preference expectedPreference = new Preference();
        expectedPreference.setId(1L);

        Set<Long> selectedIds = Set.of(expectedPreference.getId());
        List<Preference> preferenceList = List.of(expectedPreference);
        UserPreferenceForm userPreferenceForm = new UserPreferenceForm();
        userPreferenceForm.setSelectedPreferenceIds(selectedIds);

        when(preferenceRepository.findAllById(selectedIds)).thenReturn(preferenceList);
        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.empty());
        ArgumentCaptor<UserPreference> userPreferenceCaptor = ArgumentCaptor.forClass(UserPreference.class);

        // When the user selects one preference and no health condition.
        userPreferenceService.selectPreferences(testUser, userPreferenceForm);

        // Then this preference is saved and in the user's selected preferences.
        verify(userPreferenceRepository).save(userPreferenceCaptor.capture());
        UserPreference savedUserPreference = userPreferenceCaptor.getValue();
        assertEquals(1, savedUserPreference.getPreferences().size());
        assertTrue(savedUserPreference.getPreferences().contains(expectedPreference));

    }

    @Test
    public void userPreferenceShouldContainSelectedConditions() {
        // Given that it is the users first time selecting their physical condition preferences, and they do not have a health record
        PhysicalCondition expectedPhysicalCondition = new PhysicalCondition();
        expectedPhysicalCondition.setId(1L);

        Set<Long> selectedIds = Set.of(expectedPhysicalCondition.getId());
        List<PhysicalCondition> physicalConditionList = List.of(expectedPhysicalCondition);

        UserPreferenceForm userPreferenceForm = new UserPreferenceForm();
        userPreferenceForm.setSelectedConditionIds(selectedIds);

        when(physicalConditionRepository.findAllById(selectedIds)).thenReturn(physicalConditionList);
        when(userPreferenceRepository.getByUser(testUser)).thenReturn(Optional.empty());
        ArgumentCaptor<UserPreference> userPreferenceCaptor = ArgumentCaptor.forClass(UserPreference.class);

        // When the user selects one health condition and no preference.
        userPreferenceService.selectConditions(testUser, userPreferenceForm);

        // Then this preference is saved and in the user's selected preferences.
        verify(userPreferenceRepository).save(userPreferenceCaptor.capture());
        UserPreference savedUserPreference = userPreferenceCaptor.getValue();
        assertEquals(1, savedUserPreference.getPhysicalConditions().size());
        assertTrue(savedUserPreference.getPhysicalConditions().contains(expectedPhysicalCondition));
    }
}
