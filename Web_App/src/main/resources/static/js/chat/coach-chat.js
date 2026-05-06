document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("coachChat");
    const page = document.getElementById("coachChatPage");
    if (!root) return;

    const listEl = document.getElementById("coachConversationList");
    const searchEl = document.getElementById("coachSearch");
    const newChatBtn = document.getElementById("coachNewChat");
    const messagesEl = document.getElementById("coachMessages");
    const inputEl = document.getElementById("coachInput");
    const sendBtn = document.getElementById("coachSend");
    const usageBadge = document.getElementById("coachUsageBadge");
    const limitModal = document.getElementById("limitModal");
    const limitModalClose = document.getElementById("limitModalClose");
    const focusModeBtn = document.getElementById("focusModeBtn");
    const metricsRefreshBtn = document.getElementById("metricsRefreshBtn");

    const csrfToken = document.getElementById("chat_csrf")?.value || "";
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const isPremium = root.dataset.premium === "true";
    let conversations = [];
    let activeId = null;
    let sending = false;

    // ── Time-of-day theme initialiser ─────────────────────────────────────
    function initTimeTheme() {
        if (!page) return;
        const hour = new Date().getHours();
        let theme;
        if (hour >= 5 && hour < 12) theme = "morning";
        else if (hour >= 12 && hour < 17) theme = "midday";
        else if (hour >= 17 && hour < 21) theme = "evening";
        else theme = "night";
        // The server already sets data-chat-time via Thymeleaf; JS refines using local time
        page.setAttribute("data-chat-time", theme);
    }

    initTimeTheme();

    // ── Focus mode ────────────────────────────────────────────────────────
    function initFocusMode() {
        if (!page || !focusModeBtn) return;
        const saved = localStorage.getItem("chatFocusMode") === "true";
        if (saved) {
            page.classList.add("focus-mode");
            focusModeBtn.classList.add("active");
            focusModeBtn.setAttribute("title", "Exit focus mode");
        }
        focusModeBtn.addEventListener("click", () => {
            const active = page.classList.toggle("focus-mode");
            focusModeBtn.classList.toggle("active", active);
            focusModeBtn.setAttribute("title", active ? "Exit focus mode" : "Toggle focus mode");
            localStorage.setItem("chatFocusMode", active ? "true" : "false");
        });
    }

    initFocusMode();

    // ── Action chips ──────────────────────────────────────────────────────
    document.querySelectorAll(".chat-action-chip").forEach(chip => {
        chip.addEventListener("click", () => {
            const text = chip.dataset.chipText;
            if (text && inputEl) {
                inputEl.value = text;
                inputEl.focus();
                // Automatically send
                sendMessage();
            }
        });
    });

    // ── Metrics refresh ───────────────────────────────────────────────────
    async function refreshMetrics() {
        try {
            const res = await fetch("/chat/context");
            if (!res.ok) return;
            const data = await res.json();
            updateMetricsPanel(data);
        } catch {
            // Silently fail — metrics will just stay at last known value
        }
    }

    function updateMetricsPanel(data) {
        if (!data) return;
        const set = (id, val) => { const el = document.getElementById(id); if (el) el.textContent = val; };

        const pct = data.completionPct ?? 0;
        set("metricsCompletionPct", pct + "%");
        set("metricsRingPct", pct + "%");

        const ring = document.getElementById("metricsRingFill");
        if (ring) {
            const offset = 125.66 - pct * 1.2566;
            ring.setAttribute("stroke-dashoffset", offset.toFixed(2));
        }

        const tasksLeft = (data.tasksTotal ?? 0) - (data.tasksDone ?? 0);
        const workoutsLeft = (data.workoutsTotal ?? 0) - (data.workoutsDone ?? 0);
        set("metricsTasksLeft", tasksLeft);
        set("metricsWorkoutsLeft", workoutsLeft);
        set("metricsStreak", data.streakDays > 0 ? data.streakDays + " days 🔥" : "—");

        const nextEl = document.getElementById("metricsNextWorkout");
        if (nextEl) {
            nextEl.textContent = data.nextWorkoutName
                ? (data.nextWorkoutName + (data.nextWorkoutDate ? " · " + data.nextWorkoutDate : ""))
                : "—";
        }

        // Update 7-day insights panel
        const panel7 = document.getElementById("insights7DayPanel");
        if (panel7 && data.sevenDayTasksTotal != null) {
            const missed7 = data.sevenDayMissedSessions ?? 0;
            let html7 = `Tasks: <strong>${data.sevenDayTasksCompleted ?? 0}/${data.sevenDayTasksTotal ?? 0}</strong> · Workouts: <strong>${data.sevenDayWorkoutsCompleted ?? 0}/${data.sevenDayWorkoutsTotal ?? 0}</strong>`;
            if (missed7 > 0) html7 += ` · <span style="color:#d97706">${missed7} missed</span>`;
            if (data.trendNote) html7 += `<div style="margin-top:4px;font-weight:600">${data.trendNote}</div>`;
            panel7.innerHTML = html7;
        }

        // Update 30-day insights panel
        const panel30 = document.getElementById("insights30DayPanel");
        if (panel30 && data.thirtyDayTasksTotal != null) {
            const missed30 = data.thirtyDayMissedSessions ?? 0;
            let html30 = `Tasks: <strong>${data.thirtyDayTasksCompleted ?? 0}/${data.thirtyDayTasksTotal ?? 0}</strong> · Workouts: <strong>${data.thirtyDayWorkoutsCompleted ?? 0}/${data.thirtyDayWorkoutsTotal ?? 0}</strong>`;
            if (missed30 > 0) html30 += ` · <span style="color:#dc2626">${missed30} missed</span>`;
            panel30.innerHTML = html30;
        }
    }

    metricsRefreshBtn?.addEventListener("click", refreshMetrics);

    // ── Helpers ───────────────────────────────────────────────────────────
    function headers() {
        const out = { "Content-Type": "application/json" };
        if (csrfToken) out[csrfHeader] = csrfToken;
        return out;
    }

    function showModal() {
        if (!limitModal) return;
        limitModal.classList.remove("hidden");
        limitModal.classList.add("flex");
    }

    function hideModal() {
        if (!limitModal) return;
        limitModal.classList.add("hidden");
        limitModal.classList.remove("flex");
    }

    function updateUsage(usage) {
        if (isPremium || !usageBadge || !usage) return;
        const remaining = usage.remaining != null ? usage.remaining : null;
        if (remaining != null) {
            usageBadge.textContent = `${remaining} left today`;
        }
    }

    function clearMessages() {
        messagesEl.innerHTML = "";
    }

    function addMessage(role, text) {
        const wrap = document.createElement("div");
        wrap.className = role === "user"
            ? "flex justify-end"
            : "flex justify-start";

        const bubble = document.createElement("div");
        if (role === "user") {
            bubble.className = "max-w-[80%] whitespace-pre-wrap rounded-2xl bg-slate-900 px-4 py-3 text-sm text-white shadow-lg dark:bg-slate-100 dark:text-slate-900";
        } else {
            bubble.className = "max-w-[80%] whitespace-pre-wrap rounded-2xl border border-slate-200/70 bg-gradient-to-br from-white to-slate-50 px-4 py-3 text-sm text-slate-800 shadow-sm dark:border-slate-800/60 dark:from-slate-950 dark:to-slate-900 dark:text-slate-100";
        }
        bubble.textContent = text;
        wrap.appendChild(bubble);
        messagesEl.appendChild(wrap);
        messagesEl.scrollTop = messagesEl.scrollHeight;
        return wrap;
    }

    function addTyping() {
        const wrap = document.createElement("div");
        wrap.id = "coachTyping";
        wrap.className = "flex justify-start";
        const bubble = document.createElement("div");
        bubble.className = "max-w-[60%] rounded-2xl border border-slate-200/60 bg-slate-50 px-4 py-3 text-xs italic text-slate-500 dark:border-slate-800/60 dark:bg-slate-900/60 dark:text-slate-400";
        bubble.textContent = "✨ The Coach is thinking…";
        wrap.appendChild(bubble);
        messagesEl.appendChild(wrap);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function removeTyping() {
        const t = document.getElementById("coachTyping");
        if (t) t.remove();
    }

    function renderConversations(list) {
        listEl.innerHTML = "";
        if (!list.length) {
            const empty = document.createElement("div");
            empty.className = "rounded-xl border border-dashed border-slate-200 bg-slate-50 px-3 py-4 text-center text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-900/40 dark:text-slate-400";
            empty.textContent = "No conversations yet.";
            listEl.appendChild(empty);
            return;
        }
        list.forEach(conv => {
            const btn = document.createElement("button");
            btn.type = "button";
            btn.dataset.id = conv.id;
            btn.className = `w-full rounded-xl border px-3 py-3 text-left text-sm shadow-sm transition ${
                conv.id === activeId
                    ? "border-slate-900 bg-slate-900 text-white"
                    : "border-slate-200 bg-white text-slate-800 hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-950 dark:text-slate-100 dark:hover:bg-slate-900"
            }`;
            btn.innerHTML = `<div class="font-semibold">${conv.title || "New chat"}</div>`;
            btn.addEventListener("click", () => openConversation(conv.id));
            listEl.appendChild(btn);
        });
    }

    async function loadConversations() {
        const res = await fetch("/chat/conversations");
        if (!res.ok) return;
        conversations = await res.json();
        const filtered = filterConversations(conversations);
        renderConversations(filtered);
        if (!activeId && conversations.length > 0) {
            await openConversation(conversations[0].id);
        }
    }

    function filterConversations(list) {
        const query = (searchEl?.value || "").trim().toLowerCase();
        if (!query) return list;
        return list.filter(c => (c.title || "").toLowerCase().includes(query));
    }

    async function openConversation(id) {
        activeId = id;
        renderConversations(filterConversations(conversations));
        clearMessages();
        addMessage("assistant", "Loading your thread…");
        const res = await fetch(`/chat/conversations/${id}/messages?limit=200`);
        if (!res.ok) return;
        const data = await res.json();
        clearMessages();
        if (!data.length) {
            addMessage("assistant", "Ask about workouts, planning, or tomorrow's momentum.");
            return;
        }
        data.forEach(m => addMessage(m.role === "user" ? "user" : "assistant", m.content));
    }

    async function createConversation() {
        const res = await fetch("/chat/conversations", { method: "POST", headers: headers() });
        if (!res.ok) return null;
        const data = await res.json();
        await loadConversations();
        if (data?.id) {
            await openConversation(data.id);
        }
        return data?.id || null;
    }

    async function sendMessage() {
        if (sending) return;
        const text = (inputEl.value || "").trim();
        if (!text) return;
        sending = true;
        inputEl.value = "";

        if (!activeId) {
            await createConversation();
        }
        if (!activeId) {
            sending = false;
            return;
        }

        // Hide greeting card once user starts chatting
        const greetingCard = document.getElementById("chatGreetingCard");
        if (greetingCard) greetingCard.style.display = "none";

        const userRow = addMessage("user", text);
        addTyping();

        try {
            const res = await fetch(`/chat/conversations/${activeId}/messages`, {
                method: "POST",
                headers: headers(),
                body: JSON.stringify({ message: text })
            });

            removeTyping();

            if (res.status === 429) {
                userRow.remove();
                showModal();
                return;
            }

            const data = await res.json();
            if (!res.ok) {
                addMessage("assistant", data?.error || "Something went wrong. Try again.");
                return;
            }

            addMessage("assistant", data.reply || "No response");
            updateUsage(data.usage);
            await loadConversations();

            // Refresh metrics after each message (lightweight, throttled by natural send cadence)
            refreshMetrics();
        } catch {
            removeTyping();
            addMessage("assistant", "Network error. Please try again.");
        } finally {
            sending = false;
        }
    }

    newChatBtn?.addEventListener("click", async () => {
        activeId = null;
        await createConversation();
    });

    sendBtn?.addEventListener("click", sendMessage);

    inputEl?.addEventListener("keydown", (event) => {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    });

    searchEl?.addEventListener("input", () => {
        renderConversations(filterConversations(conversations));
    });

    limitModalClose?.addEventListener("click", hideModal);
    limitModal?.addEventListener("click", (event) => {
        if (event.target === limitModal) hideModal();
    });

    loadConversations();
});
