package uk.ac.cf._5.group14.One_To_One.Workout;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExercise;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExerciseRequest;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExerciseService;
import uk.ac.cf._5.group14.One_To_One.CustomExerciseData.CustomExerciseView;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.Exercise;
import uk.ac.cf._5.group14.One_To_One.ExerciseData.ExerciseService;
import uk.ac.cf._5.group14.One_To_One.PlatformBilling.PlatformSubscriptionService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.net.URI;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class WorkoutController {

    private final ExerciseService exerciseService;
    private final CustomExerciseService customExerciseService;
    private final WorkoutService workoutService;
    private final WorkoutAiSuggestionService workoutAiSuggestionService;
    private final PlatformSubscriptionService platformSubscriptionService;
    private final Clock clock;
    private final AuthHelper authHelper;

    @GetMapping("/workout")
    public ModelAndView createWorkout() {
        ModelAndView mav = new ModelAndView("schedule/workout");
        User user = authHelper.getAuthenticatedUser();
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);

        List<Exercise> suggestedExercises = exerciseService.suggestExercises(user);
        mav.addObject("suggestedExercises", suggestedExercises);

        List<Workout> usersWorkouts = workoutService.getWorkouts();
        mav.addObject("usersWorkouts", usersWorkouts);

        List<CustomExerciseView> customExercises = customExerciseService.getCustomExercisesByUser(user.getId())
            .stream()
            .map(this::toView)
            .collect(Collectors.toList());
        mav.addObject("customExercises", customExercises);
        mav.addObject("isPremium", isPremium);

        return mav;
    }

    @GetMapping("/workout/create")
    public String getCreateFragment() {
        return "fragments/workout/workout-frags :: createWorkout";
    }

    @GetMapping("/workout/edit/{id}")
    public String editWorkout(@PathVariable Long id, Model model) {
        Workout workout = workoutService.getWorkoutToEdit(id);
        model.addAttribute("workout", workout);
        Map<Long, String> customExerciseEmbeds = workout.getCustomExercises() == null
                ? Map.of()
                : workout.getCustomExercises().stream()
                .collect(Collectors.toMap(CustomExercise::getId, ex -> toEmbedUrl(ex.getVideoUrl())));
        model.addAttribute("customExerciseEmbeds", customExerciseEmbeds);

        return "fragments/workout/workout-frags.html :: editWorkout";
    }

    @PostMapping("/save-workout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveWorkout(
            @RequestBody SaveWorkoutDTO dto
    )
    {
        workoutService.saveWorkout(dto);
        return ResponseEntity.ok(Map.of("message", "Workout saved successfully"));
    }

    @PostMapping("/delete-workout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteWorkout(
            @RequestBody Map <String, Long> payload
    )
    {
        Long id = payload.get("id");
        workoutService.deleteWorkout(id);
        return ResponseEntity.ok(Map.of("message", "Workout deleted successfully"));
    }

    @GetMapping("/workout/custom-exercises")
    @ResponseBody
    public ResponseEntity<List<CustomExerciseView>> listCustomExercises() {
        User user = authHelper.getAuthenticatedUser();
        List<CustomExerciseView> views = customExerciseService.getCustomExercisesByUser(user.getId())
                .stream()
                .map(this::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(views);
    }

    @PostMapping("/workout/custom-exercises")
    @ResponseBody
    public ResponseEntity<?> createCustomExercise(@RequestBody CustomExerciseRequest request) {
        User user = authHelper.getAuthenticatedUser();
        if (request == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Name is required."));
        }

        String sanitizedVideo = sanitizeVideoUrl(request.videoUrl());
        if (request.videoUrl() != null && !request.videoUrl().isBlank() && sanitizedVideo == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Video URL must be a valid YouTube or Vimeo link."));
        }

        CustomExercise exercise = new CustomExercise();
        exercise.setUserId(user.getId());
        exercise.setName(request.name().trim());
        exercise.setDescription(trimToNull(request.description()));
        exercise.setHowTo(trimToNull(request.howTo()));
        exercise.setVideoUrl(sanitizedVideo);

        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        exercise.setColorTag(isPremium ? trimToNull(request.colorTag()) : null);

        CustomExercise saved = customExerciseService.saveCustomExercise(exercise);
        return ResponseEntity.ok(toView(saved));
    }

    @PostMapping("/workout/custom-exercises/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCustomExercise(@PathVariable Long id, @RequestBody CustomExerciseRequest request) {
        User user = authHelper.getAuthenticatedUser();
        if (request == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Name is required."));
        }

        String sanitizedVideo = sanitizeVideoUrl(request.videoUrl());
        if (request.videoUrl() != null && !request.videoUrl().isBlank() && sanitizedVideo == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Video URL must be a valid YouTube or Vimeo link."));
        }

        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        CustomExerciseRequest adjusted = new CustomExerciseRequest(
                request.name().trim(),
                trimToNull(request.description()),
                trimToNull(request.howTo()),
                sanitizedVideo,
                isPremium ? trimToNull(request.colorTag()) : null
        );

        CustomExercise updated = customExerciseService.updateCustomExercise(user.getId(), id, adjusted);
        if (updated == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Custom exercise not found."));
        }

        return ResponseEntity.ok(toView(updated));
    }

    @PostMapping("/workout/custom-exercises/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCustomExercise(@PathVariable Long id) {
        User user = authHelper.getAuthenticatedUser();
        customExerciseService.deleteCustomExercise(user.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Custom exercise deleted."));
    }

    @PostMapping("/workout/ai-suggestions")
    @ResponseBody
    public ResponseEntity<?> aiSuggestions(@RequestBody(required = false) Map<String, String> payload) {
        User user = authHelper.getAuthenticatedUser();
        boolean isPremium = platformSubscriptionService.isPremium(user.getId(), clock);
        if (!isPremium) {
            return ResponseEntity.status(403).body(Map.of("message", "Premium required"));
        }
        String prompt = payload != null ? payload.getOrDefault("prompt", "") : "";
        List<String> suggestions = workoutAiSuggestionService.generateSuggestions(prompt);
        return ResponseEntity.ok(Map.of("suggestions", suggestions));
    }

    private CustomExerciseView toView(CustomExercise exercise) {
        if (exercise == null) {
            return null;
        }
        String embedUrl = toEmbedUrl(exercise.getVideoUrl());
        return new CustomExerciseView(
                exercise.getId(),
                exercise.getName(),
                exercise.getDescription(),
                exercise.getHowTo(),
                exercise.getVideoUrl(),
                embedUrl,
                exercise.getColorTag()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String sanitizeVideoUrl(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            URI uri = URI.create(trimmed);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return null;
            }
            String host = uri.getHost();
            if (host == null) {
                return null;
            }
            String normalizedHost = host.toLowerCase();
            if (!ALLOWED_VIDEO_HOSTS.contains(normalizedHost)) {
                return null;
            }
            return trimmed;
        } catch (Exception ex) {
            return null;
        }
    }

    private String toEmbedUrl(String value) {
        String safe = sanitizeVideoUrl(value);
        if (safe == null) {
            return null;
        }
        try {
            URI uri = URI.create(safe);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
            String path = uri.getPath() == null ? "" : uri.getPath();

            if (host.contains("youtu.be")) {
                String id = path.replaceFirst("/", "");
                return id.isBlank() ? null : "https://www.youtube.com/embed/" + id;
            }

            if (host.contains("youtube.com")) {
                String query = uri.getQuery();
                if (query != null) {
                    for (String part : query.split("&")) {
                        String[] kv = part.split("=");
                        if (kv.length == 2 && kv[0].equals("v")) {
                            return "https://www.youtube.com/embed/" + kv[1];
                        }
                    }
                }
                if (path.startsWith("/embed/")) {
                    return "https://www.youtube.com" + path;
                }
            }

            if (host.contains("vimeo.com")) {
                String id = path.replaceFirst("/", "");
                if (id.startsWith("video/")) {
                    id = id.substring("video/".length());
                }
                return id.isBlank() ? null : "https://player.vimeo.com/video/" + id;
            }
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }

    private static final Set<String> ALLOWED_VIDEO_HOSTS = Set.of(
            "youtube.com",
            "www.youtube.com",
            "m.youtube.com",
            "youtu.be",
            "vimeo.com",
            "www.vimeo.com",
            "player.vimeo.com"
    );
}
