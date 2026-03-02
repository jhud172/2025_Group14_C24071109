package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MerchProductService {

    List<MerchProduct> getActiveProducts();

    List<MerchProduct> getAllProducts();

    Optional<MerchProduct> findById(Long id);

    MerchProduct save(MerchProduct product);

    /**
     * Creates or updates a product; stores the uploaded image if provided.
     */
    MerchProduct saveWithImage(MerchProduct product, MultipartFile image);

    /**
     * Atomically decrements stock for the given product by qty.
     * Returns true if successful, false if stock was insufficient.
     */
    boolean decrementStock(Long productId, int qty);

    /**
     * Deactivates the product. PENDING orders that contain it are cancelled;
     * CONFIRMED/SHIPPED/DELIVERED orders are left untouched.
     */
    void deleteProduct(Long productId);
}
