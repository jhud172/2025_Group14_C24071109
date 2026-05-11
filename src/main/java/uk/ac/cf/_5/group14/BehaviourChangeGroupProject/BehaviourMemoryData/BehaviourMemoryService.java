package uk.ac.cf._5.group14.BehaviourChangeGroupProject.BehaviourMemoryData;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTask;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.CalendarTaskRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletion;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionRepository;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.CalendarData.DailyCompletionStatus;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BehaviourMemoryService {

    static final int DEFAULT_WINDOW_DAYS = 14;
    static final Duration AI_REFERENCE_COOLDOWN = Duration.ofMinutes(15);

    private final BehaviourMemoryRepository repository;
    private final DailyCompletionRepository dailyCompletionRepository;
    private final CalendarTaskRepository calendarTaskRepository;
    private final EntityManager entityManager;

    public BehaviourMemoryService(BehaviourMemoryRepository repository,
                                 DailyCompletionRepository dailyCompletionRepository,
                                 CalendarTaskRepository calendarTaskRepository,
                                 EntityManager entityManager) {
        this.repository = repository;
        this.dailyCompletionRepository = dailyCompletionRepository;
        this.calendarTaskRepository = calendarTaskRepository;
        this.entityManager = entityManager;
    }

    /**
     * Returns a short, non-sensitive aggregate summary for AI prompts.
     * Cooldown prevents this from being injected on every request.
     */
    @Transactional
    public Optional<String> maybeGetAiContext(User user) {
        return maybeGetAiContext(user, LocalDate.now(), Instant.now());
    }

    @Transactional
    Optional<String> maybeGetAiContext(User user, LocalDate asOfDate, Instant now) {
        if (user == null || user.getId() == null || asOfDate == null || now == null) {
            return Optional.empty();
        }

        BehaviourMemory memory = getOrUpdateMemory(user, asOfDate, DEFAULT_WINDOW_DAYS);

        Instant last = memory.getLastAiReferenceAt();
        if (last != null && now.isBefore(last.plus(AI_REFERENCE_COOLDOWN))) {
            return Optional.empty();
        }

        memory.setLastAiReferenceAt(now);
        repository.save(memory);

        return Optional.of(buildAiContext(memory));
    }

    @Transactional
    BehaviourMemory getOrUpdateMemory(User user, LocalDate asOfDate, int windowDays) {
        final int effectiveWindowDays = windowDays <= 0 ? DEFAULT_WINDOW_DAYS : windowDays;

        Optional<BehaviourMemory> existing = repository.findById(user.getId());
        BehaviourMemory memory = existing.orElseGet(() -> {
            BehaviourMemory m = new BehaviourMemory();
            m.setUser(entityManager.getReference(User.class, user.getId()));
            m.setAsOfDate(asOfDate);
            m.setWindowDays(effectiveWindowDays);
            return m;
        });

        boolean needsRefresh = existing.isEmpty()
                || memory.getAsOfDate() == null
                || !memory.getAsOfDate().equals(asOfDate)
                || memory.getWindowDays() != effectiveWindowDays;

        if (!needsRefresh) {
            return memory;
        }

        BehaviourMemory computed = compute(user, asOfDate, effectiveWindowDays);

        memory.setAsOfDate(asOfDate);
        memory.setWindowDays(effectiveWindowDays);

        memory.setGreenDays(computed.getGreenDays());
        memory.setOrangeDays(computed.getOrangeDays());
        memory.setRedDays(computed.getRedDays());
        memory.setGreyDays(computed.getGreyDays());

        memory.setAvgCompletionPercentage(computed.getAvgCompletionPercentage());
        memory.setAvgTasksPerDay(computed.getAvgTasksPerDay());
        memory.setHighLoadDays(computed.getHighLoadDays());
        memory.setTimePressureScore(computed.getTimePressureScore());

        return repository.save(memory);
    }

    private BehaviourMemory compute(User user, LocalDate asOfDate, int windowDays) {
        LocalDate start = asOfDate.minusDays(windowDays - 1L);

        List<DailyCompletion> completions = dailyCompletionRepository.findByUserAndDateBetween(user, start, asOfDate);
        Map<LocalDate, DailyCompletion> completionByDate = new HashMap<>();
        if (completions != null) {
            for (DailyCompletion c : completions) {
                if (c == null || c.getDate() == null) continue;
                completionByDate.put(c.getDate(), c);
            }
        }

        List<CalendarTask> tasks = calendarTaskRepository.findByUserAndDateBetween(user, start, asOfDate);
        Map<LocalDate, Integer> tasksPerDay = new HashMap<>();
        if (tasks != null) {
            for (CalendarTask t : tasks) {
                if (t == null || t.getDate() == null) continue;
                tasksPerDay.merge(t.getDate(), 1, Integer::sum);
            }
        }

        int green = 0;
        int orange = 0;
        int red = 0;
        int grey = 0;

        int sumCompletionPct = 0;
        int sumTasks = 0;
        int highLoadDays = 0;

        double timePressureAccum = 0;

        LocalDate d = start;
        while (!d.isAfter(asOfDate)) {
            DailyCompletion completion = completionByDate.get(d);
            DailyCompletionStatus status = completion == null ? DailyCompletionStatus.GREY : completion.getCompletionStatus();
            if (status == null) status = DailyCompletionStatus.GREY;

            int pct = completion == null ? 0 : completion.getCompletionPercentage();
            pct = clamp(pct, 0, 100);

            int taskCount = tasksPerDay.getOrDefault(d, 0);

            switch (status) {
                case GREEN -> green++;
                case ORANGE -> orange++;
                case RED -> red++;
                case GREY -> grey++;
            }

            sumCompletionPct += pct;
            sumTasks += taskCount;
            if (taskCount >= 6) highLoadDays++;

            // Time pressure proxy: more tasks + lower completion => higher pressure.
            // Scale tasks by an 8-task "full day" reference.
            double taskLoad = Math.min(1.0, taskCount / 8.0);
            double incomplete = 1.0 - (pct / 100.0);
            timePressureAccum += (taskLoad * incomplete);

            d = d.plusDays(1);
        }

        int avgCompletionPct = windowDays == 0 ? 0 : Math.round((float) sumCompletionPct / windowDays);
        double avgTasksPerDay = windowDays == 0 ? 0 : ((double) sumTasks / windowDays);
        int timePressureScore = clamp((int) Math.round(100.0 * (timePressureAccum / Math.max(1, windowDays))), 0, 100);

        BehaviourMemory out = new BehaviourMemory();
        out.setUser(user);
        out.setAsOfDate(asOfDate);
        out.setWindowDays(windowDays);
        out.setGreenDays(green);
        out.setOrangeDays(orange);
        out.setRedDays(red);
        out.setGreyDays(grey);
        out.setAvgCompletionPercentage(avgCompletionPct);
        out.setAvgTasksPerDay(avgTasksPerDay);
        out.setHighLoadDays(highLoadDays);
        out.setTimePressureScore(timePressureScore);
        return out;
    }

    private static String buildAiContext(BehaviourMemory m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Behaviour memory (last ").append(m.getWindowDays()).append(" days, aggregates):\n");
        sb.append("- Completion habits: ")
                .append(m.getGreenDays()).append(" GREEN, ")
                .append(m.getOrangeDays()).append(" ORANGE, ")
                .append(m.getRedDays()).append(" RED, ")
                .append(m.getGreyDays()).append(" GREY").append("\n");
        sb.append("- Success rate: avg ").append(m.getAvgCompletionPercentage()).append("% completion\n");
        sb.append("- Time pressure: avg ")
                .append(String.format(java.util.Locale.ROOT, "%.1f", m.getAvgTasksPerDay()))
                .append(" tasks/day; high-load days ")
                .append(m.getHighLoadDays())
                .append("; pressure score ")
                .append(m.getTimePressureScore())
                .append("/100\n");
        sb.append("Use these only as gentle context. Do not mention tracking or storage.");
        return sb.toString();
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
