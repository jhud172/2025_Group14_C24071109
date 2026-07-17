document.addEventListener("DOMContentLoaded", () => {
    const threadRoot = document.getElementById("inboxThreadRoot");
    const listRoot = document.getElementById("inboxThreadList");
    const emptyState = document.getElementById("inboxEmptyState");
    const notificationList = document.getElementById("notificationInboxList");
    const notificationEmpty = document.getElementById("notificationInboxEmpty");
    const notificationReadAll = document.getElementById("notificationInboxReadAll");

    const csrfToken = document.getElementById("inbox_csrf")?.value || null;
    const csrfHeader = document.getElementById("inbox_csrf_header")?.value || "X-CSRF-TOKEN";

    const headers = { "Content-Type": "application/json" };
    if (csrfToken) headers[csrfHeader] = csrfToken;
    const notificationSyncChannel = "BroadcastChannel" in window ? new BroadcastChannel("one-to-one-notifications") : null;

    function broadcastNotificationSync(detail) {
        const payload = detail || {};
        window.dispatchEvent(new CustomEvent("one-to-one:notifications-updated", { detail: payload }));
        try {
            notificationSyncChannel?.postMessage(payload);
        } catch (_) {
            // ignore
        }
    }

    function formatDate(value) {
        if (!value) return "";
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return "";
        return date.toLocaleString();
    }

    async function fetchThreads() {
        try {
            const res = await fetch("/api/inbox/threads", { method: "GET" });
            if (!res.ok) return;
            const data = await res.json();
            if (!Array.isArray(data)) return;
            renderThreads(data);
        } catch {
            // ignore
        }
    }

    async function fetchNotifications() {
        if (!notificationList || !notificationEmpty) return;
        try {
            const res = await fetch("/api/notifications?limit=50", { method: "GET" });
            if (!res.ok) return;
            const data = await res.json();
            if (!Array.isArray(data)) return;
            renderNotifications(data);
        } catch {
            // ignore
        }
    }

    function renderThreads(threads) {
        if (!listRoot || !emptyState) return;
        if (!threads.length) {
            listRoot.classList.add("hidden");
            emptyState.classList.remove("hidden");
            return;
        }

        listRoot.classList.remove("hidden");
        emptyState.classList.add("hidden");

        const list = document.createElement("ul");
        list.className = "divide-y divide-slate-200/70 dark:divide-slate-800";

        threads.forEach(thread => {
            const item = document.createElement("li");
            const link = document.createElement("a");
            link.href = `/inbox/${thread.threadId}`;
            link.className = "group flex items-start justify-between gap-4 px-5 py-4 hover:bg-slate-50 dark:hover:bg-slate-900/30";

            const left = document.createElement("div");
            left.className = "flex min-w-0 items-start gap-3";

            const avatar = document.createElement("div");
            avatar.className = "mt-0.5 inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl bg-slate-100 text-sm font-semibold text-slate-900 ring-1 ring-slate-200/70 dark:bg-slate-900/60 dark:text-slate-100 dark:ring-slate-800";
            avatar.textContent = (thread.title || "•").substring(0, 1).toUpperCase();

            const main = document.createElement("div");
            main.className = "min-w-0";

            const titleRow = document.createElement("div");
            titleRow.className = "flex items-center gap-2";

            const title = document.createElement("p");
            title.className = "truncate text-sm font-semibold text-slate-900 dark:text-slate-100";
            title.textContent = thread.title || "Conversation";

            titleRow.appendChild(title);

            if (thread.unreadCount && thread.unreadCount > 0) {
                const badge = document.createElement("span");
                badge.className = "inline-flex items-center rounded-full bg-slate-900 px-2 py-0.5 text-xs font-semibold text-white dark:bg-slate-100 dark:text-slate-900";
                badge.textContent = thread.unreadCount;
                titleRow.appendChild(badge);
            }

            const snippet = document.createElement("p");
            snippet.className = "mt-1 truncate text-sm text-slate-600 dark:text-slate-300";
            snippet.textContent = thread.lastMessageSnippet || "";

            main.appendChild(titleRow);
            main.appendChild(snippet);

            left.appendChild(avatar);
            left.appendChild(main);

            const right = document.createElement("div");
            right.className = "flex shrink-0 flex-col items-end gap-2";

            const date = document.createElement("span");
            date.className = "text-xs text-slate-500";
            date.textContent = formatDate(thread.lastMessageAt);

            const open = document.createElement("span");
            open.className = "text-xs font-semibold text-slate-400 group-hover:text-slate-600 dark:group-hover:text-slate-300";
            open.textContent = "Open";

            right.appendChild(date);
            right.appendChild(open);

            link.appendChild(left);
            link.appendChild(right);
            item.appendChild(link);
            list.appendChild(item);
        });

        listRoot.innerHTML = "";
        listRoot.appendChild(list);
    }

    function renderNotifications(notifications) {
        if (!notificationList || !notificationEmpty) return;
        notificationList.innerHTML = "";

        if (!notifications.length) {
            notificationEmpty.classList.remove("hidden");
            return;
        }

        notificationEmpty.classList.add("hidden");
        notifications.forEach((notification) => {
            notificationList.appendChild(buildNotificationRow(notification));
        });
    }

    function buildNotificationRow(notification) {
        const row = document.createElement("div");
        const isUnread = !notification.readAt && !notification.dismissedAt;
        row.className = `flex flex-col gap-2 px-6 py-4 transition ${
            isUnread
                ? "bg-emerald-500/5"
                : "bg-white dark:bg-slate-950/10"
        }`;

        const header = document.createElement("div");
        header.className = "flex items-start justify-between gap-3";

        const titleWrap = document.createElement("div");
        titleWrap.className = "flex items-center gap-2 text-sm font-semibold text-slate-900 dark:text-slate-100";

        if (isUnread) {
            const dot = document.createElement("span");
            dot.className = "h-2 w-2 rounded-full bg-emerald-500";
            titleWrap.appendChild(dot);
        }

        const title = document.createElement("span");
        title.textContent = notification.title || "Notification";
        titleWrap.appendChild(title);

        const actions = document.createElement("div");
        actions.className = "flex items-center gap-2";

        if (isUnread) {
            const markRead = document.createElement("button");
            markRead.type = "button";
            markRead.className = "inline-flex min-h-11 items-center rounded-lg px-2 text-xs font-semibold text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200";
            markRead.textContent = "Mark read";
            markRead.addEventListener("click", async (e) => {
                e.stopPropagation();
                await markNotificationRead(notification.id);
                await fetchNotifications();
            });
            actions.appendChild(markRead);
        }

        header.appendChild(titleWrap);
        header.appendChild(actions);

        const message = document.createElement("p");
        message.className = "text-sm text-slate-600 dark:text-slate-300";
        message.textContent = notification.message || "";

        const meta = document.createElement("div");
        meta.className = "text-xs text-slate-400";
        meta.textContent = formatDate(notification.createdAt);

        row.appendChild(header);
        row.appendChild(message);

        if (notification.ctaUrl) {
            const cta = document.createElement("a");
            cta.href = notification.ctaUrl;
            cta.className = "inline-flex min-h-11 w-fit items-center rounded-lg border border-slate-200 bg-white px-3 py-2 text-xs font-semibold text-slate-700 shadow-sm hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-950 dark:text-slate-200";
            cta.textContent = "Open";
            cta.addEventListener("click", async (e) => {
                e.stopPropagation();
                if (isUnread) {
                    await markNotificationRead(notification.id);
                }
            });
            row.appendChild(cta);
        }

        row.appendChild(meta);

        if (isUnread) {
            row.addEventListener("click", async () => {
                await markNotificationRead(notification.id);
                await fetchNotifications();
            });
            row.style.cursor = "pointer";
        }

        return row;
    }

    async function fetchThread(threadId) {
        try {
            const res = await fetch(`/api/inbox/threads/${threadId}`, { method: "GET" });
            if (!res.ok) return null;
            return res.json();
        } catch {
            return null;
        }
    }

    function renderMessages(payload) {
        const messagesEl = document.getElementById("inboxMessages");
        const emptyEl = document.getElementById("inboxMessagesEmpty");
        if (!messagesEl || !emptyEl || !payload) return;

        const messages = payload.messages || [];
        if (!messages.length) {
            messagesEl.classList.add("hidden");
            emptyEl.classList.remove("hidden");
            return;
        }

        messagesEl.classList.remove("hidden");
        emptyEl.classList.add("hidden");
        messagesEl.innerHTML = "";

        messages.forEach(msg => {
            const row = document.createElement("div");
            row.className = "flex";

            const card = document.createElement("div");
            card.className = "max-w-2xl rounded-2xl border border-slate-200/70 bg-white px-4 py-3 text-sm shadow-sm dark:border-slate-800 dark:bg-slate-950/40";

            const meta = document.createElement("p");
            meta.className = "text-xs text-slate-500";
            meta.textContent = formatDate(msg.createdAt);

            const body = document.createElement("p");
            body.className = "mt-1 whitespace-pre-wrap text-slate-800 dark:text-slate-200";
            body.textContent = msg.bodyText || "";

            card.appendChild(meta);
            card.appendChild(body);

            if (msg.attachmentUrl) {
                const attachment = document.createElement("a");
                attachment.href = msg.attachmentUrl;
                attachment.target = "_blank";
                attachment.rel = "noopener noreferrer";
                attachment.className = "mt-2 inline-flex items-center text-xs font-semibold text-slate-600 hover:underline dark:text-slate-300";
                attachment.textContent = msg.attachmentName || "View attachment";
                card.appendChild(attachment);
            }

            if (payload.currentUserId === msg.senderUserId) {
                const receipt = document.createElement("div");
                receipt.className = "mt-2 text-[11px] text-slate-400";
                receipt.textContent = msg.readByOther ? "Read" : "Sent";
                card.appendChild(receipt);
            }

            row.appendChild(card);
            messagesEl.appendChild(row);
        });
    }

    async function markThreadRead(threadId) {
        try {
            await fetch(`/api/inbox/threads/${threadId}/read`, { method: "POST", headers });
        } catch {
            // ignore
        }
    }

    async function markNotificationRead(id) {
        if (!id) return;
        try {
            await fetch(`/api/notifications/${id}/read`, { method: "POST", headers });
            broadcastNotificationSync({ source: "inbox", notificationId: id, action: "read" });
        } catch {
            // ignore
        }
    }

    async function sendMessage(threadId, bodyText, attachmentUrl) {
        const payload = {
            bodyText,
            attachmentUrl: attachmentUrl || null,
            attachmentName: attachmentUrl ? "Attachment" : null,
            attachmentType: attachmentUrl ? "link" : null
        };
        const res = await fetch(`/api/inbox/threads/${threadId}/send`, {
            method: "POST",
            headers,
            body: JSON.stringify(payload)
        });
        return res.ok;
    }

    if (listRoot) {
        fetchThreads();
        setInterval(fetchThreads, 8000);
    }

    if (notificationList) {
        fetchNotifications();
    }

    if (notificationReadAll) {
        notificationReadAll.addEventListener("click", async () => {
            await fetch("/api/notifications/read-all", { method: "POST", headers });
            await fetchNotifications();
            broadcastNotificationSync({ source: "inbox", action: "read-all" });
        });
    }

    window.addEventListener("one-to-one:notifications-updated", async () => {
        if (notificationList) await fetchNotifications();
    });
    notificationSyncChannel?.addEventListener("message", async () => {
        if (notificationList) await fetchNotifications();
    });

    if (threadRoot) {
        const threadId = threadRoot.dataset.threadId;
        const sendForm = document.getElementById("inboxSendForm");
        const bodyInput = document.getElementById("inboxBody");
        const attachmentInput = document.getElementById("inboxAttachmentUrl");

        const refreshThread = async () => {
            const payload = await fetchThread(threadId);
            if (!payload) return;
            renderMessages(payload);
            await markThreadRead(threadId);
        };

        refreshThread();
        setInterval(refreshThread, 5000);

        sendForm?.addEventListener("submit", async (event) => {
            if (!threadId || !bodyInput) return;
            event.preventDefault();
            const attachmentUrl = attachmentInput?.value?.trim() || "";
            const bodyText = bodyInput.value.trim();
            if (!bodyText && !attachmentUrl) return;
            bodyInput.value = "";
            if (attachmentInput) attachmentInput.value = "";
            await sendMessage(threadId, bodyText, attachmentUrl);
            refreshThread();
        });
    }
});
