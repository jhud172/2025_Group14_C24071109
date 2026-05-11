document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("coachChat");
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

    const csrfToken = document.getElementById("chat_csrf")?.value || "";
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const isPremium = root.dataset.premium === "true";
    let conversations = [];
    let activeId = null;
    let sending = false;

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
            addMessage("assistant", "Ask about workouts, planning, or tomorrow’s momentum.");
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
