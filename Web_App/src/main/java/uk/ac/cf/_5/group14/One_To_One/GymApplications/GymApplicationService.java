package uk.ac.cf._5.group14.One_To_One.GymApplications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfile;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileService;
import uk.ac.cf._5.group14.One_To_One.Membership.EmailService;
import uk.ac.cf._5.group14.One_To_One.Users.GymSignupForm;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;
import uk.ac.cf._5.group14.One_To_One.Verification.EmailVerificationService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class GymApplicationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final GymApplicationRepository gymApplicationRepository;
    private final GymApplicationMessageRepository gymApplicationMessageRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final GymProfileService gymProfileService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    @Value("${app.site.base-url:https://crystal-production.com}")
    private String baseUrl;

    public GymApplicationService(GymApplicationRepository gymApplicationRepository,
                                 GymApplicationMessageRepository gymApplicationMessageRepository,
                                 PasswordEncoder passwordEncoder,
                                 UserRepository userRepository,
                                 UserService userService,
                                 GymProfileService gymProfileService,
                                 EmailVerificationService emailVerificationService,
                                 EmailService emailService) {
        this.gymApplicationRepository = gymApplicationRepository;
        this.gymApplicationMessageRepository = gymApplicationMessageRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.gymProfileService = gymProfileService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
    }

    @Transactional
    public GymApplication submitApplication(GymSignupForm form) {
        GymApplication application = new GymApplication();
        application.setGymName(form.getGymName().trim());
        application.setAdminEmail(form.getAdminEmail().trim().toLowerCase(Locale.ROOT));
        application.setGymUsername(form.getGymUsername().trim().toLowerCase(Locale.ROOT));
        application.setRequestedPasswordHash(passwordEncoder.encode(form.getPassword()));
        application.setAddress(form.getAddress().trim());
        application.setCity(form.getCity().trim());
        application.setContactName(form.getContactName().trim());
        application.setContactPhone(form.getContactPhone().trim());
        application.setStatus(GymApplicationStatus.PENDING);
        application.setAccessToken(generateAccessToken());

        GymApplication saved = gymApplicationRepository.save(application);

        addSystemMessage(saved, "Application received",
            "Your gym application has been submitted for review. We will contact you if we need more information.");
        sendApplicationEmail(saved,
            "Your 1 to 1 gym application is under review",
            buildApplicantEmailBody(saved,
                "We have received your gym application and placed it into the review queue.",
                "You can review the application status and reply with more information here: " + buildPortalUrl(saved)));

        return saved;
    }

    public List<GymApplication> getAllApplications() {
        return gymApplicationRepository.findAllByOrderBySubmittedAtDesc();
    }

    public List<GymApplication> getOpenApplications() {
        return gymApplicationRepository.findAllByOrderBySubmittedAtDesc().stream()
            .filter(application -> application.getStatus() != GymApplicationStatus.APPROVED
                && application.getStatus() != GymApplicationStatus.DECLINED)
            .toList();
    }

    public long countOpenApplications() {
        return gymApplicationRepository.countByStatusIn(List.of(
            GymApplicationStatus.PENDING,
            GymApplicationStatus.UNDER_REVIEW,
            GymApplicationStatus.NEEDS_INFO
        ));
    }

    public GymApplication getApplication(Long id) {
        return gymApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Gym application not found."));
    }

    public GymApplication getApplicationByAccessToken(String accessToken) {
        return gymApplicationRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new IllegalArgumentException("Gym application not found."));
    }

    public List<GymApplicationMessage> getMessages(Long applicationId) {
        return gymApplicationMessageRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId);
    }

    @Transactional
    public GymApplication markUnderReview(Long applicationId, User reviewer, String notes) {
        GymApplication application = getApplication(applicationId);
        application.setStatus(GymApplicationStatus.UNDER_REVIEW);
        application.setReviewedAt(Instant.now());
        application.setReviewedByUserId(reviewer == null ? null : reviewer.getId());
        application.setReviewNotes(trimToNull(notes));
        return gymApplicationRepository.save(application);
    }

    @Transactional
    public GymApplication requestMoreInfo(Long applicationId, User reviewer, String subject, String message) {
        GymApplication application = markUnderReview(applicationId, reviewer, null);
        application.setStatus(GymApplicationStatus.NEEDS_INFO);
        gymApplicationRepository.save(application);
        addAdminMessage(application, reviewer, subject, message);
        return application;
    }

    @Transactional
    public GymApplication decline(Long applicationId, User reviewer, String subject, String message, String reviewNotes) {
        GymApplication application = getApplication(applicationId);
        application.setStatus(GymApplicationStatus.DECLINED);
        application.setReviewedAt(Instant.now());
        application.setReviewedByUserId(reviewer == null ? null : reviewer.getId());
        application.setReviewNotes(trimToNull(reviewNotes));
        gymApplicationRepository.save(application);
        addAdminMessage(application, reviewer, subject, message);
        return application;
    }

    @Transactional
    public GymApplication approve(Long applicationId, User reviewer, String welcomeMessage) {
        GymApplication application = getApplication(applicationId);
        if (userRepository.existsByEmailIgnoreCase(application.getAdminEmail())) {
            throw new IllegalStateException("This gym email already belongs to an existing account.");
        }
        if (userRepository.existsByUsernameIgnoreCase(application.getGymUsername())) {
            throw new IllegalStateException("This gym username is no longer available.");
        }

        String[] contactNames = splitName(application.getContactName());
        User user = new User(
            application.getAdminEmail(),
            contactNames[0],
            contactNames[1],
            application.getGymUsername(),
            application.getRequestedPasswordHash()
        );
        user.setRole(Role.GYM_ADMIN);
        user.setEmailVerified(false);

        User savedUser = userService.saveUser(user, true);

        GymProfile profile = new GymProfile(savedUser.getId(), application.getGymName());
        profile.setAddress(application.getAddress());
        profile.setCity(application.getCity());
        profile.setContactName(application.getContactName());
        profile.setContactPhone(application.getContactPhone());
        GymProfile savedProfile = gymProfileService.saveProfile(profile);

        application.setStatus(GymApplicationStatus.APPROVED);
        application.setReviewedAt(Instant.now());
        application.setReviewedByUserId(reviewer == null ? null : reviewer.getId());
        application.setApprovedUserId(savedUser.getId());
        gymApplicationRepository.save(application);

        try {
            emailVerificationService.sendVerification(savedUser);
        } catch (Exception ex) {
            log.warn("Failed to send verification email after gym approval for {}", savedUser.getEmail(), ex);
        }

        String formattedCode = formatGymCode(savedProfile.getGymCode());
        String body = buildApplicantEmailBody(
            application,
            "Your gym application has been approved. Your live gym admin account is ready.",
            "Gym Username: " + savedUser.getUsername()
                + "\nGym Secret Code: " + formattedCode
                + "\n\nVerify your email to complete access."
                + (trimToNull(welcomeMessage) == null ? "" : "\n\n" + welcomeMessage)
        );
        sendApplicationEmail(application, "Your 1 to 1 gym application was approved", body);
        addSystemMessage(application, "Application approved", body);
        return application;
    }

    @Transactional
    public GymApplicationMessage addAdminMessage(GymApplication application, User sender, String subject, String message) {
        String cleanSubject = trimToNull(subject);
        String cleanMessage = trimToNull(message);
        if (cleanMessage == null) {
            throw new IllegalArgumentException("Message cannot be blank.");
        }

        GymApplicationMessage record = new GymApplicationMessage();
        record.setApplicationId(application.getId());
        record.setSenderType(GymApplicationMessageSender.ADMIN);
        record.setSenderUserId(sender == null ? null : sender.getId());
        record.setSenderEmail(application.getAdminEmail());
        record.setSubject(cleanSubject);
        record.setBody(cleanMessage);
        record.setEmailed(true);
        GymApplicationMessage saved = gymApplicationMessageRepository.save(record);

        String body = cleanMessage + "\n\nReply in your application portal: " + buildPortalUrl(application);
        sendApplicationEmail(application, cleanSubject == null ? "Update on your 1 to 1 gym application" : cleanSubject, body);
        return saved;
    }

    @Transactional
    public GymApplicationMessage addApplicantReply(String accessToken, String message) {
        GymApplication application = getApplicationByAccessToken(accessToken);
        if (application.getStatus() == GymApplicationStatus.APPROVED || application.getStatus() == GymApplicationStatus.DECLINED) {
            throw new IllegalStateException("This application is already closed.");
        }

        String cleanMessage = trimToNull(message);
        if (cleanMessage == null) {
            throw new IllegalArgumentException("Reply cannot be blank.");
        }

        if (application.getStatus() == GymApplicationStatus.NEEDS_INFO) {
            application.setStatus(GymApplicationStatus.UNDER_REVIEW);
            gymApplicationRepository.save(application);
        }

        GymApplicationMessage reply = new GymApplicationMessage();
        reply.setApplicationId(application.getId());
        reply.setSenderType(GymApplicationMessageSender.APPLICANT);
        reply.setSenderEmail(application.getAdminEmail());
        reply.setBody(cleanMessage);
        reply.setEmailed(false);
        return gymApplicationMessageRepository.save(reply);
    }

    private GymApplicationMessage addSystemMessage(GymApplication application, String subject, String message) {
        GymApplicationMessage systemMessage = new GymApplicationMessage();
        systemMessage.setApplicationId(application.getId());
        systemMessage.setSenderType(GymApplicationMessageSender.SYSTEM);
        systemMessage.setSubject(subject);
        systemMessage.setBody(message);
        systemMessage.setEmailed(false);
        return gymApplicationMessageRepository.save(systemMessage);
    }

    private void sendApplicationEmail(GymApplication application, String subject, String body) {
        emailService.sendAdminMessage(application.getAdminEmail(), subject, body);
    }

    private String buildApplicantEmailBody(GymApplication application, String headline, String detail) {
        return headline
            + "\n\nGym: " + application.getGymName()
            + "\nPrimary contact: " + application.getContactName()
            + "\nPortal: " + buildPortalUrl(application)
            + "\n\n" + detail;
    }

    private String buildPortalUrl(GymApplication application) {
        return baseUrl + "/signup/gym/application/" + application.getAccessToken();
    }

    private String generateAccessToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String[] splitName(String fullName) {
        String cleaned = trimToNull(fullName);
        if (cleaned == null) {
            return new String[]{"Gym", "Admin"};
        }
        String[] parts = cleaned.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], "Admin"};
        }
        return new String[]{parts[0], parts[1]};
    }

    private String formatGymCode(String gymCode) {
        if (gymCode == null || gymCode.length() != 16) {
            return gymCode;
        }
        return gymCode.substring(0, 4)
            + "-" + gymCode.substring(4, 8)
            + "-" + gymCode.substring(8, 12)
            + "-" + gymCode.substring(12, 16);
    }
}
