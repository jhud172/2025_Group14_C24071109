package uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.PhysicalCondition;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.ExerciseData.Tag;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.HealthDataInput.HealthRecord;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "physical_conditions")
public class PhysicalCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "physicalConditions")
    private Set<HealthRecord> healthRecords = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "physical_condition_tag",
            joinColumns = @JoinColumn(name = "physical_condition_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();
}
