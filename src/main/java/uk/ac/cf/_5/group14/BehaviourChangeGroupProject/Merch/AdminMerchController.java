package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Merch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/merch")
public class AdminMerchController {

    private final MerchProductService productService;
    private final AuthHelper authHelper;

    public AdminMerchController(MerchProductService productService, AuthHelper authHelper) {
        this.productService = productService;
        this.authHelper = authHelper;
    }

    // ── List all products ────────────────────────────────────────────────────

    @GetMapping
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("merch/admin-list");
        mav.addObject("products", productService.getAllProducts());
        return mav;
    }

    // ── Create form ──────────────────────────────────────────────────────────

    @GetMapping("/new")
    public ModelAndView newForm() {
        ModelAndView mav = new ModelAndView("merch/admin-form");
        mav.addObject("product", new MerchProduct());
        mav.addObject("formAction", "/admin/merch/create");
        mav.addObject("formTitle", "Add New Product");
        return mav;
    }

    @PostMapping("/create")
    public String create(@RequestParam("name") String name,
                         @RequestParam("description") String description,
                         @RequestParam("price") String price,
                         @RequestParam("category") String category,
                         @RequestParam("stockQuantity") int stockQuantity,
                         @RequestParam(value = "active", defaultValue = "false") boolean active,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         RedirectAttributes ra) {
        try {
            User admin = authHelper.getAuthenticatedUser();
            MerchProduct product = new MerchProduct();
            product.setName(name.trim());
            product.setDescription(description != null ? description.trim() : null);
            product.setPrice(new BigDecimal(price.trim()));
            product.setCategory(category != null && !category.isBlank() ? category.trim() : null);
            product.setStockQuantity(Math.max(0, stockQuantity));
            product.setActive(active);
            if (admin != null) product.setCreatedBy(admin.getId());
            productService.saveWithImage(product, image);
            ra.addFlashAttribute("successMessage", "Product created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error creating product: " + e.getMessage());
        }
        return "redirect:/admin/merch";
    }

    // ── Edit form ─────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public ModelAndView editForm(@PathVariable Long id, RedirectAttributes ra) {
        return productService.findById(id).map(product -> {
            ModelAndView mav = new ModelAndView("merch/admin-form");
            mav.addObject("product", product);
            mav.addObject("formAction", "/admin/merch/" + id + "/update");
            mav.addObject("formTitle", "Edit Product");
            return mav;
        }).orElseGet(() -> {
            ra.addFlashAttribute("errorMessage", "Product not found.");
            return new ModelAndView("redirect:/admin/merch");
        });
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam("name") String name,
                         @RequestParam("description") String description,
                         @RequestParam("price") String price,
                         @RequestParam("category") String category,
                         @RequestParam("stockQuantity") int stockQuantity,
                         @RequestParam(value = "active", defaultValue = "false") boolean active,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         RedirectAttributes ra) {
        try {
            MerchProduct product = productService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
            product.setName(name.trim());
            product.setDescription(description != null ? description.trim() : null);
            product.setPrice(new BigDecimal(price.trim()));
            product.setCategory(category != null && !category.isBlank() ? category.trim() : null);
            product.setStockQuantity(Math.max(0, stockQuantity));
            product.setActive(active);
            productService.saveWithImage(product, image);
            ra.addFlashAttribute("successMessage", "Product updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error updating product: " + e.getMessage());
        }
        return "redirect:/admin/merch";
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("successMessage",
                    "Product deactivated. Any pending orders have been cancelled.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error removing product: " + e.getMessage());
        }
        return "redirect:/admin/merch";
    }
}
