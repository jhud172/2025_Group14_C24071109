package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLink;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerClient.TrainerClientLinkStatus;

import java.util.List;

@Service
public class MessagingService {

    private final MessageThreadRepository threadRepository;
    private final ThreadMessageRepository threadMessageRepository;
    private final TrainerClientLinkRepository linkRepository;
    private final OffPlatformPaymentAttemptRepository offPlatformPaymentAttemptRepository;

    public MessagingService(MessageThreadRepository threadRepository,
                            ThreadMessageRepository threadMessageRepository,
                            TrainerClientLinkRepository linkRepository,
                            OffPlatformPaymentAttemptRepository offPlatformPaymentAttemptRepository) {
        this.threadRepository = threadRepository;
        this.threadMessageRepository = threadMessageRepository;
        this.linkRepository = linkRepository;
        this.offPlatformPaymentAttemptRepository = offPlatformPaymentAttemptRepository;
    }

    @Transactional
    public MessageThread ensureThreadForLink(TrainerClientLink link) {
        MessageThreadStatus desired = desiredThreadStatus(link.getStatus());
        return threadRepository.findByLinkId(link.getId())
                .map(existing -> {
                    if (existing.getStatus() != desired) {
                        existing.setStatus(desired);
                        return threadRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> threadRepository.save(new MessageThread(
                        link.getClientUserId(),
                        link.getTrainerUserId(),
                        link.getId(),
                        desired)));
    }

    @Transactional
    public MessageThread getThreadForUser(Long threadId, Long userId) {
        MessageThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

        TrainerClientLink link = linkRepository.findById(thread.getLinkId())
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));

        // Keep thread status in sync with link status (defensive in case link was edited elsewhere).
        MessageThreadStatus desired = desiredThreadStatus(link.getStatus());
        if (thread.getStatus() != desired) {
            thread.setStatus(desired);
            thread = threadRepository.save(thread);
        }

        requireParticipant(thread, userId);
        return thread;
    }

    public List<MessageThread> getTrainerInboxThreads(Long trainerId) {
        return threadRepository.findByTrainerIdAndStatusOrderByCreatedAtDesc(trainerId, MessageThreadStatus.OPEN);
    }

    public List<MessageThread> getClientInboxThreads(Long clientId) {
        return threadRepository.findByClientIdAndStatusOrderByCreatedAtDesc(clientId, MessageThreadStatus.OPEN);
    }

    public List<Message> getMessagesForThread(Long threadId, Long userId) {
        MessageThread thread = getThreadForUser(threadId, userId);
        return threadMessageRepository.findByThread_IdOrderByCreatedAtAsc(thread.getId());
    }

    @Transactional
    public void sendMessage(Long threadId, Long senderUserId, MessageType type, String bodyText) {
        MessageThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

        requireParticipant(thread, senderUserId);

        TrainerClientLink link = linkRepository.findById(thread.getLinkId())
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));

        // Thread must be OPEN and link must be ACTIVE to send.
        if (desiredThreadStatus(link.getStatus()) == MessageThreadStatus.LOCKED) {
            thread.setStatus(MessageThreadStatus.LOCKED);
            threadRepository.save(thread);
            throw new IllegalStateException("THREAD_LOCKED");
        }
        if (thread.getStatus() != MessageThreadStatus.OPEN) {
            throw new IllegalStateException("THREAD_LOCKED");
        }
        if (link.getStatus() != TrainerClientLinkStatus.ACTIVE) {
            throw new IllegalStateException("THREAD_NOT_ACTIVE");
        }

        if (bodyText == null || bodyText.isBlank()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }
        String trimmed = bodyText.trim();
        String matched = PaymentKeywordDetector.firstMatch(trimmed);
        if (matched != null) {
            offPlatformPaymentAttemptRepository.save(new OffPlatformPaymentAttempt(
                    thread.getId(),
                    senderUserId,
                    matched,
                    trimmed
            ));
            throw new IllegalStateException("OFF_PLATFORM_PAYMENT");
        }

        threadMessageRepository.save(new Message(thread, senderUserId, type, trimmed));
    }

    private void requireParticipant(MessageThread thread, Long userId) {
        if (userId == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        boolean isParticipant = userId.equals(thread.getClientId()) || userId.equals(thread.getTrainerId());
        if (!isParticipant) {
            throw new AccessDeniedException("Not a participant in this thread");
        }
    }

    private static MessageThreadStatus desiredThreadStatus(TrainerClientLinkStatus linkStatus) {
        return linkStatus == TrainerClientLinkStatus.ACTIVE ? MessageThreadStatus.OPEN : MessageThreadStatus.LOCKED;
    }
}
