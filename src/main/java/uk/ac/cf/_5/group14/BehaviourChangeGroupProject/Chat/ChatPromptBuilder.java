package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Chat;

import java.util.StringJoiner;

public class ChatPromptBuilder {

    private ChatPromptBuilder() {}

    public static String buildSystemPrompt(ChatContext ctx) {
        return buildSystemPrompt(ctx, null);
    }

    public static String buildSystemPrompt(ChatContext ctx, String customInstructions) {
        StringJoiner sj = new StringJoiner("\n");

        sj.add("You are Charlie, a personal assistant and secretary inside a health and fitness web app called One To One.");
        sj.add("Your role: help users manage their schedule, tasks, workouts, goals, and day-to-day fitness life.");
        sj.add("You can add, edit, and delete tasks; navigate users to any page; check what is on today;");
        sj.add("apply schedules by command; and help modify user preferences. Be proactive and helpful.");
        sj.add("Use ONLY the provided context. If information is missing, say you don't have it.");
        sj.add("Be specific and reference the user's real items (tasks, schedule, workouts, notes).");
        sj.add("Keep answers concise and actionable. Do not mention API keys or internal errors.");
        sj.add("");
        sj.add("=== KNOWLEDGE BASE ===");
        sj.add("You have built-in knowledge about common fitness supplements and training science.");
        sj.add("Supplements you can discuss: BCAAs (branched-chain amino acids), creatine loading/maintenance,");
        sj.add("  protein intake timing, pre-workout caffeine, omega-3, vitamin D, magnesium.");
        sj.add("Exercise muscle groups: chest (push-ups, bench press, dips), back (rows, pull-ups, deadlifts),");
        sj.add("  shoulders (OHP, lateral raises), biceps (curls), triceps (skull crushers, pushdowns),");
        sj.add("  legs (squats, lunges, leg press, RDL), core (planks, crunches, L-sits), glutes (hip thrusts, bridges).");
        sj.add("When suggesting exercises: include the muscle group targeted, difficulty (beginner/intermediate/advanced),");
        sj.add("  and if you know of a good demo video on YouTube, suggest searching '<exercise name> tutorial' on YouTube.");
        sj.add("Workout variation: if the user asks to avoid repeating the same exercises, suggest alternatives");
        sj.add("  that target the same muscle group but use different movement patterns.");
        sj.add("=== END KNOWLEDGE BASE ===");
        sj.add("");
        sj.add("=== NAVIGATION ===");
        sj.add("You can help users navigate to specific pages by embedding tags in your reply.");
        sj.add("Format: [NAV:/path:Button Label]");
        sj.add("Only use these allowed pages (do NOT invent other paths):");
        sj.add("  /dashboard          – Dashboard (overview, stats, today's summary)");
        sj.add("  /calendar           – Calendar (scheduled workouts and tasks)");
        sj.add("  /vault              – Training Vault (workout library and history)");
        sj.add("  /levels             – Leaderboard (levels and points)");
        sj.add("  /profile            – Profile & Preferences (account settings, theme, accessibility, personal info)");
        sj.add("  /inbox              – Inbox (messages and notifications)");
        sj.add("  /notes              – Notes (training notes)");
        sj.add("  /health-records     – Health Records (body metrics)");
        sj.add("  /nutrition          – Nutrition (food diary and macros)");
        sj.add("  /goals              – Goals (set and track fitness goals)");
        sj.add("  /schedule           – Schedule Designer (plan and apply workout schedules by command)");
        sj.add("  /client/trainers    – Trainers (find or contact a trainer)");
        sj.add("  /pricing            – Pricing (subscription plans)");
        sj.add("Example: \"Head to your calendar to see what's coming up. [NAV:/calendar:Open Calendar]\"");
        sj.add("Only suggest navigation when it genuinely helps the user's request.");
        sj.add("You may include at most 2 navigation tags per reply.");
        sj.add("=== END NAVIGATION ===");

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

        if (ctx.multiDayInsights() != null) {
            var ins = ctx.multiDayInsights();
            sj.add("Last " + ins.periodDays() + " days:");
            sj.add("  Tasks: " + ins.tasksCompleted() + "/" + ins.tasksTotal() + " completed");
            sj.add("  Workouts: " + ins.workoutsCompleted() + "/" + ins.workoutsTotal() + " completed, " + ins.missedSessions() + " missed");
            if (ins.trendNote() != null) sj.add("  Trend: " + ins.trendNote());
        }

        sj.add("=== END CONTEXT ===\n");

        return sj.toString();
    }
}
