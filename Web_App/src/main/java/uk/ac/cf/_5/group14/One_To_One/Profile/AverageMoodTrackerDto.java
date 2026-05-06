package uk.ac.cf._5.group14.One_To_One.Profile;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public interface AverageMoodTrackerDto {

    Double getMoodDifference();

    LocalDate getDateRated();

    default String getMonth(){
        return getDateRated().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
