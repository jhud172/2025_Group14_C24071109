document.addEventListener("DOMContentLoaded", () => {
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfParamMeta = document.querySelector('meta[name="_csrf_param"]');

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfParam = csrfParamMeta ? csrfParamMeta.content : null;

    const preview = document.getElementById("preview-card");
    if (!preview) return;

    let hoverTimer = null;
    let closeTimer = null;
    let lockPosition = false;
    let mouseX = 0;
    let mouseY = 0;

    document.addEventListener("mousemove", (e) => {
        mouseX = e.clientX;
        mouseY = e.clientY;
        if (!lockPosition) {
            positionPreview(mouseX, mouseY);
        }
    });

    function positionPreview(x, y) {
        const offset = 18;
        const rect = preview.getBoundingClientRect();
        let left = x + offset;
        let top = y + offset;
        if (left + rect.width > window.innerWidth) {
            left = x - rect.width - offset;
        }
        if (top + rect.height > window.innerHeight) {
            top = y - rect.height - offset;
        }
        preview.style.left = left + "px";
        preview.style.top = top + "px";
    }

    function showPreview() {
        preview.classList.remove("hidden");
        preview.classList.remove("opacity-0");
        requestAnimationFrame(() => {
            preview.classList.add("opacity-100");
        });
        lockPosition = true;
        positionPreview(mouseX, mouseY);
    }

    function hidePreview() {
        preview.classList.remove("opacity-100");
        preview.classList.add("opacity-0");

        setTimeout(() => {
            preview.classList.add("hidden");
            lockPosition = false;
        }, 180);
    }

    function renderPreview(item) {
        const title = item.dataset.title || "Unknown";
        const time = item.dataset.time || "—";
        const notes = item.dataset.notes || "No notes";
        const completed = item.dataset.completed === "true";
        const type = item.dataset.type;
        const id = item.dataset.id;
        let html = `
            <p class="mb-1 text-lg font-semibold text-slate-100">${title}</p>
            <p class="text-xs uppercase tracking-widest text-slate-400">${type === "occurrence" ? "Schedule" : "Task"}</p>
            <p class="mt-2 text-sm text-slate-300">Time: ${time}</p>
            <p class="mb-4 text-sm text-slate-400">${notes}</p>
        `;

        if (completed) {
            html += `
                    <div class="mt-3 flex items-center gap-2 font-semibold text-emerald-300">
                        <span class="text-xl">✓</span> Completed
                    </div>
                `;
        } else if (type === "exercise") {
            html += `
                    <a href="/exercise-log/add-calendar?taskId=${id}"
                        class="mt-3 block rounded-lg bg-white px-3 py-2 text-center text-sm font-semibold text-slate-900 shadow-sm hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-white/40">
                        Complete Exercise Log
                    </a>
                `;
        } else if (type === "occurrence") {
            html += `
                    <a href="/exercise-log/add-occurrence?occId=${id}"
                        class="mt-3 block rounded-lg bg-white px-3 py-2 text-center text-sm font-semibold text-slate-900 shadow-sm hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-white/40">
                        Complete Scheduled Exercise
                    </a>
                `;
        } else if (type === "task") {
            html += `
                    <form method="post" action="/calendar/day/${item.dataset.date}/toggle-complete">
                        <input type="hidden" name="taskId" value="${id}">
                        <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                        <button class="mt-3 rounded-lg bg-white px-3 py-2 text-sm font-semibold text-slate-900 shadow-sm hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-white/40">
                            Mark Completed
                        </button>
                    </form>
                `;
        }
        preview.innerHTML = html;
        positionPreview(mouseX, mouseY);
    }

    function attachPreviewHandlers(root) {
        if (!root) return;
        root.querySelectorAll(".calendar-item").forEach((item) => {
            item.addEventListener("mouseenter", () => {
                clearTimeout(closeTimer);
                hoverTimer = setTimeout(() => {
                    renderPreview(item);
                    showPreview();
                }, 500);
            });
            item.addEventListener("mouseleave", () => {
                clearTimeout(hoverTimer);
                closeTimer = setTimeout(() => {
                    if (!preview.matches(":hover")) hidePreview();
                }, 200);
            });
        });
    }

    preview.addEventListener("mouseenter", () => clearTimeout(closeTimer));
    preview.addEventListener("mouseleave", () => {
        closeTimer = setTimeout(() => hidePreview(), 250);
    });

    const slider = document.getElementById("week-slider");
    const track = document.getElementById("week-slider-track");
    const prevLink = document.getElementById("week-prev");
    const nextLink = document.getElementById("week-next");
    const weekStartEl = document.getElementById("week-start");
    const weekEndEl = document.getElementById("week-end");
    const weekStrip = document.getElementById("week-strip");
    const weekRedirectInput = document.getElementById("week-redirect");
    const currentSlot = track?.querySelector('[data-week-pane-slot="current"]');
    const prevSlot = track?.querySelector('[data-week-pane-slot="prev"]');
    const nextSlot = track?.querySelector('[data-week-pane-slot="next"]');

    const hasSlider = slider && track && prevLink && nextLink && currentSlot && prevSlot && nextSlot;
    if (!hasSlider) {
        attachPreviewHandlers(document);
        return;
    }

    const prefersReducedMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    function keyFor(weekYear, week) {
        return `${weekYear}-W${String(week).padStart(2, "0")}`;
    }

    function getIsoWeekStart(weekYear, week) {
        const jan4 = new Date(weekYear, 0, 4);
        const day = jan4.getDay() === 0 ? 7 : jan4.getDay();
        const monday = new Date(jan4);
        monday.setDate(jan4.getDate() - (day - 1));
        const target = new Date(monday);
        target.setDate(monday.getDate() + (week - 1) * 7);
        return target;
    }

    function formatIsoDate(date) {
        return date.toISOString().slice(0, 10);
    }

    function updateWeekHeaderAndStrip() {
        const start = getIsoWeekStart(currentWeekYear, currentWeek);
        const end = new Date(start);
        end.setDate(start.getDate() + 6);

        if (weekStartEl) weekStartEl.textContent = formatIsoDate(start);
        if (weekEndEl) weekEndEl.textContent = formatIsoDate(end);
        if (weekRedirectInput) {
            weekRedirectInput.value = `/calendar?view=week&week=${currentWeek}&weekYear=${currentWeekYear}`;
        }

        if (weekStrip) {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            weekStrip.innerHTML = "";
            for (let i = 0; i < 7; i++) {
                const day = new Date(start);
                day.setDate(start.getDate() + i);
                const isToday = day.getTime() === today.getTime();
                const dayLabel = day.toLocaleDateString("en-GB", { weekday: "short" }).toUpperCase();
                const dayNumber = day.getDate();
                const cell = document.createElement("div");
                cell.className = `flex-1 rounded-xl px-2 py-1 text-center${isToday ? " bg-blue-500/15 text-blue-600" : ""}`;
                cell.innerHTML = `
                    <div>${dayLabel}</div>
                    <div class="text-[10px] text-slate-500">${dayNumber}</div>
                `;
                weekStrip.appendChild(cell);
            }
        }
    }

    function getIsoWeekFromDate(date) {
        const d = new Date(date);
        d.setHours(0, 0, 0, 0);
        d.setDate(d.getDate() + 3 - ((d.getDay() + 6) % 7));
        const weekYear = d.getFullYear();
        const week1 = new Date(weekYear, 0, 4);
        const week = 1 + Math.round(((d.getTime() - week1.getTime()) / 86400000 - 3 + ((week1.getDay() + 6) % 7)) / 7);
        return { weekYear, week };
    }

    function addWeeks(weekYear, week, delta) {
        const start = getIsoWeekStart(weekYear, week);
        start.setDate(start.getDate() + delta * 7);
        return getIsoWeekFromDate(start);
    }

    function weekPaneUrl(weekYear, week) {
        const url = new URL("/calendar", window.location.origin);
        url.searchParams.set("view", "week");
        url.searchParams.set("week", String(week));
        url.searchParams.set("weekYear", String(weekYear));
        url.searchParams.set("fragment", "weekPane");
        return url.toString();
    }

    function parsePaneHtml(html) {
        const template = document.createElement("template");
        template.innerHTML = html.trim();
        return template.content.firstElementChild;
    }

    const cache = new Map();
    let currentWeek = parseInt(slider.dataset.week || "", 10);
    let currentWeekYear = parseInt(slider.dataset.weekYear || "", 10);
    if (!Number.isFinite(currentWeek) || !Number.isFinite(currentWeekYear)) {
        const now = getIsoWeekFromDate(new Date());
        currentWeek = now.week;
        currentWeekYear = now.weekYear;
    }

    let isAnimating = false;

    async function fetchPane(weekYear, week) {
        const url = weekPaneUrl(weekYear, week);
        const res = await fetch(url, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            credentials: "same-origin"
        });
        if (!res.ok) throw new Error(`Failed to load week pane: ${res.status}`);
        return await res.text();
    }

    async function ensureCached(weekYear, week) {
        const key = keyFor(weekYear, week);
        if (cache.has(key)) return;
        const html = await fetchPane(weekYear, week);
        cache.set(key, html);
    }

    function evictOutsideWindow(centerYear, centerWeek) {
        const keep = new Set();
        for (let d = -2; d <= 2; d++) {
            const t = addWeeks(centerYear, centerWeek, d);
            keep.add(keyFor(t.weekYear, t.week));
        }
        for (const key of Array.from(cache.keys())) {
            if (!keep.has(key)) cache.delete(key);
        }
    }

    function setSlotFromCache(slot, weekYear, week) {
        const key = keyFor(weekYear, week);
        const html = cache.get(key);
        if (!html) return false;
        const paneEl = parsePaneHtml(html);
        if (!paneEl) return false;
        slot.replaceChildren(paneEl);
        attachPreviewHandlers(slot);
        return true;
    }

    function updateNavHrefs() {
        const prev = addWeeks(currentWeekYear, currentWeek, -1);
        const next = addWeeks(currentWeekYear, currentWeek, 1);
        prevLink.href = `/calendar?view=week&week=${prev.week}&weekYear=${prev.weekYear}`;
        nextLink.href = `/calendar?view=week&week=${next.week}&weekYear=${next.weekYear}`;
    }

    async function warmCache(centerYear, centerWeek) {
        const targets = [];
        for (let d = -2; d <= 2; d++) {
            const t = addWeeks(centerYear, centerWeek, d);
            targets.push(ensureCached(t.weekYear, t.week));
        }
        await Promise.allSettled(targets);
        evictOutsideWindow(centerYear, centerWeek);
    }

    async function renderAdjacentPanes() {
        const prev = addWeeks(currentWeekYear, currentWeek, -1);
        const next = addWeeks(currentWeekYear, currentWeek, 1);
        setSlotFromCache(prevSlot, prev.weekYear, prev.week);
        setSlotFromCache(currentSlot, currentWeekYear, currentWeek);
        setSlotFromCache(nextSlot, next.weekYear, next.week);
        updateNavHrefs();
        updateWeekHeaderAndStrip();
        slider.dataset.week = String(currentWeek);
        slider.dataset.weekYear = String(currentWeekYear);
    }

    function animateTo(offsetPercent) {
        return new Promise((resolve) => {
            if (prefersReducedMotion) {
                track.style.transition = "";
                track.style.transform = `translateX(${offsetPercent}%)`;
                resolve();
                return;
            }

            track.style.transition = "transform 360ms cubic-bezier(0.22, 0.61, 0.36, 1)";
            const onEnd = (e) => {
                if (e.propertyName !== "transform") return;
                track.removeEventListener("transitionend", onEnd);
                resolve();
            };
            track.addEventListener("transitionend", onEnd);
            requestAnimationFrame(() => {
                track.style.transform = `translateX(${offsetPercent}%)`;
            });
        });
    }

    function snapToCenter() {
        track.style.transition = "";
        track.style.transform = "translateX(-33.333333%)";
    }

    async function go(delta) {
        if (isAnimating) return;
        isAnimating = true;
        prevLink.setAttribute("aria-disabled", "true");
        nextLink.setAttribute("aria-disabled", "true");
        prevLink.classList.add("pointer-events-none", "opacity-60");
        nextLink.classList.add("pointer-events-none", "opacity-60");

        const target = addWeeks(currentWeekYear, currentWeek, delta);
        await ensureCached(target.weekYear, target.week);
        await renderAdjacentPanes();

        await animateTo(delta > 0 ? -66.666666 : 0);

        currentWeekYear = target.weekYear;
        currentWeek = target.week;
        await warmCache(currentWeekYear, currentWeek);
        await renderAdjacentPanes();
        snapToCenter();

        const url = new URL(window.location.href);
        url.searchParams.set("view", "week");
        url.searchParams.set("week", String(currentWeek));
        url.searchParams.set("weekYear", String(currentWeekYear));
        window.history.pushState({ week: currentWeek, weekYear: currentWeekYear }, "", url.toString());

        prevLink.removeAttribute("aria-disabled");
        nextLink.removeAttribute("aria-disabled");
        prevLink.classList.remove("pointer-events-none", "opacity-60");
        nextLink.classList.remove("pointer-events-none", "opacity-60");
        isAnimating = false;
    }

    prevLink.addEventListener("click", (e) => {
        e.preventDefault();
        go(-1);
    });

    nextLink.addEventListener("click", (e) => {
        e.preventDefault();
        go(1);
    });

    let pointerStartX = null;
    let pointerActive = false;
    slider.addEventListener("pointerdown", (e) => {
        if (e.target.closest("a, button, input, textarea, select")) return;
        pointerStartX = e.clientX;
        pointerActive = true;
        slider.setPointerCapture?.(e.pointerId);
    });
    slider.addEventListener("pointerup", (e) => {
        if (!pointerActive || pointerStartX == null) return;
        const delta = e.clientX - pointerStartX;
        pointerActive = false;
        pointerStartX = null;
        if (Math.abs(delta) > 60) {
            go(delta < 0 ? 1 : -1);
        }
    });
    slider.addEventListener("pointercancel", () => {
        pointerActive = false;
        pointerStartX = null;
    });

    window.addEventListener("popstate", async () => {
        const params = new URLSearchParams(window.location.search);
        const view = params.get("view") || "week";
        if (view !== "week") return;
        const week = parseInt(params.get("week") || "", 10);
        const weekYear = parseInt(params.get("weekYear") || "", 10);
        if (!Number.isFinite(week) || !Number.isFinite(weekYear)) return;
        currentWeek = week;
        currentWeekYear = weekYear;
        await warmCache(currentWeekYear, currentWeek);
        await renderAdjacentPanes();
        snapToCenter();
    });

    const initialPane = currentSlot.querySelector('[data-week-pane]');
    if (initialPane) {
        const key = keyFor(currentWeekYear, currentWeek);
        cache.set(key, initialPane.outerHTML);
        attachPreviewHandlers(currentSlot);
    }

    if (track) {
        track.style.willChange = "transform";
    }
    updateWeekHeaderAndStrip();
    warmCache(currentWeekYear, currentWeek).then(() => {
        renderAdjacentPanes();
        snapToCenter();
    });
});
