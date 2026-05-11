package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

import jakarta.validation.Valid;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/gym/admin/memberships")
@RequiredArgsConstructor
public class GymAdminMembershipController {
    
    private final MembershipProductService membershipService;
    private final GymMemberSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    /**
     * List all membership products for the gym
     */
    @GetMapping
    public String listProducts(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "You must be associated with a gym to manage memberships");
            return "error/403";
        }
        
        List<GymMembershipProduct> products = membershipService.getProductsByGymId(admin.getGymId());
        model.addAttribute("products", products);
        
        return "gym-admin/memberships/list";
    }
    
    /**
     * Show form to create a new product
     */
    @GetMapping("/create")
    public String showCreateForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "You must be associated with a gym to create memberships");
            return "error/403";
        }
        
        GymMembershipProduct product = new GymMembershipProduct();
        product.setGymId(admin.getGymId());
        product.setBillingPeriod(BillingPeriod.MONTHLY);
        
        model.addAttribute("product", product);
        return "gym-admin/memberships/form";
    }
    
    /**
     * Create a new membership product
     */
    @PostMapping("/create")
    public String createProduct(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute GymMembershipProduct product,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be associated with a gym");
            return "redirect:/gym/admin/memberships";
        }
        
        if (result.hasErrors()) {
            return "gym-admin/memberships/form";
        }
        
        product.setGymId(admin.getGymId());
        membershipService.createProduct(product);
        
        redirectAttributes.addFlashAttribute("successMessage", "Membership product created successfully");
        return "redirect:/gym/admin/memberships";
    }
    
    /**
     * Show form to edit an existing product
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        }
        
        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        model.addAttribute("product", product);
        
        return "gym-admin/memberships/form";
    }
    
    /**
     * Update an existing product (name, description, active status only - not price)
     */
    @PostMapping("/{id}/edit")
    public String updateProduct(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute GymMembershipProduct updatedProduct,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/memberships";
        }
        
        if (result.hasErrors()) {
            return "gym-admin/memberships/form";
        }
        
        GymMembershipProduct existingProduct = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        
        // Update allowed fields only (not price)
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setActive(updatedProduct.isActive());
        
        membershipService.updateProduct(existingProduct);
        
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully");
        return "redirect:/gym/admin/memberships";
    }
    
    /**
     * Show price change confirmation form
     */
    @GetMapping("/{id}/price-change")
    public String showPriceChangeForm(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        }
        
        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        long affectedCount = subscriptionRepository.countByProductIdAndStatus(id, SubscriptionStatus.ACTIVE);
        LocalDate defaultEffectiveDate = resolveDefaultEffectiveDate(id).orElse(LocalDate.now().plusDays(30));
        
        model.addAttribute("product", product);
        model.addAttribute("affectedMemberCount", affectedCount);
        model.addAttribute("defaultEffectiveDate", defaultEffectiveDate);
        model.addAttribute("priceChange", new PriceChangeRequest());
        
        return "gym-admin/memberships/price-change";
    }
    
    /**
     * Execute price change
     */
    @PostMapping("/{id}/price-change")
    public String executePriceChange(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute PriceChangeRequest priceChange,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/memberships";
        }

        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid price change data");
            return "redirect:/gym/admin/memberships/" + id + "/price-change";
        }
        
        try {
            Integer newPriceCents = priceChange.toNewPriceCents();
            if (newPriceCents == null) {
                throw new IllegalArgumentException("New price is required");
            }
            if (newPriceCents < 0) {
                throw new IllegalArgumentException("Price must be zero or greater");
            }

            LocalDate effectiveDate = priceChange.getEffectiveDate();
            if (effectiveDate == null) {
                effectiveDate = resolveDefaultEffectiveDate(id).orElse(LocalDate.now().plusDays(30));
            }
            if (!effectiveDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Effective date must be in the future");
            }

            Instant effectiveAt = effectiveDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            log.info("[AUDIT] Price change request submitted by user {} for product {} (gym {}). New price: ${}, effective {}, reason length {}",
                admin.getId(), product.getId(), product.getGymId(), newPriceCents / 100.0, effectiveAt, priceChange.getReason().length());
            
            membershipService.initiatePriceChange(
                id,
                newPriceCents,
                effectiveAt,
                priceChange.getReason(),
                admin.getId()
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Price change initiated successfully. Affected members have been notified.");
        } catch (Exception e) {
            log.error("Error initiating price change", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/gym/admin/memberships/" + id + "/price-change";
        }
        
        return "redirect:/gym/admin/memberships";
    }
    
    /**
     * View price change history for a product
     */
    @GetMapping("/{id}/price-history")
    public String viewPriceHistory(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        }
        
        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        List<PriceChangeEvent> history = membershipService.getPriceChangeHistory(id);
        
        model.addAttribute("product", product);
        model.addAttribute("priceChanges", history);

        int increaseCount = (int) history.stream()
            .filter(change -> change.getNewPriceCents() > change.getOldPriceCents())
            .count();
        int decreaseCount = (int) history.stream()
            .filter(change -> change.getNewPriceCents() < change.getOldPriceCents())
            .count();

        Map<Long, User> changedByUsers = new HashMap<>();
        List<Long> changerIds = history.stream()
            .map(PriceChangeEvent::getChangedByUserId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        if (!changerIds.isEmpty()) {
            userRepository.findAllById(changerIds)
                .forEach(user -> changedByUsers.put(user.getId(), user));
        }

        model.addAttribute("increaseCount", increaseCount);
        model.addAttribute("decreaseCount", decreaseCount);
        model.addAttribute("changedByUsers", changedByUsers);
        
        return "gym-admin/memberships/price-history";
    }

    private Optional<LocalDate> resolveDefaultEffectiveDate(Long productId) {
        return subscriptionRepository.findByProductIdAndStatus(productId, SubscriptionStatus.ACTIVE)
            .stream()
            .map(GymMemberSubscription::getRenewsAt)
            .filter(Objects::nonNull)
            .min(Comparator.naturalOrder())
            .map(instant -> instant.atZone(ZoneId.systemDefault()).toLocalDate());
    }
    
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
