package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Waitlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "waitlist_emails")
@Getter
@Setter
@NoArgsConstructor
public class WaitlistEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "signed_up_at", nullable = false, updatable = false)
    private Instant signedUpAt;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed = false;

    public WaitlistEmail(String email) {
        this.email = email;
        this.signedUpAt = Instant.now();
    }
}
