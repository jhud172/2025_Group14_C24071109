package uk.ac.cf._5.group14.One_To_One.GymProfile;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class GymProfileService {
    private static final int GYM_CODE_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

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
        String normalized = gymCode.replaceAll("\\D", "").trim();
        if (normalized.isBlank()) {
            return null;
        }
        return normalized.length() == GYM_CODE_LENGTH ? normalized : null;
    }

    private String generateUniqueGymCode() {
        String code;
        int attempts = 0;
        do {
            StringBuilder builder = new StringBuilder(GYM_CODE_LENGTH);
            for (int i = 0; i < GYM_CODE_LENGTH; i++) {
                builder.append(RANDOM.nextInt(10));
            }
            code = builder.toString();
            attempts++;
            if (attempts > 100) {
                throw new IllegalStateException("Unable to generate unique gym code");
            }
        } while (repository.existsByGymCodeIgnoreCase(code));
        return code;
    }
}
