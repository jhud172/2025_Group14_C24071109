package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class DailyUsageService {

    public record UsageStatus(boolean premium, int used, int limit, int remaining, boolean allowed) {}

    private final DailyUsageRepository repository;
    private final Clock clock;

    public DailyUsageService(DailyUsageRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public UsageStatus peek(Long userId, int limit, boolean premium) {
        if (premium) {
            return new UsageStatus(true, 0, limit, -1, true);
        }
        LocalDate today = LocalDate.now(clock);
        int used = repository.findByUserIdAndDate(userId, today)
                .map(DailyUsage::getUsedCount)
                .orElse(0);
        int remaining = Math.max(0, limit - used);
        return new UsageStatus(false, used, limit, remaining, remaining > 0);
    }

    @Transactional
    public UsageStatus consume(Long userId, int limit, boolean premium) {
        if (premium) {
            return new UsageStatus(true, 0, limit, -1, true);
        }
        LocalDate today = LocalDate.now(clock);
        Optional<DailyUsage> existing = repository.findByUserIdAndDate(userId, today);
        DailyUsage usage = existing.orElseGet(() -> {
            DailyUsage fresh = new DailyUsage();
            fresh.setUserId(userId);
            fresh.setDate(today);
            fresh.setUsedCount(0);
            return fresh;
        });
        if (usage.getUsedCount() >= limit) {
            return new UsageStatus(false, usage.getUsedCount(), limit, 0, false);
        }
        usage.setUsedCount(usage.getUsedCount() + 1);
        repository.save(usage);
        int remaining = Math.max(0, limit - usage.getUsedCount());
        return new UsageStatus(false, usage.getUsedCount(), limit, remaining, true);
    }
}
