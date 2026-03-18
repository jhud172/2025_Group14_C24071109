package uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.Preference;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreferenceServiceImpl implements PreferenceService {
    private final PreferenceRepository preferenceRepository;

    // Define display order for categories so the form renders predictably
    private static final List<String> CATEGORY_ORDER = List.of(
        "Goal", "Experience Level", "Workout Frequency",
        "Workout Style", "Diet / Nutrition", "Recovery"
    );

    public PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<Preference> getAllPreferences() {
        return preferenceRepository.findAll();
    }

    public Map<String, List<Preference>> getPreferencesByCategory() {
        Map<String, List<Preference>> unordered = preferenceRepository.findAll().stream()
            .collect(Collectors.groupingBy(Preference::getCategory));

        // Return categories in defined order, then any remaining categories alphabetically
        Map<String, List<Preference>> ordered = new LinkedHashMap<>();
        for (String cat : CATEGORY_ORDER) {
            if (unordered.containsKey(cat)) {
                ordered.put(cat, unordered.get(cat));
            }
        }
        unordered.entrySet().stream()
            .filter(e -> !ordered.containsKey(e.getKey()))
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> ordered.put(e.getKey(), e.getValue()));
        return ordered;
    }
}
