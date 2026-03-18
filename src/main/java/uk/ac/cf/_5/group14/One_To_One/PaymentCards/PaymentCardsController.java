package uk.ac.cf._5.group14.One_To_One.PaymentCards;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;

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
                          @RequestParam("providerToken") String providerToken,
                          @RequestParam("lastFour") String lastFour,
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
            cardService.addCard(user, cardHolderName, providerToken, lastFour, brand, expiryMonth, expiryYear, makeDefault);
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
}
