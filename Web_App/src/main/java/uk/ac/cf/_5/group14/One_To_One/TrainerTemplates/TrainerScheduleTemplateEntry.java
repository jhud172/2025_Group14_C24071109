package uk.ac.cf._5.group14.One_To_One.TrainerTemplates;

import jakarta.persistence.*;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;

import java.time.LocalTime;

@Entity
@Table(name = "trainer_schedule_template_entries")
public class TrainerScheduleTemplateEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "template_id")
    private TrainerScheduleTemplate template;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "time_window_start")
    private LocalTime timeWindowStart;

    @Column(name = "time_window_end")
    private LocalTime timeWindowEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrainerScheduleTemplateEntryType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "defaults_json", columnDefinition = "TEXT")
    private String defaultsJson;

    @Column(name = "intensity_label", length = 80)
    private String intensityLabel;

    @Column(name = "intensity_level")
    private Integer intensityLevel;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "custom_exercise_id")
    private CustomExercise customExercise;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    public Long getId() {
        return id;
    }

    public TrainerScheduleTemplate getTemplate() {
        return template;
    }

    public void setTemplate(TrainerScheduleTemplate template) {
        this.template = template;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(LocalTime timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public LocalTime getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(LocalTime timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public TrainerScheduleTemplateEntryType getType() {
        return type;
    }

    public void setType(TrainerScheduleTemplateEntryType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultsJson() {
        return defaultsJson;
    }

    public void setDefaultsJson(String defaultsJson) {
        this.defaultsJson = defaultsJson;
    }

    public String getIntensityLabel() {
        return intensityLabel;
    }

    public void setIntensityLabel(String intensityLabel) {
        this.intensityLabel = intensityLabel;
    }

    public Integer getIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(Integer intensityLevel) {
        this.intensityLevel = intensityLevel;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public CustomExercise getCustomExercise() {
        return customExercise;
    }

    public void setCustomExercise(CustomExercise customExercise) {
        this.customExercise = customExercise;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
