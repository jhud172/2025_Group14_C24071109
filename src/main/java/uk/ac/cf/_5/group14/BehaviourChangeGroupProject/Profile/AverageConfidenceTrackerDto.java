package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public interface AverageConfidenceTrackerDto {
    Double getConfidence();

    LocalDate getDateRated();

    default String getMonth() {
        return getDateRated().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
