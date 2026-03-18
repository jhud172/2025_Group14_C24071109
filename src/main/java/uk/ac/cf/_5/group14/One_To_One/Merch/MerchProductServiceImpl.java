package uk.ac.cf._5.group14.One_To_One.Merch;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class MerchProductServiceImpl implements MerchProductService {

    private static final Set<String> ALLOWED_IMAGE_TYPES =
            Set.of("image/png", "image/jpeg", "image/jpg", "image/webp");
    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024; // 5 MB

    private final MerchProductRepository productRepo;
    private final MerchOrderService orderService;

    public MerchProductServiceImpl(MerchProductRepository productRepo,
                                   @Lazy MerchOrderService orderService) {
        this.productRepo = productRepo;
        this.orderService = orderService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchProduct> getActiveProducts() {
        return productRepo.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchProduct> getAllProducts() {
        return productRepo.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MerchProduct> findById(Long id) {
        return productRepo.findById(id);
    }

    @Override
    public MerchProduct save(MerchProduct product) {
        return productRepo.save(product);
    }

    @Override
    public MerchProduct saveWithImage(MerchProduct product, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                String contentType = image.getContentType();
                if (contentType == null ||
                        !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException(
                            "Unsupported image type. Use PNG, JPG, or WEBP.");
                }
                if (image.getSize() > MAX_IMAGE_BYTES) {
                    throw new IllegalArgumentException("Image too large (max 5 MB).");
                }

                String ext = contentType.toLowerCase(Locale.ROOT).contains("png") ? ".png"
                        : contentType.toLowerCase(Locale.ROOT).contains("webp") ? ".webp" : ".jpg";

                Path uploadRoot = Paths.get("uploads", "merch");
                Files.createDirectories(uploadRoot);

                String filename = "product-" + System.currentTimeMillis() + ext;
                Path target = uploadRoot.resolve(filename);
                Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                product.setImageUrl("/uploads/merch/" + filename);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to store product image", e);
            }
        }
        return productRepo.save(product);
    }

    @Override
    public boolean decrementStock(Long productId, int qty) {
        return productRepo.decrementStock(productId, qty) == 1;
    }

    @Override
    public void deleteProduct(Long productId) {
        MerchProduct product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Cancel only PENDING orders; CONFIRMED/SHIPPED/DELIVERED are left for explicit admin action
        orderService.cancelPendingOrdersForProduct(productId);

        // Soft-delete: deactivate so existing order snapshots remain intact
        product.setActive(false);
        productRepo.save(product);
    }
}
