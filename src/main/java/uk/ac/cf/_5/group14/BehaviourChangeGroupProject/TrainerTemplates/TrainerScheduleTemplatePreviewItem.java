package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerTemplates;

import java.time.LocalDate;
import java.time.LocalTime;

public class TrainerScheduleTemplatePreviewItem {

    private final LocalDate date;
    private final TrainerScheduleTemplateEntryType type;
    private final String title;
    private final LocalTime timeWindowStart;
    private final LocalTime timeWindowEnd;
    private final boolean duplicate;

    public TrainerScheduleTemplatePreviewItem(LocalDate date,
                                              TrainerScheduleTemplateEntryType type,
                                              String title,
                                              LocalTime timeWindowStart,
                                              LocalTime timeWindowEnd,
                                              boolean duplicate) {
        this.date = date;
        this.type = type;
        this.title = title;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.duplicate = duplicate;
    }

    public LocalDate getDate() {
        return date;
    }

    public TrainerScheduleTemplateEntryType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public LocalTime getTimeWindowStart() {
        return timeWindowStart;
    }

    public LocalTime getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public boolean isDuplicate() {
        return duplicate;
    }
}
