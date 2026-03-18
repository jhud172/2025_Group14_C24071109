package uk.ac.cf._5.group14.One_To_One.MerchTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProduct;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductService;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrder;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderRepository;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.MerchOrderServiceImpl;
import uk.ac.cf._5.group14.One_To_One.MerchOrders.RefundStatus;
import uk.ac.cf._5.group14.One_To_One.Users.User;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests that stock decrement is performed atomically via a single conditional
 * DB update (not read-then-write), preventing overselling under concurrency.
 */
@ExtendWith(MockitoExtension.class)
class StockAtomicUpdateTest {

    @Mock
    private MerchOrderRepository orderRepo;

    @Mock
    private MerchProductService productService;

    @InjectMocks
    private MerchOrderServiceImpl orderService;

    private MerchProduct activeProduct(int stock) {
        MerchProduct p = new MerchProduct();
        p.setId(42L);
        p.setName("Test Product");
        p.setPrice(BigDecimal.valueOf(10.00));
        p.setStockQuantity(stock);
        p.setActive(true);
        return p;
    }

    private User testUser() {
        User u = new User();
        u.setId(1L);
        return u;
    }

    @Test
    void placeOrder_usesAtomicDecrement_notReadThenWrite() {
        MerchProduct product = activeProduct(5);
        when(productService.decrementStock(42L, 2)).thenReturn(true);
        when(orderRepo.save(any())).thenAnswer(inv -> {
            MerchOrder o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });

        MerchOrder order = orderService.placeOrder(testUser(), product, 2, null);

        // Verify atomic decrement was called (not a direct field mutation)
        verify(productService).decrementStock(42L, 2);
        assertNotNull(order);
        assertEquals(RefundStatus.NONE, order.getRefundStatus());
    }

    @Test
    void placeOrder_throwsWhenAtomicDecrementReturnsFalse() {
        MerchProduct product = activeProduct(1);
        when(productService.decrementStock(42L, 2)).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderService.placeOrder(testUser(), product, 2, null));

        assertTrue(ex.getMessage().toLowerCase().contains("stock"));
        verify(orderRepo, never()).save(any());
    }

    @Test
    void placeOrder_setsImageAndCategorySnapshot() {
        MerchProduct product = activeProduct(5);
        product.setImageUrl("/uploads/merch/shirt.jpg");
        product.setCategory("Apparel");
        when(productService.decrementStock(42L, 1)).thenReturn(true);
        when(orderRepo.save(any())).thenAnswer(inv -> {
            MerchOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        MerchOrder order = orderService.placeOrder(testUser(), product, 1, null);

        assertFalse(order.getItems().isEmpty());
        assertEquals("/uploads/merch/shirt.jpg", order.getItems().get(0).getImageUrlSnapshot());
        assertEquals("Apparel", order.getItems().get(0).getCategorySnapshot());
    }
}
