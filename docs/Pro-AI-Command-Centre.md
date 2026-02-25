# Pro AI Command Centre – Feature Guide

## Overview

The `/chat` route has been upgraded into an intelligent, context-aware AI coaching experience called the **Pro AI Command Centre**. It requires a Platform Premium subscription.

---

## Features

### 1. Personalised Zero-Blank State

On page load, the chat panel displays a personalised greeting card (server-rendered via Thymeleaf) that includes:

- **Greeting** – "Good morning/afternoon/evening, [First Name]" based on server-side time.
- **Task stats** – tasks done / total for today.
- **Workout stats** – workouts done / total for today.
- **Streak** – consecutive active days (shown if > 0).
- **Next workout** – name and date of the next scheduled occurrence.

No user input required.

### 2. Context-Aware AI Requests

Every message automatically includes structured context injected server-side by `ChatContextService`:

- Current date/time
- Today's tasks and completion stats
- Today's scheduled workouts and completion stats
- Next upcoming workout
- Recent workout history

The context is **never shown to the user** — it is built in `ChatContextBuilder` and passed to the AI system prompt.

#### Context JSON Endpoint

```
GET /chat/context
```

Returns a `ChatSummaryDto` JSON object with today's metrics (premium only).

### 3. Action Chips (Command Shortcuts)

Clickable chips above the input field send predefined coach intents:

| Chip | Sent message |
|------|-------------|
| 📅 Plan tomorrow | "Plan my day for tomorrow" |
| ⚡ Optimise this week | "Optimise my training schedule for this week" |
| 🏋 Adjust today's workout | "Suggest adjustments to today's workout" |
| 📊 Analyse missed tasks | "Analyse my missed tasks from this week…" |
| 🎯 Reduce workload today | "Help me reduce my workload for today…" |

Clicking a chip fills the input and sends immediately.

### 4. Time-of-Day Theme

The chat page background subtly shifts based on the server-side time-of-day:

| Theme | Hours | Effect |
|-------|-------|--------|
| `morning` | 05:00–11:59 | Cool blue tint |
| `midday` | 12:00–16:59 | Neutral / default |
| `evening` | 17:00–20:59 | Warm amber tint |
| `night` | 21:00–04:59 | Dark blue tint |

Implemented via CSS variables on `#coachChatPage[data-chat-time="..."]`.  
JS (`coach-chat.js`) refines with local client time on load.

### 5. Live Metrics Panel (Right Sidebar)

Replaces "coming soon" Premium tools with a live panel showing:

- **Today's completion %** (animated ring)
- **Tasks left** today
- **Workouts left** today
- **Activity streak** (consecutive active days)
- **Next workout** (name + date)

Refreshes automatically after each AI message. Manual refresh via the ↺ button.

Expandable **Insights** cards driven by real data:
- Weekly completion summary
- Activity streak analysis
- Next session preparation tip

### 6. Focus Mode

Click the **Focus** button in the chat header to hide both sidepanels and expand the chat to full width. Preference is persisted in `localStorage`.

### 7. UI Cleanup

- **Removed** the duplicate `username-logout` fragment that appeared outside the navbar on `/chat`.
- Single keyboard shortcuts hint row (no duplicates).

---

## Backend Architecture

```
ChatController          – thin controller: page model, /chat/context endpoint
ChatContextBuilder      – service: builds ChatSummaryDto (greeting, stats, streak, next workout)
ChatContextService      – service: builds AI prompt context (existing)
ChatSummaryDto          – DTO record for page model and JSON endpoint
```

---

## Running

```bash
# Build CSS after any component CSS changes
npm run build:css

# Run tests (chat-related)
./gradlew test --tests "uk.ac.cf._5.group14.BehaviourChangeGroupProject.ChatTests.*"
```
