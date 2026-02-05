package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Verification;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

import java.util.Locale;
import java.util.Optional;

@Controller
public class VerificationController {

    private final EmailVerificationService emailVerificationService;
    private final PhoneVerificationService phoneVerificationService;
    private final AuthHelper authHelper;
    private final MessageSource messageSource;

    public VerificationController(EmailVerificationService emailVerificationService,
                                  PhoneVerificationService phoneVerificationService,
                                  AuthHelper authHelper,
                                  MessageSource messageSource) {
        this.emailVerificationService = emailVerificationService;
        this.phoneVerificationService = phoneVerificationService;
        this.authHelper = authHelper;
        this.messageSource = messageSource;
    }

    @PostMapping("/verify/email/send")
    public String sendEmailVerification(RedirectAttributes redirectAttributes, Locale locale) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        emailVerificationService.sendVerification(user);
        redirectAttributes.addFlashAttribute(
                "verifySuccess",
                messageSource.getMessage("verify.email.sent", null, locale)
        );
        return "redirect:/profile";
    }

    @GetMapping("/verify/email")
    public String verifyEmail(@RequestParam("token") String token, Model model, Locale locale) {
        Optional<String> error = emailVerificationService.verifyToken(token);
        if (error.isPresent()) {
            String key = mapEmailErrorKey(error.get());
            model.addAttribute("verificationError", messageSource.getMessage(key, null, locale));
        } else {
            model.addAttribute("verificationSuccess", messageSource.getMessage("verify.email.success", null, locale));
        }
        return "verify/email-confirm";
    }

    @PostMapping("/verify/phone/send")
    public String sendPhoneVerification(RedirectAttributes redirectAttributes, Locale locale) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "verifyError",
                    messageSource.getMessage("verify.phone.missing", null, locale)
            );
            return "redirect:/profile";
        }
        phoneVerificationService.sendCode(user);
        redirectAttributes.addFlashAttribute(
                "verifySuccess",
                messageSource.getMessage("verify.phone.sent", null, locale)
        );
        return "redirect:/profile";
    }

    @PostMapping("/verify/phone/confirm")
    public String confirmPhoneVerification(@RequestParam("code") String code,
                                           RedirectAttributes redirectAttributes,
                                           Locale locale) {
        User user = authHelper.getAuthenticatedUser();
        if (user == null) {
            return "redirect:/login";
        }
        Optional<String> error = phoneVerificationService.confirmCode(user, code);
        if (error.isPresent()) {
            String key = mapPhoneErrorKey(error.get());
            redirectAttributes.addFlashAttribute(
                    "verifyError",
                    messageSource.getMessage(key, null, locale)
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "verifySuccess",
                    messageSource.getMessage("verify.phone.success", null, locale)
            );
        }
        return "redirect:/profile";
    }

    private String mapEmailErrorKey(String message) {
        if (message == null) {
            return "verify.email.error.generic";
        }
        return switch (message) {
            case "Invalid verification token." -> "verify.email.error.invalid";
            case "Verification token not found." -> "verify.email.error.notFound";
            case "Verification token already used." -> "verify.email.error.used";
            case "Verification token expired." -> "verify.email.error.expired";
            default -> "verify.email.error.generic";
        };
    }

    private String mapPhoneErrorKey(String message) {
        if (message == null) {
            return "verify.phone.error.generic";
        }
        return switch (message) {
            case "User not found." -> "verify.phone.error.user";
            case "No verification code found. Please request a new code." -> "verify.phone.error.missing";
            case "Verification code already used." -> "verify.phone.error.used";
            case "Verification code expired. Please request a new code." -> "verify.phone.error.expired";
            case "Too many attempts. Please request a new code." -> "verify.phone.error.attempts";
            case "Invalid code. Please try again." -> "verify.phone.error.invalid";
            default -> "verify.phone.error.generic";
        };
    }
}
