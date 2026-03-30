package uk.ac.cf._5.group14.One_To_One.GymApplications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GymApplicationMessageRepository extends JpaRepository<GymApplicationMessage, Long> {
    List<GymApplicationMessage> findByApplicationIdOrderByCreatedAtAsc(Long applicationId);
}
