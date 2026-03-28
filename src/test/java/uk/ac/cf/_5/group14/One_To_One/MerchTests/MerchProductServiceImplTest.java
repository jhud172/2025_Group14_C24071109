package uk.ac.cf._5.group14.One_To_One.MerchTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProduct;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductRepository;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductServiceImpl;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderService;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MerchProductServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void saveWithImage_storesSanitizedImageUsingDetectedSignature() throws Exception {
        MerchProductRepository repo = mock(MerchProductRepository.class);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MerchProductServiceImpl service = new MerchProductServiceImpl(repo, mock(MerchOrderService.class), tempDir.toString());
        MerchProduct product = new MerchProduct();
        product.setName("Training Tee");

        MockMultipartFile upload = new MockMultipartFile(
                "image",
                "tee.txt",
                "text/plain",
                pngBytes());

        MerchProduct saved = service.saveWithImage(product, upload);

        assertThat(saved.getImageUrl()).startsWith("/uploads/merch/product-").endsWith(".png");
        assertThat(tempDir.resolve(Path.of(saved.getImageUrl().substring("/uploads/merch/".length())).getFileName())).exists();
    }

    @Test
    void saveWithImage_rejectsFakeImagePayload() {
        MerchProductRepository repo = mock(MerchProductRepository.class);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MerchProductServiceImpl service = new MerchProductServiceImpl(repo, mock(MerchOrderService.class), tempDir.toString());
        MerchProduct product = new MerchProduct();
        product.setName("Training Tee");

        MockMultipartFile upload = new MockMultipartFile(
                "image",
                "tee.png",
                "image/png",
                "not-an-image".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.saveWithImage(product, upload));
        assertThat(ex.getMessage()).contains("Unsupported image");
    }

    private byte[] pngBytes() throws Exception {
        BufferedImage image = new BufferedImage(6, 6, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, Color.CYAN.getRGB());
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}
