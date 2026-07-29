package uk.ac.cf._5.group14.One_To_One.Operations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.cf._5.group14.One_To_One.Security.LoginAttemptService;
import uk.ac.cf._5.group14.One_To_One.Security.PrivilegedAuditService;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class OperationalRetentionScheduler {

    private static final Duration LOGIN_ATTEMPT_RETENTION = Duration.ofDays(2);

    private final PrivilegedAuditService auditService;
    private final LoginAttemptService loginAttemptService;
    private final Clock clock;
    private final int auditRetentionDays;

    public OperationalRetentionScheduler(
            PrivilegedAuditService auditService,
            LoginAttemptService loginAttemptService,
            Clock clock,
            @Value("${app.operations.audit-retention-days:180}") int auditRetentionDays) {
        this.auditService = auditService;
        this.loginAttemptService = loginAttemptService;
        this.clock = clock;
        this.auditRetentionDays = auditRetentionDays;
    }

    @Scheduled(cron = "0 30 2 * * *")
    @ExclusiveScheduledJob(value = "operational-retention", lockAtMostFor = "PT20M")
    public void enforceRetention() {
        Instant now = Instant.now(clock);
        int auditsRemoved = auditService.purgeBefore(now.minus(Duration.ofDays(auditRetentionDays)));
        int loginAttemptsRemoved = loginAttemptService.purgeBefore(now.minus(LOGIN_ATTEMPT_RETENTION));
        log.info(
                "Operational retention removed {} audit event(s) and {} expired login-attempt row(s)",
                auditsRemoved,
                loginAttemptsRemoved);
    }
}
