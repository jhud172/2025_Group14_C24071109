package uk.ac.cf._5.group14.One_To_One.Config;

import uk.ac.cf._5.group14.One_To_One.Users.Role;

/**
 * Shared wording and recovery actions for pages used by more than one account role.
 */
public record RoleSurfaceContext(
        String roleKey,
        String roleLabel,
        String dashboardPath,
        String inboxHeading,
        String inboxCopy,
        String notificationCopy,
        String inboxEmptyTitle,
        String inboxEmptyCopy,
        String inboxEmptyActionLabel,
        String inboxEmptyActionPath,
        String threadEmptyCopy,
        String messagePlaceholder,
        String supportHeading,
        String supportCopy,
        String supportSubmitLabel,
        String workoutHeading,
        String workoutCopy,
        String workoutEmptyCopy,
        String workoutCreateLabel,
        String goalsHeading,
        String goalsCopy,
        String goalsEmptyCopy,
        String goalsPrimaryLabel,
        String goalsPrimaryPath,
        boolean coachingToolsPrimary
) {
    public static RoleSurfaceContext forRole(Role role) {
        if (role == null) {
            return guest();
        }
        return switch (role) {
            case CLIENT -> new RoleSurfaceContext(
                    "client", "Client", "/dashboard",
                    "Coaching inbox", "Keep your conversations with your trainer in one place.",
                    "Session updates, replies and coaching reminders for your account.",
                    "No coaching conversations yet", "Once you connect with a trainer, your messages will appear here.",
                    "Explore trainers", "/explore", "Send the first message to start this coaching conversation.",
                    "Write a message to your trainer…",
                    "Client support", "Tell the support team what is getting in the way of your training experience.",
                    "Send to client support",
                    "My workouts", "View and build workout templates for your own training.",
                    "Create your first workout template when you are ready to train.", "Create workout",
                    "My goals", "Set a clear target and keep your progress visible.",
                    "Create a goal to give your next training block a clear focus.", "Create goal", "/goals/create", true);
            case TRAINER -> new RoleSurfaceContext(
                    "trainer", "Trainer", "/trainer/dashboard",
                    "Client inbox", "Keep client questions, check-ins and coaching replies together.",
                    "New client messages and coaching follow-ups that need your attention.",
                    "No client conversations yet", "Open your client list to start or continue a coaching conversation.",
                    "Open clients", "/trainer/clients", "Send the first message to begin this client conversation.",
                    "Write a message to your client…",
                    "Trainer support", "Share an account, coaching-tool or service issue with the support team.",
                    "Send to trainer support",
                    "Client workout studio", "Build reusable workout templates for client programmes.",
                    "Create your first reusable workout template for a client programme.", "Create template",
                    "Client goals", "Choose a client before reviewing or creating their coaching goals.",
                    "Open your client list to choose whose goals you want to manage.", "Choose client", "/trainer/clients", true);
            case GYM_ADMIN -> new RoleSurfaceContext(
                    "gym-admin", "Gym admin", "/gym/dashboard",
                    "Coaching inbox", "This inbox is for personal coaching conversations, not gym operations.",
                    "Personal coaching updates for this account. Use support for gym administration queries.",
                    "No personal conversations", "Gym operations are managed from your dashboard; support can help with account queries.",
                    "Contact support", "/support", "Send the first message in this personal coaching conversation.",
                    "Write a personal coaching message…",
                    "Gym support", "Send a gym account, trainer, membership or platform query to the support team.",
                    "Send to gym support",
                    "Personal workout studio", "Workouts created here belong to your account and are not gym-wide programmes.",
                    "No personal workout templates have been created for this account.", "Create personal workout",
                    "Personal goals", "Goals here belong to your account and do not control gym targets or memberships.",
                    "No personal goals have been created for this account.", "Return to gym dashboard", "/gym/dashboard", false);
            case PLATFORM_ADMIN, SUPER_ADMIN -> new RoleSurfaceContext(
                    "platform-admin", "Platform admin", "/admin/dashboard",
                    "Coaching inbox", "This inbox is for personal coaching conversations, not platform operations.",
                    "Personal account updates. Customer support requests are managed in the support inbox.",
                    "No personal conversations", "Use the support inbox to review requests from clients, trainers and gyms.",
                    "Open support inbox", "/admin/feedback", "Send the first message in this personal coaching conversation.",
                    "Write a personal coaching message…",
                    "Platform support", "Submit a support request for this account or review incoming requests in platform operations.",
                    "Send support request",
                    "Personal workout studio", "Workouts created here belong to your account and are not platform content.",
                    "No personal workout templates have been created for this account.", "Create personal workout",
                    "Personal goals", "Goals here belong to your account and are separate from platform operations.",
                    "No personal goals have been created for this account.", "Return to operations", "/admin/dashboard", false);
        };
    }

    public static RoleSurfaceContext guest() {
        return new RoleSurfaceContext(
                "guest", "Visitor", "/", "Inbox", "Sign in to access your conversations.",
                "Account notifications appear here after you sign in.", "No conversations", "Sign in to see your conversations.",
                "Sign in", "/login", "Sign in to start a conversation.", "Write a message…",
                "Contact support", "Tell us what you need help with and the support team will review it.",
                "Send support request", "Workout studio", "Sign in to access your workouts.",
                "Sign in to create and manage workout templates.", "Sign in", "Goals", "Sign in to access your goals.",
                "Sign in to create and track goals.", "Sign in", "/login", false);
    }
}
