package uk.ac.cf._5.group14.One_To_One.WorkoutTemplate;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class WorkoutSessionViewModel {

    private Long sessionId;
    private String workoutName;
    private SessionStatus status;
    private Instant startedAt;
    private Instant endedAt;
    private Integer moodBefore;
    private Integer moodAfter;
    private Integer confidence;
    private BigDecimal totalVolume;
    private boolean allowCompletedWithoutLog;

    private String templateName;
    private String templateLayoutType;
    private String templateConfigJson;

    private List<ExerciseView> exercises;

    @Getter
    @Setter
    public static class ExerciseView {
        private Long id;
        private Long exerciseId;
        private Long customExerciseId;
        private String exerciseName;
        private String exerciseCategory;
        private int orderIndex;
        private String mode;
        private String groupKey;
        private String notes;
        private String demoUrl;
        private List<SetView> sets;
    }

    @Getter
    @Setter
    public static class SetView {
        private Long id;
        private int setIndex;
        private Integer reps;
        private BigDecimal weight;
        private BigDecimal rpe;
        private String tempo;
        private boolean isDrop;
        private Instant completedAt;
    }
}
