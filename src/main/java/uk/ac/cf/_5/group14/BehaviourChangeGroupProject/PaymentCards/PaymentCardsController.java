package uk.ac.cf._5.group14.BehaviourChangeGroupProject.PaymentCards;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
@RequestMapping("/profile/settings/cards")
public class PaymentCardsController {

    private final SavedPaymentMethodService cardService;
    private final AuthHelper authHelper;

    public PaymentCardsController(SavedPaymentMethodService cardService, AuthHelper authHelper) {
        this.cardService = cardService;
        this.authHelper = authHelper;
    }

    /** Add a new card */
    @PostMapping("/add")
    public String addCard(@RequestParam("cardHolderName") String cardHolderName,
                          @RequestParam("cardNumber") String cardNumber,
                          @RequestParam("brand") String brand,
                          @RequestParam("expiryMonth") short expiryMonth,
                          @RequestParam("expiryYear") short expiryYear,
                          @RequestParam(value = "makeDefault", defaultValue = "false") boolean makeDefault,
                          RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        try {
            validateCardInput(cardHolderName, cardNumber, expiryMonth, expiryYear);
            cardService.addCard(user, cardHolderName, cardNumber, brand, expiryMonth, expiryYear, makeDefault);
            ra.addFlashAttribute("cardSuccess", "Card added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("cardError", e.getMessage());
        }
        return "redirect:/profile#cards";
    }

    /** Edit an existing card */
    @PostMapping("/{id}/edit")
    public String editCard(@PathVariable Long id,
                           @RequestParam("cardHolderName") String cardHolderName,
                           @RequestParam("brand") String brand,
                           @RequestParam("expiryMonth") short expiryMonth,
                           @RequestParam("expiryYear") short expiryYear,
                           @RequestParam(value = "makeDefault", defaultValue = "false") boolean makeDefault,
                           RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        try {
            if (cardHolderName == null || cardHolderName.isBlank()) {
                throw new IllegalArgumentException("Cardholder name is required.");
            }
            cardService.updateCard(user, id, cardHolderName, brand, expiryMonth, expiryYear, makeDefault);
            ra.addFlashAttribute("cardSuccess", "Card updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("cardError", e.getMessage());
        }
        return "redirect:/profile#cards";
    }

    /** Delete a card */
    @PostMapping("/{id}/delete")
    public String deleteCard(@PathVariable Long id, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        try {
            cardService.deleteCard(user, id);
            ra.addFlashAttribute("cardSuccess", "Card removed.");
        } catch (Exception e) {
            ra.addFlashAttribute("cardError", e.getMessage());
        }
        return "redirect:/profile#cards";
    }

    /** Set a card as default */
    @PostMapping("/{id}/default")
    public String setDefault(@PathVariable Long id, RedirectAttributes ra) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) return "redirect:/login";
        try {
            cardService.setDefault(user, id);
            ra.addFlashAttribute("cardSuccess", "Default card updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("cardError", e.getMessage());
        }
        return "redirect:/profile#cards";
    }

    // ── Validation helpers ────────────────────────────────────────────────────

    private void validateCardInput(String cardHolderName,
                                   String cardNumber,
                                   short expiryMonth,
                                   short expiryYear) {
        if (cardHolderName == null || cardHolderName.isBlank()) {
            throw new IllegalArgumentException("Cardholder name is required.");
        }
        if (cardNumber == null) {
            throw new IllegalArgumentException("Card number is required.");
        }
        String cleaned = cardNumber.replaceAll("\\s+", "");
        if (!cleaned.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Card number must be 13–19 digits.");
        }
        if (expiryMonth < 1 || expiryMonth > 12) {
            throw new IllegalArgumentException("Expiry month must be 1–12.");
        }
        int currentYear = java.time.Year.now().getValue();
        if (expiryYear < currentYear || expiryYear > currentYear + 20) {
            throw new IllegalArgumentException("Invalid expiry year.");
        }
    }
}
