package uk.ac.cf._5.group14.One_To_One.Chat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ChatImageStorageService {

    private static final long MAX_BYTES = 4L * 1024L * 1024L;
    private static final int MAX_FILES = 5;

    private final Path uploadRoot;

    public ChatImageStorageService(@Value("${app.storage.chat-dir:uploads/chat}") String uploadRoot) {
        this.uploadRoot = Paths.get(uploadRoot).toAbsolutePath().normalize();
    }

    public List<ChatAttachmentPayload> storeChatImages(Long userId, List<MultipartFile> files) throws IOException {
        if (userId == null || files == null || files.isEmpty()) {
            return List.of();
        }

        List<MultipartFile> validFiles = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();
        if (validFiles.size() > MAX_FILES) {
            throw new IllegalArgumentException("You can upload up to 5 chat images at a time.");
        }

        List<ChatAttachmentPayload> attachments = new ArrayList<>();
        List<Path> storedPaths = new ArrayList<>();
        Files.createDirectories(uploadRoot);

        try {
            for (MultipartFile file : validFiles) {
                if (file.getSize() > MAX_BYTES) {
                    throw new IllegalArgumentException("Each chat image must be 4MB or smaller.");
                }

                byte[] uploadedBytes = file.getBytes();
                BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(uploadedBytes));
                if (decoded == null || decoded.getWidth() <= 0 || decoded.getHeight() <= 0) {
                    throw new IllegalArgumentException("Unsupported image data. Use PNG, JPG, or WEBP.");
                }

                SanitizedImage sanitized = sanitizeImage(decoded);
                String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                String filename = "chat-" + userId + "-" + System.currentTimeMillis() + "-" + uniqueSuffix + sanitized.extension();
                Path target = uploadRoot.resolve(filename).normalize();

                if (!target.startsWith(uploadRoot)) {
                    throw new IOException("Invalid target upload path.");
                }

                Files.write(target, sanitized.bytes());
                storedPaths.add(target);
                attachments.add(new ChatAttachmentPayload(
                        "/uploads/chat/" + filename,
                        safeFileName(file.getOriginalFilename(), filename),
                        sanitized.contentType()
                ));
            }
        } catch (IOException | RuntimeException failure) {
            for (Path storedPath : storedPaths) {
                try {
                    Files.deleteIfExists(storedPath);
                } catch (IOException ignored) {
                    // Preserve the original upload failure.
                }
            }
            throw failure;
        }

        return attachments;
    }

    public boolean deleteChatImage(String imageUrl, Long ownerUserId) throws IOException {
        if (!isChatUploadUrlForUser(imageUrl, ownerUserId)) {
            return false;
        }

        String fileName = imageUrl.substring("/uploads/chat/".length());
        Path target = uploadRoot.resolve(fileName).normalize();
        if (!target.startsWith(uploadRoot)) {
            return false;
        }
        return Files.deleteIfExists(target);
    }

    public boolean isChatUploadUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String normalized = url.trim();
        if (!normalized.startsWith("/uploads/chat/")) {
            return false;
        }
        String fileName = normalized.substring("/uploads/chat/".length());
        return !fileName.contains("..") && !fileName.contains("/") && !fileName.contains("\\");
    }

    public boolean isChatUploadUrlForUser(String url, Long ownerUserId) {
        if (ownerUserId == null || !isChatUploadUrl(url)) {
            return false;
        }
        String fileName = url.trim().substring("/uploads/chat/".length());
        return fileName.startsWith("chat-" + ownerUserId + "-");
    }

    public Path resolveOwnedChatImage(String fileName, Long ownerUserId) {
        String imageUrl = "/uploads/chat/" + (fileName == null ? "" : fileName);
        if (!isChatUploadUrlForUser(imageUrl, ownerUserId)) {
            throw new IllegalArgumentException("Chat image not found");
        }
        Path target = uploadRoot.resolve(fileName).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Chat image not found");
        }
        return target;
    }

    private SanitizedImage sanitizeImage(BufferedImage decoded) throws IOException {
        boolean hasAlpha = decoded.getColorModel().hasAlpha();
        BufferedImage sanitized = new BufferedImage(
                decoded.getWidth(),
                decoded.getHeight(),
                hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = sanitized.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(decoded, 0, 0, null);
        } finally {
            graphics.dispose();
        }

        String formatName = hasAlpha ? "png" : "jpg";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (!ImageIO.write(sanitized, formatName, outputStream)) {
            throw new IllegalArgumentException("Unsupported image data. Use PNG or JPG.");
        }

        return new SanitizedImage(
                outputStream.toByteArray(),
                hasAlpha ? ".png" : ".jpg",
                hasAlpha ? "image/png" : "image/jpeg"
        );
    }

    private String safeFileName(String originalName, String fallback) {
        if (originalName == null || originalName.isBlank()) {
            return fallback;
        }
        String cleaned = originalName.replaceAll("[^a-zA-Z0-9._-]", "-").trim();
        if (cleaned.isBlank()) {
            return fallback;
        }
        return cleaned.toLowerCase(Locale.ROOT);
    }

    private record SanitizedImage(byte[] bytes, String extension, String contentType) {
    }
}
