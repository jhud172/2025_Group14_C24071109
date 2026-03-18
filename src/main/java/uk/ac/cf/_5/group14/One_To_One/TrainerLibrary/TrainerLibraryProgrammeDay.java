package uk.ac.cf._5.group14.One_To_One.TrainerLibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "trainer_library_programme_days")
public class TrainerLibraryProgrammeDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "programme_id", nullable = false)
    private Long programmeId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private String dayOfWeek;

    @NotNull
    @Column(name = "workout_id", nullable = false)
    private Long workoutId;

    @NotNull
    @Min(0)
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    public TrainerLibraryProgrammeDay() {
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
