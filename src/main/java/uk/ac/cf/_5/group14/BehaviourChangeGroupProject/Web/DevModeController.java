package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;

/**
 * Controller for development mode features
 * Provides navigation hub and demo pages when DEV_MODE is enabled
 */
@Controller
@RequestMapping("/dev-mode")
public class DevModeController {
    
    @Autowired
    private DevModeProperties devModeProperties;
    
    /**
     * Development mode navigation hub
     * Shows available pages users can browse in dev mode
     */
    @GetMapping
    public String devModeHub(Model model) {
        if (!devModeProperties.isDevMode()) {
            return "redirect:/";
        }
        
        model.addAttribute("isDevMode", true);
        return "dev-mode/hub";
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
        
        model.addAttribute("isDevMode", true);
        return "dev-mode/unauthorized";
    }
}
