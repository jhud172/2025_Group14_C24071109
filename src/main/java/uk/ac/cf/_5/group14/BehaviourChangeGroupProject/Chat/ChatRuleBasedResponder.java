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

        if (lower.contains("this week") || lower.contains("how am i doing") || lower.contains("7 day") || lower.contains("seven day") || lower.contains("last 7")) {
            return buildWeekInsights(ctx);
        }

        if (lower.contains("last 30") || lower.contains("30 day") || lower.contains("thirty day") || lower.contains("this month")) {
            return buildMonthInsights(ctx);
        }

        if (lower.contains("supplement") || lower.contains("bcaa") || lower.contains("creatine") || lower.contains("protein powder") || lower.contains("pre-workout") || lower.contains("pre workout")) {
            return buildSupplementAdvice(lower);
        }

        boolean askingExercise = lower.contains("exercise") || lower.contains("workout") || lower.contains("training");
        if (askingExercise && (lower.contains("chest") || lower.contains("back") || lower.contains("shoulder") || lower.contains("leg") || lower.contains("arm") || lower.contains("core") || lower.contains("glute") || lower.contains("bicep") || lower.contains("tricep"))) {
            return buildExerciseSuggestions(lower);
        }

        if ((lower.contains("variation") || lower.contains("alternative") || lower.contains("different") || lower.contains("mix")) &&
                (lower.contains("exercise") || lower.contains("workout"))) {
            return buildVariationSuggestions(ctx);
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

    private static String buildWeekInsights(ChatContext ctx) {
        if (ctx.multiDayInsights() != null) {
            var ins = ctx.multiDayInsights();
            StringBuilder sb = new StringBuilder("📊 Last 7 days:\n");
            sb.append("- Tasks: ").append(ins.tasksCompleted()).append("/").append(ins.tasksTotal()).append(" completed\n");
            sb.append("- Workouts: ").append(ins.workoutsCompleted()).append("/").append(ins.workoutsTotal()).append(" completed");
            if (ins.missedSessions() > 0) sb.append(", ").append(ins.missedSessions()).append(" missed");
            sb.append("\n");
            if (ins.trendNote() != null) sb.append(ins.trendNote()).append("\n");
            return sb.toString().trim();
        }
        return buildThisWeekHint(ctx);
    }

    private static String buildMonthInsights(ChatContext ctx) {
        // Context only carries 7-day; give a note and use recent workouts
        StringBuilder sb = new StringBuilder("📅 30-day summary isn't fully loaded in this mode.\n");
        sb.append("Here's what I can see from your recent workouts:\n\n");
        sb.append(buildRecentWorkouts(ctx));
        return sb.toString().trim();
    }

    private static String buildSupplementAdvice(String lower) {
        if (lower.contains("creatine")) {
            return "💊 Creatine:\n" +
                   "- Loading phase: 20g/day for 5–7 days (split into 4 x 5g doses)\n" +
                   "- Maintenance: 3–5g/day\n" +
                   "- Timing: anytime; consistency matters more than timing\n" +
                   "- Evidence: well-researched for strength and power output\n" +
                   "- Take with carbs post-workout for slightly better uptake";
        }
        if (lower.contains("bcaa")) {
            return "💊 BCAAs (Leucine, Isoleucine, Valine):\n" +
                   "- Most useful if training fasted or have low protein intake\n" +
                   "- Dose: 5–10g around training\n" +
                   "- Leucine (key for muscle protein synthesis): aim for ≥2.5g per dose\n" +
                   "- If you hit your daily protein target (~1.6–2.2g/kg), BCAAs add little benefit";
        }
        if (lower.contains("protein")) {
            return "🥩 Protein:\n" +
                   "- Daily target: 1.6–2.2g per kg bodyweight for muscle gain\n" +
                   "- Post-workout window: 20–40g within 2 hours\n" +
                   "- Distribute across 3–4 meals for optimal muscle protein synthesis";
        }
        if (lower.contains("pre-workout") || lower.contains("pre workout") || lower.contains("caffeine")) {
            return "⚡ Pre-workout / Caffeine:\n" +
                   "- Effective dose: 3–6mg caffeine per kg bodyweight\n" +
                   "- Take 30–45 minutes before training\n" +
                   "- Avoid within 6 hours of sleep\n" +
                   "- Cycle off for 2 weeks every 8–12 weeks to maintain sensitivity";
        }
        return "💊 Common supplements:\n" +
               "- Creatine: proven for strength/power, 3–5g/day maintenance\n" +
               "- Protein powder: to hit daily protein targets\n" +
               "- BCAAs: useful if training fasted\n" +
               "- Caffeine: 3–6mg/kg pre-workout\n" +
               "- Omega-3: anti-inflammatory, 1–3g EPA+DHA/day\n" +
               "- Vitamin D: 1000–2000 IU/day, especially in low-sun climates\n" +
               "Ask me about a specific supplement for more detail!";
    }

    private static String buildExerciseSuggestions(String lower) {
        StringBuilder sb = new StringBuilder();
        if (lower.contains("chest")) {
            sb.append("🏋 Chest exercises:\n");
            sb.append("- Barbell bench press (intermediate) – compound push\n");
            sb.append("- Dumbbell flyes (beginner) – isolation stretch\n");
            sb.append("- Push-ups (beginner) – bodyweight compound\n");
            sb.append("- Incline dumbbell press (intermediate) – upper chest\n");
            sb.append("- Cable crossover (intermediate) – chest squeeze\n");
            sb.append("📺 Demo: search 'chest workout tutorial' on YouTube for form guides");
        } else if (lower.contains("back")) {
            sb.append("🏋 Back exercises:\n");
            sb.append("- Pull-ups / chin-ups (intermediate) – lat width\n");
            sb.append("- Barbell rows (intermediate) – thickness\n");
            sb.append("- Lat pulldown (beginner) – lat width\n");
            sb.append("- Seated cable row (beginner) – mid-back\n");
            sb.append("- Deadlift (advanced) – full posterior chain\n");
            sb.append("📺 Demo: search 'back workout tutorial' on YouTube for form guides");
        } else if (lower.contains("shoulder")) {
            sb.append("🏋 Shoulder exercises:\n");
            sb.append("- Overhead press (intermediate) – all deltoid heads\n");
            sb.append("- Lateral raises (beginner) – medial delts\n");
            sb.append("- Face pulls (beginner) – rear delts & rotator cuff\n");
            sb.append("- Arnold press (intermediate) – full range\n");
            sb.append("📺 Demo: search 'shoulder workout tutorial' on YouTube for form guides");
        } else if (lower.contains("leg")) {
            sb.append("🏋 Leg exercises:\n");
            sb.append("- Back squat (intermediate) – quads, glutes\n");
            sb.append("- Romanian deadlift (intermediate) – hamstrings, glutes\n");
            sb.append("- Leg press (beginner) – quads\n");
            sb.append("- Walking lunges (beginner) – unilateral legs\n");
            sb.append("- Leg curl (beginner) – hamstrings isolation\n");
            sb.append("📺 Demo: search 'leg workout tutorial' on YouTube for form guides");
        } else if (lower.contains("core")) {
            sb.append("🏋 Core exercises:\n");
            sb.append("- Plank (beginner) – anti-extension\n");
            sb.append("- Dead bug (beginner) – core stability\n");
            sb.append("- Hanging leg raise (intermediate) – lower abs\n");
            sb.append("- Cable woodchop (intermediate) – rotational core\n");
            sb.append("📺 Demo: search 'core workout tutorial' on YouTube for form guides");
        } else if (lower.contains("glute")) {
            sb.append("🏋 Glute exercises:\n");
            sb.append("- Hip thrust (intermediate) – glute max\n");
            sb.append("- Glute bridge (beginner) – beginner hip thrust\n");
            sb.append("- Bulgarian split squat (intermediate) – glutes + quads\n");
            sb.append("- Cable kickbacks (beginner) – glute isolation\n");
            sb.append("📺 Demo: search 'glute workout tutorial' on YouTube for form guides");
        } else if (lower.contains("bicep") || lower.contains("arm")) {
            sb.append("🏋 Bicep / arm exercises:\n");
            sb.append("- Barbell curl (beginner) – peak bicep\n");
            sb.append("- Incline dumbbell curl (intermediate) – long head stretch\n");
            sb.append("- Hammer curl (beginner) – brachialis\n");
            sb.append("- Preacher curl (beginner) – short head\n");
            sb.append("📺 Demo: search 'bicep curl tutorial' on YouTube for form guides");
        } else if (lower.contains("tricep")) {
            sb.append("🏋 Tricep exercises:\n");
            sb.append("- Close-grip bench press (intermediate) – mass builder\n");
            sb.append("- Skull crushers (intermediate) – long head\n");
            sb.append("- Cable pushdown (beginner) – lateral head\n");
            sb.append("- Overhead cable extension (beginner) – long head stretch\n");
            sb.append("📺 Demo: search 'tricep workout tutorial' on YouTube for form guides");
        } else {
            sb.append("💡 Specify a muscle group for targeted suggestions:\n");
            sb.append("chest, back, shoulders, legs, core, glutes, biceps, triceps");
        }
        return sb.toString().trim();
    }

    private static String buildVariationSuggestions(ChatContext ctx) {
        if (ctx.recentWorkouts() == null || ctx.recentWorkouts().isEmpty()) {
            return "I don't see any recent workout history. Once you've logged some sessions, I can suggest variations to keep things fresh!";
        }
        StringBuilder sb = new StringBuilder("🔄 Workout variations to keep things fresh:\n");
        sb.append("Based on your recent sessions, here are alternatives:\n\n");
        sb.append("- Swap barbell movements for dumbbell versions (e.g., DB bench instead of BB bench)\n");
        sb.append("- Replace machine exercises with cable alternatives for more range of motion\n");
        sb.append("- Try unilateral movements (single-leg, single-arm) to address imbalances\n");
        sb.append("- Use bodyweight alternatives on deload weeks\n");
        sb.append("- Rotate between horizontal and vertical pulling patterns for back variety\n\n");
        sb.append("Your last " + ctx.recentWorkouts().size() + " sessions: ");
        sb.append(ctx.recentWorkouts().stream()
                .map(w -> w.name() + " (" + w.date() + ")")
                .collect(java.util.stream.Collectors.joining(", ")));
        return sb.toString().trim();
    }
}
