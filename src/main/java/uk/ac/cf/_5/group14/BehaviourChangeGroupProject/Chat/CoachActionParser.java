package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.util.Optional;

public interface CoachActionParser {
    Optional<CoachParsedAction> parse(String message);
}
