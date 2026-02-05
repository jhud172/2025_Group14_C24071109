package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DataExport;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Instant;

@Entity
@Table(name = "data_export_requests")
@Getter
@Setter
public class DataExportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DataExportStatus status = DataExportStatus.REQUESTED;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @PrePersist
    public void onCreate() {
        if (requestedAt == null) {
            requestedAt = Instant.now();
        }
    }
}
