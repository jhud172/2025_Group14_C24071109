package uk.ac.cf._5.group14.One_To_One.Users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfile;
import uk.ac.cf._5.group14.One_To_One.TrainerProfile.TrainerProfileService;
import uk.ac.cf._5.group14.One_To_One.Verification.EmailVerificationService;
import uk.ac.cf._5.group14.One_To_One.Verification.PhoneVerificationService;
import uk.ac.cf._5.group14.One_To_One.Verification.VerificationController;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerGuestStateTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void trainerSignupRedirectsThroughGuardedSuccessWithCodeAndEmail() {
        UserService userService = mock(UserService.class);
        TrainerProfileService trainerProfileService = mock(TrainerProfileService.class);
        EmailVerificationService emailVerificationService = mock(EmailVerificationService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "trainerProfileService", trainerProfileService);
        ReflectionTestUtils.setField(controller, "emailVerificationService", emailVerificationService);

        TrainerSignupForm form = validTrainerSignupForm();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.emailExists(form.getEmail())).thenReturn(false);
        when(userService.usernameExists(form.getUsername())).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        TrainerProfile profile = new TrainerProfile(42L);
        profile.setTrainerCode("ABCD1234EFGH");
        when(trainerProfileService.getOrCreateProfile(42L)).thenReturn(profile);
        when(trainerProfileService.updateProfile(42L, profile)).thenReturn(profile);

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.signupTrainer(
                form,
                bindingResult,
                new ExtendedModelMap(),
                redirectAttributes
        );

        assertThat(result).isEqualTo("redirect:/signup/trainer/success");
        assertThat(redirectAttributes.getFlashAttributes().get("trainerCode"))
                .isEqualTo("ABCD-1234-EFGH");
        assertThat(redirectAttributes.getFlashAttributes().get("verifyEmail"))
                .isEqualTo("trainer@example.com");
        assertThat(redirectAttributes.getFlashAttributes().get("verificationEmailSent"))
                .isEqualTo(true);
        verify(emailVerificationService).sendVerification(any(User.class));
    }

    @Test
    void trainerSuccessRequiresOneTimeFlashState() {
        UserController controller = new UserController();

        assertThat(controller.showTrainerSignupSuccess(new ExtendedModelMap()))
                .isEqualTo("redirect:/signup/trainer");

        ExtendedModelMap model = new ExtendedModelMap();
        model.addAttribute("trainerCode", "ABCD-1234-EFGH");
        model.addAttribute("verifyEmail", "trainer@example.com");

        assertThat(controller.showTrainerSignupSuccess(model))
                .isEqualTo("public-views/auth/signup-trainer-success");
        assertThat(model)
                .containsEntry("authPageLayout", true)
                .containsEntry("disableGlobalChatbot", true);
    }

    @Test
    void trainerSignupCarriesEmailDeliveryFailureToTheSuccessPage() {
        UserService userService = mock(UserService.class);
        TrainerProfileService trainerProfileService = mock(TrainerProfileService.class);
        EmailVerificationService emailVerificationService = mock(EmailVerificationService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "trainerProfileService", trainerProfileService);
        ReflectionTestUtils.setField(controller, "emailVerificationService", emailVerificationService);

        TrainerSignupForm form = validTrainerSignupForm();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });
        TrainerProfile profile = new TrainerProfile(42L);
        profile.setTrainerCode("ABCD1234EFGH");
        when(trainerProfileService.getOrCreateProfile(42L)).thenReturn(profile);
        when(trainerProfileService.updateProfile(42L, profile)).thenReturn(profile);
        doThrow(new IllegalStateException("mail unavailable"))
                .when(emailVerificationService).sendVerification(any(User.class));

        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String result = controller.signupTrainer(
                form,
                bindingResult,
                new ExtendedModelMap(),
                redirectAttributes
        );

        assertThat(result).isEqualTo("redirect:/signup/trainer/success");
        assertThat(redirectAttributes.getFlashAttributes().get("verificationEmailSent"))
                .isEqualTo(false);
    }

    @Test
    void phoneCodePageRejectsAnonymousUsersButStillRendersForSignedInUsers() {
        EmailVerificationService emailVerificationService = mock(EmailVerificationService.class);
        PhoneVerificationService phoneVerificationService = mock(PhoneVerificationService.class);
        AuthHelper authHelper = mock(AuthHelper.class);
        UserService userService = mock(UserService.class);
        MessageSource messageSource = mock(MessageSource.class);
        VerificationController controller = new VerificationController(
                emailVerificationService,
                phoneVerificationService,
                authHelper,
                userService,
                messageSource
        );

        ExtendedModelMap anonymousModel = new ExtendedModelMap();
        assertThat(controller.showPhoneCodePage(anonymousModel)).isEqualTo("redirect:/login");
        assertThat(anonymousModel).doesNotContainKey("authPageLayout");

        User signedInUser = new User();
        when(authHelper.getAuthenticatedUser()).thenReturn(signedInUser);
        ExtendedModelMap signedInModel = new ExtendedModelMap();
        assertThat(controller.showPhoneCodePage(signedInModel))
                .isEqualTo("public-views/verify/phone-code");
        assertThat(signedInModel).containsEntry("authPageLayout", true);
    }

    @Test
    void anonymousEmailCodePageRequiresAnEmailContext() {
        EmailVerificationService emailVerificationService = mock(EmailVerificationService.class);
        PhoneVerificationService phoneVerificationService = mock(PhoneVerificationService.class);
        AuthHelper authHelper = mock(AuthHelper.class);
        UserService userService = mock(UserService.class);
        VerificationController controller = new VerificationController(
                emailVerificationService,
                phoneVerificationService,
                authHelper,
                userService,
                mock(MessageSource.class)
        );

        assertThat(controller.showEmailCodePage(
                null, null, null, null, new ExtendedModelMap(), Locale.UK
        )).isEqualTo("redirect:/login");

        ExtendedModelMap emailModel = new ExtendedModelMap();
        assertThat(controller.showEmailCodePage(
                "trainer@example.com", null, null, null, emailModel, Locale.UK
        )).isEqualTo("public-views/verify/email-code");
        assertThat(emailModel).containsEntry("verifyEmail", "trainer@example.com");
    }

    private TrainerSignupForm validTrainerSignupForm() {
        TrainerSignupForm form = new TrainerSignupForm();
        form.setEmail("trainer@example.com");
        form.setUsername("trainer_one");
        form.setPassword("StrongPass123!");
        form.setConfirmPassword("StrongPass123!");
        form.setBio("Strength and mobility coach.");
        form.setLocation("Cardiff");
        return form;
    }
}
