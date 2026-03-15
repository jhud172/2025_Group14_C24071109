package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config.DevModeProperties;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.GymProfile.GymProfile;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.GymProfile.GymProfileService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile.TrainerProfile;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification.EmailVerificationService;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TrainerProfileService trainerProfileService;

    @Autowired
    private GymProfileService gymProfileService;
    
    @Autowired
    private DevModeProperties devModeProperties;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // signup choice
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        applyAuthLayout(model);
        return "User/signup-choice";
    }

    @GetMapping("/signup/client")
    public String showClientSignup(Model model) {
        applyAuthLayout(model);
        model.addAttribute("signupForm", new ClientSignupForm());
        return "User/signup-client";
    }

    @PostMapping("/signup/client")
    public String signupClient(@Valid @ModelAttribute("signupForm") ClientSignupForm signupForm,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        applyAuthLayout(model);

        // check for validation errors
        if (result.hasErrors()) {
            return "User/signup-client";
        }

        // check whether email exists
        if (userService.emailExists(signupForm.getEmail())) {
            result.rejectValue("email", "email.taken", "Email already registered!");
            return "User/signup-client";
        }

        // check whether username exists
        if (userService.usernameExists(signupForm.getUsername())) {
            result.rejectValue("username", "username.taken", "Username already taken!");
            return "User/signup-client";
        }

        String normalizedUsername = normalizeUsername(signupForm.getUsername());
        String displayName = normalizedUsername != null ? normalizedUsername : "client";

        // save user to database
        User user = new User(
                signupForm.getEmail(),
                displayName,
                "Client",
                normalizedUsername,
                signupForm.getPassword()
        );
        user.setRole(Role.CLIENT);

        userService.saveUser(user);

        try {
            emailVerificationService.sendVerification(user);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {} after client signup", user.getEmail(), e);
        }

        redirectAttributes.addFlashAttribute("verifyRegistered", true);
        return "redirect:/verify/email/code?email=" + encodeEmail(user.getEmail());
    }

    @GetMapping("/signup/trainer")
    public String showTrainerSignup(Model model) {
        applyAuthLayout(model);
        model.addAttribute("signupForm", new TrainerSignupForm());
        return "User/signup-trainer";
    }

    @GetMapping("/signup/trainer/success")
    public String showTrainerSignupSuccess(Model model) {
        applyAuthLayout(model);
        // trainerCode and verifyEmail are flash attributes added after successful signup
        return "User/signup-trainer-success";
    }

    @PostMapping("/signup/trainer")
    public String signupTrainer(@Valid @ModelAttribute("signupForm") TrainerSignupForm signupForm,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        applyAuthLayout(model);

        if (result.hasErrors()) {
            return "User/signup-trainer";
        }

        if (userService.emailExists(signupForm.getEmail())) {
            result.rejectValue("email", "email.taken", "Email already registered!");
            return "User/signup-trainer";
        }

        if (userService.usernameExists(signupForm.getUsername())) {
            result.rejectValue("username", "username.taken", "Username already taken!");
            return "User/signup-trainer";
        }

        String normalizedUsername = normalizeUsername(signupForm.getUsername());
        String displayName = normalizedUsername != null ? normalizedUsername : "trainer";

        User user = new User(
                signupForm.getEmail(),
                displayName,
                "Trainer",
                normalizedUsername,
                signupForm.getPassword()
        );
        user.setRole(Role.TRAINER);

        User savedUser = userService.saveUser(user);

        TrainerProfile profile = trainerProfileService.getOrCreateProfile(savedUser.getId());
        profile.setBio(signupForm.getBio());
        profile.setLocation(signupForm.getLocation());
        profile.setInstagramUrl(signupForm.getInstagramUrl());
        profile.setTiktokUrl(signupForm.getTiktokUrl());
        profile.setYoutubeUrl(signupForm.getYoutubeUrl());
        profile.setLinkedInUrl(signupForm.getLinkedInUrl());
        profile.setWebsiteUrl(signupForm.getWebsiteUrl());
        trainerProfileService.updateProfile(savedUser.getId(), profile);

        try {
            emailVerificationService.sendVerification(savedUser);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {} after trainer signup", savedUser.getEmail(), e);
        }

        // Show trainer code on success page before proceeding to email verification
        String trainerCode = profile.getTrainerCode();
        if (trainerCode != null) {
            String formatted = trainerCode.substring(0, 4) + "-" + trainerCode.substring(4, 8) + "-" + trainerCode.substring(8, 12);
            redirectAttributes.addFlashAttribute("trainerCode", formatted);
        }
        redirectAttributes.addFlashAttribute("verifyRegistered", true);
        return "redirect:/verify/email/code?email=" + encodeEmail(savedUser.getEmail());
    }

    @GetMapping("/signup/gym")
    public String showGymSignup(Model model) {
        applyAuthLayout(model);
        model.addAttribute("signupForm", new GymSignupForm());
        return "User/signup-gym";
    }

    @PostMapping("/signup/gym")
    public String signupGym(@Valid @ModelAttribute("signupForm") GymSignupForm signupForm,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        applyAuthLayout(model);

        if (result.hasErrors()) {
            return "User/signup-gym";
        }

        if (userService.emailExists(signupForm.getAdminEmail())) {
            result.rejectValue("adminEmail", "email.taken", "Email already registered!");
            return "User/signup-gym";
        }

        String generatedUsername = generateGymUsername(signupForm.getGymName());
        if (generatedUsername == null) {
            result.rejectValue("gymName", "gym.invalid", "Please provide a valid gym name.");
            return "User/signup-gym";
        }

        String[] contactNames = splitName(signupForm.getContactName());

        User user = new User(
                signupForm.getAdminEmail(),
                contactNames[0],
                contactNames[1],
                generatedUsername,
                signupForm.getPassword()
        );
        user.setRole(Role.GYM_ADMIN);

        User savedUser = userService.saveUser(user);

        GymProfile profile = new GymProfile(savedUser.getId(), signupForm.getGymName());
        profile.setAddress(signupForm.getAddress());
        profile.setCity(signupForm.getCity());
        profile.setContactName(signupForm.getContactName());
        profile.setContactPhone(signupForm.getContactPhone());
        gymProfileService.saveProfile(profile);

        try {
            emailVerificationService.sendVerification(savedUser);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {} after gym signup", savedUser.getEmail(), e);
        }

        redirectAttributes.addFlashAttribute("verifyRegistered", true);
        return "redirect:/verify/email/code?email=" + encodeEmail(savedUser.getEmail());
    }

    // login page
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "expired", required = false) String expired,
                                @RequestParam(value = "devLogin", required = false) Boolean devLogin,
                                @RequestParam(value = "role", required = false) String role,
                                Model model) {
        applyAuthLayout(model);
        if (expired != null) {
            return "redirect:/?expired=1";
        }

        // Pass pre-selected role to template (validated to known roles only)
        if (role != null && (role.equals("client") || role.equals("trainer") || role.equals("gym"))) {
            model.addAttribute("preselectedRole", role);
        }
        
        // Check if dev mode is enabled
        if (devModeProperties.isDevMode()) {
            model.addAttribute("compactTopContent", true);
            if (Boolean.TRUE.equals(devLogin)) {
                model.addAttribute("isDevMode", true);
                return "User/login";
            }
            model.addAttribute("isDevMode", true);
            return "User/login-demo";
        }
        
        return "User/login";
    }

    @PostMapping("/login")
    public String loginUser() {
        // Handled by Spring Security
        return "redirect:/";
    }

    private void applyAuthLayout(Model model) {
        model.addAttribute("authPageLayout", true);
        model.addAttribute("compactTopContent", true);
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }
        return username.trim().toLowerCase();
    }

    private String generateGymUsername(String gymName) {
        if (gymName == null || gymName.isBlank()) {
            return null;
        }
        String base = gymName.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
        base = base.replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "gym";
        }
        String candidate = base;
        int suffix = 1;
        while (userService.usernameExists(candidate)) {
            candidate = base + "_" + suffix;
            suffix++;
        }
        return candidate;
    }

    private String[] splitName(String fullName) {
        String cleaned = fullName == null ? "" : fullName.trim();
        if (cleaned.isBlank()) {
            return new String[]{"Gym", "Admin"};
        }
        String[] parts = cleaned.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], "Admin"};
        }
        return new String[]{parts[0], parts[1]};
    }

    private String encodeEmail(String email) {
        if (email == null) {
            return "";
        }
        return java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
    }
}
