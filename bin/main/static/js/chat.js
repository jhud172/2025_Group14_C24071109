document.addEventListener("DOMContentLoaded", () => {
    const fab = document.getElementById("chatFab");
    const panel = document.getElementById("chatPanel");
    const closeBtn = document.getElementById("chatClose");
    const clearBtn = document.getElementById("chatClear");
    const form = document.getElementById("chatForm");
    const input = document.getElementById("chatInput");
    const body = document.getElementById("chatBody");
    const sendBtn = document.getElementById("chatSend");
    const dot = document.querySelector(".chat-fab-dot");

    const csrfToken = document.getElementById("chat_csrf")?.value || null;
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const STORAGE_KEY = "one2one_chat_history_v1";

    function open() {
        panel.classList.add("open");
        panel.setAttribute("aria-hidden", "false");
        dot?.classList.remove("on");
        setTimeout(() => input.focus(), 50);
    }

    function close() {
        panel.classList.remove("open");
        panel.setAttribute("aria-hidden", "true");
    }

    function scrollToBottom() {
        body.scrollTop = body.scrollHeight;
    }

    function addMsg(text, who) {
        const wrap = document.createElement("div");
        wrap.className = `chat-msg ${who === "me" ? "chat-msg-me" : "chat-msg-ai"}`;

        const bubble = document.createElement("div");
        bubble.className = "bubble";
        bubble.textContent = text;

        wrap.appendChild(bubble);
        body.appendChild(wrap);
        scrollToBottom();
    }

    function addTyping() {
        const wrap = document.createElement("div");
        wrap.className = "chat-msg chat-msg-ai";
        wrap.id = "typingRow";

        const bubble = document.createElement("div");
        bubble.className = "bubble chat-typing";
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
        localStorage.setItem(STORAGE_KEY, JSON.stringify(msgs.slice(-40)));
    }

    function loadHistory() {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            if (!raw) return;
            const msgs = JSON.parse(raw);
            if (!Array.isArray(msgs) || msgs.length === 0) return;

            // keep the first default greeting, then add history
            msgs.forEach(m => addMsg(m.text, m.who));
        } catch (e) {
            // ignore
        }
    }

    async function sendMessage(message) {
        sendBtn.disabled = true;
        addTyping();

        try {
            const headers = { "Content-Type": "application/json" };
            if (csrfToken) headers[csrfHeader] = csrfToken;

            // Endpoint expected: POST /chat/api  -> returns { reply: "..." }
            const res = await fetch("/chat/api", {
                method: "POST",
                headers,
                body: JSON.stringify({ message })
            });

            removeTyping();

            if (!res.ok) {
                addMsg("Something went wrong talking to the AI. Try again.", "ai");
                dot?.classList.add("on");
                return;
            }

            const data = await res.json();
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

    // Events
    fab.addEventListener("click", () => {
        panel.classList.contains("open") ? close() : open();
    });
    closeBtn.addEventListener("click", close);

    clearBtn.addEventListener("click", () => {
        if (!confirm("Clear this chat?")) return;
        // Keep the first greeting message
        body.innerHTML = `
      <div class="chat-msg chat-msg-ai">
        <div class="bubble">Ask me anything about your workouts, progress, notes, or what to do today.</div>
      </div>`;
        localStorage.removeItem(STORAGE_KEY);
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const msg = (input.value || "").trim();
        if (!msg) return;
        input.value = "";
        addMsg(msg, "me");
        saveHistory();
        await sendMessage(msg);
    });

    // Load previous messages
    loadHistory();
    scrollToBottom();
});
