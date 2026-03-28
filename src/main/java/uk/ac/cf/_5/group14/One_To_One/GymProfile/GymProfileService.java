package uk.ac.cf._5.group14.One_To_One.GymProfile;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class GymProfileService {

    private final GymProfileRepository repository;

    public GymProfileService(GymProfileRepository repository) {
        this.repository = repository;
    }

    public GymProfile saveProfile(GymProfile profile) {
        String normalizedGymCode = normalizeGymCode(profile.getGymCode());
        if (normalizedGymCode == null) {
            profile.setGymCode(generateUniqueGymCode());
        } else {
            profile.setGymCode(normalizedGymCode);
        }
        return repository.save(profile);
    }

    public static String normalizeGymCode(String gymCode) {
        if (gymCode == null) {
            return null;
        }
        String normalized = gymCode.replaceAll("[^A-Za-z0-9]", "")
                .trim()
                .toUpperCase(Locale.ROOT);
        return normalized.isBlank() ? null : normalized;
    }

    private String generateUniqueGymCode() {
        String code;
        int attempts = 0;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
            attempts++;
            if (attempts > 100) {
                throw new IllegalStateException("Unable to generate unique gym code");
            }
        } while (repository.existsByGymCodeIgnoreCase(code));
        return code;
    }
}
