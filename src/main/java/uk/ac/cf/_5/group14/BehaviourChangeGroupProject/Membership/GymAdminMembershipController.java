package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Membership;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    
    private final MembershipProductService membershipService;
    private final GymMemberSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    /**
     * List all membership products for the gym
     */
    @GetMapping
    public String listProducts(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", required = false) Integer size,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "You must be associated with a gym to manage memberships");
            return "error/403";
        }
        
        int pageSize = resolvePageSize(size);
        Page<GymMembershipProduct> productsPage = membershipService.getProductsByGymId(
            admin.getGymId(),
            PageRequest.of(Math.max(page, 0), pageSize)
        );
        List<GymMembershipProduct> products = productsPage.getContent();

        Map<Long, Long> subscriberCounts = new HashMap<>();
        long totalSubscribers = 0;
        for (GymMembershipProduct product : products) {
            long count = subscriptionRepository.countByProductIdAndStatus(product.getId(), SubscriptionStatus.ACTIVE);
            subscriberCounts.put(product.getId(), count);
            totalSubscribers += count;
        }

        int activeProductsCount = membershipService.getActiveProductsByGymId(admin.getGymId()).size();

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("products", products);
        model.addAttribute("subscriberCounts", subscriberCounts);
        model.addAttribute("subscribersCount", totalSubscribers);
        model.addAttribute("activeProductsCount", activeProductsCount);
        model.addAttribute("pageSize", pageSize);
        
        return "gym-admin/memberships/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
        @PathVariable Long id,
        @RequestParam("active") boolean active,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);

        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/memberships";
        }

        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        product.setActive(active);
        membershipService.updateProduct(product);

        redirectAttributes.addFlashAttribute(
            "successMessage",
            active ? "Product activated successfully" : "Product deactivated successfully"
        );
        return "redirect:/gym/admin/memberships";
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
        @Valid @ModelAttribute("product") GymMembershipProduct product,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be associated with a gym");
            return "redirect:/gym/admin/memberships";
        }

        if (product.getPriceCents() == null) {
            result.rejectValue("priceDollars", "NotNull", "Price is required");
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
        @Valid @ModelAttribute("product") GymMembershipProduct updatedProduct,
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

        PriceChangeRequest priceChange = new PriceChangeRequest();
        priceChange.setEffectiveDate(defaultEffectiveDate);

        model.addAttribute("product", product);
        model.addAttribute("affectedMemberCount", affectedCount);
        model.addAttribute("defaultEffectiveDate", defaultEffectiveDate);
        model.addAttribute("priceChange", priceChange);
        
        return "gym-admin/memberships/price-change";
    }
    
    /**
     * Execute price change
     */
    @PostMapping("/{id}/price-change")
    public String executePriceChange(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @ModelAttribute("priceChange") PriceChangeRequest priceChange,
        BindingResult result,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied");
            return "redirect:/gym/admin/memberships";
        }

        GymMembershipProduct product = membershipService.getProductByIdAndGymId(id, admin.getGymId());

        Integer newPriceCents = priceChange.toNewPriceCents();
        if (newPriceCents == null) {
            result.rejectValue("newPriceDollars", "NotNull", "New price is required");
        } else if (newPriceCents < 0) {
            result.rejectValue("newPriceDollars", "Min", "Price must be zero or greater");
        } else if (product.getPriceCents() != null && newPriceCents.equals(product.getPriceCents())) {
            result.rejectValue("newPriceDollars", "Same", "New price must be different from current price");
        }

        LocalDate effectiveDate = priceChange.getEffectiveDate();
        if (effectiveDate != null && !effectiveDate.isAfter(LocalDate.now())) {
            result.rejectValue("effectiveDate", "Future", "Effective date must be in the future");
        }

        if (result.hasErrors()) {
            long affectedCount = subscriptionRepository.countByProductIdAndStatus(id, SubscriptionStatus.ACTIVE);
            LocalDate defaultEffectiveDate = resolveDefaultEffectiveDate(id).orElse(LocalDate.now().plusDays(30));
            model.addAttribute("product", product);
            model.addAttribute("affectedMemberCount", affectedCount);
            model.addAttribute("defaultEffectiveDate", defaultEffectiveDate);
            return "gym-admin/memberships/price-change";
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

        redirectAttributes.addFlashAttribute(
            "successMessage",
            "Price change initiated successfully. Notification queued for affected members."
        );
        return "redirect:/gym/admin/memberships/" + id + "/price-change";
    }
    
    /**
     * View price change history for a product
     */
    @GetMapping("/{id}/price-history")
    public String viewPriceHistory(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", required = false) Integer size,
        Model model
    ) {
        User admin = getUserFromDetails(userDetails);
        
        if (admin.getGymId() == null) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        }
        
        GymMembershipProduct product;
        try {
            product = membershipService.getProductByIdAndGymId(id, admin.getGymId());
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "Access denied");
            return "error/403";
        }

        int pageSize = resolvePageSize(size);
        Page<PriceChangeEvent> historyPage = membershipService.getPriceChangeHistory(
            id,
            PageRequest.of(Math.max(page, 0), pageSize)
        );
        List<PriceChangeEvent> history = historyPage.getContent();
        
        model.addAttribute("product", product);
        model.addAttribute("priceChanges", history);
        model.addAttribute("priceChangesPage", historyPage);
        model.addAttribute("pageSize", pageSize);

        int increaseCount = (int) historyPage.getContent().stream()
            .filter(change -> change.getNewPriceCents() > change.getOldPriceCents())
            .count();
        int decreaseCount = (int) historyPage.getContent().stream()
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

    private int resolvePageSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
