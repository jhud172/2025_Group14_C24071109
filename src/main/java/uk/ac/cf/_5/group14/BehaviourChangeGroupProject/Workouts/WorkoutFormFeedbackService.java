package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Workouts;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class WorkoutFormFeedbackService {

    private final WorkoutBuilderService workoutBuilderService;
    private final WorkoutSetLogRepository setLogRepository;
    private final WorkoutSetVideoRepository videoRepository;
    private final AiFormFeedbackRepository feedbackRepository;

    public WorkoutFormFeedbackService(WorkoutBuilderService workoutBuilderService,
                                      WorkoutSetLogRepository setLogRepository,
                                      WorkoutSetVideoRepository videoRepository,
                                      AiFormFeedbackRepository feedbackRepository) {
        this.workoutBuilderService = workoutBuilderService;
        this.setLogRepository = setLogRepository;
        this.videoRepository = videoRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public WorkoutSetVideo storeVideo(User user, Long sessionId, Long setId, MultipartFile file) throws IOException {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file uploaded");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("video/")) {
            throw new IllegalArgumentException("Unsupported video type");
        }

        WorkoutSession session = workoutBuilderService.getSession(user, sessionId);
        WorkoutSetLog setLog = setLogRepository.findByIdAndSession(setId, session)
                .orElseThrow(() -> new IllegalArgumentException("Set not found"));

        String extension = contentType.toLowerCase(Locale.ROOT).contains("mp4") ? ".mp4" : ".webm";
        Path uploadRoot = Paths.get("uploads", "workout-videos", "user-" + user.getId(), "session-" + sessionId);
        Files.createDirectories(uploadRoot);

        String filename = "set-" + setId + "-" + System.currentTimeMillis() + extension;
        Path target = uploadRoot.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        WorkoutSetVideo video = new WorkoutSetVideo();
        video.setSetLog(setLog);
        video.setStatus(VideoProcessingStatus.PENDING);
        video.setPath("/uploads/workout-videos/user-" + user.getId() + "/session-" + sessionId + "/" + filename);
        return videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public WorkoutSetVideo getLatestVideo(User user, Long sessionId, Long setId) {
        if (user == null || user.getId() == null) {
            return null;
        }
        WorkoutSession session = workoutBuilderService.getSession(user, sessionId);
        WorkoutSetLog setLog = setLogRepository.findByIdAndSession(setId, session).orElse(null);
        if (setLog == null) {
            return null;
        }
        return videoRepository.findTopBySetLogOrderByCreatedAtDesc(setLog).orElse(null);
    }

    @Transactional(readOnly = true)
    public AiFormFeedback getFeedback(WorkoutSetVideo video) {
        if (video == null) {
            return null;
        }
        return feedbackRepository.findByVideo(video).orElse(null);
    }

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void processPending() {
        List<WorkoutSetVideo> pending = videoRepository.findByStatusOrderByCreatedAtAsc(VideoProcessingStatus.PENDING);
        for (WorkoutSetVideo video : pending) {
            video.setStatus(VideoProcessingStatus.PROCESSING);
            videoRepository.save(video);

            AiFormFeedback feedback = new AiFormFeedback();
            feedback.setVideo(video);
            feedback.setRepCount(8);
            feedback.setTempo("2-1-2");
            feedback.setFlagsJson("{\"depth\":\"ok\",\"knees\":\"stable\",\"core\":\"braced\"}");
            feedback.setConfidence(0.78);
            feedback.setCreatedAt(Instant.now());
            feedbackRepository.save(feedback);

            video.setStatus(VideoProcessingStatus.COMPLETE);
            videoRepository.save(video);
        }
    }

    public Map<String, Object> buildFeedbackPayload(WorkoutSetVideo video, AiFormFeedback feedback) {
        if (video == null) {
            return Map.of("status", "NONE");
        }
        if (feedback == null) {
            return Map.of("status", video.getStatus().name());
        }
        return Map.of(
                "status", video.getStatus().name(),
                "feedback", Map.of(
                        "repCount", feedback.getRepCount(),
                        "tempo", feedback.getTempo(),
                        "flags", feedback.getFlagsJson(),
                        "confidence", feedback.getConfidence()
                )
        );
    }
}
