package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.cf._5.group14.One_To_One.Users.Role;

import java.util.Locale;
import java.util.Set;

@Controller
public class SocialAuthController {

    private static final Set<String> SUPPORTED_PROVIDERS = Set.of("google", "microsoft", "apple");

    private final SocialAuthAvailabilityService socialAuthAvailabilityService;
    private final SocialAuthAccountService socialAuthAccountService;

    public SocialAuthController(SocialAuthAvailabilityService socialAuthAvailabilityService,
                                SocialAuthAccountService socialAuthAccountService) {
        this.socialAuthAvailabilityService = socialAuthAvailabilityService;
        this.socialAuthAccountService = socialAuthAccountService;
    }

    @GetMapping("/auth/social/{provider}")
    public String startSocialAuth(@PathVariable("provider") String provider,
                                  @RequestParam("role") String role,
                                  HttpServletRequest request) {
        String normalizedProvider = provider == null ? "" : provider.trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_PROVIDERS.contains(normalizedProvider) || !socialAuthAvailabilityService.isProviderEnabled(normalizedProvider)) {
            return "redirect:/login?error=social";
        }

        Role requestedRole = switch (role == null ? "" : role.trim().toLowerCase(Locale.ROOT)) {
            case "client" -> Role.CLIENT;
            case "trainer" -> Role.TRAINER;
            default -> null;
        };

        if (requestedRole == null) {
            return "redirect:/login?error=social";
        }

        socialAuthAccountService.storeRequestedRole(request, requestedRole);
        return "redirect:/oauth2/authorization/" + normalizedProvider;
    }
}
