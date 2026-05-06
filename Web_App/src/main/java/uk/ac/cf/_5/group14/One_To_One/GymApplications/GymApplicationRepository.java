package uk.ac.cf._5.group14.One_To_One.GymApplications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GymApplicationRepository extends JpaRepository<GymApplication, Long> {
    Optional<GymApplication> findByAccessToken(String accessToken);
    List<GymApplication> findAllByOrderBySubmittedAtDesc();
    List<GymApplication> findByStatusOrderBySubmittedAtAsc(GymApplicationStatus status);
    long countByStatusIn(List<GymApplicationStatus> statuses);
}
