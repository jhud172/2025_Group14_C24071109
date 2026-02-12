package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Nutrition;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Service
public class ProteinTargetService {

    public static final int DEFAULT_TARGET_GRAMS = 160;

    public int resolveTargetGrams(User user) {
        // TODO: Replace with per-user preferences when available.
        return DEFAULT_TARGET_GRAMS;
    }
}
