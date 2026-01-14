# Checklist

- [x] “Next action” card (the brain of the page)
	A single, decisive block that tells the user what to do next (log workout / complete next task / prep next session / plan week) with one primary button and one secondary.

- [x] Upgrade the “Daily Snapshot” into a dashboard header
	Add a proper header row that can hold: greeting, today status, quick streak/consistency chip, and a trainer link status (linked / not linked).

- [x] Trainer connection status panel
	Show whether the user is linked to a verified trainer (or “none yet”), with CTAs: “Explore trainers” / “Message trainer” / “Switch trainer”.
	Assumption: there is no trainer marketplace/verification model yet; “linked” is inferred from an Inbox conversation that includes a participant with role TRAINER. “Explore/Switch” routes to `/explore` placeholder.

- [ ] Week strip: snap scrolling + better “day cards”
	Make it feel premium with snap-x snap-mandatory, and improve each day card layout so it’s more informative.

- [ ] Week strip: “day load” indicator (tiny bars/dots)
	A little visual indicator per day based on tasks+workouts count (light → heavy). It adds signal without adding content.

- [ ] Week strip: hover preview / micro-interaction
	On hover, reveal a small “Open day →” and/or “Next item: …” line so it feels interactive and alive.

- [ ] Upcoming list (next 3 items)
	A list of the next few scheduled items (workouts/tasks/sessions). Users care about specifics more than totals.

- [ ] “Plan health” / availability check card
	A small card that nudges “Set your availability” or “Adjust your week” (ties directly into calendar-led behaviour change).

- [ ] Progress signals (mini analytics without heavy analytics)
	Small, meaningful signals: logs this week, planned vs completed, best mood uplift, consistency score.

- [ ] Consistency / streak component (lightweight)
	Even a simple “Days logged this week” strip or streak indicator makes it feel like progress is building.

- [ ] “Insights” card becomes actionable
	Instead of just “busy day / next up”, add recommended actions: “Move Saturday workout” / “Add 1 recovery slot” / “You’re overloaded Thursday”.

- [ ] Quick actions become “Quick add”, not just links
	Add buttons like “+ Add workout”, “+ Add task”, “Log workout”, “Message trainer” so the user can do things instantly.

- [ ] Replace emojis with clean icons
	Swap 📅🏋️💬📝 for inline SVG icons (same meaning, far more premium).

- [ ] Premium tease inside authenticated view
	A subtle “Premium unlock” component: shows premium insights blurred/locked with a CTA (keeps it premium without a full pricing page).

- [ ] Personalised suggestions block (based on preferences)
	Since you mentioned preferences earlier: show “Suggested workout today” / “Recommended plan” based on selected goals and availability.

- [ ] “Recent logs” upgrade to show the latest highlight
	Instead of just a list: show the most recent log as a featured card (“Mood improved +2”, “Confidence 3/4”) then the list under it.

- [ ] “This week goal” card
	A simple target like “3 workouts planned” + “You’re on 1/3” (ties behaviour-change + motivation together).

- [ ] Futuristic micro motion (without being distracting)
	Subtle hover lift, glow ring, snap scrolling, smooth reveal animations — tiny interactions that make it feel like a premium product.