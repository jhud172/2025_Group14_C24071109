package uk.ac.cf._5.group14.BehaviourChangeGroupProject.ConditionsPreferences.Preference;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreferenceServiceImpl implements PreferenceService {
    private final PreferenceRepository preferenceRepository;

    public PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<Preference> getAllPreferences() {
        return preferenceRepository.findAll();
    }

    public Map<String, List<Preference>> getPreferencesByCategory() {
        return preferenceRepository.findAll().stream().collect(Collectors.groupingBy(Preference::getCategory));
    }
}
