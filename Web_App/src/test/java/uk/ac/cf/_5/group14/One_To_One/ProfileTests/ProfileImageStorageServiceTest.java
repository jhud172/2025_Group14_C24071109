package uk.ac.cf._5.group14.One_To_One.ProfileTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import uk.ac.cf._5.group14.One_To_One.Profile.ProfileImageStorageService;

class ProfileImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storesSanitizedPngUsingDetectedImageSignature() throws Exception {
        ProfileImageStorageService service = new ProfileImageStorageService(tempDir.toString());
        MockMultipartFile upload = new MockMultipartFile(
                "profileImage",
                "avatar.png",
                "image/png",
                imageBytes("png"));

        String imageUrl = service.storeProfileImage(42L, upload);

        assertThat(imageUrl).startsWith("/uploads/profile/user-42-").endsWith(".png");
        Path storedFile = tempDir.resolve(Path.of(imageUrl).getFileName().toString());
        assertThat(Files.exists(storedFile)).isTrue();
        assertThat(ImageIO.read(storedFile.toFile())).isNotNull();
    }

    @Test
    void trustsImageSignatureInsteadOfDeclaredMimeType() throws Exception {
        ProfileImageStorageService service = new ProfileImageStorageService(tempDir.toString());
        MockMultipartFile upload = new MockMultipartFile(
                "profileImage",
                "avatar.png",
                "image/png",
                imageBytes("jpg"));

        String imageUrl = service.storeProfileImage(7L, upload);

        assertThat(imageUrl).endsWith(".jpg");
        Path storedFile = tempDir.resolve(Path.of(imageUrl).getFileName().toString());
        assertThat(ImageIO.read(storedFile.toFile())).isNotNull();
    }

    @Test
    void rejectsFilesThatOnlyPretendToBeImages() {
        ProfileImageStorageService service = new ProfileImageStorageService(tempDir.toString());
        MockMultipartFile upload = new MockMultipartFile(
                "profileImage",
                "avatar.png",
                "image/png",
                "not-an-image".getBytes());

        assertThatThrownBy(() -> service.storeProfileImage(99L, upload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported image type. Use PNG, JPG, or WEBP.");
    }

    @Test
    void replacesOlderProfileImageForSameUser() throws Exception {
        ProfileImageStorageService service = new ProfileImageStorageService(tempDir.toString());
        MockMultipartFile firstUpload = new MockMultipartFile(
                "profileImage",
                "first.png",
                "image/png",
                imageBytes("png"));
        MockMultipartFile secondUpload = new MockMultipartFile(
                "profileImage",
                "second.png",
                "image/png",
                imageBytes("png"));

        String firstImageUrl = service.storeProfileImage(5L, firstUpload);
        String secondImageUrl = service.storeProfileImage(5L, secondUpload);

        Path firstFile = tempDir.resolve(Path.of(firstImageUrl).getFileName().toString());
        Path secondFile = tempDir.resolve(Path.of(secondImageUrl).getFileName().toString());

        assertThat(Files.exists(firstFile)).isFalse();
        assertThat(Files.exists(secondFile)).isTrue();
        try (Stream<Path> files = Files.list(tempDir)) {
            assertThat(files.count()).isEqualTo(1);
        }
    }

    @Test
    void deleteRejectsTraversalPaths() throws Exception {
        ProfileImageStorageService service = new ProfileImageStorageService(tempDir.toString());

        assertThat(service.deleteProfileImage("/uploads/profile/../secret.txt")).isFalse();
    }

    private byte[] imageBytes(String format) throws IOException {
        BufferedImage image = new BufferedImage(8, 8, "jpg".equals(format) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, new Color(40 + x * 10, 80 + y * 10, 140).getRGB());
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean written = ImageIO.write(image, format, outputStream);
        assertThat(written).isTrue();
        return outputStream.toByteArray();
    }
}
