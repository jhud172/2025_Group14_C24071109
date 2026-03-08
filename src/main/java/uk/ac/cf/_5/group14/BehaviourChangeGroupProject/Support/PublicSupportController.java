package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.AuthHelper;
import uk.ac.cf._5.group14.BehaviourChangeGroupProject.Users.User;

@Controller
public class PublicSupportController {

    private final SupportRequestRepository supportRequestRepository;
    private final AuthHelper authHelper;

    public PublicSupportController(SupportRequestRepository supportRequestRepository,
                                   AuthHelper authHelper) {
        this.supportRequestRepository = supportRequestRepository;
        this.authHelper = authHelper;
    }

    @PostMapping("/support/feedback")
    public String submitFeedback(@RequestParam("requestType") String requestType,
                                 @RequestParam("subject") String subject,
                                 @RequestParam("message") String message,
                                 @RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "email", required = false) String email,
                                 @RequestParam(value = "allowEmailReply", required = false) String allowEmailReply,
                                 RedirectAttributes redirectAttributes) {

        SupportRequestType type;
        try {
            type = SupportRequestType.valueOf(requestType.trim().toUpperCase());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("feedbackError", "Choose a valid support type.");
            return "redirect:/";
        }

        String cleanSubject = subject == null ? "" : subject.trim();
        String cleanMessage = message == null ? "" : message.trim();
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim();
        boolean canReply = allowEmailReply != null;

        if (cleanSubject.isBlank() || cleanSubject.length() > 180) {
            redirectAttributes.addFlashAttribute("feedbackError", "Subject is required and must be under 180 characters.");
            return "redirect:/";
        }
        if (cleanMessage.isBlank() || cleanMessage.length() > 5000) {
            redirectAttributes.addFlashAttribute("feedbackError", "Message is required and must be under 5000 characters.");
            return "redirect:/";
        }

        if (canReply && (cleanEmail.isBlank() || !cleanEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$"))) {
            redirectAttributes.addFlashAttribute("feedbackError", "Add a valid email if you'd like a response.");
            return "redirect:/";
        }

        if (type == SupportRequestType.QUERY && !canReply) {
            redirectAttributes.addFlashAttribute("feedbackError", "For queries, tick the box to allow an email response.");
            return "redirect:/";
        }

        User user = authHelper.getAuthenticatedUser();
        SupportRequest row = new SupportRequest();
        row.setRequestType(type);
        row.setSubject(cleanSubject);
        row.setMessage(cleanMessage);
        row.setAllowEmailReply(canReply);

        if (user != null) {
            row.setUser(user);
            row.setSubmitterName((user.getFirstName() + " " + user.getLastName()).trim());
            row.setSubmitterEmail(user.getEmail());
        } else {
            row.setSubmitterName(cleanName.isBlank() ? null : cleanName);
            row.setSubmitterEmail(cleanEmail.isBlank() ? null : cleanEmail);
        }

        supportRequestRepository.save(row);
        redirectAttributes.addFlashAttribute("feedbackSuccess", "Thanks. Your support request was submitted.");
        return "redirect:/";
    }
}
