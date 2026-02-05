package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Accountability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserStreakRepository extends JpaRepository<UserStreak, UserStreakKey> {

    Optional<UserStreak> findByUserIdAndStreakType(Long userId, StreakType streakType);

    List<UserStreak> findByUserId(Long userId);
}
