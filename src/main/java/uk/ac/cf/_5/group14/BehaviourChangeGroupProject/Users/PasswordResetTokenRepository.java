package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = :usedAt where t.userId = :userId and t.usedAt is null")
    int markUsedForUser(@Param("userId") Long userId, @Param("usedAt") Instant usedAt);
}
