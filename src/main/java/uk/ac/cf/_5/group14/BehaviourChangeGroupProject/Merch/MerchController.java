package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/merch")
public class MerchController {

    private final MerchProductService productService;
    private final AuthHelper authHelper;

    public MerchController(MerchProductService productService, AuthHelper authHelper) {
        this.productService = productService;
        this.authHelper = authHelper;
    }

    @GetMapping
    public ModelAndView shop() {
        User user = authHelper.getAuthenticatedUser();
        ModelAndView mav = new ModelAndView("merch/shop");
        List<MerchProduct> products = productService.getActiveProducts();
        boolean demoMode = products.isEmpty();
        if (demoMode) {
            products = buildDemoProducts();
        }
        mav.addObject("products", products);
        mav.addObject("demoMode", demoMode);
        mav.addObject("user", user);
        return mav;
    }

    /** Demo products displayed when no real products have been added yet.
     *  Image URLs point to the Unsplash CDN for demonstration purposes only.
     *  Admins can replace products (and their images) via the store admin panel.
     */
    private List<MerchProduct> buildDemoProducts() {
        return List.of(
            demo(1L, "Performance Training Tee", "Moisture-wicking, anti-odour fabric engineered for high-intensity workouts. Features reflective logo detail and flatlock seams.", new BigDecimal("29.99"), "Apparel", "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=600&q=80", 50, true),
            demo(2L, "Compression Leggings Pro", "4-way stretch compression fabric with deep pockets and a high-waist band. Designed to support muscle recovery and peak performance.", new BigDecimal("49.99"), "Apparel", "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=600&q=80", 30, true),
            demo(3L, "Stainless Steel Shaker Bottle", "750 ml double-wall insulated shaker with a leak-proof lid and mixing ball. Keeps your protein shake cold for 12 hours.", new BigDecimal("19.99"), "Accessories", "https://images.unsplash.com/photo-1544441893-675973e31985?w=600&q=80", 100, true),
            demo(4L, "Elite Gym Gloves", "Full palm padding with wrist wrap support. Breathable mesh back and non-slip silicone grip pattern — built for heavy lifts.", new BigDecimal("24.99"), "Accessories", "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=600&q=80", 75, true),
            demo(5L, "Recovery Foam Roller", "High-density EVA foam roller with a textured surface for deep-tissue myofascial release. 45 cm length, suitable for all body parts.", new BigDecimal("34.99"), "Equipment", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&q=80", 40, true),
            demo(6L, "Branded Gym Backpack", "25 L capacity with a dedicated wet/dry compartment, laptop sleeve, and adjustable padded straps. One To One logo embroidery.", new BigDecimal("54.99"), "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&q=80", 20, true),
            demo(7L, "Resistance Band Set", "Set of 5 progressive resistance bands (extra-light to extra-heavy) made from natural latex. Includes a carry bag and exercise guide.", new BigDecimal("22.99"), "Equipment", "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=600&q=80", 60, true),
            demo(8L, "Protein Shaker Bottle – 1 L", "Extra-large BPA-free shaker with a pill compartment, powder funnel, and a secure twist lock lid. Dishwasher safe.", new BigDecimal("14.99"), "Accessories", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80", 0, true)
        );
    }

    private MerchProduct demo(Long id, String name, String description, BigDecimal price,
                               String category, String imageUrl, int stock, boolean active) {
        MerchProduct p = new MerchProduct();
        p.setId(id);
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        p.setImageUrl(imageUrl);
        p.setStockQuantity(stock);
        p.setActive(active);
        return p;
    }
}
