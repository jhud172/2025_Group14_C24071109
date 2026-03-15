package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DevMode;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dev_mode_page_settings")
@Getter
@Setter
@NoArgsConstructor
public class DevModePageSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_key", nullable = false, unique = true, length = 80)
    private String pageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_mode", nullable = false, length = 20)
    private DevModePageAccessMode accessMode = DevModePageAccessMode.ENABLED;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public DevModePageSetting(String pageKey, DevModePageAccessMode accessMode) {
        this.pageKey = pageKey;
        this.accessMode = accessMode;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
    }
}
