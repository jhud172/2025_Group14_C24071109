package uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "trainer_library_workout_items")
public class TrainerLibraryWorkoutItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "workout_id", nullable = false)
    private Long workoutId;

    @NotNull
    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @NotNull
    @Min(1)
    @Column(name = "sets", nullable = false)
    private Integer sets;

    @NotNull
    @Min(1)
    @Column(name = "reps", nullable = false)
    private Integer reps;

    @NotNull
    @Min(0)
    @Column(name = "rest_seconds", nullable = false)
    private Integer restSeconds;

    @Min(1)
    @Column(name = "rpe")
    private Integer rpe;

    @NotNull
    @Min(0)
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    public TrainerLibraryWorkoutItem() {
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

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Integer getRpe() {
        return rpe;
    }

    public void setRpe(Integer rpe) {
        this.rpe = rpe;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
