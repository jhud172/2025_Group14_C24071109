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
    const instructionsToggle = document.getElementById("chatInstructionsToggle");
    const instructionsDrawer = document.getElementById("chatInstructionsDrawer");
    const titleInput = document.getElementById("chatTitleInput");
    const colorInput = document.getElementById("chatColorInput");
    const iconInput = document.getElementById("chatIconInput");
    const pinnedInput = document.getElementById("chatPinnedInput");
    const archivedInput = document.getElementById("chatArchivedInput");
    const instructionsInput = document.getElementById("chatInstructionsInput");
    const instructionsReset = document.getElementById("chatInstructionsReset");
    const instructionsClear = document.getElementById("chatInstructionsResetBtn");
    const instructionsSave = document.getElementById("chatInstructionsSave");
    const instructionsBadge = document.getElementById("chatInstructionsBadge");
    const settingsSave = document.getElementById("chatSettingsSave");
    const folderMove = document.getElementById("chatFolderMove");
    const quickAddTask = document.getElementById("quickAddTask");
    const presetButtons = Array.from(document.querySelectorAll(".chat-preset"));

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

                const actionSpec = resolveActionSpec(block, item);
                if (actionSpec) {
                    const action = document.createElement("button");
                    action.type = "button";
                    action.className = "rounded-lg bg-slate-900 px-2.5 py-1 text-xs font-semibold text-white";
                    action.textContent = actionSpec.label;
                    action.dataset.actionType = actionSpec.type;
                    action.dataset.actionPayload = JSON.stringify(actionSpec.payload || {});
                    action.disabled = actionSpec.disabled;
                    if (actionSpec.disabled) {
                        action.classList.add("opacity-60", "cursor-not-allowed");
                    }
                    action.addEventListener("click", () => handleBlockAction(action));
                    row.appendChild(text);
                    row.appendChild(action);
                } else {
                    row.appendChild(text);
                }
                list.appendChild(row);
            });

            card.appendChild(title);
            card.appendChild(list);
            messagesEl.appendChild(card);
        });
    }

    async function handleBlockAction(button) {
        const type = button.dataset.actionType;
        const payload = button.dataset.actionPayload ? JSON.parse(button.dataset.actionPayload) : {};
        const result = await runAction(type, payload);
        if (result) {
            renderActionResults([result]);
        }
        if (result?.status === "OK") {
            button.textContent = "Done";
            button.disabled = true;
            button.classList.add("opacity-60", "cursor-not-allowed");
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
        if (data?.actions) {
            renderActionResults(data.actions);
        }
    }

    function resolveActionSpec(block, item) {
        if (!block || !item) return null;
        const blockType = (block.type || "").toLowerCase();
        const status = (item.status || "").toLowerCase();

        if (blockType === "tasks") {
            if (status === "done") {
                return { type: "TASK_COMPLETE", label: "Done", payload: { taskId: item.id }, disabled: true };
            }
            if (item.id) {
                return { type: "TASK_COMPLETE", label: "Complete", payload: { taskId: item.id } };
            }
            return { type: "TASK_CREATE", label: "Add", payload: { title: item.label || "New task" } };
        }

        if (blockType === "notes") {
            return { type: "NOTE_CREATE", label: "Add", payload: { title: item.label || "New note", content: item.value || "" } };
        }

        if (blockType === "schedule") {
            if (!item.id) return null;
            return { type: "SCHEDULE_APPLY", label: "Apply", payload: { scheduleId: item.id } };
        }

        return null;
    }

    function renderActionResults(actions) {
        if (!Array.isArray(actions) || !messagesEl || actions.length === 0) return;
        const card = document.createElement("div");
        card.className = "rounded-2xl border border-slate-200 bg-slate-50 p-3 text-xs text-slate-700";
        const title = document.createElement("div");
        title.className = "text-[11px] font-semibold uppercase tracking-widest text-slate-400";
        title.textContent = "Action results";
        card.appendChild(title);

        actions.forEach(action => {
            const row = document.createElement("div");
            row.className = "mt-2 flex items-center justify-between gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2";
            const label = document.createElement("div");
            label.className = "text-xs font-semibold text-slate-700";
            label.textContent = action.type || "Action";
            const status = document.createElement("div");
            const ok = (action.status || "").toUpperCase() === "OK";
            status.className = ok
                ? "rounded-full bg-emerald-500/10 px-2 py-0.5 text-[11px] font-semibold text-emerald-700"
                : "rounded-full bg-rose-500/10 px-2 py-0.5 text-[11px] font-semibold text-rose-700";
            status.textContent = action.message || (ok ? "Done" : "Failed");
            row.appendChild(label);
            row.appendChild(status);
            card.appendChild(row);
        });

        messagesEl.appendChild(card);
        messagesEl.scrollTop = messagesEl.scrollHeight;
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

    instructionsToggle?.addEventListener("click", () => {
        instructionsDrawer?.classList.toggle("hidden");
    });

    instructionsReset?.addEventListener("click", () => {
        if (instructionsInput) instructionsInput.value = "";
    });

    instructionsClear?.addEventListener("click", () => {
        if (instructionsInput) instructionsInput.value = "";
    });

    presetButtons.forEach(button => {
        button.addEventListener("click", () => {
            if (!instructionsInput) return;
            const preset = button.dataset.preset;
            const textMap = {
                fatloss: "Focus on sustainable fat loss. Keep guidance simple, emphasize protein, daily movement, and recovery.",
                strength: "Prioritize strength progressions. Use progressive overload, compound lifts, and form cues.",
                rehab: "Use rehab-friendly guidance. Prioritize pain-free range, controlled tempo, and gradual progression.",
                motivation: "Be motivating and upbeat. Encourage consistency, celebrate wins, and suggest small next steps."
            };
            const insert = textMap[preset] || "";
            if (!insert) return;
            const current = instructionsInput.value || "";
            instructionsInput.value = current ? current + "\n" + insert : insert;
            instructionsInput.focus();
        });
    });

    async function saveInstructions() {
        if (!threadId) return;
        const headers = { "Content-Type": "application/x-www-form-urlencoded" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        const body = new URLSearchParams({
            customInstructions: instructionsInput?.value || ""
        });
        await fetch(`/chatv2/${threadId}/settings`, { method: "POST", headers, body });
        const hasValue = instructionsInput?.value?.trim().length > 0;
        if (instructionsBadge) {
            instructionsBadge.classList.toggle("hidden", !hasValue);
        }
    }

    instructionsSave?.addEventListener("click", saveInstructions);

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
        const hasValue = instructionsInput?.value?.trim().length > 0;
        if (instructionsBadge) {
            instructionsBadge.classList.toggle("hidden", !hasValue);
        }
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
