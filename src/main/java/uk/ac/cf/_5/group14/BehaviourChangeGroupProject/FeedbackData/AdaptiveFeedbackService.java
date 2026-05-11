package uk.ac.cf._5.group14.BehaviourChangeGroupProject.FeedbackData;

import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AdaptiveFeedbackService {

    private static final int RECENT_HISTORY_LIMIT = 7;

    private final AdaptiveFeedbackRepository repository;
    private final AdaptiveFeedbackAiService aiService;

    public AdaptiveFeedbackService(AdaptiveFeedbackRepository repository, AdaptiveFeedbackAiService aiService) {
        this.repository = repository;
        this.aiService = aiService;
    }

    public String getOrGenerateForTodayHome(User user,
                                           LocalDate today,
                                           long logsLast7DaysCount,
                                           long logsThisWeekCount,
                                           int daysLoggedThisWeek,
                                           int bestMoodUplift,
                                           int weekPlannedCount,
                                           int weekCompletedCount,
                                           int consistencyScore,
                                           boolean todayHasWorkoutToLog) {

        if (user == null || user.getId() == null || today == null) {
            return null;
        }

        Long userId = user.getId();

        Optional<AdaptiveFeedback> existing = repository.findByUserIdAndDate(userId, today);
        if (existing.isPresent()) {
            return existing.get().getFeedbackText();
        }

        AdaptiveFeedbackTone tone = rotateTone(userId, today);

        List<AdaptiveFeedback> recent = repository.findTop7ByUserIdOrderByDateDesc(userId);
        List<String> avoid = new ArrayList<>();
        for (AdaptiveFeedback r : recent) {
            if (r == null) continue;
            String t = r.getFeedbackText();
            if (t != null && !t.isBlank()) {
                avoid.add(t.trim());
            }
            if (avoid.size() >= RECENT_HISTORY_LIMIT) break;
        }

        String userContext = buildHomeContext(
                logsLast7DaysCount,
                logsThisWeekCount,
                daysLoggedThisWeek,
                bestMoodUplift,
                weekPlannedCount,
                weekCompletedCount,
                consistencyScore,
                todayHasWorkoutToLog
        );

        String feedback = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            feedback = aiService.suggestFeedback(today, tone, userContext, avoid);
            if (feedback == null || feedback.isBlank()) {
                continue;
            }

            String normalized = normalize(feedback);
            boolean repeats = avoid.stream().anyMatch(a -> normalize(a).equals(normalized));
            if (repeats) {
                avoid.add(feedback);
                continue;
            }

            if (!isSupportive(feedback)) {
                feedback = null;
                continue;
            }

            break;
        }

        if (feedback == null || feedback.isBlank()) {
            feedback = fallbackHomeFeedback(tone, weekPlannedCount, weekCompletedCount, consistencyScore, todayHasWorkoutToLog);
        }

        String hash = sha256Hex(normalize(feedback));
        AdaptiveFeedback row = new AdaptiveFeedback(userId, today, feedback.trim(), tone.name(), hash);
        repository.save(row);

        return feedback.trim();
    }

    private static AdaptiveFeedbackTone rotateTone(Long userId, LocalDate date) {
        AdaptiveFeedbackTone[] tones = AdaptiveFeedbackTone.values();
        int seed = (int) (userId == null ? 0 : (userId % 997));
        int idx = Math.floorMod(seed + (date == null ? 0 : date.getDayOfYear()), tones.length);
        return tones[idx];
    }

    private static String buildHomeContext(long logsLast7DaysCount,
                                          long logsThisWeekCount,
                                          int daysLoggedThisWeek,
                                          int bestMoodUplift,
                                          int weekPlannedCount,
                                          int weekCompletedCount,
                                          int consistencyScore,
                                          boolean todayHasWorkoutToLog) {

        return "Logs last 7 days: " + logsLast7DaysCount + "\n" +
                "Logs this week: " + logsThisWeekCount + "\n" +
                "Days logged this week: " + daysLoggedThisWeek + "\n" +
                "Best mood uplift this week: " + bestMoodUplift + "\n" +
                "Planned this week: " + weekPlannedCount + "\n" +
                "Completed this week: " + weekCompletedCount + "\n" +
                "Consistency score: " + consistencyScore + "\n" +
                "Has workout to log today: " + todayHasWorkoutToLog;
    }

    private static boolean isSupportive(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase(Locale.ROOT);

        // Basic guardrails: avoid shaming language.
        String[] banned = {"lazy", "pathetic", "failure", "worthless", "shame", "disgusting"};
        for (String b : banned) {
            if (lower.contains(b)) {
                return false;
            }
        }
        return true;
    }

    private static String fallbackHomeFeedback(AdaptiveFeedbackTone tone,
                                              int weekPlannedCount,
                                              int weekCompletedCount,
                                              int consistencyScore,
                                              boolean todayHasWorkoutToLog) {

        boolean hasPlan = weekPlannedCount > 0;
        boolean highConsistency = consistencyScore >= 70;

        String nudge = todayHasWorkoutToLog
                ? "Quick win: log your workout today to keep momentum."
                : "Pick one small action today and finish it.";

        return switch (tone == null ? AdaptiveFeedbackTone.ENCOURAGING : tone) {
            case COACHING -> {
                if (!hasPlan) yield "Plan 1 small thing for today, then do it. " + nudge;
                if (highConsistency) yield "You’re building consistency — keep it simple and repeatable. " + nudge;
                yield "Focus on one priority and get it done, then reassess. " + nudge;
            }
            case GENTLE -> {
                if (!hasPlan) yield "No pressure — start with one small plan for today. " + nudge;
                if (highConsistency) yield "You’re doing well — keep going at a steady pace. " + nudge;
                yield "Be kind to yourself and aim for one clear win today. " + nudge;
            }
            default -> {
                if (!hasPlan) yield "Let’s build momentum: plan one small thing for today. " + nudge;
                if (highConsistency) yield "Nice work — your consistency is paying off. " + nudge;
                yield "You’ve got this — pick one win and lock it in. " + nudge;
            }
        };
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((s == null ? "" : s).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return "";
        }
    }
}
