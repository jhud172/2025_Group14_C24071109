package uk.ac.cf._5.group14.One_To_One.Security;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatImageStorageService;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutFormFeedbackService;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class PrivateUploadController {

    private final AuthHelper authHelper;
    private final ChatImageStorageService chatImageStorageService;
    private final WorkoutFormFeedbackService workoutFormFeedbackService;

    public PrivateUploadController(
            AuthHelper authHelper,
            ChatImageStorageService chatImageStorageService,
            WorkoutFormFeedbackService workoutFormFeedbackService
    ) {
        this.authHelper = authHelper;
        this.chatImageStorageService = chatImageStorageService;
        this.workoutFormFeedbackService = workoutFormFeedbackService;
    }

    @GetMapping("/uploads/chat/{filename:.+}")
    public ResponseEntity<Resource> chatImage(@PathVariable String filename) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return serve(chatImageStorageService.resolveOwnedChatImage(filename, user.getId()));
        } catch (IllegalArgumentException | MalformedURLException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/uploads/workout-videos/user-{ownerUserId}/session-{sessionId}/{filename:.+}")
    public ResponseEntity<Resource> workoutVideo(
            @PathVariable Long ownerUserId,
            @PathVariable Long sessionId,
            @PathVariable String filename
    ) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            Path path = workoutFormFeedbackService.resolveOwnedVideo(
                    user, ownerUserId, sessionId, filename);
            return serve(path);
        } catch (IllegalArgumentException | MalformedURLException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> serve(Path path) throws MalformedURLException {
        if (path == null || !Files.isRegularFile(path)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(path.toUri());
        MediaType contentType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(contentType)
                .body(resource);
    }
}
