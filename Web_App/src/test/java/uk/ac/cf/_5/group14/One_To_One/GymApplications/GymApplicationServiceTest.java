package uk.ac.cf._5.group14.One_To_One.GymApplications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.cf._5.group14.One_To_One.GymProfile.GymProfileRepository;
import uk.ac.cf._5.group14.One_To_One.Membership.EmailService;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;
import uk.ac.cf._5.group14.One_To_One.Verification.EmailVerificationService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GymApplicationServiceTest {

    @Autowired
    private GymApplicationService gymApplicationService;

    @Autowired
    private GymApplicationRepository applicationRepository;

    @Autowired
    private GymApplicationMessageRepository messageRepository;

    @Autowired
    private GymProfileRepository gymProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    private User reviewer;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        reviewer = new User("platform+" + suffix + "@example.com", "Platform", "Admin", "platform_admin_" + suffix, "password123");
        reviewer.setRole(Role.PLATFORM_ADMIN);
        reviewer = userRepository.save(reviewer);
    }

    @Test
    void requestMoreInfoMovesApplicationToNeedsInfoAndRecordsAdminMessage() {
        GymApplication application = applicationRepository.save(application("needs_info"));

        GymApplication updated = gymApplicationService.requestMoreInfo(
                application.getId(),
                reviewer,
                "More information needed",
                "Please add your insurance details.");

        assertThat(updated.getStatus()).isEqualTo(GymApplicationStatus.NEEDS_INFO);
        assertThat(updated.getReviewedAt()).isNotNull();
        assertThat(updated.getReviewedByUserId()).isEqualTo(reviewer.getId());

        List<GymApplicationMessage> messages = messageRepository.findByApplicationIdOrderByCreatedAtAsc(application.getId());
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getSenderType()).isEqualTo(GymApplicationMessageSender.ADMIN);
        assertThat(messages.get(0).getBody()).contains("insurance details");
        assertThat(messages.get(0).isEmailed()).isTrue();
        verify(emailService).sendAdminMessage(eq(application.getAdminEmail()), eq("More information needed"), contains("insurance details"));
    }

    @Test
    void declineClosesApplicationWithReviewNotesAndMessage() {
        GymApplication application = applicationRepository.save(application("decline"));

        GymApplication updated = gymApplicationService.decline(
                application.getId(),
                reviewer,
                "Application update",
                "We cannot approve this application.",
                "Missing verification documents.");

        assertThat(updated.getStatus()).isEqualTo(GymApplicationStatus.DECLINED);
        assertThat(updated.getReviewedByUserId()).isEqualTo(reviewer.getId());
        assertThat(updated.getReviewNotes()).isEqualTo("Missing verification documents.");
        assertThat(messageRepository.findByApplicationIdOrderByCreatedAtAsc(application.getId()))
                .anySatisfy(message -> {
                    assertThat(message.getSenderType()).isEqualTo(GymApplicationMessageSender.ADMIN);
                    assertThat(message.getBody()).contains("cannot approve");
                });
    }

    @Test
    void approveCreatesGymAdminAccountProfileAndSystemMessage() {
        GymApplication application = applicationRepository.save(application("approve"));

        GymApplication updated = gymApplicationService.approve(application.getId(), reviewer, "Welcome to the platform.");

        assertThat(updated.getStatus()).isEqualTo(GymApplicationStatus.APPROVED);
        assertThat(updated.getReviewedByUserId()).isEqualTo(reviewer.getId());
        assertThat(updated.getApprovedUserId()).isNotNull();

        User approvedUser = userRepository.findById(updated.getApprovedUserId()).orElseThrow();
        assertThat(approvedUser.getRole()).isEqualTo(Role.GYM_ADMIN);
        assertThat(approvedUser.getUsername()).isEqualTo(application.getGymUsername());
        assertThat(approvedUser.isEmailVerified()).isFalse();

        assertThat(gymProfileRepository.findByUserId(approvedUser.getId())).isPresent();
        assertThat(messageRepository.findByApplicationIdOrderByCreatedAtAsc(application.getId()))
                .anySatisfy(message -> {
                    assertThat(message.getSenderType()).isEqualTo(GymApplicationMessageSender.SYSTEM);
                    assertThat(message.getBody()).contains("Gym Secret Code");
                    assertThat(message.getBody()).contains("Welcome to the platform.");
                });
        verify(emailVerificationService).sendVerification(approvedUser);
    }

    private GymApplication application(String label) {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        GymApplication application = new GymApplication();
        application.setGymName("Audit Gym " + label);
        application.setAdminEmail("gym_" + label + "_" + suffix + "@example.com");
        application.setGymUsername("gym_" + label + "_" + suffix);
        application.setRequestedPasswordHash("{noop}Demo123!");
        application.setAddress("1 Audit Street");
        application.setCity("Cardiff");
        application.setContactName("Gym Owner");
        application.setContactPhone("07123456789");
        application.setStatus(GymApplicationStatus.PENDING);
        application.setAccessToken("token_" + label + "_" + suffix);
        return application;
    }
}
