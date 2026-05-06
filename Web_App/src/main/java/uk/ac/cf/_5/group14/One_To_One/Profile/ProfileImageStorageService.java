package uk.ac.cf._5.group14.One_To_One.Profile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileImageStorageService implements FileStorageService {

    private static final long MAX_BYTES = 2 * 1024 * 1024;
    private final Path uploadRoot;

    public ProfileImageStorageService(@Value("${app.storage.profile-dir:uploads/profile}") String uploadRoot) {
        this.uploadRoot = Paths.get(uploadRoot).toAbsolutePath().normalize();
    }

    public String storeProfileImage(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || userId == null) {
            return null;
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Profile image too large (max 2MB).");
        }

        byte[] uploadedBytes = file.getBytes();
        ImageFormat sourceFormat = detectImageFormat(uploadedBytes);
        if (sourceFormat == null) {
            throw new IllegalArgumentException("Unsupported image type. Use PNG, JPG, or WEBP.");
        }

        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(uploadedBytes));
        if (decoded == null || decoded.getWidth() <= 0 || decoded.getHeight() <= 0) {
            throw new IllegalArgumentException("Unsupported image data. Use a valid PNG, JPG, or WEBP image.");
        }

        SanitizedImage sanitized = sanitizeImage(decoded, sourceFormat);

        Files.createDirectories(uploadRoot);
        cleanupExistingUserImages(uploadRoot, userId);

        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String filename = "user-" + userId + "-" + System.currentTimeMillis() + "-" + uniqueSuffix + sanitized.extension();
        Path target = uploadRoot.resolve(filename);

        Files.write(target, sanitized.bytes());
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

        Path target = uploadRoot.resolve(fileName).normalize();
        if (!target.startsWith(uploadRoot)) {
            return false;
        }

        return Files.deleteIfExists(target);
    }

    private SanitizedImage sanitizeImage(BufferedImage decoded, ImageFormat sourceFormat) throws IOException {
        boolean encodeAsJpeg = sourceFormat == ImageFormat.JPEG;
        int imageType = encodeAsJpeg ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage sanitized = new BufferedImage(decoded.getWidth(), decoded.getHeight(), imageType);

        Graphics2D graphics = sanitized.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.drawImage(decoded, 0, 0, null);
        } finally {
            graphics.dispose();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String formatName = encodeAsJpeg ? "jpg" : "png";
        boolean written = ImageIO.write(sanitized, formatName, outputStream);
        if (!written) {
            throw new IllegalArgumentException("Unsupported image data. Use a valid PNG or JPG image.");
        }

        return new SanitizedImage(outputStream.toByteArray(), encodeAsJpeg ? ".jpg" : ".png");
    }

    private ImageFormat detectImageFormat(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return null;
        }
        if (hasPrefix(bytes, (byte) 0x89, (byte) 'P', (byte) 'N', (byte) 'G', (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A)) {
            return ImageFormat.PNG;
        }
        if (hasPrefix(bytes, (byte) 0xFF, (byte) 0xD8, (byte) 0xFF)) {
            return ImageFormat.JPEG;
        }
        if (hasPrefix(bytes, (byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')
                && bytes.length >= 12
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P') {
            return ImageFormat.WEBP;
        }
        return null;
    }

    private boolean hasPrefix(byte[] bytes, byte... prefix) {
        if (bytes == null || prefix == null || bytes.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private void cleanupExistingUserImages(Path root, Long userId) throws IOException {
        if (root == null || userId == null || !Files.exists(root)) {
            return;
        }

        String prefix = "user-" + userId + "-";
        try (Stream<Path> fileStream = Files.list(root)) {
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

    private enum ImageFormat {
        PNG,
        JPEG,
        WEBP
    }

    private record SanitizedImage(byte[] bytes, String extension) {
    }
}
