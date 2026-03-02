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
     * Deactivates the product and cancels all CONFIRMED orders that contain it,
     * marking them CANCELLED_REFUND_PENDING.
     */
    void deleteProduct(Long productId);
}
