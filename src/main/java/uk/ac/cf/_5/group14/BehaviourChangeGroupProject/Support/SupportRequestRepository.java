package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Support;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findAllByOrderBySubmittedAtDesc();
    long countByViewedFalse();
    long countByStatus(SupportRequestStatus status);
}
