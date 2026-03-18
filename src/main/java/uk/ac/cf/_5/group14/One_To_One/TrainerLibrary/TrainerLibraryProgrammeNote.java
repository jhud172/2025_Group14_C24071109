package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trainer_library_programme_notes")
public class TrainerLibraryProgrammeNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "programme_id", nullable = false)
    private Long programmeId;

    @NotBlank
    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    public TrainerLibraryProgrammeNote() {
    }

    public TrainerLibraryProgrammeNote(Long programmeId, String noteText) {
        this.programmeId = programmeId;
        this.noteText = noteText;
    }

    public Long getId() {
        return id;
    }

    public Long getProgrammeId() {
        return programmeId;
    }

    public void setProgrammeId(Long programmeId) {
        this.programmeId = programmeId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
