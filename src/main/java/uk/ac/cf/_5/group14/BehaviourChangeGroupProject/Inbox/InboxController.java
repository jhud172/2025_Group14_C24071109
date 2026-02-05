package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Inbox;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging.MessageThread;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.UserRepository;

@Controller
@RequestMapping("/inbox")
public class InboxController {

    private final InboxService inboxService;
    private final AuthHelper authHelper;
    private final UserRepository userRepository;

    public InboxController(InboxService inboxService, AuthHelper authHelper, UserRepository userRepository) {
        this.inboxService = inboxService;
        this.authHelper = authHelper;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ModelAndView inbox() {
        User user = authHelper.getAuthenticatedUser();
        ModelAndView mav = new ModelAndView("inbox/index");
        mav.addObject("conversations", inboxService.listConversations(user));
        return mav;
    }

    @GetMapping("/{conversationId}")
    public ModelAndView thread(@PathVariable Long conversationId) {
        User user = authHelper.getAuthenticatedUser();
        MessageThread thread = inboxService.getConversationOrThrow(user, conversationId);
        inboxService.markRead(user, conversationId);

        Long otherUserId = user != null && user.getId() != null && user.getId().equals(thread.getClientId())
                ? thread.getTrainerId()
                : thread.getClientId();
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        ModelAndView mav = new ModelAndView("inbox/thread");
        mav.addObject("conversations", inboxService.listConversations(user));
        mav.addObject("thread", thread);
        mav.addObject("messages", inboxService.getMessages(user, conversationId));
        mav.addObject("conversationId", conversationId);
        mav.addObject("currentUserId", user != null ? user.getId() : null);
        mav.addObject("otherUser", otherUser);
        return mav;
    }

    @PostMapping("/{conversationId}/send")
    public ModelAndView send(@PathVariable Long conversationId, @RequestParam("body") String body) {
        User user = authHelper.getAuthenticatedUser();
        inboxService.sendMessage(user, conversationId, body, null, null, null);
        return new ModelAndView("redirect:/inbox/" + conversationId);
    }

    @PostMapping("/start/{otherUserId}")
    public ModelAndView start(@PathVariable Long otherUserId) {
        User user = authHelper.getAuthenticatedUser();
        Long conversationId = inboxService.startOrGetDirectConversation(user, otherUserId);
        return new ModelAndView("redirect:/inbox/" + conversationId);
    }
}
