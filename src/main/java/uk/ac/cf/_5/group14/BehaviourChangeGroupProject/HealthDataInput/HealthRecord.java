package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "health_records")
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime baselineDate;
    private Integer systolicBloodPressure;
    private Integer diastolicBloodPressure;
    private Double cholesterol;
    private Double weightKg;
    private Double heightCm;
    private Double bmi;
    private Double waistCm;
    private Double waistHeightRatio;
    private String activityLevel;

    @ManyToMany
    @JoinTable(name = "record_condition",
            joinColumns = @JoinColumn(name = "health_record_id"),
            inverseJoinColumns = @JoinColumn(name = "physical_condition_id"))
    private List<PhysicalCondition> physicalConditions = new ArrayList<>();
}
