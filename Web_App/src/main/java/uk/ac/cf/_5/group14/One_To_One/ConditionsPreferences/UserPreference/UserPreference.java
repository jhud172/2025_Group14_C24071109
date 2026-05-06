package uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.UserPreference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.cf._5.group14.One_To_One.ConditionsPreferences.Preference.Preference;
import uk.ac.cf._5.group14.One_To_One.HealthDataInput.PhysicalCondition.PhysicalCondition;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_preferences")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @ManyToMany
    @JoinTable(name = "user_preference_conditions",
            joinColumns = @JoinColumn(name = "user_preference_id"),
            inverseJoinColumns = @JoinColumn(name = "physical_condition_id"))
    private List<PhysicalCondition> physicalConditions = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "selected_preferences",
            joinColumns = @JoinColumn(name = "user_preference_id"),
            inverseJoinColumns = @JoinColumn(name = "preference_id"))
    private List<Preference> preferences = new ArrayList<>();
}
