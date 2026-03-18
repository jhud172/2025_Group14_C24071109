package uk.ac.cf._5.group14.One_To_One.Verification;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.util.Optional;

public interface PhoneVerificationCodeRepository extends JpaRepository<PhoneVerificationCode, Long> {
    Optional<PhoneVerificationCode> findTopByUserOrderByCreatedAtDesc(User user);
}
