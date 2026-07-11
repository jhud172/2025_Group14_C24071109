package uk.ac.cf._5.group14.One_To_One.WorkoutTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.cf._5.group14.One_To_One.Config.DatabaseTableAvailability;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Workouts.AiFormFeedbackRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.VideoProcessingStatus;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutBuilderService;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutFormFeedbackService;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSession;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetLog;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetLogRepository;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetVideo;
import uk.ac.cf._5.group14.One_To_One.Workouts.WorkoutSetVideoRepository;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkoutFormFeedbackServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeVideo_rejectsFilesWithoutKnownVideoSignature() throws Exception {
        WorkoutFormFeedbackService service = new WorkoutFormFeedbackService(
                mock(WorkoutBuilderService.class),
                mock(WorkoutSetLogRepository.class),
                mock(WorkoutSetVideoRepository.class),
                mock(AiFormFeedbackRepository.class),
                mock(DatabaseTableAvailability.class),
                tempDir.toString());

        User user = new User();
        user.setId(9L);

        MockMultipartFile upload = new MockMultipartFile("video", "clip.mp4", "video/mp4", "not-a-video".getBytes());

        assertThrows(IllegalArgumentException.class, () -> service.storeVideo(user, 1L, 2L, upload));
    }

    @Test
    void storeVideo_acceptsMp4SignatureAndPersistsPendingVideo() throws Exception {
        WorkoutBuilderService builderService = mock(WorkoutBuilderService.class);
        WorkoutSetLogRepository setLogRepository = mock(WorkoutSetLogRepository.class);
        WorkoutSetVideoRepository videoRepository = mock(WorkoutSetVideoRepository.class);

        WorkoutFormFeedbackService service = new WorkoutFormFeedbackService(
                builderService,
                setLogRepository,
                videoRepository,
                mock(AiFormFeedbackRepository.class),
                mock(DatabaseTableAvailability.class),
                tempDir.toString());

        User user = new User();
        user.setId(9L);

        WorkoutSession session = new WorkoutSession();
        WorkoutSetLog setLog = new WorkoutSetLog();

        when(builderService.getSession(user, 11L)).thenReturn(session);
        when(setLogRepository.findByIdAndSession(7L, session)).thenReturn(Optional.of(setLog));
        when(videoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MockMultipartFile upload = new MockMultipartFile("video", "clip.mp4", "video/mp4", mp4HeaderBytes());

        WorkoutSetVideo saved = service.storeVideo(user, 11L, 7L, upload);

        assertThat(saved.getStatus()).isEqualTo(VideoProcessingStatus.PENDING);
        assertThat(saved.getPath()).contains("/uploads/workout-videos/user-9/session-11/");
    }

    private byte[] mp4HeaderBytes() {
        return new byte[] {
                0x00, 0x00, 0x00, 0x18,
                'f', 't', 'y', 'p',
                'i', 's', 'o', 'm',
                0x00, 0x00, 0x02, 0x00
        };
    }
}
