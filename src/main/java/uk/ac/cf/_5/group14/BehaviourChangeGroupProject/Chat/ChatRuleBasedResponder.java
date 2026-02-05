package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ChatRuleBasedResponder {

    private ChatRuleBasedResponder() {}

    public static String respond(String userMessage, ChatContext ctx) {
        String msg = userMessage == null ? "" : userMessage.trim();
        String lower = msg.toLowerCase(Locale.ROOT);

        if (ctx == null) {
            return "I can help, but I don't have your in-app context right now.";
        }

        if (lower.contains("what should i do today") || (lower.contains("today") && lower.contains("do"))) {
            return buildTodayPlan(ctx);
        }

        if (lower.contains("last 3 workout") || lower.contains("last three workout") || lower.contains("summarise my last 3")) {
            return buildRecentWorkouts(ctx);
        }

        if (lower.contains("this week") || lower.contains("how am i doing")) {
            return buildThisWeekHint(ctx);
        }

        if (ctx.requestedDate() != null && (lower.contains("workout") || lower.contains("scheduled") || lower.contains("schedule"))) {
            return buildRequestedDate(ctx);
        }

        // Default: give a compact context-based hint
        return buildTodayPlan(ctx);
    }

    private static String buildTodayPlan(ChatContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("Today (" + ctx.today() + "):\n");

        if (ctx.todaysTasks() != null && !ctx.todaysTasks().isEmpty()) {
            sb.append("Tasks:\n");
            for (String t : ctx.todaysTasks()) sb.append("- ").append(t).append("\n");
        } else {
            sb.append("Tasks: none found.\n");
        }

        if (ctx.todaysScheduledItems() != null && !ctx.todaysScheduledItems().isEmpty()) {
            sb.append("Training:\n");
            for (String w : ctx.todaysScheduledItems()) sb.append("- ").append(w).append("\n");
        } else {
            sb.append("Training: nothing scheduled.\n");
        }

        if (ctx.level() != null || ctx.points() != null) {
            sb.append("\nStats: level ").append(ctx.level() != null ? ctx.level() : "?")
              .append(", points ").append(ctx.points() != null ? ctx.points() : "?").append(".");
        }

        return sb.toString().trim();
    }

    private static String buildRecentWorkouts(ChatContext ctx) {
        if (ctx.recentWorkouts() == null || ctx.recentWorkouts().isEmpty()) {
            return "I can't see any recent workout sessions yet.";
        }
        StringBuilder sb = new StringBuilder("Your last workouts:\n");
        for (var w : ctx.recentWorkouts()) {
            sb.append("- ").append(w.date()).append(" - ").append(w.name())
              .append(" (sets=").append(w.totalSets()).append(", completed=").append(w.completed()).append(")\n");
        }
        return sb.toString().trim();
    }

    private static String buildRequestedDate(ChatContext ctx) {
        LocalDate d = ctx.requestedDate();
        String pretty = d.format(DateTimeFormatter.ofPattern("d MMM uuuu"));
        if (ctx.requestedDateItems() == null || ctx.requestedDateItems().isEmpty()) {
            return "For " + pretty + ", I don't see any scheduled tasks or training items.";
        }
        StringBuilder sb = new StringBuilder("For " + pretty + ":\n");
        for (String i : ctx.requestedDateItems()) sb.append("- ").append(i).append("\n");
        return sb.toString().trim();
    }

    private static String buildThisWeekHint(ChatContext ctx) {
        // Keep simple without heavy queries; steer user with what we have.
        StringBuilder sb = new StringBuilder();
        sb.append("I can summarise your week if you tell me which dates you mean (e.g. Mon-Sun).\n");
        sb.append("Right now I can see today and your recent workouts.\n\n");
        sb.append(buildRecentWorkouts(ctx));
        return sb.toString().trim();
    }
}
