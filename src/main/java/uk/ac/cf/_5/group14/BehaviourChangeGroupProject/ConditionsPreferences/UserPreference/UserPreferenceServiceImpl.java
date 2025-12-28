package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.UserPreference;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference.PreferenceRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecordService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalConditionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final HealthRecordService healthRecordService;
    private final UserPreferenceRepository userPreferenceRepository;
    private final PreferenceRepository preferenceRepository;
    private final PhysicalConditionRepository physicalConditionRepository;

    public UserPreferenceServiceImpl(HealthRecordService healthRecordService, UserPreferenceRepository userPreferenceRepository, PreferenceRepository preferenceRepository, PhysicalConditionRepository physicalConditionRepository) {
        this.healthRecordService = healthRecordService;
        this.userPreferenceRepository = userPreferenceRepository;
        this.preferenceRepository = preferenceRepository;
        this.physicalConditionRepository = physicalConditionRepository;
    }

    private UserPreference getCurrentUserPreference(User user) {
        return userPreferenceRepository.getByUser(user).orElseGet(() -> createUserPreference(user));
    }

    private UserPreference createUserPreference(User user) {
        UserPreference userPreference = new UserPreference();
        userPreference.setUser(user);
        return userPreference;
    }

    public List<PhysicalCondition> getUsersPhysicalConditions(User user) {
        UserPreference userPreference = getCurrentUserPreference(user);
        return userPreference.getPhysicalConditions();
    }

    public List<Preference> getUserPreferences(User user) {
        UserPreference userPreference = getCurrentUserPreference(user);
        return userPreference.getPreferences();
    }

    public Map<String, List<Preference>> getUserPreferencesByCategory(User user) {
        UserPreference userPreference = getCurrentUserPreference(user);
        return userPreference.getPreferences().stream().collect(Collectors.groupingBy(Preference::getCategory));
    }


    public UserPreferenceForm getUserPreferenceForm(User user) {
        UserPreferenceForm userPreferenceForm = new UserPreferenceForm();
        UserPreference userPreference = getCurrentUserPreference(user);

        Set<Long> currentPreferenceIds = userPreference.getPreferences().stream()
                .map(Preference::getId).collect(Collectors.toSet());

        Set<Long> currentConditionIds = userPreference.getPhysicalConditions().stream()
                .map(PhysicalCondition::getId).collect(Collectors.toSet());

        currentConditionIds.addAll(getLockedConditions(user));

        userPreferenceForm.setSelectedConditionIds(currentConditionIds);
        userPreferenceForm.setSelectedPreferenceIds(currentPreferenceIds);

        return userPreferenceForm;
    }


    public void selectPreferences(User user, UserPreferenceForm userPreferenceForm) {
        Set<Long> selectedIds = new HashSet<>(userPreferenceForm.getSelectedPreferenceIds());
        List<Preference> selectedPreferences = preferenceRepository.findAllById(selectedIds);

        UserPreference userPreference = getCurrentUserPreference(user);

        userPreference.setPreferences(selectedPreferences);
        userPreferenceRepository.save(userPreference);
    }

    public Set<Long> getLockedConditions(User user) {
        Set<Long> lockedHealthConditions = new HashSet<>();
        HealthRecord recentHealthRecord = healthRecordService.getMostRecentHealthRecord(user);

        if (recentHealthRecord == null) {
            return Collections.emptySet();
        } else {
            for (PhysicalCondition physicalCondition : recentHealthRecord.getPhysicalConditions())
                lockedHealthConditions.add(physicalCondition.getId());
        }
        return lockedHealthConditions;
    }

    public void selectConditions(User user, UserPreferenceForm userPreferenceForm) {
        Set<Long> selectedIds = new HashSet<>(getLockedConditions(user));

        if (userPreferenceForm.getSelectedConditionIds() != null) {
            selectedIds.addAll(userPreferenceForm.getSelectedConditionIds());
        }

        List<PhysicalCondition> selectedConditions = physicalConditionRepository.findAllById(selectedIds);

        UserPreference userPreference = getCurrentUserPreference(user);

        userPreference.setPhysicalConditions(selectedConditions);
        userPreferenceRepository.save(userPreference);
    }

}
