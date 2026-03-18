package uk.ac.cf._5.group14.One_To_One.Dashboard;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;

@Entity
@Table(
        name = "dashboard_layout",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_dashboard_layout_user_module", columnNames = {"user_id", "module_key"})
        }
)
@Getter
@Setter
public class DashboardLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "module_key", nullable = false, length = 80)
    private String moduleKey;

    @Column(name = "sort_index", nullable = false)
    private int sortIndex;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

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
