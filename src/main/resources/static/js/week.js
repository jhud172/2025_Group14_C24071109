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

    function attachDayCardNavigation(root) {
        if (!root) return;

        root.querySelectorAll('.calendar-day-card[data-day-link]').forEach((card) => {
            if (card.dataset.dayCardNavBound === 'true') return;
            card.dataset.dayCardNavBound = 'true';
            card.classList.add('cursor-pointer');

            card.addEventListener('click', (event) => {
                if (event.defaultPrevented) return;
                if (event.target.closest('button, input, textarea, select, form')) return;

                const href = card.getAttribute('data-day-link');
                if (href) {
                    window.location.href = href;
                }
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
    const weekRedirectInput = document.getElementById("week-redirect");
    const jumpTodayBtn = document.getElementById("week-jump-today");
    const jumpDateInput = document.getElementById("week-jump-date");
    const jumpDateBtn = document.getElementById("week-jump-date-go");
    const jumpNextWorkoutBtn = document.getElementById("week-jump-next-workout");
    const jumpTaskInput = document.getElementById("week-jump-task");
    const jumpTaskBtn = document.getElementById("week-jump-task-go");
    const jumpStatusEl = document.getElementById("week-jump-status");
    const jumpControls = [jumpTodayBtn, jumpDateInput, jumpDateBtn, jumpNextWorkoutBtn, jumpTaskInput, jumpTaskBtn].filter(Boolean);
    let jumpActionInProgress = false;
    const currentSlot = track?.querySelector('[data-week-pane-slot="current"]');
    const prevSlot = track?.querySelector('[data-week-pane-slot="prev"]');
    const nextSlot = track?.querySelector('[data-week-pane-slot="next"]');

    const hasSlider = slider && track && prevLink && nextLink && currentSlot && prevSlot && nextSlot;
    if (!hasSlider) {
        attachPreviewHandlers(document);
        attachDayCardNavigation(document);
        return;
    }

    const prefersReducedMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    function setJumpStatus(message, type = 'error') {
        if (!jumpStatusEl) return;
        jumpStatusEl.textContent = message || '';
        jumpStatusEl.classList.toggle('is-visible', !!message);
        jumpStatusEl.classList.toggle('is-error', !!message && type === 'error');
        jumpStatusEl.classList.toggle('is-success', !!message && type === 'success');
    }

    function setJumpControlsDisabled(disabled) {
        jumpControls.forEach((control) => {
            control.disabled = disabled;
        });
    }

    async function runJumpAction(action) {
        if (jumpActionInProgress) return;
        jumpActionInProgress = true;
        setJumpControlsDisabled(true);
        try {
            await action();
        } catch (error) {
            console.error(error);
            setJumpStatus('Jump failed. Please try again.', 'error');
        } finally {
            setJumpControlsDisabled(false);
            jumpActionInProgress = false;
        }
    }

    function toLocalIsoDate(date) {
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, "0");
        const dd = String(date.getDate()).padStart(2, "0");
        return `${yyyy}-${mm}-${dd}`;
    }

    function parseIsoDateInput(value) {
        if (!value || !/^\d{4}-\d{2}-\d{2}$/.test(value)) return null;
        const [year, month, day] = value.split("-").map((part) => parseInt(part, 10));
        if (!Number.isFinite(year) || !Number.isFinite(month) || !Number.isFinite(day)) return null;
        return new Date(year, month - 1, day);
    }

    function getCurrentPane() {
        return currentSlot.querySelector('[data-week-pane]');
    }

    function clearJumpHighlights() {
        document.querySelectorAll('.calendar-day-card--jump-target').forEach((card) => {
            card.classList.remove('calendar-day-card--jump-target');
        });
    }

    function scrollJumpTargetIntoView(card) {
        if (!card) return;
        const rect = card.getBoundingClientRect();
        const topInset = 110;
        const bottomInset = 24;
        const outsideViewport = rect.top < topInset || rect.bottom > (window.innerHeight - bottomInset);
        if (!outsideViewport) return;
        card.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' });
    }

    function highlightJumpCard(card) {
        if (!card) return false;
        clearJumpHighlights();
        card.classList.add('calendar-day-card--jump-target');
        scrollJumpTargetIntoView(card);
        setTimeout(() => {
            card.classList.remove('calendar-day-card--jump-target');
        }, 1800);
        return true;
    }

    function sortedCardsInPane(pane) {
        return Array.from(pane.querySelectorAll('.calendar-day-card[data-date]')).sort((a, b) => {
            return (a.dataset.date || '').localeCompare(b.dataset.date || '');
        });
    }

    function findDateCardInCurrentPane(dateIso) {
        const pane = getCurrentPane();
        if (!pane) return null;
        return pane.querySelector(`.calendar-day-card[data-date="${dateIso}"]`);
    }

    function weekDifference(fromWeekYear, fromWeek, toWeekYear, toWeek) {
        const fromStart = getIsoWeekStart(fromWeekYear, fromWeek);
        const toStart = getIsoWeekStart(toWeekYear, toWeek);
        return Math.round((toStart.getTime() - fromStart.getTime()) / (7 * 24 * 60 * 60 * 1000));
    }

    async function jumpToWeek(targetWeekYear, targetWeek) {
        let safety = 0;
        while ((currentWeekYear !== targetWeekYear || currentWeek !== targetWeek) && safety < 260) {
            const diff = weekDifference(currentWeekYear, currentWeek, targetWeekYear, targetWeek);
            await go(diff > 0 ? 1 : -1);
            safety += 1;
        }
        return currentWeekYear === targetWeekYear && currentWeek === targetWeek;
    }

    function getCurrentWeekState() {
        return { weekYear: currentWeekYear, week: currentWeek };
    }

    async function restoreWeekState(state) {
        if (!state) return;
        if (currentWeekYear === state.weekYear && currentWeek === state.week) return;
        await jumpToWeek(state.weekYear, state.week);
    }

    function findNextWorkoutCard(pane, fromDateIso) {
        const cards = sortedCardsInPane(pane);
        return cards.find((card) => {
            const date = card.dataset.date || '';
            if (date < fromDateIso) return false;
            return !!card.querySelector('.calendar-item[data-type="workout"]');
        }) || null;
    }

    function findTaskCardByQuery(pane, query, fromDateIso) {
        const cards = sortedCardsInPane(pane);
        return cards.find((card) => {
            const date = card.dataset.date || '';
            if (date < fromDateIso) return false;
            return Array.from(card.querySelectorAll('.calendar-item[data-type="task"]')).some((item) => {
                const title = (item.dataset.title || '').toLowerCase();
                return title.includes(query);
            });
        }) || null;
    }

    async function jumpToDate(dateIso) {
        const parsed = parseIsoDateInput(dateIso);
        if (!parsed) {
            setJumpStatus('Please enter a valid date.', 'error');
            return;
        }
        const target = getIsoWeekFromDate(parsed);
        const reached = await jumpToWeek(target.weekYear, target.week);
        if (!reached) {
            setJumpStatus('Could not navigate to that date.', 'error');
            return;
        }
        const targetCard = findDateCardInCurrentPane(dateIso);
        if (!highlightJumpCard(targetCard)) {
            setJumpStatus('No day card found for that date.', 'error');
            return;
        }
        setJumpStatus('Jumped to selected date.', 'success');
    }

    async function jumpToNextWorkout() {
        const fromDateIso = toLocalIsoDate(new Date());
        const initialState = getCurrentWeekState();
        let safety = 0;
        while (safety < 52) {
            const pane = getCurrentPane();
            if (pane) {
                const targetCard = findNextWorkoutCard(pane, fromDateIso);
                if (targetCard) {
                    highlightJumpCard(targetCard);
                    setJumpStatus('Jumped to next workout.', 'success');
                    return;
                }
            }
            await go(1);
            safety += 1;
        }
        await restoreWeekState(initialState);
        setJumpStatus('No upcoming workout found.', 'error');
    }

    async function jumpToTask() {
        const query = (jumpTaskInput?.value || '').trim().toLowerCase();
        if (!query) {
            setJumpStatus('Please enter a task name to search.', 'error');
            return;
        }
        const fromDateIso = toLocalIsoDate(new Date());
        const initialState = getCurrentWeekState();
        let safety = 0;
        while (safety < 52) {
            const pane = getCurrentPane();
            if (pane) {
                const targetCard = findTaskCardByQuery(pane, query, fromDateIso);
                if (targetCard) {
                    highlightJumpCard(targetCard);
                    setJumpStatus('Jumped to matching task.', 'success');
                    return;
                }
            }
            await go(1);
            safety += 1;
        }
        await restoreWeekState(initialState);
        setJumpStatus('No matching task found.', 'error');
    }

    jumpTodayBtn?.addEventListener('click', () => {
        runJumpAction(() => jumpToDate(toLocalIsoDate(new Date())));
    });

    jumpDateBtn?.addEventListener('click', () => {
        runJumpAction(() => jumpToDate(jumpDateInput?.value || ''));
    });

    jumpDateInput?.addEventListener('keydown', (event) => {
        if (event.key !== 'Enter') return;
        event.preventDefault();
        runJumpAction(() => jumpToDate(jumpDateInput.value || ''));
    });

    jumpNextWorkoutBtn?.addEventListener('click', () => {
        runJumpAction(() => jumpToNextWorkout());
    });

    jumpTaskBtn?.addEventListener('click', () => {
        runJumpAction(() => jumpToTask());
    });

    jumpTaskInput?.addEventListener('keydown', (event) => {
        if (event.key !== 'Enter') return;
        event.preventDefault();
        runJumpAction(() => jumpToTask());
    });

    jumpDateInput?.addEventListener('input', () => {
        if (jumpStatusEl?.classList.contains('is-error')) {
            setJumpStatus('', 'error');
        }
    });

    jumpTaskInput?.addEventListener('input', () => {
        if (jumpStatusEl?.classList.contains('is-error')) {
            setJumpStatus('', 'error');
        }
    });

    const heatmapCache = new Map();

    function heatClassFor(loadScore) {
        if (loadScore <= 0) return "heat-none";
        if (loadScore <= 3) return "heat-low";
        if (loadScore <= 7) return "heat-med";
        return "heat-high";
    }

    function applySummaryToPane(pane, summaries) {
        if (!pane) return;
        const summaryMap = new Map(summaries.map((item) => [item.date, item]));

        pane.querySelectorAll(".calendar-day-card[data-date]").forEach((card) => {
            const date = card.getAttribute("data-date");
            const summary = summaryMap.get(date);
            const loadScore = summary ? summary.loadScore : 0;
            card.setAttribute("data-load", String(loadScore));
            card.classList.remove("heat-none", "heat-low", "heat-med", "heat-high");
            card.classList.add(heatClassFor(loadScore));

            const workoutIcon = card.querySelector(".calendar-icon-workout");
            const nutritionIcon = card.querySelector(".calendar-icon-nutrition");
            const prIcon = card.querySelector(".calendar-icon-pr");
            const prCount = card.querySelector("[data-pr-count]");

            if (workoutIcon) workoutIcon.classList.toggle("hidden", !(summary && summary.hasWorkout));
            if (nutritionIcon) nutritionIcon.classList.toggle("hidden", !(summary && summary.hasNutrition));

            if (prIcon) {
                const count = summary ? summary.prHitCount : 0;
                prIcon.classList.toggle("hidden", count <= 0);
                if (prCount) {
                    prCount.textContent = count > 1 ? String(count) : "";
                    prCount.classList.toggle("hidden", count <= 1);
                }
            }
        });
    }

    async function fetchHeatmapSummary(start, end) {
        const cacheKey = `${start}|${end}`;
        if (heatmapCache.has(cacheKey)) return heatmapCache.get(cacheKey);

        const url = new URL("/api/calendar/summary", window.location.origin);
        url.searchParams.set("start", start);
        url.searchParams.set("end", end);
        const res = await fetch(url, { credentials: "same-origin" });
        if (!res.ok) throw new Error(`Failed to load calendar summary: ${res.status}`);
        const data = await res.json();
        heatmapCache.set(cacheKey, data);
        return data;
    }

    async function updateHeatmapForPane(pane) {
        if (!pane) return;
        const week = parseInt(pane.getAttribute("data-week") || "", 10);
        const weekYear = parseInt(pane.getAttribute("data-week-year") || "", 10);
        if (!Number.isFinite(week) || !Number.isFinite(weekYear)) return;

        const startDate = getIsoWeekStart(weekYear, week);
        const endDate = new Date(startDate);
        endDate.setDate(startDate.getDate() + 6);
        const start = formatIsoDate(startDate);
        const end = formatIsoDate(endDate);

        try {
            const summary = await fetchHeatmapSummary(start, end);
            applySummaryToPane(pane, summary);
        } catch (err) {
            console.error(err);
        }
    }

    function refreshHeatmaps() {
        const panes = [
            prevSlot.querySelector("[data-week-pane]"),
            currentSlot.querySelector("[data-week-pane]"),
            nextSlot.querySelector("[data-week-pane]")
        ].filter(Boolean);
        panes.forEach((pane) => {
            updateHeatmapForPane(pane);
        });
    }

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
        const url = new URL("/calendar/week-fragment", window.location.origin);
        url.searchParams.set("week", String(week));
        url.searchParams.set("weekYear", String(weekYear));
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
        attachDayCardNavigation(slot);
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
        refreshHeatmaps();
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

    slider.addEventListener("keydown", (e) => {
        if (e.key === "ArrowLeft") {
            e.preventDefault();
            go(-1);
        } else if (e.key === "ArrowRight") {
            e.preventDefault();
            go(1);
        }
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
        attachDayCardNavigation(currentSlot);
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
