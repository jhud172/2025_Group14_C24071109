package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TrainerLibraryProgrammeTemplateForm {

    @NotBlank
    @Size(max = 120)
    private String title;

    @Min(1)
    private Integer weeks;

    /**
     * Newline-separated notes, stored as separate rows.
     */
    private String notesText;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWeeks() {
        return weeks;
    }

    public void setWeeks(Integer weeks) {
        this.weeks = weeks;
    }

    public String getNotesText() {
        return notesText;
    }

    public void setNotesText(String notesText) {
        this.notesText = notesText;
    }
}
