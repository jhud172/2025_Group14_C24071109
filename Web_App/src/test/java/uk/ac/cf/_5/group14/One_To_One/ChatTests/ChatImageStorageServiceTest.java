package uk.ac.cf._5.group14.One_To_One.ChatTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatAttachmentPayload;
import uk.ac.cf._5.group14.One_To_One.Chat.ChatImageStorageService;
import uk.ac.cf._5.group14.One_To_One.Chat.CoachConversation;
import uk.ac.cf._5.group14.One_To_One.Chat.CoachMessage;
import uk.ac.cf._5.group14.One_To_One.Chat.CoachMessageRepository;
import uk.ac.cf._5.group14.One_To_One.Chat.CoachMessageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeChatImages_rejectsMoreThanFiveImagesInsteadOfSilentlyTruncating() throws Exception {
        ChatImageStorageService service = new ChatImageStorageService(tempDir.toString());
        MockMultipartFile image = png("image.png");

        assertThatThrownBy(() -> service.storeChatImages(12L, List.of(image, image, image, image, image, image)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5");
    }

    @Test
    void clear_doesNotDeleteAnAttachmentOwnedByAnotherUser() throws Exception {
        ChatImageStorageService storage = new ChatImageStorageService(tempDir.toString());
        ChatAttachmentPayload attachment = storage.storeChatImages(22L, List.of(png("peer.png"))).getFirst();
        Path storedFile = tempDir.resolve(Path.of(attachment.url()).getFileName());

        CoachConversation conversation = new CoachConversation();
        conversation.setUserId(11L);

        CoachMessageRepository repository = mock(CoachMessageRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CoachMessageService service = new CoachMessageService(repository, new ObjectMapper(), storage);
        CoachMessage message = service.append(conversation, CoachMessage.Role.USER, "borrowed", List.of(attachment));
        when(repository.findByConversationOrderByCreatedAtAsc(any(), any(Pageable.class))).thenReturn(List.of(message));

        assertThat(service.attachments(message)).isEmpty();
        service.clear(conversation);

        assertThat(Files.exists(storedFile)).isTrue();
    }

    @Test
    void storeChatImages_removesEarlierFilesWhenALaterImageIsRejected() throws Exception {
        ChatImageStorageService service = new ChatImageStorageService(tempDir.toString());
        MockMultipartFile invalid = new MockMultipartFile(
                "files", "broken.png", "image/png", "not-an-image".getBytes());

        assertThatThrownBy(() -> service.storeChatImages(12L, List.of(png("valid.png"), invalid)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported image");
        assertThat(tempDir).isEmptyDirectory();
    }

    @Test
    void storeChatImages_rejectsAnImageLargerThanFourMiBBeforeWriting() {
        ChatImageStorageService service = new ChatImageStorageService(tempDir.toString());
        MockMultipartFile oversized = new MockMultipartFile(
                "files",
                "oversized.png",
                "image/png",
                new byte[(4 * 1024 * 1024) + 1]);

        assertThatThrownBy(() -> service.storeChatImages(12L, List.of(oversized)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("4MB");
        assertThat(tempDir).isEmptyDirectory();
    }

    @Test
    void deleteChatImage_removesOnlyTheOwningUsersStoredFile() throws Exception {
        ChatImageStorageService service = new ChatImageStorageService(tempDir.toString());
        ChatAttachmentPayload attachment = service.storeChatImages(12L, List.of(png("owned.png"))).getFirst();
        Path storedFile = tempDir.resolve(Path.of(attachment.url()).getFileName());

        assertThat(service.deleteChatImage(attachment.url(), 12L)).isTrue();
        assertThat(storedFile).doesNotExist();
    }

    private MockMultipartFile png(String name) throws Exception {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return new MockMultipartFile("files", name, "image/png", output.toByteArray());
    }
}
