package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.MerchOrders.MerchOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for grouping orders by product name in the purchases drawer.
 */
@Getter
@Setter
@AllArgsConstructor
public class OrderGroup {
    
    private String productName;
    private int count;
    private BigDecimal totalAmount;
    private List<MerchOrder> orders;
}
