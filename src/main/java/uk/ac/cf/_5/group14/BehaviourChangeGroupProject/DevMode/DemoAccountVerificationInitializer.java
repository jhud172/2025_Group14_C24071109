package uk.ac.cf._5.group14.BehaviourChangeGroupProject.DevMode;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoAccountVerificationInitializer implements ApplicationRunner {

    private static final List<String> DEMO_USERNAMES = List.of(
        "demo_admin",
        "demo_client",
        "demo_trainer",
        "demo_gym"
    );

    private final DevModeProperties devModeProperties;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!devModeProperties.isDevMode()) {
            return;
        }

        Instant verifiedAt = Instant.now();
        for (String username : DEMO_USERNAMES) {
            userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(user -> ensureEmailVerified(user, verifiedAt));
        }
    }

    private void ensureEmailVerified(User user, Instant verifiedAt) {
        boolean changed = false;

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            changed = true;
        }

        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(verifiedAt);
            changed = true;
        }

        if (!changed) {
            return;
        }

        userRepository.save(user);
        log.info("Marked demo account {} as email-verified for dev mode.", user.getUsername());
    }
}
