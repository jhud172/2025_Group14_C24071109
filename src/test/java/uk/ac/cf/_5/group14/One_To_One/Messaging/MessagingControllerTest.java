package uk.ac.cf._5.group14.One_To_One.Messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import uk.ac.cf._5.group14.One_To_One.Security.AccessGuard;
import uk.ac.cf._5.group14.One_To_One.Users.AuthHelper;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingControllerTest {

    @Mock
    private AuthHelper authHelper;

    @Mock
    private UserService userService;

    @Mock
    private MessagingService messagingService;

    @Mock
    private AccessGuard accessGuard;

    @Test
    void sendFlagsOffPlatformPaymentWithoutMatchingExceptionText() {
        MessagingController controller = new MessagingController(authHelper, userService, messagingService, accessGuard);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        User sender = new User();
        sender.setId(9L);

        MessageThread thread = new MessageThread(44L, 33L, 21L, MessageThreadStatus.OPEN);

        when(authHelper.getAuthenticatedUser()).thenReturn(sender);
        when(messagingService.getThreadForUser(12L, 9L)).thenReturn(thread);
        doThrow(new MessagingException(MessagingException.Reason.OFF_PLATFORM_PAYMENT))
                .when(messagingService)
                .sendMessage(12L, 9L, MessageType.TEXT, "Pay me off platform");

        String view = controller.send(
                12L,
                MessageType.TEXT,
                "Pay me off platform",
                null,
                null,
                null,
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/inbox/12");
        assertThat(redirectAttributes.getFlashAttributes().get("offPlatformBlocked")).isEqualTo(Boolean.TRUE);
    }
}
