package uk.ac.cf._5.group14.One_To_One.Nutrition;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "daily_nutrition_logs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_daily_nutrition_user_date", columnNames = {"user_id", "log_date"})
    }
)
@Getter
@Setter
public class DailyNutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate date;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "protein_grams", nullable = false)
    private Integer proteinGrams;

    @Column(name = "carbs_grams", nullable = false)
    private Integer carbsGrams;

    @Column(name = "fat_grams", nullable = false)
    private Integer fatGrams;

    @Column(name = "fibre_grams")
    private Integer fibreGrams;

    @Column(name = "water_ml")
    private Integer waterMl;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
