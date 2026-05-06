package uk.ac.cf._5.group14.One_To_One.Chat;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTaskActionPayload(
        LocalDate date,
        LocalTime time,
        String title,
        String description,
        String requirements
) {}
