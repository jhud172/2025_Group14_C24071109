package uk.ac.cf._5.group14.One_To_One.Profile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface CholesterolTrackerDto {
    Double getCholesterol();

    LocalDateTime getDateRated();

    default String getMonthAndDay() {
        return getDateRated().format(DateTimeFormatter.ofPattern("dd MMM"));
    }
}
