document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("quickActionsRoot");
    if (!root) return;

    const isPremium = root.dataset.premium === "true";
    const isAuthenticated = root.dataset.auth === "true";

    const shelf = document.getElementById("quickActionsShelf");
    const toggleBtn = document.getElementById("quickActionsToggle");
    const closeBtn = document.getElementById("quickActionsClose");
    const customizeToggle = document.getElementById("quickActionsCustomizeToggle");
    const viewPane = document.getElementById("quickActionsView");
    const customizePane = document.getElementById("quickActionsCustomize");
    const listEl = document.getElementById("quickActionsList");
    const emptyEl = document.getElementById("quickActionsEmpty");
    const messageEl = document.getElementById("quickActionsMessage");
    const searchInput = document.getElementById("quickActionsSearch");
    const customizeList = document.getElementById("quickActionsCustomizeList");
    const customName = document.getElementById("quickActionsCustomName");
    const customPrompt = document.getElementById("quickActionsCustomPrompt");
    const customCreate = document.getElementById("quickActionsCustomCreate");
    const customLimit = document.getElementById("quickActionsCustomLimit");
    const customLock = document.getElementById("quickActionsCustomLock");

    const csrfToken = document.getElementById("quick_actions_csrf")?.value || null;
    const csrfHeader = document.getElementById("quick_actions_csrf_header")?.value || "X-CSRF-TOKEN";

    let actions = [];
    let customizeOpen = false;

    const PREMIUM_BADGE_CLASS = "text-xs font-medium text-amber-600 bg-amber-50 border border-amber-200/60 rounded-md px-1.5 py-0.5 dark:bg-amber-950/40 dark:border-amber-800/60 dark:text-amber-400";

    const ACTION_META = {
        OPEN_CALENDAR: { icon: "🗓️", desc: "View schedule" },
        LOG_NUTRITION: { icon: "🥗", desc: "Log today" },
        START_WORKOUT: { icon: "🏋️", desc: "Start session" },
        PROGRESS_CHECK: { icon: "📈", desc: "Levels + trends" },
        OPEN_NOTES: { icon: "📝", desc: "Capture a note" },
        OPEN_INBOX: { icon: "📥", desc: "See messages" },
        CREATE_TASK: { icon: "✅", desc: "New task" }
    };

    function getActionMeta(action) {
        if (action.type === "CUSTOM_AI") {
            return { icon: "✨", desc: "Custom AI" };
        }
        return ACTION_META[action.actionKey] || { icon: "⚡", desc: action.actionKey || "" };
    }

    function showMessage(text) {
        if (!messageEl) return;
        messageEl.textContent = text;
        messageEl.classList.remove("hidden");
        setTimeout(() => messageEl.classList.add("hidden"), 4000);
    }

    function openShelf() {
        shelf?.classList.add("open");
        shelf?.setAttribute("aria-hidden", "false");
        toggleBtn?.setAttribute("aria-expanded", "true");
        if (customizeOpen) setCustomizeMode(false);
    }

    function closeShelf() {
        shelf?.classList.remove("open");
        shelf?.setAttribute("aria-hidden", "true");
        toggleBtn?.setAttribute("aria-expanded", "false");
    }

    function toggleShelf() {
        if (shelf?.classList.contains("open")) {
            closeShelf();
        } else {
            openShelf();
        }
    }

    toggleBtn?.addEventListener("click", toggleShelf);
    closeBtn?.addEventListener("click", closeShelf);

    function setCustomizeMode(enabled) {
        customizeOpen = enabled;
        viewPane?.classList.toggle("hidden", enabled);
        customizePane?.classList.toggle("hidden", !enabled);
        if (!enabled) renderActive();
    }

    customizeToggle?.addEventListener("click", () => {
        setCustomizeMode(!customizeOpen);
    });

    function authHeaders() {
        const headers = { "Content-Type": "application/json" };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        return headers;
    }

    async function loadActions() {
        if (!isAuthenticated) {
            actions = [];
            render();
            return;
        }
        try {
            const res = await fetch("/api/quick-actions", { credentials: "same-origin" });
            if (!res.ok) throw new Error("Failed to load actions");
            actions = await res.json();
            render();
        } catch (err) {
            showMessage("Quick actions unavailable.");
        }
    }

    function sortActions(list) {
        return list.slice().sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0));
    }

    function render() {
        renderActive();
        renderCustomize();
    }

    function renderActive() {
        if (!listEl) return;
        listEl.innerHTML = "";
        const active = sortActions(actions).filter(a => a.isActive).slice(0, 10);
        if (emptyEl) emptyEl.classList.toggle("hidden", active.length > 0);

        active.forEach(action => {
            const meta = getActionMeta(action);
            const button = document.createElement("button");
            button.type = "button";
            button.className = "group flex flex-col items-start justify-between rounded-2xl border border-slate-200 bg-white px-3 py-3 text-left text-sm font-semibold text-slate-900 shadow-sm transition hover:border-slate-300 hover:bg-slate-50 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-100 dark:hover:bg-slate-800";
            button.dataset.actionId = action.id;
            button.dataset.actionType = action.type;
            button.dataset.actionKey = action.actionKey || "";
            button.dataset.prompt = action.prompt || "";

            const top = document.createElement("div");
            top.className = "flex w-full items-start justify-between gap-2";

            const icon = document.createElement("span");
            icon.className = "grid h-8 w-8 place-items-center rounded-xl border border-slate-200 bg-white text-base shadow-sm dark:border-slate-800 dark:bg-slate-950";
            icon.textContent = meta.icon;
            top.appendChild(icon);

            const badge = document.createElement("span");
            badge.className = action.type === "CUSTOM_AI" && !isPremium
                ? PREMIUM_BADGE_CLASS
                : "text-[10px] text-slate-400";
            badge.textContent = action.type === "CUSTOM_AI" ? (isPremium ? "AI" : "🔒 Premium") : "";
            top.appendChild(badge);

            const label = document.createElement("div");
            label.className = "mt-3 text-sm font-semibold text-slate-900 dark:text-slate-100";
            label.textContent = action.name;

            const desc = document.createElement("div");
            desc.className = "mt-1 text-xs text-slate-500";
            desc.textContent = meta.desc;

            button.appendChild(top);
            button.appendChild(label);
            button.appendChild(desc);

            button.addEventListener("click", () => handleActionClick(action));
            if (action.type === "CUSTOM_AI" && !isPremium) {
                button.classList.add("cursor-not-allowed", "opacity-60");
                button.title = "Premium required";
                button.setAttribute("aria-disabled", "true");
                button.setAttribute("aria-label", `${action.name} — Premium required`);
            }
            listEl.appendChild(button);
        });
    }

    function renderCustomize() {
        if (!customizeList) return;
        const list = sortActions(actions);
        customizeList.innerHTML = "";

        const customCount = list.filter(a => a.type === "CUSTOM_AI").length;
        if (customLimit) customLimit.textContent = `${customCount}/2`;
        if (customLock) customLock.classList.toggle("hidden", isPremium);
        if (customCreate) {
            customCreate.disabled = !isPremium || customCount >= 2;
        }

        list.forEach(action => {
            const item = document.createElement("li");
            item.className = "quick-actions-item flex items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-white px-3 py-2 text-xs text-slate-700 shadow-sm dark:border-slate-800 dark:bg-slate-900 dark:text-slate-200";
            item.dataset.actionId = action.id;
            item.draggable = true;

            const left = document.createElement("div");
            left.className = "flex items-center gap-2";

            const handle = document.createElement("span");
            handle.className = "quick-actions-handle text-slate-400";
            handle.textContent = "⋮⋮";
            left.appendChild(handle);

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.checked = action.isActive;
            checkbox.disabled = action.type === "CUSTOM_AI" && !isPremium;
            checkbox.className = "h-4 w-4 rounded border-slate-300 accent-emerald-500";
            checkbox.addEventListener("change", () => toggleActive(action, checkbox));
            left.appendChild(checkbox);

            const meta = getActionMeta(action);
            const label = document.createElement("div");
            label.innerHTML = `<div class="font-semibold">${action.name}</div><div class="text-[10px] text-slate-400">${meta.desc || (action.type === "CUSTOM_AI" ? "Custom AI" : action.actionKey)}</div>`;
            left.appendChild(label);

            item.appendChild(left);
            if (action.type === "CUSTOM_AI" && !isPremium) {
                item.classList.add("cursor-not-allowed");
                const lock = document.createElement("span");
                lock.className = PREMIUM_BADGE_CLASS;
                lock.textContent = "🔒 Premium";
                item.appendChild(lock);
            }
            customizeList.appendChild(item);
        });

        attachDragAndDrop();
        applySearchFilter();
    }

    function applySearchFilter() {
        const term = (searchInput?.value || "").trim().toLowerCase();
        if (!customizeList) return;
        Array.from(customizeList.children).forEach((item) => {
            const id = item.dataset.actionId;
            const action = actions.find(a => String(a.id) === String(id));
            if (!action) return;
            const meta = getActionMeta(action);
            const haystack = `${action.name} ${action.actionKey || ""} ${meta.desc || ""}`.toLowerCase();
            item.classList.toggle("hidden", term && !haystack.includes(term));
        });
    }

    searchInput?.addEventListener("input", applySearchFilter);

    async function toggleActive(action, checkbox) {
        if (action.type === "CUSTOM_AI" && !isPremium) {
            checkbox.checked = false;
            showMessage("Premium required for custom AI actions.");
            return;
        }

        try {
            const res = await fetch(`/api/quick-actions/${action.id}/active`, {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify({ active: checkbox.checked })
            });
            if (!res.ok) {
                checkbox.checked = !checkbox.checked;
                if (res.status === 403) {
                    showMessage("Premium required for custom AI actions.");
                } else if (res.status === 400) {
                    showMessage("You can only activate up to 10 actions.");
                }
                return;
            }
            const updated = await res.json();
            const idx = actions.findIndex(a => a.id === updated.id);
            if (idx >= 0) actions[idx] = updated;
            renderActive();
        } catch {
            checkbox.checked = !checkbox.checked;
            showMessage("Unable to update action.");
        }
    }

    async function createCustomAction() {
        if (!customName || !customPrompt || !customCreate) return;
        const name = customName.value.trim();
        const prompt = customPrompt.value.trim();
        if (!name || !prompt) {
            showMessage("Name and prompt are required.");
            return;
        }

        try {
            const res = await fetch("/api/quick-actions/custom", {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify({ name, prompt })
            });
            if (!res.ok) {
                if (res.status === 403) {
                    showMessage("Premium required for custom actions.");
                } else {
                    showMessage("Unable to create custom action.");
                }
                return;
            }
            const created = await res.json();
            actions.push(created);
            customName.value = "";
            customPrompt.value = "";
            render();
        } catch {
            showMessage("Unable to create custom action.");
        }
    }

    customCreate?.addEventListener("click", createCustomAction);

    function attachDragAndDrop() {
        if (!customizeList) return;
        let dragItem = null;

        customizeList.querySelectorAll(".quick-actions-item").forEach((item) => {
            item.addEventListener("dragstart", (event) => {
                dragItem = item;
                item.classList.add("dragging");
                event.dataTransfer.effectAllowed = "move";
            });
            item.addEventListener("dragend", () => {
                item.classList.remove("dragging");
            });
        });

        customizeList.addEventListener("dragover", (event) => {
            event.preventDefault();
            const target = event.target.closest(".quick-actions-item");
            if (!target || target === dragItem) return;
            const rect = target.getBoundingClientRect();
            const after = event.clientY > rect.top + rect.height / 2;
            if (after) {
                target.after(dragItem);
            } else {
                target.before(dragItem);
            }
        });

        customizeList.addEventListener("drop", async () => {
            if (!customizeList) return;
            const ids = Array.from(customizeList.querySelectorAll(".quick-actions-item"))
                .map(item => Number(item.dataset.actionId))
                .filter(id => Number.isFinite(id));
            if (ids.length === 0) return;

            try {
                const res = await fetch("/api/quick-actions/reorder", {
                    method: "POST",
                    headers: authHeaders(),
                    body: JSON.stringify({ ids })
                });
                if (!res.ok) {
                    showMessage("Unable to reorder actions.");
                    return;
                }
                actions = await res.json();
                renderActive();
            } catch {
                showMessage("Unable to reorder actions.");
            }
        });
    }

    function buildContext() {
        const selection = window.getSelection?.().toString() || "";
        return {
            title: document.title,
            url: window.location.pathname + window.location.search,
            selection: selection.slice(0, 200)
        };
    }

    async function handleActionClick(action) {
        if (action.type === "CUSTOM_AI") {
            if (!isPremium) {
                showMessage("Premium required for custom AI actions.");
                return;
            }
            await runCustomAction(action);
            return;
        }

        switch (action.actionKey) {
            case "OPEN_CALENDAR":
                window.location.href = "/calendar";
                break;
            case "OPEN_NOTES":
                window.location.href = "/notes";
                break;
            case "LOG_NUTRITION":
                window.location.href = "/nutrition/daily-log";
                break;
            case "START_WORKOUT":
                window.location.href = "/workouts/start";
                break;
            case "PROGRESS_CHECK":
                window.location.href = "/levels/me";
                break;
            case "OPEN_INBOX":
                window.location.href = "/inbox";
                break;
            case "CREATE_TASK":
                window.location.href = "/calendar";
                break;
            default:
                showMessage("Action not available.");
        }
    }

    async function runCustomAction(action) {
        const prompt = action.prompt || "";
        if (!prompt) {
            showMessage("Action prompt missing.");
            return;
        }

        const context = buildContext();
        const message = `You are a quick action assistant. Respond ONLY with JSON.\n\nAllowed actions:\n- create_note: {action, title, content}\n- navigate: {action, url}\n- fill_form: {action, selector, value}\n- none: {action, message}\n\nRules:\n- url must be a relative path starting with '/'.\n- selector must target a visible input, textarea, or select on the page.\n- Keep responses concise.\n\nUser prompt: ${prompt}\n\nPage context: title='${context.title}', url='${context.url}', selection='${context.selection}'`;

        try {
            const res = await fetch("/chat/api", {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify({ message })
            });
            const data = await res.json();
            const reply = data?.reply || "";
            const actionObj = parseActionJson(reply);
            if (!actionObj) {
                showMessage("AI response was not usable.");
                return;
            }
            await executeAction(actionObj);
        } catch {
            showMessage("AI request failed.");
        }
    }

    function parseActionJson(text) {
        if (!text) return null;
        const match = text.match(/\{[\s\S]*\}/);
        if (!match) return null;
        try {
            return JSON.parse(match[0]);
        } catch {
            return null;
        }
    }

    async function executeAction(actionObj) {
        const action = actionObj.action;
        if (!action) {
            showMessage("AI returned no action.");
            return;
        }

        if (action === "create_note") {
            const title = actionObj.title || "Quick action note";
            const content = actionObj.content || "";
            await createNote(title, content);
            return;
        }

        if (action === "navigate") {
            const url = actionObj.url || "";
            if (url.startsWith("/")) {
                window.location.href = url;
            } else {
                showMessage("Navigation blocked.");
            }
            return;
        }

        if (action === "fill_form") {
            const selector = actionObj.selector || "";
            const value = actionObj.value || "";
            const field = document.querySelector(selector);
            if (!field || !["INPUT", "TEXTAREA", "SELECT"].includes(field.tagName)) {
                showMessage("Form field not found.");
                return;
            }
            field.value = value;
            field.dispatchEvent(new Event("input", { bubbles: true }));
            field.focus();
            return;
        }

        if (action === "none") {
            showMessage(actionObj.message || "No action taken.");
            return;
        }

        showMessage("Action not permitted.");
    }

    async function createNote(title, content) {
        try {
            const res = await fetch("/notes/api/notes", {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify({ title, content })
            });
            if (!res.ok) {
                showMessage("Unable to create note.");
                return;
            }
            const note = await res.json();
            if (note?.id) {
                window.location.href = `/notes/${note.id}`;
            } else {
                showMessage("Note created.");
            }
        } catch {
            showMessage("Unable to create note.");
        }
    }

    loadActions();
});
