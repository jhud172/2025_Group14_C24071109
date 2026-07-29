package uk.ac.cf._5.group14.One_To_One.StorageTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.One_To_One.Merch.MerchProductService;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserLookupService;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutBuilderService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateUploadAccessIntegrationTest {

    private static final byte[] CHAT_BYTES = "synthetic-chat-image".getBytes();
    private static final byte[] VIDEO_BYTES = "synthetic-workout-video".getBytes();
    private static final byte[] MERCH_BYTES = "synthetic-merch-image".getBytes();
    private static final Path UPLOAD_ROOT = createUploadRoot();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserLookupService userLookupService;

    @MockitoBean
    private WorkoutBuilderService workoutBuilderService;

    @MockitoBean
    private MerchProductService merchProductService;

    private User owner;
    private String chatUrl;
    private String workoutUrl;

    @DynamicPropertySource
    static void storageProperties(DynamicPropertyRegistry registry) {
        registry.add("app.storage.profile-dir", () -> UPLOAD_ROOT.resolve("profile").toString());
        registry.add("app.storage.chat-dir", () -> UPLOAD_ROOT.resolve("chat").toString());
        registry.add("app.storage.merch-dir", () -> UPLOAD_ROOT.resolve("merch").toString());
        registry.add("app.storage.workout-video-dir", () -> UPLOAD_ROOT.resolve("workout-videos").toString());
    }

    @BeforeEach
    void createSyntheticFiles() throws IOException {
        owner = userLookupService.findByLoginIdentifier("demo");
        String chatFilename = "chat-" + owner.getId() + "-synthetic.png";
        chatUrl = "/uploads/chat/" + chatFilename;
        Path chatPath = UPLOAD_ROOT.resolve("chat").resolve(chatFilename);
        Files.createDirectories(chatPath.getParent());
        Files.write(chatPath, CHAT_BYTES);

        workoutUrl = "/uploads/workout-videos/user-" + owner.getId()
                + "/session-321/synthetic.webm";
        Path workoutPath = UPLOAD_ROOT.resolve("workout-videos")
                .resolve("user-" + owner.getId())
                .resolve("session-321")
                .resolve("synthetic.webm");
        Files.createDirectories(workoutPath.getParent());
        Files.write(workoutPath, VIDEO_BYTES);

        Path merchPath = UPLOAD_ROOT.resolve("merch").resolve("synthetic.png");
        Files.createDirectories(merchPath.getParent());
        Files.write(merchPath, MERCH_BYTES);
    }

    @Test
    void ownerCanReadChatImage() throws Exception {
        mockMvc.perform(get(chatUrl).with(user("demo").roles("USER", "CLIENT")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(CHAT_BYTES));
    }

    @Test
    void anonymousCannotReadChatImage() throws Exception {
        mockMvc.perform(get(chatUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anotherUserCannotReadChatImage() throws Exception {
        mockMvc.perform(get(chatUrl).with(user("user2").roles("USER", "CLIENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void ownerCanReadWorkoutVideo() throws Exception {
        mockMvc.perform(get(workoutUrl).with(user("demo").roles("USER", "CLIENT")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().bytes(VIDEO_BYTES));
    }

    @Test
    void anonymousCannotReadWorkoutVideo() throws Exception {
        mockMvc.perform(get(workoutUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anotherUserCannotReadWorkoutVideo() throws Exception {
        mockMvc.perform(get(workoutUrl).with(user("user2").roles("USER", "CLIENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void merchandiseImageRemainsPublic() throws Exception {
        mockMvc.perform(get("/uploads/merch/synthetic.png"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(MERCH_BYTES));
    }

    private static Path createUploadRoot() {
        try {
            return Files.createTempDirectory("one-to-one-private-upload-test-");
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
