package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.time.LocalDate;

public record ApplyScheduleActionPayload(
        String scheduleName,
        LocalDate startDate,
        int durationWeeks
) {}
