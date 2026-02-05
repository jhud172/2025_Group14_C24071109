package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoachActionLogService {

    private final CoachActionLogRepository repository;

    public CoachActionLogService(CoachActionLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CoachActionLog log(Long userId,
                              Long conversationId,
                              CoachActionType type,
                              String payloadJson,
                              boolean success,
                              String errorMessage) {
        CoachActionLog log = new CoachActionLog();
        log.setUserId(userId);
        log.setConversationId(conversationId);
        log.setActionType(type.name());
        log.setPayloadJson(payloadJson == null ? "{}" : payloadJson);
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        return repository.save(log);
    }
}
