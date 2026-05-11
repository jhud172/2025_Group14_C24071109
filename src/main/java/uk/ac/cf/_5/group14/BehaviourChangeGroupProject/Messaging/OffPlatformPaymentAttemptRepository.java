package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OffPlatformPaymentAttemptRepository extends JpaRepository<OffPlatformPaymentAttempt, Long> {
	List<OffPlatformPaymentAttempt> findAllByOrderByCreatedAtDesc();
}
