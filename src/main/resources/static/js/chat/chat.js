// Global fallback toggle function (in case DOMContentLoaded hasn't fired yet)
window.toggleChatPanel = function() {
    const panel = document.getElementById("chatPanel");
    const fab = document.getElementById("chatFab");
    if (!panel || !fab) return;
    
    const isOpen = panel.classList.contains("open");
    if (isOpen) {
        panel.classList.remove("open");
        panel.setAttribute("aria-hidden", "true");
        fab.setAttribute("aria-expanded", "false");
    } else {
        panel.classList.add("open");
        panel.setAttribute("aria-hidden", "false");
        fab.setAttribute("aria-expanded", "true");
    }
    console.log("Chat panel toggled via fallback:", !isOpen ? "open" : "closed");
};

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
    const dot = document.getElementById("chatNotificationDot");
    const normalTabBtn = document.getElementById("chatTabNormal");
    const notificationsToggle2 = document.getElementById("chatNotificationsToggle2");
    const notifFilterAll = document.getElementById("chatNotifFilterAll");
    const notifFilterUnread = document.getElementById("chatNotifFilterUnread");
    const notificationsUnreadCount = document.getElementById("chatNotificationsUnreadCount");
    const notificationsView = document.getElementById("chatNotificationsView");
    const chatView = document.getElementById("chatChatView");
    const notificationsList = document.getElementById("chatNotificationsList");
    const notificationsReadAll = document.getElementById("chatNotificationsReadAll");
    const unreadBadge = document.getElementById("chatUnreadBadge");
    const toastRegion = document.getElementById("chatToastRegion");
    const inlineToast = document.getElementById("chatInlineToast");
    const proChatBtn = document.getElementById("chatProChatBtn");
    const chatImageInput = document.getElementById("chatImageInput");
    const chatAttachImageBtn = document.getElementById("chatAttachImageBtn");

    const clearModal = document.getElementById("chatClearModal");
    const clearModalCancel = document.getElementById("chatClearCancel");
    const clearModalConfirm = document.getElementById("chatClearConfirm");

    // Detailed error logging for debugging
    if (!fab || !panel || !closeBtn || !clearBtn || !form || !input || !body || !sendBtn) {
        console.error("Chat widget init failed - missing elements:", {
            fab: !!fab, panel: !!panel, closeBtn: !!closeBtn, clearBtn: !!clearBtn,
            form: !!form, input: !!input, body: !!body, sendBtn: !!sendBtn
        });
        
        // Fallback: Create a minimal toggle function if panel exists
        if (panel && fab) {
            window.toggleChatPanel = function() {
                const isOpen = panel.classList.contains("open");
                if (isOpen) {
                    panel.classList.remove("open");
                    panel.setAttribute("aria-hidden", "true");
                    fab.setAttribute("aria-expanded", "false");
                } else {
                    panel.classList.add("open");
                    panel.setAttribute("aria-hidden", "false");
                    fab.setAttribute("aria-expanded", "true");
                }
                console.log("Chat panel toggled:", !isOpen ? "open" : "closed");
            };
            console.log("Chat: Using fallback toggle function");
        }
        return;
    }

    const authMarker = document.getElementById("chatAuth");
    const isAuthenticated = authMarker?.dataset?.authenticated === "true";
    const historyAllowed = document.body?.dataset?.chatHistory !== "off";
    const isPremium = root?.dataset?.premium === "true";
    const accountRole = root?.dataset?.role || "GUEST";
    const initialWelcomeText = body.querySelector(".chat-message-bubble")?.textContent?.trim() || "";

    const csrfToken = document.getElementById("chat_csrf")?.value || null;
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const STORAGE_KEY = "one2one_chat_history_v1";
    const TOAST_DURATION = 5000;
    let notificationsPollTimer = null;
    let eventSource = null;
    let sseRetryCount = 0;
    const MAX_SSE_RETRIES = 5;
    const notificationSyncChannel = "BroadcastChannel" in window ? new BroadcastChannel("one-to-one-notifications") : null;
    let pendingImageAttachment = null;

    function broadcastNotificationSync(detail) {
        const payload = detail || {};
        window.dispatchEvent(new CustomEvent("one-to-one:notifications-updated", { detail: payload }));
        try {
            notificationSyncChannel?.postMessage(payload);
        } catch (_) {
            // ignore
        }
    }

    function open() {
        panel.classList.add("open");
        panel.setAttribute("aria-hidden", "false");
        fab.setAttribute("aria-expanded", "true");
        hideNotificationsPanel();
        dot?.classList.remove("active");
        if (!input.disabled) {
            setTimeout(() => input.focus(), 50);
        }
    }

    function close() {
        const activeElement = document.activeElement;
        if (activeElement && panel.contains(activeElement)) {
            fab.focus();
        }
        hideNotificationsPanel();
        panel.classList.remove("open");
        panel.setAttribute("aria-hidden", "true");
        fab.setAttribute("aria-expanded", "false");
    }

    function scrollToBottom() {
        body.scrollTop = body.scrollHeight;
    }

    function addMsg(text, who) {
        const wrap = document.createElement("div");
        wrap.className = `chat-message-wrapper ${who === "me" ? "user" : "assistant"}`;

        const bubble = document.createElement("div");
        bubble.className = "chat-message-bubble";
        bubble.appendChild(renderRichMessageContent(text));

        wrap.appendChild(bubble);
        appendImagePreviews(wrap, text);
        body.appendChild(wrap);
        scrollToBottom();
    }

    function renderRichMessageContent(text) {
        const fragment = document.createDocumentFragment();
        const safeText = (text || "").trim();
        const urlRegex = /(https?:\/\/[^\s]+)/gi;
        const parts = safeText.split(urlRegex);
        parts.forEach((part) => {
            if (!part) return;
            if (urlRegex.test(part)) {
                const link = document.createElement("a");
                link.href = part;
                link.target = "_blank";
                link.rel = "noopener noreferrer";
                link.textContent = part;
                fragment.appendChild(link);
            } else {
                fragment.appendChild(document.createTextNode(part));
            }
            urlRegex.lastIndex = 0;
        });
        return fragment;
    }

    function appendImagePreviews(target, text) {
        const matches = (text || "").match(/https?:\/\/[^\s]+/gi) || [];
        matches
            .filter((url) => /\.(png|jpe?g|gif|webp|svg)(\?.*)?$/i.test(url))
            .slice(0, 2)
            .forEach((url) => {
                const image = document.createElement("img");
                image.src = url;
                image.alt = "Shared image";
                image.className = "chat-image-preview";
                target.appendChild(image);
            });
    }

    function addMsgWithNav(text, who, navActions, imageUrl) {
        const wrap = document.createElement("div");
        wrap.className = `chat-message-wrapper ${who === "me" ? "user" : "assistant"}`;

        const bubble = document.createElement("div");
        bubble.className = "chat-message-bubble";
        bubble.appendChild(renderRichMessageContent(text));
        wrap.appendChild(bubble);

        if (imageUrl) {
            const image = document.createElement("img");
            image.src = imageUrl;
            image.alt = "Attached image";
            image.className = "chat-image-preview";
            wrap.appendChild(image);
        }
        appendImagePreviews(wrap, text);

        if (Array.isArray(navActions) && navActions.length > 0) {
            const navRow = document.createElement("div");
            navRow.className = "chat-nav-actions";
            navActions.forEach(action => {
                const btn = document.createElement("a");
                btn.href = action.url;
                btn.className = "chat-nav-btn";
                btn.textContent = action.label;
                // Safety: only relative paths are rendered (whitelist enforced server-side)
                if (!action.url.startsWith("/")) return;
                navRow.appendChild(btn);
            });
            if (navRow.children.length > 0) {
                wrap.appendChild(navRow);
            }
        }

        body.appendChild(wrap);
        scrollToBottom();
    }

    function addTyping() {
        const wrap = document.createElement("div");
        wrap.className = "chat-typing-indicator";
        wrap.id = "typingRow";

        const bubble = document.createElement("div");
        bubble.className = "chat-typing-bubble";
        bubble.textContent = "Charlie is thinking";
        const dots = document.createElement("span");
        dots.className = "chat-typing-dots";
        dots.innerHTML = "<span></span><span></span><span></span>";
        bubble.appendChild(dots);

        wrap.appendChild(bubble);
        body.appendChild(wrap);
        scrollToBottom();
    }

    function removeTyping() {
        const t = document.getElementById("typingRow");
        if (t) t.remove();
    }

    function getAuthenticatedWelcome() {
        if (initialWelcomeText) return initialWelcomeText;
        const roleLabel = accountRole === "TRAINER"
            ? "trainer"
            : accountRole === "GYM_ADMIN"
                ? "gym"
                : "client";
        if (isPremium) {
            return `Welcome back — I’m Charlie, your premium ${roleLabel} assistant.`;
        }
        return `Welcome back — I’m Charlie. You have 15 prompts per day on Starter access.`;
    }

    function getUnauthWelcome() {
        return initialWelcomeText || "Hi, I’m Charlie — the One To One website assistant. I can explain platform features and guide you to the right pages.";
    }

    function saveHistory() {
        if (!historyAllowed) return;

        const msgs = [];
        body.querySelectorAll(".chat-message-wrapper, .chat-msg").forEach(m => {
            const isUser = m.classList.contains("user") || m.classList.contains("chat-msg-me");
            const text = m.querySelector(".chat-message-bubble, .bubble")?.textContent || "";
            if (text.trim().length > 0 && text !== "Typing…") msgs.push({ who: isUser ? "me" : "ai", text });
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
            addMsg(getAuthenticatedWelcome(), "ai");
            return;
        }
        msgs.forEach(m => addMsg(m.text, m.who));
    }

    function renderUnauth() {
        body.innerHTML = "";
        addMsg(getUnauthWelcome(), "ai");
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
            addMsg(getUnauthWelcome(), "ai");
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
                dot?.classList.add("active");
                return;
            }

            const reply = data?.reply || "No response";
            const navActions = Array.isArray(data?.navActions) ? data.navActions : [];
            addMsgWithNav(reply, "ai", navActions);
            saveHistory();

        } catch (err) {
            removeTyping();
            addMsg("AI request failed (network/server).", "ai");
            dot?.classList.add("active");
        } finally {
            sendBtn.disabled = false;
        }
    }

    function showInlineToast(message, tone = "default") {
        if (!inlineToast) return;
        inlineToast.textContent = message;
        inlineToast.classList.toggle("chat-inline-toast-warning", tone === "warning");
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
        // Also update the notifications panel header count
        if (notificationsUnreadCount) {
            if (count > 0) {
                notificationsUnreadCount.textContent = count > 99 ? "99+" : `${count}`;
                notificationsUnreadCount.classList.remove("hidden");
                notificationsUnreadCount.classList.add("inline-flex");
            } else {
                notificationsUnreadCount.textContent = "";
                notificationsUnreadCount.classList.add("hidden");
                notificationsUnreadCount.classList.remove("inline-flex");
            }
        }
    }

    function setNavUnreadBadges(count) {
        const badges = document.querySelectorAll(".nav-notification-badge");
        if (!badges.length) return;
        badges.forEach((badge) => {
            if (count > 0) {
                badge.textContent = count > 99 ? "99+" : `${count}`;
                badge.classList.remove("hidden");
                badge.classList.add("inline-flex");
            } else {
                badge.textContent = "";
                badge.classList.add("hidden");
                badge.classList.remove("inline-flex");
            }
        });
    }

    async function refreshUnreadCount() {
        if (!isAuthenticated) return;
        try {
            const res = await fetch("/api/notifications/unread-count");
            if (!res.ok) return;
            const data = await res.json();
            const count = Number(data?.count || 0);
            setUnreadBadge(count);
            setNavUnreadBadges(count);
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
            broadcastNotificationSync({ source: "chat-widget", notificationId: notification.id, action: "dismiss" });
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

        if (notification.ctaUrl) {
            const cta = document.createElement("a");
            cta.href = notification.ctaUrl;
            cta.className = "mt-2 inline-flex items-center rounded-lg border border-slate-200 bg-white px-2.5 py-1 text-[11px] font-semibold text-slate-700 shadow-sm transition hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-950 dark:text-slate-200";
            cta.textContent = "Open";
            cta.addEventListener("click", async (e) => {
                e.stopPropagation();
                if (isUnread) {
                    const headers = {};
                    if (csrfToken) headers[csrfHeader] = csrfToken;
                    await fetch(`/api/notifications/${notification.id}/read`, { method: "POST", headers });
                    await refreshUnreadCount();
                    broadcastNotificationSync({ source: "chat-widget", notificationId: notification.id, action: "read" });
                }
            });
            row.appendChild(cta);
        }
        
        // Mark as read on click if unread
        if (isUnread) {
            row.addEventListener("click", async () => {
                 const headers = {};
                 if (csrfToken) headers[csrfHeader] = csrfToken;
                 await fetch(`/api/notifications/${notification.id}/read`, { method: "POST", headers });
                 await loadNotifications();
                 await refreshUnreadCount();
                 broadcastNotificationSync({ source: "chat-widget", notificationId: notification.id, action: "read" });
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
            let data = await res.json();
            notificationsList.innerHTML = "";
            if (!Array.isArray(data)) return;
            // Apply filter
            if (currentFilter === "unread") {
                data = data.filter(n => !n.readAt && !n.dismissedAt);
            }
            if (data.length === 0) {
                const emptyMsg = currentFilter === "unread" ? "No unread notifications." : "No notifications yet.";
                notificationsList.innerHTML = `<div class="rounded-xl border border-dashed border-slate-200 bg-slate-50 px-3 py-6 text-center text-xs text-slate-500 dark:border-slate-800 dark:bg-slate-900/40 dark:text-slate-400"><div class="text-2xl mb-1" aria-hidden="true">🔔</div>${emptyMsg}</div>`;
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
        broadcastNotificationSync({ source: "chat-widget", notificationId: id, action: "dismiss" });
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
                broadcastNotificationSync({ source: "chat-widget", notificationId: data?.id, action: "create" });
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

    window.addEventListener("one-to-one:notifications-updated", async () => {
        await refreshUnreadCount();
        if (notificationsView && !notificationsView.classList.contains("hidden")) {
            await loadNotifications();
        }
    });
    notificationSyncChannel?.addEventListener("message", async () => {
        await refreshUnreadCount();
        if (notificationsView && !notificationsView.classList.contains("hidden")) {
            await loadNotifications();
        }
    });

    // Events
    window.toggleChatPanel = function() {
        try {
            const isOpen = panel.classList.contains("open");
            console.log("toggleChatPanel called, current state:", isOpen ? "open" : "closed");
            isOpen ? close() : open();
        } catch (err) {
            console.error("Error in toggleChatPanel:", err);
        }
    };

    fab.addEventListener("click", (e) => {
        try {
            e.preventDefault();
            e.stopPropagation();
            console.log("Chat FAB clicked");
            window.toggleChatPanel();
        } catch (err) {
            console.error("Error handling chat FAB click:", err);
        }
    });
    
    closeBtn.addEventListener("click", (e) => {
        try {
            e.preventDefault();
            close();
        } catch (err) {
            console.error("Error handling chat close:", err);
        }
    });
    
    console.log("Chat widget initialized successfully. FAB element:", fab);

    // Quick-action suggestion chips
    const suggestions = document.getElementById("chatSuggestions");
    if (suggestions) {
        suggestions.addEventListener("click", async (e) => {
            const chip = e.target.closest(".chat-suggestion-chip");
            if (!chip || !isAuthenticated) return;
            const msg = chip.dataset.msg;
            if (!msg) return;
            input.value = msg;
            form.dispatchEvent(new Event("submit", { cancelable: true, bubbles: true }));
        });
    }

    function showNotificationsPanel() {
        if (!notificationsView || !chatView) return;
        chatView.classList.add("hidden");
        notificationsView.classList.remove("hidden");
        chatView.style.display = "none";
        notificationsView.style.display = "flex";
        notificationsToggle2?.classList.add("active");
        normalTabBtn?.classList.remove("active");
        loadNotifications();
    }

    function hideNotificationsPanel() {
        if (!notificationsView || !chatView) return;
        notificationsView.classList.add("hidden");
        chatView.classList.remove("hidden");
        notificationsView.style.display = "none";
        chatView.style.display = "flex";
        notificationsToggle2?.classList.remove("active");
        normalTabBtn?.classList.add("active");
    }

    function showCharlieOutput(message) {
        if (!message || typeof message !== "string") return;
        open();
        hideNotificationsPanel();
        addMsg(message.trim(), "ai");
        saveHistory();
    }

    // Public helper so any page can hand off Charlie text to chat panel.
    window.openCharlieChatWithMessage = function(message) {
        showCharlieOutput(message);
    };

    if (notificationsToggle2 && notificationsView && chatView) {
        notificationsToggle2.addEventListener("click", async () => {
            const showingNotifications = !notificationsView.classList.contains("hidden");
            if (showingNotifications) {
                hideNotificationsPanel();
            } else {
                showNotificationsPanel();
            }
        });
    }

    if (normalTabBtn) {
        normalTabBtn.addEventListener("click", () => {
            hideNotificationsPanel();
            if (!panel.classList.contains("open")) open();
        });
    }

    // Notification filter tabs
    let currentFilter = "all";
    if (notifFilterAll) {
        notifFilterAll.addEventListener("click", async () => {
            currentFilter = "all";
            notifFilterAll.classList.add("active");
            notifFilterUnread?.classList.remove("active");
            await loadNotifications();
        });
    }
    if (notifFilterUnread) {
        notifFilterUnread.addEventListener("click", async () => {
            currentFilter = "unread";
            notifFilterUnread.classList.add("active");
            notifFilterAll?.classList.remove("active");
            await loadNotifications();
        });
    }

    // Mobile notifications toggle button (legacy, keep for compat)
    const notificationsToggleMobile = document.getElementById("chatNotificationsToggleMobile");
    if (notificationsToggleMobile && notificationsView && chatView) {
        notificationsToggleMobile.addEventListener("click", async () => {
            const showingNotifications = !notificationsView.classList.contains("hidden");
            if (showingNotifications) {
                hideNotificationsPanel();
            } else {
                showNotificationsPanel();
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
            broadcastNotificationSync({ source: "chat-widget", action: "read-all" });
        });
    }

    const dashboardPrompt = document.getElementById("charlieDashboardPrompt");
    if (dashboardPrompt && dashboardPrompt.dataset.message) {
        const accountKey = (dashboardPrompt.dataset.account || "anonymous").trim().toLowerCase();
        const promptSeenKey = `charlieDashboardPromptSeen:${accountKey}`;
        let promptAlreadySeen = false;
        try {
            promptAlreadySeen = window.localStorage.getItem(promptSeenKey) === "1";
        } catch (_) {
            promptAlreadySeen = false;
        }

        if (!promptAlreadySeen) {
            setTimeout(() => {
                showCharlieOutput(dashboardPrompt.dataset.message);
                try {
                    window.localStorage.setItem(promptSeenKey, "1");
                } catch (_) {
                    // Ignore storage failures (private mode, quota, blocked storage).
                }
            }, 140);
        }
    }

    if (proChatBtn) {
        const locked = proChatBtn.getAttribute("data-locked") === "true" || !isPremium;
        if (locked) {
            proChatBtn.classList.add("locked-pill");
            proChatBtn.setAttribute("aria-disabled", "true");
            proChatBtn.title = "Upgrade to use Pro Chat";
        }
        proChatBtn.addEventListener("click", (e) => {
            if (locked) {
                e.preventDefault();
                showInlineToast("Upgrade to use Pro Chat", "warning");
                setTimeout(() => {
                    window.location.href = "/pricing";
                }, 320);
            } else {
                window.location.href = "/chat";
            }
        });
    }

    clearBtn.addEventListener("click", () => {
        if (clearModal) {
            clearModal.classList.add("show");
        }
    });

    clearModalCancel?.addEventListener("click", () => {
        clearModal?.classList.remove("show");
    });

    clearModal?.addEventListener("click", (e) => {
        if (e.target === clearModal) clearModal.classList.remove("show");
    });

    clearModalConfirm?.addEventListener("click", () => {
        clearModal?.classList.remove("show");

        if (isAuthenticated) {
            renderHistory([]);
        } else {
            renderUnauth();
        }
        localStorage.removeItem(STORAGE_KEY);
        showInlineToast("✓ Chat history cleared");

        if (!isAuthenticated) return;
        const headers = {};
        if (csrfToken) headers[csrfHeader] = csrfToken;
        fetch("/chat/clear", { method: "POST", headers }).catch(() => {
            // ignore
        });
    });

    chatAttachImageBtn?.addEventListener("click", () => {
        if (input.disabled) return;
        chatImageInput?.click();
    });

    chatImageInput?.addEventListener("change", () => {
        const file = chatImageInput.files?.[0];
        if (!file) return;
        const previewUrl = URL.createObjectURL(file);
        pendingImageAttachment = {
            fileName: file.name,
            previewUrl
        };
        showInlineToast(`Attached image: ${file.name}`);
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!isAuthenticated || input.disabled) {
            addMsg(getUnauthWelcome(), "ai");
            return;
        }

        const msg = (input.value || "").trim();
        if (!msg && !pendingImageAttachment) return;
        const finalMessage = pendingImageAttachment
            ? `${msg}${msg ? "\n\n" : ""}[Attached image: ${pendingImageAttachment.fileName}]`
            : msg;
        input.value = "";
        addMsgWithNav(finalMessage, "me", [], pendingImageAttachment?.previewUrl || null);
        if (pendingImageAttachment?.previewUrl) {
            URL.revokeObjectURL(pendingImageAttachment.previewUrl);
        }
        pendingImageAttachment = null;
        if (chatImageInput) chatImageInput.value = "";
        saveHistory();
        await sendMessage(finalMessage);
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
