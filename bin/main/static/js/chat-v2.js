document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("chatV2Root");
    if (!root) return;

    const threadId = root.dataset.threadId;
    const messagesEl = document.getElementById("chatMessages");
    const composer = document.getElementById("chatComposer");
    const sendBtn = document.getElementById("chatSendBtn");
    const commandMenu = document.getElementById("chatCommandMenu");
    const settingsToggle = document.getElementById("chatSettingsToggle");
    const settingsDrawer = document.getElementById("chatSettingsDrawer");
    const titleInput = document.getElementById("chatTitleInput");
    const colorInput = document.getElementById("chatColorInput");
    const iconInput = document.getElementById("chatIconInput");
    const pinnedInput = document.getElementById("chatPinnedInput");
    const archivedInput = document.getElementById("chatArchivedInput");
    const instructionsInput = document.getElementById("chatInstructionsInput");
    const instructionsReset = document.getElementById("chatInstructionsReset");
    const settingsSave = document.getElementById("chatSettingsSave");
    const folderMove = document.getElementById("chatFolderMove");
    const quickAddTask = document.getElementById("quickAddTask");

    const csrfToken = document.getElementById("chat_csrf")?.value || null;
    const csrfHeader = document.getElementById("chat_csrf_header")?.value || "X-CSRF-TOKEN";

    const commands = [
        { key: "/task add", desc: "Create a task", example: "/task add Run 5k @ 2026-02-01 09:00" },
        { key: "/task done", desc: "Complete a task", example: "/task done 123" },
        { key: "/task list", desc: "List today's tasks", example: "/task list" },
        { key: "/note add", desc: "Create a note", example: "/note add Title :: body" },
        { key: "/note search", desc: "Search notes", example: "/note search mobility" },
        { key: "/schedule list", desc: "Show schedules", example: "/schedule list" }
    ];

    function renderMessage(text, role) {
        if (!messagesEl) return;
        const row = document.createElement("div");
        row.className = role === "user" ? "flex justify-end" : "flex justify-start";
        const bubble = document.createElement("div");
        bubble.className = "max-w-[80%] whitespace-pre-wrap rounded-2xl border px-3 py-2 shadow-sm";
        bubble.classList.add(role === "user" ? "border-slate-900 bg-slate-900 text-white" : "border-slate-200 bg-slate-50 text-slate-900");
        bubble.textContent = text;
        row.appendChild(bubble);
        messagesEl.appendChild(row);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function renderBlocks(blocks) {
        if (!Array.isArray(blocks) || !messagesEl) return;
        blocks.forEach(block => {
            const card = document.createElement("div");
            card.className = "rounded-2xl border border-slate-200 bg-white/90 p-4 shadow-sm";
            const title = document.createElement("div");
            title.className = "text-xs font-semibold uppercase tracking-widest text-slate-400";
            title.textContent = block.title || "Block";
            const list = document.createElement("div");
            list.className = "mt-3 space-y-2";

            (block.items || []).forEach(item => {
                const row = document.createElement("div");
                row.className = "flex items-center justify-between rounded-xl border border-slate-200 px-3 py-2 text-sm";
                const text = document.createElement("div");
                const label = document.createElement("div");
                label.className = "font-semibold text-slate-800";
                label.textContent = item.label || "Item";
                const value = document.createElement("div");
                value.className = "text-xs text-slate-500";
                value.textContent = item.value || "";
                text.appendChild(label);
                text.appendChild(value);

                const action = document.createElement("button");
                action.type = "button";
                action.className = "rounded-lg bg-slate-900 px-2.5 py-1 text-xs font-semibold text-white";
                action.textContent = item.status === "done" ? "Done" : "Add";
                action.dataset.actionType = item.status === "done" ? "TASK_COMPLETE" : "TASK_CREATE";
                action.dataset.itemId = item.id || "";
                action.addEventListener("click", () => handleBlockAction(action));

                row.appendChild(text);
                row.appendChild(action);
                list.appendChild(row);
            });

            card.appendChild(title);
            card.appendChild(list);
            messagesEl.appendChild(card);
        });
    }

    async function handleBlockAction(button) {
        const type = button.dataset.actionType;
        const payload = {};
        if (type === "TASK_COMPLETE") payload.taskId = button.dataset.itemId;
        const result = await runAction(type, payload);
        if (result?.status === "OK") {
            button.textContent = "Done";
        }
    }

    async function runAction(type, payload) {
        if (!threadId) return;
        const headers = { "Content-Type": "application/json" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        const res = await fetch(`/chatv2/${threadId}/actions`, {
            method: "POST",
            headers,
            body: JSON.stringify({ type, payload })
        });
        return res.ok ? res.json() : null;
    }

    async function sendMessage() {
        if (!threadId) return;
        const message = composer.value.trim();
        if (!message) return;
        composer.value = "";
        renderMessage(message, "user");

        const headers = { "Content-Type": "application/json" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        const res = await fetch(`/chatv2/${threadId}/message`, {
            method: "POST",
            headers,
            body: JSON.stringify({ message })
        });

        if (!res.ok) {
            renderMessage("Something went wrong.", "assistant");
            return;
        }

        const data = await res.json();
        if (data?.assistantText) {
            renderMessage(data.assistantText, "assistant");
        }
        if (data?.blocks) {
            renderBlocks(data.blocks);
        }
    }

    function updateCommandMenu() {
        const value = composer.value;
        if (!value.startsWith("/")) {
            commandMenu?.classList.add("hidden");
            return;
        }
        const filtered = commands.filter(c => c.key.startsWith(value.trim()) || value.trim() === "/");
        if (!commandMenu) return;
        commandMenu.innerHTML = "";
        filtered.forEach(cmd => {
            const item = document.createElement("button");
            item.type = "button";
            item.className = "w-full text-left rounded-xl px-3 py-2 text-sm hover:bg-slate-50";
            item.innerHTML = `<div class="font-semibold">${cmd.key}</div><div class="text-xs text-slate-500">${cmd.desc} · ${cmd.example}</div>`;
            item.addEventListener("click", () => {
                composer.value = cmd.key + " ";
                commandMenu.classList.add("hidden");
                composer.focus();
            });
            commandMenu.appendChild(item);
        });
        commandMenu.classList.remove("hidden");
    }

    if (composer) {
        composer.addEventListener("input", updateCommandMenu);
        composer.addEventListener("keydown", (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }

    sendBtn?.addEventListener("click", sendMessage);

    settingsToggle?.addEventListener("click", () => {
        settingsDrawer?.classList.toggle("hidden");
    });

    instructionsReset?.addEventListener("click", () => {
        if (instructionsInput) instructionsInput.value = "";
    });

    settingsSave?.addEventListener("click", async () => {
        if (!threadId) return;
        const headers = { "Content-Type": "application/x-www-form-urlencoded" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        const body = new URLSearchParams({
            title: titleInput?.value || "",
            colorHex: colorInput?.value || "",
            iconKey: iconInput?.value || "",
            pinned: pinnedInput?.checked ? "true" : "false",
            archived: archivedInput?.checked ? "true" : "false",
            customInstructions: instructionsInput?.value || ""
        });
        await fetch(`/chatv2/${threadId}/settings`, { method: "POST", headers, body });
    });

    folderMove?.addEventListener("change", async () => {
        if (!threadId) return;
        const headers = { "Content-Type": "application/x-www-form-urlencoded" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        const body = new URLSearchParams({ folderId: folderMove.value });
        await fetch(`/chatv2/${threadId}/move`, { method: "POST", headers, body });
    });

    quickAddTask?.addEventListener("click", () => {
        if (!composer) return;
        composer.value = "/task add ";
        composer.focus();
    });
});
