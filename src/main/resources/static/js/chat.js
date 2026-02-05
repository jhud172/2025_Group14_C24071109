document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("chatWidget") || document;
    if (root !== document && root.dataset.chatInitialized === "true") return;
    if (root !== document) root.dataset.chatInitialized = "true";

    const fab = document.getElementById("chatFab");
    const panel = document.getElementById("chatPanel");
    const closeBtn = document.getElementById("chatClose");
    const clearBtn = document.getElementById("chatClear");
    const form = document.getElementById("chatForm");
    const input = document.getElementById("chatInput");
    const body = document.getElementById("chatBody");
    const sendBtn = document.getElementById("chatSend");
    const dot = document.querySelector(".chat-fab-dot");
    const notificationsToggle = document.getElementById("chatNotificationsToggle");
    const notificationsView = document.getElementById("chatNotificationsView");
    const chatView = document.getElementById("chatChatView");
    const notificationsList = document.getElementById("chatNotificationsList");
    const notificationsReadAll = document.getElementById("chatNotificationsReadAll");
    const unreadBadge = document.getElementById("chatUnreadBadge");
    const toastRegion = document.getElementById("chatToastRegion");
    const inlineToast = document.getElementById("chatInlineToast");
    const proChatBtn = document.getElementById("chatProChatBtn");

    if (!fab || !panel || !closeBtn || !clearBtn || !form || !input || !body || !sendBtn) {
        console.warn("Chat widget elements missing, skipping init");
        return;
    }

    const authMarker = document.getElementById("chatAuth");
    const isAuthenticated = authMarker?.dataset?.authenticated === "true";
    const historyAllowed = document.body?.dataset?.chatHistory !== "off";
    const isPremium = root?.dataset?.premium === "true";

    const csrfToken = document.getElementById("chat_csrf")?.value || null;
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const STORAGE_KEY = "one2one_chat_history_v1";
    const TOAST_DURATION = 5000;
    let notificationsPollTimer = null;
    let eventSource = null;
    let sseRetryCount = 0;
    const MAX_SSE_RETRIES = 5;

    function open() {
        panel.classList.add("open");
        panel.setAttribute("aria-hidden", "false");
        fab.setAttribute("aria-expanded", "true");
        dot?.classList.remove("on");
        if (!input.disabled) {
            setTimeout(() => input.focus(), 50);
        }
    }

    function close() {
        panel.classList.remove("open");
        panel.setAttribute("aria-hidden", "true");
        fab.setAttribute("aria-expanded", "false");
    }

    function scrollToBottom() {
        body.scrollTop = body.scrollHeight;
    }

    function addMsg(text, who) {
        const wrap = document.createElement("div");
        wrap.className = `chat-msg ${who === "me" ? "chat-msg-me justify-end" : "chat-msg-ai justify-start"} flex`;

        const bubble = document.createElement("div");
        bubble.className = `bubble max-w-[82%] whitespace-pre-wrap rounded-2xl border px-3 py-2 text-sm shadow-sm ${
            who === "me"
                ? "border-slate-900 bg-slate-900 text-white dark:border-slate-100 dark:bg-slate-100 dark:text-slate-900"
                : "border-slate-200 bg-slate-50 text-slate-900 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-100"
        }`;
        bubble.textContent = text;

        wrap.appendChild(bubble);
        body.appendChild(wrap);
        scrollToBottom();
    }

    function addTyping() {
        const wrap = document.createElement("div");
        wrap.className = "chat-msg chat-msg-ai flex justify-start";
        wrap.id = "typingRow";

        const bubble = document.createElement("div");
        bubble.className = "bubble max-w-[82%] whitespace-pre-wrap rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm italic text-slate-600 shadow-sm dark:border-slate-800 dark:bg-slate-900 dark:text-slate-400";
        bubble.textContent = "Typing…";

        wrap.appendChild(bubble);
        body.appendChild(wrap);
        scrollToBottom();
    }

    function removeTyping() {
        const t = document.getElementById("typingRow");
        if (t) t.remove();
    }

    function saveHistory() {
        const msgs = [];
        body.querySelectorAll(".chat-msg").forEach(m => {
            const who = m.classList.contains("chat-msg-me") ? "me" : "ai";
            const text = m.querySelector(".bubble")?.textContent || "";
            if (text.trim().length > 0 && text !== "Typing…") msgs.push({ who, text });
        });
        localStorage.setItem(STORAGE_KEY, JSON.stringify(msgs.slice(-200)));
    }

    function readHistory() {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            if (!raw) return;
            const msgs = JSON.parse(raw);
            if (!Array.isArray(msgs) || msgs.length === 0) return;

            return msgs;
        } catch (e) {
            // ignore
        }
    }

    function renderHistory(msgs) {
        body.innerHTML = "";
        if (!Array.isArray(msgs) || msgs.length === 0) {
            addMsg("Ask me anything about your workouts, progress, notes, or what to do today.", "ai");
            return;
        }
        msgs.forEach(m => addMsg(m.text, m.who));
    }

    function renderUnauth() {
        body.innerHTML = "";
        addMsg("Please log in to use chat.", "ai");
    }

    async function fetchServerHistory() {
        if (!isAuthenticated) return null;
        try {
            const res = await fetch("/chat/history", { method: "GET" });
            if (!res.ok) return null;
            const data = await res.json();
            if (!Array.isArray(data)) return null;
            return data
                .filter(m => m && typeof m.text === "string" && (m.who === "me" || m.who === "ai"))
                .map(m => ({ who: m.who, text: m.text }));
        } catch {
            return null;
        }
    }

    async function sendMessage(message) {
        if (!isAuthenticated) {
            addMsg("Please log in to use chat.", "ai");
            return;
        }
        sendBtn.disabled = true;
        addTyping();

        try {
            const headers = { "Content-Type": "application/json" };
            if (csrfToken) headers[csrfHeader] = csrfToken;

            const res = await fetch("/chat/api", {
                method: "POST",
                headers,
                body: JSON.stringify({ message })
            });

            removeTyping();

            let data = null;
            try {
                data = await res.json();
            } catch {
                // ignore
            }

            if (!res.ok) {
                addMsg(data?.reply || "Something went wrong talking to the AI. Try again.", "ai");
                dot?.classList.add("on");
                return;
            }

            const reply = data?.reply || "No response";
            addMsg(reply, "ai");
            saveHistory();

        } catch (err) {
            removeTyping();
            addMsg("AI request failed (network/server).", "ai");
            dot?.classList.add("on");
        } finally {
            sendBtn.disabled = false;
        }
    }

    function showInlineToast(message) {
        if (!inlineToast) return;
        inlineToast.textContent = message;
        inlineToast.classList.remove("hidden");
        setTimeout(() => inlineToast.classList.add("hidden"), 2500);
    }

    function setUnreadBadge(count) {
        if (!unreadBadge) return;
        if (count > 0) {
            unreadBadge.textContent = count > 99 ? "99+" : `${count}`;
            unreadBadge.classList.remove("hidden");
            unreadBadge.classList.add("inline-flex");
        } else {
            unreadBadge.textContent = "";
            unreadBadge.classList.add("hidden");
            unreadBadge.classList.remove("inline-flex");
        }
    }

    async function refreshUnreadCount() {
        if (!isAuthenticated) return;
        try {
            const res = await fetch("/api/notifications/unread-count");
            if (!res.ok) return;
            const data = await res.json();
            setUnreadBadge(Number(data?.count || 0));
        } catch {
            // ignore
        }
    }

    function buildNotificationRow(notification) {
        const row = document.createElement("div");
        const isUnread = !notification.readAt && !notification.dismissedAt;
        
        // Premium styling: Unread has subtle accent color
        row.className = `group rounded-xl border p-3 shadow-sm transition-all ${
            isUnread 
                ? "border-emerald-500/30 bg-emerald-500/5 dark:bg-emerald-500/10" 
                : "border-slate-200 bg-slate-50/60 hover:bg-white dark:border-slate-800 dark:bg-slate-900/60 dark:hover:bg-slate-900"
        }`;

        const header = document.createElement("div");
        header.className = "flex items-start justify-between gap-2";

        const title = document.createElement("div");
        title.className = "flex items-center gap-2 text-xs font-semibold text-slate-600 dark:text-slate-300";
        
        if (isUnread) {
             const dot = document.createElement("span");
             dot.className = "h-2 w-2 rounded-full bg-emerald-500 ring-2 ring-emerald-500/20";
             title.appendChild(dot);
        }
        
        const titleText = document.createElement("span");
        titleText.textContent = notification.title || "Notification";
        title.appendChild(titleText);

        const dismiss = document.createElement("button");
        dismiss.type = "button";
        dismiss.className = "opacity-0 group-hover:opacity-100 transition-opacity text-[11px] font-semibold text-slate-400 hover:text-slate-700 dark:hover:text-slate-200";
        dismiss.textContent = "Dismiss";
        dismiss.addEventListener("click", async (e) => {
            e.stopPropagation();
            await dismissNotification(notification.id);
            await loadNotifications();
            await refreshUnreadCount();
        });

        header.appendChild(title);
        header.appendChild(dismiss);

        const message = document.createElement("div");
        message.className = "mt-1.5 text-sm text-slate-700 dark:text-slate-200 leading-snug";
        message.textContent = notification.message;

        const time = document.createElement("div");
        time.className = "mt-2 text-[10px] text-slate-400";
        // Simple relative time approximation or date parse could go here
        
        row.appendChild(header);
        row.appendChild(message);
        
        // Mark as read on click if unread
        if (isUnread) {
            row.addEventListener("click", async () => {
                 const headers = {};
                 if (csrfToken) headers[csrfHeader] = csrfToken;
                 await fetch(`/api/notifications/${notification.id}/read`, { method: "POST", headers });
                 await loadNotifications();
                 await refreshUnreadCount();
            });
            row.style.cursor = "pointer";
        }
        
        return row;
    }

    async function loadNotifications() {
        if (!isAuthenticated || !notificationsList) return;
        try {
            const res = await fetch("/api/notifications?limit=20");
            if (!res.ok) return;
            const data = await res.json();
            notificationsList.innerHTML = "";
            if (!Array.isArray(data) || data.length === 0) {
                notificationsList.innerHTML = `<div class="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-3 py-4 text-center text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-900/40 dark:text-slate-400">No notifications yet.</div>`;
                return;
            }

            data.forEach(n => notificationsList.appendChild(buildNotificationRow(n)));
        } catch {
            // ignore
        }
    }

    async function dismissNotification(id) {
        if (!id) return;
        const headers = {};
        if (csrfToken) headers[csrfHeader] = csrfToken;
        await fetch(`/api/notifications/${id}/dismiss`, { method: "POST", headers });
    }

    function showToast(notification) {
        if (!toastRegion || !notification) return;

        const toast = document.createElement("div");
        toast.className = "pointer-events-auto overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-lg dark:border-slate-800 dark:bg-slate-950";

        const content = document.createElement("div");
        content.className = "flex items-start justify-between gap-3 px-4 py-3";

        const textWrap = document.createElement("div");
        textWrap.className = "flex-1";

        if (notification.title) {
            const title = document.createElement("div");
            title.className = "text-xs font-semibold text-slate-500 dark:text-slate-400";
            title.textContent = notification.title;
            textWrap.appendChild(title);
        }

        const message = document.createElement("div");
        message.className = "mt-1 text-sm text-slate-900 dark:text-slate-100";
        message.textContent = notification.message || "Notification";
        textWrap.appendChild(message);

        const close = document.createElement("button");
        close.type = "button";
        close.className = "inline-flex h-7 w-7 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-50 dark:border-slate-800 dark:text-slate-300 dark:hover:bg-slate-900";
        close.textContent = "✕";

        const progress = document.createElement("div");
        progress.className = "h-1 w-full bg-slate-200 dark:bg-slate-800";
        const bar = document.createElement("div");
        bar.className = "h-full bg-emerald-500";
        bar.style.width = "100%";
        bar.style.transition = `width ${TOAST_DURATION}ms linear`;
        progress.appendChild(bar);

        content.appendChild(textWrap);
        content.appendChild(close);
        toast.appendChild(content);
        toast.appendChild(progress);
        toastRegion.prepend(toast);

        const finish = async () => {
            toast.remove();
            await refreshUnreadCount();
            if (notificationsView && !notificationsView.classList.contains("hidden")) {
                await loadNotifications();
            }
        };

        const timeoutId = setTimeout(finish, TOAST_DURATION);
        setTimeout(() => {
            bar.style.width = "0%";
        }, 20);

        close.addEventListener("click", async () => {
            clearTimeout(timeoutId);
            await dismissNotification(notification.id);
            await finish();
        });
    }

    function connectSse() {
        if (!isAuthenticated) return;
        if (eventSource) {
            // If already OPEN (1) or CONNECTING (0), do nothing
            if (eventSource.readyState === 0 || eventSource.readyState === 1) return;
            eventSource.close();
        }

        eventSource = new EventSource("/api/notifications/stream");
        
        eventSource.addEventListener("open", () => {
             sseRetryCount = 0; // Reset retry count on successful connection
        });

        eventSource.addEventListener("notification", (event) => {
            try {
                const data = JSON.parse(event.data);
                showToast(data);
                if (notificationsView && !notificationsView.classList.contains("hidden")) {
                    loadNotifications();
                }
                refreshUnreadCount();
            } catch {
                // ignore
            }
        });

        eventSource.onerror = () => {
            eventSource.close();
            eventSource = null;
            
            if (sseRetryCount < MAX_SSE_RETRIES) {
                const delay = Math.pow(2, sseRetryCount) * 2000; // 2s, 4s, 8s, 16s, 32s
                console.log(`SSE connection lost. Retrying in ${delay}ms (Attempt ${sseRetryCount + 1}/${MAX_SSE_RETRIES})`);
                sseRetryCount++;
                setTimeout(connectSse, delay);
            } else {
                console.warn("SSE connection failed tailored after max retries. Switching to polling fallback.");
                startPolling();
            }
        };
    }

    function startPolling() {
        if (notificationsPollTimer || !isAuthenticated) return;
        // Poll unread count frequently (30s)
        notificationsPollTimer = setInterval(refreshUnreadCount, 30000);
        
        // Also verify immediate sync
        refreshUnreadCount();
    }

    // Events
    fab.addEventListener("click", () => {
        panel.classList.contains("open") ? close() : open();
    });
    closeBtn.addEventListener("click", close);

    if (notificationsToggle && notificationsView && chatView) {
        notificationsToggle.addEventListener("click", async () => {
            const showingNotifications = !notificationsView.classList.contains("hidden");
            if (showingNotifications) {
                notificationsView.classList.add("hidden");
                chatView.classList.remove("hidden");
                notificationsToggle.textContent = "Notifications";
            } else {
                chatView.classList.add("hidden");
                notificationsView.classList.remove("hidden");
                notificationsToggle.textContent = "Back";
                await loadNotifications();
            }
        });
    }

    if (notificationsReadAll) {
        notificationsReadAll.addEventListener("click", async () => {
            if (!isAuthenticated) return;
            const headers = {};
            if (csrfToken) headers[csrfHeader] = csrfToken;
            await fetch("/api/notifications/read-all", { method: "POST", headers });
            await loadNotifications();
            await refreshUnreadCount();
        });
    }

    if (proChatBtn) {
        const locked = proChatBtn.getAttribute("data-locked") === "true" || !isPremium;
        if (locked) {
            proChatBtn.classList.add("opacity-60");
            proChatBtn.classList.add("cursor-not-allowed");
            proChatBtn.setAttribute("aria-disabled", "true");
        }
        proChatBtn.addEventListener("click", (e) => {
            if (locked) {
                e.preventDefault();
                showInlineToast("Upgrade to use Pro Chat");
            } else {
                window.location.href = "/chat";
            }
        });
    }

    clearBtn.addEventListener("click", () => {
        if (!confirm("Clear this chat?")) return;

        if (isAuthenticated) {
            renderHistory([]);
        } else {
            renderUnauth();
        }
        localStorage.removeItem(STORAGE_KEY);

        if (!isAuthenticated) return;
        const headers = {};
        if (csrfToken) headers[csrfHeader] = csrfToken;
        fetch("/chat/clear", { method: "POST", headers }).catch(() => {
            // ignore
        });
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!isAuthenticated || input.disabled) {
            addMsg("Please log in to use chat.", "ai");
            return;
        }

        const msg = (input.value || "").trim();
        if (!msg) return;
        input.value = "";
        addMsg(msg, "me");
        saveHistory();
        await sendMessage(msg);
    });

    // Load previous messages
    if (isAuthenticated) {
        const local = readHistory();
        renderHistory(local);

        if (historyAllowed) {
            fetchServerHistory().then(serverMsgs => {
                if (Array.isArray(serverMsgs) && serverMsgs.length > 0) {
                    localStorage.setItem(STORAGE_KEY, JSON.stringify(serverMsgs.slice(-200)));
                    renderHistory(serverMsgs);
                }
            });
        }
    } else {
        renderUnauth();
    }
    scrollToBottom();

    if (isAuthenticated) {
        refreshUnreadCount();
        connectSse();
    }
});
