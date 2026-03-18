package uk.ac.cf._5.group14.One_To_One.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

        cleanupExistingUserImages(uploadRoot, userId);

        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String filename = "user-" + userId + "-" + System.currentTimeMillis() + "-" + uniqueSuffix + extension;
        Path target = uploadRoot.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/profile/" + filename;
    }

    @Override
    public boolean deleteProfileImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }
        String normalizedUrl = imageUrl.trim();
        if (!normalizedUrl.startsWith("/uploads/profile/")) {
            return false;
        }

        String fileName = normalizedUrl.substring("/uploads/profile/".length());
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }

        Path uploadRoot = Paths.get("uploads", "profile").toAbsolutePath().normalize();
        Path target = uploadRoot.resolve(fileName).normalize();
        if (!target.startsWith(uploadRoot)) {
            return false;
        }

        return Files.deleteIfExists(target);
    }

    private void cleanupExistingUserImages(Path uploadRoot, Long userId) throws IOException {
        if (uploadRoot == null || userId == null || !Files.exists(uploadRoot)) {
            return;
        }

        String prefix = "user-" + userId + "-";
        try (Stream<Path> fileStream = Files.list(uploadRoot)) {
            fileStream
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith(prefix))
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // best-effort cleanup only
                    }
                });
        }
    }
}
