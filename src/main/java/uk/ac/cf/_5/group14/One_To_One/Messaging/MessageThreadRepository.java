package uk.ac.cf._5.group14.One_To_One.Messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    Optional<MessageThread> findByLinkId(Long linkId);

    List<MessageThread> findByTrainerIdAndStatusOrderByCreatedAtDesc(Long trainerId, MessageThreadStatus status);

    List<MessageThread> findByClientIdAndStatusOrderByCreatedAtDesc(Long clientId, MessageThreadStatus status);

    @Query("select t from MessageThread t where (t.trainerId = :userId or t.clientId = :userId) order by t.createdAt desc")
    List<MessageThread> findByUserId(@Param("userId") Long userId);
}
