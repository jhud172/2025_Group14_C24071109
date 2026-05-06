package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trainer_library_workout_notes")
public class TrainerLibraryWorkoutNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "workout_id", nullable = false)
    private Long workoutId;

    @NotBlank
    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    public TrainerLibraryWorkoutNote() {
    }

    public TrainerLibraryWorkoutNote(Long workoutId, String noteText) {
        this.workoutId = workoutId;
        this.noteText = noteText;
    }

    public Long getId() {
        return id;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
