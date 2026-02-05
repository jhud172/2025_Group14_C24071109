package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PriceChangeRequest {

    @NotNull(message = "New price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be zero or greater")
    private BigDecimal newPriceDollars;
    
    @NotBlank(message = "Reason for price change is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    private LocalDate effectiveDate;

    public Integer toNewPriceCents() {
        if (newPriceDollars == null) {
            return null;
        }
        BigDecimal scaled = newPriceDollars.setScale(2, java.math.RoundingMode.HALF_UP);
        return scaled.movePointRight(2).intValueExact();
    }
}
