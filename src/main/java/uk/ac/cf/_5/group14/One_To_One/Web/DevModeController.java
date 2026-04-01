package uk.ac.cf._5.group14.One_To_One.Web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.cf._5.group14.One_To_One.Config.DevModeProperties;
import uk.ac.cf._5.group14.One_To_One.DevMode.DevModePageAccessService;
import uk.ac.cf._5.group14.One_To_One.Waitlist.WaitlistEmail;
import uk.ac.cf._5.group14.One_To_One.Waitlist.WaitlistEmailRepository;

/**
 * Controller for development mode features
 * Provides navigation hub and demo pages when DEV_MODE is enabled
 */
@Controller
@RequestMapping("/dev-mode")
public class DevModeController {
    
    @Autowired
    private DevModeProperties devModeProperties;

    @Autowired
    private WaitlistEmailRepository waitlistEmailRepository;

    @Autowired
    private DevModePageAccessService devModePageAccessService;
    
    /**
     * Development mode navigation hub
     * Shows available pages users can browse in dev mode
     */
    @GetMapping
    public String devModeHub(Authentication authentication, Model model) {
        if (!devModeProperties.isDevMode()) {
            return "redirect:/";
        }

        model.addAttribute("compactTopContent", true);
        model.addAttribute("isDevMode", true);
        model.addAttribute("devHubView",
                devModePageAccessService.buildHubView(authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication instanceof AnonymousAuthenticationToken)));
        return "system-views/dev-mode/hub";
    }
    
    /**
     * Alternative routing for protected pages in dev mode
     * When user tries to access a protected page without auth in dev mode,
     * they can be redirected here
     */
    @GetMapping("/unauthorized")
    public String devUnauthorized(Model model) {
        if (!devModeProperties.isDevMode()) {
            return "redirect:/";
        }

        model.addAttribute("compactTopContent", true);
        model.addAttribute("isDevMode", true);
        return "system-views/dev-mode/unauthorized";
    }

    @GetMapping("/restricted")
    public String devRestricted(@RequestParam(value = "pageKey", required = false) String pageKey,
                                Model model) {
        if (!devModeProperties.isDevMode()) {
            return "redirect:/";
        }

        model.addAttribute("compactTopContent", true);
        model.addAttribute("isDevMode", true);
        model.addAttribute("restrictedNotice", devModePageAccessService.resolveRestrictedNotice(pageKey));
        return "system-views/dev-mode/restricted";
    }

    /**
     * Accepts email sign-ups from the dev mode landing page.
     * Saves the email so the user can be notified when the site launches.
     */
    @PostMapping("/waitlist")
    public String joinWaitlist(@RequestParam("email") String email,
                               RedirectAttributes redirectAttributes) {
        String trimmed = (email == null) ? "" : email.trim();
        if (trimmed.isEmpty() || !trimmed.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")) {
            redirectAttributes.addFlashAttribute("waitlistError", "Please enter a valid email address.");
            return "redirect:/login";
        }
        if (waitlistEmailRepository.existsByEmail(trimmed)) {
            redirectAttributes.addFlashAttribute("waitlistSuccess", "You're already on the list! We'll email you when we launch.");
        } else {
            waitlistEmailRepository.save(new WaitlistEmail(trimmed));
            redirectAttributes.addFlashAttribute("waitlistSuccess", "Thanks! We'll notify you at " + trimmed + " when we go live.");
        }
        return "redirect:/login";
    }
}
