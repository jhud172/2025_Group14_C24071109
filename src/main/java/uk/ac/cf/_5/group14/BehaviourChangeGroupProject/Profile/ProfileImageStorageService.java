package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class ProfileImageStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp");
    private static final long MAX_BYTES = 2 * 1024 * 1024;

    public String storeProfileImage(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || userId == null) {
            return null;
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Profile image too large (max 2MB).");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Unsupported image type. Use PNG, JPG, or WEBP.");
        }

        String extension = contentType.toLowerCase(Locale.ROOT).contains("png") ? ".png"
                : contentType.toLowerCase(Locale.ROOT).contains("webp") ? ".webp" : ".jpg";

        Path uploadRoot = Paths.get("uploads", "profile");
        Files.createDirectories(uploadRoot);

        String filename = "user-" + userId + "-" + System.currentTimeMillis() + extension;
        Path target = uploadRoot.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/profile/" + filename;
    }
}
