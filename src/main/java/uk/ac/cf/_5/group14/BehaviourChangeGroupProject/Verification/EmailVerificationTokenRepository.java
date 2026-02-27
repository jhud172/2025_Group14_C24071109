package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findTopByUserOrderByCreatedAtDesc(User user);
}
