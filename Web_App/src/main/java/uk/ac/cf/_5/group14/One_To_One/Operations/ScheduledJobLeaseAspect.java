package uk.ac.cf._5.group14.One_To_One.Operations;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ScheduledJobLeaseAspect {

    private final ScheduledJobLeaseService leaseService;

    public ScheduledJobLeaseAspect(ScheduledJobLeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @Around("@annotation(exclusiveScheduledJob)")
    public Object runWithLease(
            ProceedingJoinPoint joinPoint,
            ExclusiveScheduledJob exclusiveScheduledJob) throws Throwable {
        String jobName = exclusiveScheduledJob.value();
        Duration lockAtMostFor = Duration.parse(exclusiveScheduledJob.lockAtMostFor());

        final boolean acquired;
        try {
            acquired = leaseService.acquire(jobName, lockAtMostFor);
        } catch (RuntimeException ex) {
            log.error("Skipping scheduled job {} because database ownership could not be established", jobName, ex);
            return null;
        }
        if (!acquired) {
            log.debug("Skipping scheduled job {} because another instance owns its lease", jobName);
            return null;
        }

        try {
            return joinPoint.proceed();
        } finally {
            try {
                leaseService.release(jobName);
            } catch (RuntimeException ex) {
                log.error("Could not release scheduled job lease {}; expiry will recover ownership", jobName, ex);
            }
        }
    }
}
