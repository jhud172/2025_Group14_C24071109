package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.util.StringJoiner;

public class ChatPromptBuilder {

    private ChatPromptBuilder() {}

    public static String buildSystemPrompt(ChatContext ctx) {
        return buildSystemPrompt(ctx, null);
    }

    public static String buildSystemPrompt(ChatContext ctx, String customInstructions) {
        StringJoiner sj = new StringJoiner("\n");

        sj.add("You are Coach Bot, a supportive fitness and habit coach inside a web app.");
        sj.add("Use ONLY the provided context. If information is missing, say you don't have it.");
        sj.add("Be specific and reference the user's real items (tasks, schedule, workouts, notes).");
        sj.add("Keep answers concise and actionable. Do not mention API keys or internal errors.");

        if (customInstructions != null && !customInstructions.isBlank()) {
            sj.add("Custom instructions: " + customInstructions.trim());
        }

        if (ctx == null) {
            return sj.toString();
        }

        sj.add("\n=== CONTEXT ===");
        sj.add("Today: " + ctx.today());

        if (ctx.level() != null || ctx.points() != null) {
            sj.add("User stats: level=" + (ctx.level() != null ? ctx.level() : "?") + ", points=" + (ctx.points() != null ? ctx.points() : "?") );
        }

        if (ctx.todaysTasks() != null && !ctx.todaysTasks().isEmpty()) {
            sj.add("Today's tasks:");
            for (String t : ctx.todaysTasks()) sj.add("- " + t);
        } else {
            sj.add("Today's tasks: (none found)");
        }

        if (ctx.todaysScheduledItems() != null && !ctx.todaysScheduledItems().isEmpty()) {
            sj.add("Today's scheduled training:");
            for (String w : ctx.todaysScheduledItems()) sj.add("- " + w);
        } else {
            sj.add("Today's scheduled training: (none found)");
        }

        if (ctx.requestedDate() != null && ctx.requestedDateItems() != null && !ctx.requestedDateItems().isEmpty()) {
            sj.add("Requested date: " + ctx.requestedDate());
            sj.add("Items on requested date:");
            for (String i : ctx.requestedDateItems()) sj.add("- " + i);
        }

        if (ctx.recentWorkouts() != null && !ctx.recentWorkouts().isEmpty()) {
            sj.add("Recent workouts (most recent first):");
            for (var w : ctx.recentWorkouts()) {
                sj.add("- " + w.date() + " - " + w.name() + " (sets=" + w.totalSets() + ", completed=" + w.completed() + ")");
            }
        }

        if (ctx.recentNotes() != null && !ctx.recentNotes().isEmpty()) {
            sj.add("Recent notes:");
            for (String n : ctx.recentNotes()) sj.add("- " + n);
        }

        sj.add("=== END CONTEXT ===\n");

        return sj.toString();
    }
}
