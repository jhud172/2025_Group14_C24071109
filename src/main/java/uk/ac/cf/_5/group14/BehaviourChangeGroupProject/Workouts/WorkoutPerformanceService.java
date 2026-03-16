package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notifications.NotificationType;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.text.DecimalFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutPerformanceService {

    private static final long PR_NOTIFICATION_WINDOW_MINUTES = 60;

    private final WorkoutSetLogRepository setLogRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    public WorkoutPerformanceService(WorkoutSetLogRepository setLogRepository,
                                     NotificationService notificationService,
                                     Clock clock) {
        this.setLogRepository = setLogRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    public WorkoutPerformanceHint buildHint(User user, String exerciseName, LocalDateTime cutoff) {
        if (user == null || user.getId() == null || exerciseName == null || exerciseName.isBlank()) {
            return null;
        }
        LocalDateTime resolvedCutoff = cutoff != null ? cutoff : LocalDateTime.now(clock);

        WorkoutSetLog last = setLogRepository
                .findLastCompletedBefore(user, exerciseName, resolvedCutoff, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        Double bestWeight = setLogRepository.findBestWeightBefore(user, exerciseName, resolvedCutoff);
        Double bestVolume = setLogRepository.findBestVolumeBefore(user, exerciseName, resolvedCutoff);
        Integer bestRepsAtWeight = (bestWeight != null)
                ? setLogRepository.findBestRepsAtWeightBefore(user, exerciseName, bestWeight, resolvedCutoff)
                : null;

        String lastSummary = formatLastSummary(last);
        String bestSummary = formatBestSummary(bestWeight, bestRepsAtWeight, bestVolume);

        if (lastSummary == null && bestSummary == null) {
            return null;
        }
        return new WorkoutPerformanceHint(lastSummary, bestSummary);
    }

    public void maybeNotifyPr(User user, WorkoutSession session, WorkoutSetLog setLog, boolean evaluate) {
        if (!evaluate || user == null || session == null || setLog == null) {
            return;
        }
        if (!setLog.isCompleted()) {
            return;
        }
        String exerciseName = setLog.getExerciseName();
        if (exerciseName == null || exerciseName.isBlank()) {
            return;
        }
        Double weight = setLog.getWeight();
        Integer reps = setLog.getReps();

        LocalDateTime cutoff = session.getStartedAt() != null ? session.getStartedAt() : LocalDateTime.now(clock);

        Double bestWeight = (weight != null)
                ? setLogRepository.findBestWeightBefore(user, exerciseName, cutoff)
                : null;
        Integer bestRepsAtWeight = (weight != null)
                ? setLogRepository.findBestRepsAtWeightBefore(user, exerciseName, weight, cutoff)
                : null;
        Double bestVolume = (weight != null && reps != null)
                ? setLogRepository.findBestVolumeBefore(user, exerciseName, cutoff)
                : null;

        boolean prWeight = weight != null && (bestWeight == null || weight > bestWeight);
        boolean prRepsAtWeight = weight != null && reps != null && (bestRepsAtWeight == null || reps > bestRepsAtWeight);
        boolean prVolume = weight != null && reps != null && (bestVolume == null || (weight * reps) > bestVolume);

        if (!prWeight && !prRepsAtWeight && !prVolume) {
            return;
        }

        List<String> badges = new ArrayList<>();
        if (prWeight) {
            badges.add("weight");
        }
        if (prRepsAtWeight) {
            badges.add("reps at weight");
        }
        if (prVolume) {
            badges.add("volume");
        }

        String message = "New PR on " + exerciseName + ": " + String.join(", ", badges) + ".";
        Instant after = Instant.now(clock).minusSeconds(PR_NOTIFICATION_WINDOW_MINUTES * 60);
        if (notificationService.existsRecent(user, NotificationType.PR_HIT, message, after)) {
            return;
        }

        String title = "Personal best";
        String ctaUrl = "/workouts/studio/" + session.getId();
        notificationService.create(user, NotificationType.PR_HIT, title, message, ctaUrl);
    }

    private String formatLastSummary(WorkoutSetLog last) {
        if (last == null) {
            return null;
        }
        Double weight = last.getWeight();
        Integer reps = last.getReps();
        if (weight != null && reps != null) {
            return "Last time: " + formatWeight(weight) + " kg x " + reps;
        }
        if (weight != null) {
            return "Last time: " + formatWeight(weight) + " kg";
        }
        if (reps != null) {
            return "Last time: " + reps + " reps";
        }
        return null;
    }

    private String formatBestSummary(Double bestWeight, Integer bestRepsAtWeight, Double bestVolume) {
        if (bestWeight != null && bestRepsAtWeight != null) {
            return "Best: " + formatWeight(bestWeight) + " kg x " + bestRepsAtWeight;
        }
        if (bestWeight != null) {
            return "Best: " + formatWeight(bestWeight) + " kg";
        }
        if (bestVolume != null) {
            return "Best volume: " + formatVolume(bestVolume) + " kg-reps";
        }
        return null;
    }

    private String formatWeight(Double value) {
        if (value == null) {
            return "0";
        }
        return new DecimalFormat("0.##").format(value);
    }

    private String formatVolume(Double value) {
        if (value == null) {
            return "0";
        }
        return new DecimalFormat("0.##").format(value);
    }
}
