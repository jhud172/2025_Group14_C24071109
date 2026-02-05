package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trainer_library_exercise_notes")
public class TrainerLibraryExerciseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @NotBlank
    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    public TrainerLibraryExerciseNote() {
    }

    public TrainerLibraryExerciseNote(Long exerciseId, String noteText) {
        this.exerciseId = exerciseId;
        this.noteText = noteText;
    }

    public Long getId() {
        return id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
