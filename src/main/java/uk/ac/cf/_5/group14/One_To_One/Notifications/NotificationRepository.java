package uk.ac.cf._5.group14.One_To_One.Notifications;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndReadAtIsNullAndDismissedAtIsNull(User user);

    Optional<Notification> findByIdAndUser(Long id, User user);

    boolean existsByUserAndTypeAndMessageAndCreatedAtAfter(User user, NotificationType type, String message, Instant after);

    boolean existsByUserAndTypeAndCreatedAtAfter(User user, NotificationType type, Instant after);

    @Modifying
    @Query("update Notification n set n.readAt = :now where n.user = :user and n.readAt is null and n.dismissedAt is null")
    int markAllRead(@Param("user") User user, @Param("now") Instant now);
}
