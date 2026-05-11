package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.Role;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserService;

import java.util.UUID;

@Service
public class SystemInboxNotificationService {

    private static final String SYSTEM_EMAIL = "platform-support@onetoone.app";
    private static final String SYSTEM_USERNAME = "platform-support";

    private final InboxService inboxService;
    private final UserRepository userRepository;
    private final UserService userService;

    public SystemInboxNotificationService(InboxService inboxService,
                                          UserRepository userRepository,
                                          UserService userService) {
        this.inboxService = inboxService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public void sendNotification(User recipient, String body) {
        if (recipient == null || recipient.getId() == null || body == null || body.isBlank()) {
            return;
        }
        User systemUser = getOrCreateSystemUser();
        Long conversationId = inboxService.startOrGetDirectConversation(systemUser, recipient.getId());
        inboxService.sendMessage(systemUser, conversationId, body);
    }

    @Transactional
    public User getOrCreateSystemUser() {
        User existing = userRepository.findByEmailIgnoreCase(SYSTEM_EMAIL).orElse(null);
        if (existing != null) {
            return existing;
        }

        User system = new User();
        system.setEmail(SYSTEM_EMAIL);
        system.setFirstName("Platform");
        system.setLastName("Support");
        system.setUsername(SYSTEM_USERNAME);
        system.setRole(Role.PLATFORM_ADMIN);
        system.setTrainerVerified(false);
        system.setPassword(UUID.randomUUID().toString());

        return userService.saveUser(system);
    }
}
