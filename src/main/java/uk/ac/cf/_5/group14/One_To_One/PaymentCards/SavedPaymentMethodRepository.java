package uk.ac.cf._5.group14.One_To_One.PaymentCards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedPaymentMethodRepository extends JpaRepository<SavedPaymentMethod, Long> {

    List<SavedPaymentMethod> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<SavedPaymentMethod> findByIdAndUserId(Long id, Long userId);

    Optional<SavedPaymentMethod> findByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE SavedPaymentMethod s SET s.isDefault = false WHERE s.user.id = :userId")
    void clearDefaultForUser(@Param("userId") Long userId);

    int countByUserId(Long userId);
}
