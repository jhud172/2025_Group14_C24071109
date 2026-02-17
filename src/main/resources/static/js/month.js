(function initMonthView() {
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfParamMeta = document.querySelector('meta[name="_csrf_param"]');

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfParam = csrfParamMeta ? csrfParamMeta.content : null;

    let hoverTimer = null;
    let closeTimer = null;

    const preview = document.getElementById('preview-card');
    let lockPosition = false;

    let mouseX = 0;
    let mouseY = 0;

    function positionPreview(x, y) {
        const offset = 18;
        if (!preview) return;
        const rect = preview.getBoundingClientRect();

        let left = x + offset;
        let top = y + offset;

        if (left + rect.width > window.innerWidth) {
            left = x - rect.width - offset;
        }
        if (top + rect.height > window.innerHeight) {
            top = y - rect.height - offset;
        }

        preview.style.left = left + 'px';
        preview.style.top = top + 'px';
    }

    function showPreview() {
        if (!preview) return;
        preview.classList.remove('hidden');
        preview.classList.remove('opacity-0');
        requestAnimationFrame(() => preview.classList.add('opacity-100'));
        lockPosition = true;
        positionPreview(mouseX, mouseY);
    }

    function hidePreview() {
        if (!preview) return;
        preview.classList.remove('opacity-100');
        preview.classList.add('opacity-0');

        setTimeout(() => {
            preview.classList.add('hidden');
            lockPosition = false;
        }, 180);
    }

    document.addEventListener('mousemove', (e) => {
        mouseX = e.clientX;
        mouseY = e.clientY;
        if (!lockPosition) {
            positionPreview(mouseX, mouseY);
        }
    });

    function attachPreviewHandlers(root) {
        if (!preview || !csrfToken || !csrfParam || !root) return;

        root.querySelectorAll('.calendar-item').forEach((item) => {
            item.addEventListener('mouseenter', () => {
                clearTimeout(closeTimer);

                hoverTimer = setTimeout(() => {
                    const type = item.dataset.type;
                    const id = item.dataset.id;
                    const completed = item.dataset.completed;

                    let html = `
                        <p class="mb-1 text-lg font-semibold text-slate-100">${item.dataset.title}</p>
                        <p class="text-xs uppercase tracking-widest text-slate-400">${type === 'occurrence' ? 'Schedule' : 'Task'}</p>
                        <p class="mt-2 text-sm text-slate-300">Time: ${item.dataset.time ?? '—'}</p>
                        <p class="mb-4 text-sm text-slate-400">${item.dataset.notes || 'No description'}</p>
                    `;

                    if (completed === 'true') {
                        html += `
                            <div class="mt-3 flex items-center gap-2 font-semibold text-emerald-300">
                                <span class="text-xl">✓</span> Completed
                            </div>
                        `;
                    } else if (type === 'exercise') {
                        html += `
                            <a href="/exercise-log/add-calendar?taskId=${id}"
                                class="mt-3 block rounded-lg bg-white px-3 py-2 text-center text-sm font-semibold text-slate-900 shadow-sm hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-white/40">
                                Complete Exercise Log
                            </a>
                        `;
                    } else if (type === 'occurrence') {
                        html += `
                            <a href="/exercise-log/add-occurrence?occId=${id}"
                                class="mt-3 block rounded-lg bg-white px-3 py-2 text-center text-sm font-semibold text-slate-900 shadow-sm hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-white/40">
                                Complete Scheduled Exercise
                            </a>
                        `;
                    } else if (type === 'task') {
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
                    showPreview();
                }, 600);
            });

            item.addEventListener('mouseleave', () => {
                clearTimeout(hoverTimer);
                closeTimer = setTimeout(() => {
                    if (!preview.matches(':hover')) hidePreview();
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

    if (preview) {
        preview.addEventListener('mouseenter', () => clearTimeout(closeTimer));
        preview.addEventListener('mouseleave', () => {
            closeTimer = setTimeout(() => hidePreview(), 250);
        });
    }

    // --- Sliding month navigation ---
    const slider = document.getElementById('month-slider');
    const track = document.getElementById('month-slider-track');
    const prevLink = document.getElementById('month-prev');
    const nextLink = document.getElementById('month-next');
    const currentSlot = track?.querySelector('[data-month-pane-slot="current"]');
    const prevSlot = track?.querySelector('[data-month-pane-slot="prev"]');
    const nextSlot = track?.querySelector('[data-month-pane-slot="next"]');

    const hasSlider = slider && track && prevLink && nextLink && currentSlot && prevSlot && nextSlot;
    if (!hasSlider) {
        // No slider wrapper (or missing controls) - keep legacy navigation.
        attachPreviewHandlers(document);
        attachDayCardNavigation(document);
        return;
    }

    const prefersReducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const heatmapCache = new Map();

    function formatDate(year, month, day) {
        const mm = String(month).padStart(2, '0');
        const dd = String(day).padStart(2, '0');
        return `${year}-${mm}-${dd}`;
    }

    function heatClassFor(loadScore) {
        if (loadScore <= 0) return 'heat-none';
        if (loadScore <= 3) return 'heat-low';
        if (loadScore <= 7) return 'heat-med';
        return 'heat-high';
    }

    function applySummaryToPane(pane, summaries) {
        if (!pane) return;
        const summaryMap = new Map(summaries.map((item) => [item.date, item]));

        pane.querySelectorAll('.calendar-day-card[data-date]').forEach((card) => {
            const date = card.getAttribute('data-date');
            const summary = summaryMap.get(date);
            const loadScore = summary ? summary.loadScore : 0;
            card.setAttribute('data-load', String(loadScore));
            card.classList.remove('heat-none', 'heat-low', 'heat-med', 'heat-high');
            card.classList.add(heatClassFor(loadScore));

            const workoutIcon = card.querySelector('.calendar-icon-workout');
            const nutritionIcon = card.querySelector('.calendar-icon-nutrition');
            const prIcon = card.querySelector('.calendar-icon-pr');
            const prCount = card.querySelector('[data-pr-count]');

            if (workoutIcon) workoutIcon.classList.toggle('hidden', !(summary && summary.hasWorkout));
            if (nutritionIcon) nutritionIcon.classList.toggle('hidden', !(summary && summary.hasNutrition));

            if (prIcon) {
                const count = summary ? summary.prHitCount : 0;
                prIcon.classList.toggle('hidden', count <= 0);
                if (prCount) {
                    prCount.textContent = count > 1 ? String(count) : '';
                    prCount.classList.toggle('hidden', count <= 1);
                }
            }
        });
    }

    async function fetchHeatmapSummary(start, end) {
        const cacheKey = `${start}|${end}`;
        if (heatmapCache.has(cacheKey)) return heatmapCache.get(cacheKey);

        const url = new URL('/api/calendar/summary', window.location.origin);
        url.searchParams.set('start', start);
        url.searchParams.set('end', end);
        const res = await fetch(url, { credentials: 'same-origin' });
        if (!res.ok) throw new Error(`Failed to load calendar summary: ${res.status}`);
        const data = await res.json();
        heatmapCache.set(cacheKey, data);
        return data;
    }

    async function updateHeatmapForPane(pane) {
        if (!pane) return;
        const year = parseInt(pane.getAttribute('data-year') || '', 10);
        const month = parseInt(pane.getAttribute('data-month') || '', 10);
        if (!Number.isFinite(year) || !Number.isFinite(month)) return;

        const lastDay = new Date(year, month, 0).getDate();
        const start = formatDate(year, month, 1);
        const end = formatDate(year, month, lastDay);
        try {
            const summary = await fetchHeatmapSummary(start, end);
            applySummaryToPane(pane, summary);
        } catch (err) {
            console.error(err);
        }
    }

    function refreshHeatmaps() {
        const panes = [
            prevSlot.querySelector('[data-month-pane]'),
            currentSlot.querySelector('[data-month-pane]'),
            nextSlot.querySelector('[data-month-pane]')
        ].filter(Boolean);
        panes.forEach((pane) => {
            updateHeatmapForPane(pane);
        });
    }

    function ymKey(year, month) {
        return `${year}-${String(month).padStart(2, '0')}`;
    }

    function addMonths(year, month, delta) {
        const base = new Date(year, month - 1, 1);
        base.setMonth(base.getMonth() + delta);
        return { year: base.getFullYear(), month: base.getMonth() + 1 };
    }

    function parseMonthYearFromHref(href) {
        try {
            const url = new URL(href, window.location.origin);
            const month = parseInt(url.searchParams.get('month') || '', 10);
            const year = parseInt(url.searchParams.get('year') || '', 10);
            if (!Number.isFinite(month) || !Number.isFinite(year)) return null;
            return { month, year, url };
        } catch {
            return null;
        }
    }

    function monthPaneUrl(year, month) {
        const url = new URL('/calendar/month-fragment', window.location.origin);
        url.searchParams.set('month', String(month));
        url.searchParams.set('year', String(year));
        return url.toString();
    }

    function parsePaneHtml(html) {
        const template = document.createElement('template');
        template.innerHTML = html.trim();
        return template.content.firstElementChild;
    }
        function extractMonthPane(html) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            return doc.querySelector('[data-month-pane]');
        }

    const cache = new Map();
    let currentYear = parseInt(slider.dataset.year || '', 10);
    let currentMonth = parseInt(slider.dataset.month || '', 10);
    if (!Number.isFinite(currentYear) || !Number.isFinite(currentMonth)) {
        currentYear = new Date().getFullYear();
        currentMonth = new Date().getMonth() + 1;
    }

    let isAnimating = false;

    async function fetchPane(year, month) {
        const url = monthPaneUrl(year, month);
        const res = await fetch(url, {
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            credentials: 'same-origin'
        });
        if (!res.ok) throw new Error(`Failed to load month pane: ${res.status}`);
        return await res.text();
    }

    async function ensureCached(year, month) {
        const key = ymKey(year, month);
        if (cache.has(key)) return;
        const html = await fetchPane(year, month);
        cache.set(key, html);
    }

    function evictOutsideWindow(centerYear, centerMonth) {
        const keep = new Set();
        for (let d = -2; d <= 2; d++) {
            const t = addMonths(centerYear, centerMonth, d);
            keep.add(ymKey(t.year, t.month));
        }
        for (const key of Array.from(cache.keys())) {
            if (!keep.has(key)) cache.delete(key);
        }
    }

    function setSlotFromCache(slot, year, month) {
        const key = ymKey(year, month);
        const html = cache.get(key);
        if (!html) return false;
        const paneEl = extractMonthPane(html);
        if (!paneEl) {
            console.error('Month pane missing in fragment response for', key);
            return false;
        }
        slot.replaceChildren(paneEl.cloneNode(true));
        attachPreviewHandlers(slot);
        attachDayCardNavigation(slot);
        return true;
    }

    const monthNameEl = document.getElementById('month-name');
    const monthYearEl = document.getElementById('month-year');
    const monthRedirectInput = document.getElementById('month-redirect');

    function updateMonthHeader() {
        if (monthNameEl) {
            monthNameEl.textContent = new Date(currentYear, currentMonth - 1, 1).toLocaleString('en-GB', { month: 'long' });
        }
        if (monthYearEl) {
            monthYearEl.textContent = String(currentYear);
        }
        if (monthRedirectInput) {
            monthRedirectInput.value = `/calendar?view=month&month=${currentMonth}&year=${currentYear}`;
        }
    }

    function updateNavHrefs() {
        const prev = addMonths(currentYear, currentMonth, -1);
        const next = addMonths(currentYear, currentMonth, 1);
        prevLink.href = `/calendar?view=month&month=${prev.month}&year=${prev.year}`;
        nextLink.href = `/calendar?view=month&month=${next.month}&year=${next.year}`;
    }

    async function warmCache(centerYear, centerMonth) {
        const targets = [];
        for (let d = -2; d <= 2; d++) {
            const t = addMonths(centerYear, centerMonth, d);
            targets.push(ensureCached(t.year, t.month));
        }
        await Promise.allSettled(targets);
        evictOutsideWindow(centerYear, centerMonth);
    }

    async function renderAdjacentPanes() {
        const prev = addMonths(currentYear, currentMonth, -1);
        const next = addMonths(currentYear, currentMonth, 1);
        setSlotFromCache(prevSlot, prev.year, prev.month);
        setSlotFromCache(currentSlot, currentYear, currentMonth);
        setSlotFromCache(nextSlot, next.year, next.month);
        updateNavHrefs();
        updateMonthHeader();
        slider.dataset.year = String(currentYear);
        slider.dataset.month = String(currentMonth);
        refreshHeatmaps();
    }

    function animateTo(offsetPercent) {
        return new Promise((resolve) => {
            if (prefersReducedMotion) {
                track.style.transition = '';
                track.style.transform = `translateX(${offsetPercent}%)`;
                resolve();
                return;
            }

            track.style.transition = 'transform 360ms cubic-bezier(0.22, 0.61, 0.36, 1)';
            const onEnd = (e) => {
                if (e.propertyName !== 'transform') return;
                track.removeEventListener('transitionend', onEnd);
                resolve();
            };
            track.addEventListener('transitionend', onEnd);
            requestAnimationFrame(() => {
                track.style.transform = `translateX(${offsetPercent}%)`;
            });
        });
    }

    function snapToCenter() {
        track.style.transition = '';
        track.style.transform = 'translateX(-33.333333%)';
    }

    async function go(delta) {
        if (isAnimating) return;
        isAnimating = true;
        prevLink.setAttribute('aria-disabled', 'true');
        nextLink.setAttribute('aria-disabled', 'true');
        prevLink.classList.add('pointer-events-none', 'opacity-60');
        nextLink.classList.add('pointer-events-none', 'opacity-60');

        const target = addMonths(currentYear, currentMonth, delta);
        await ensureCached(target.year, target.month);

        // Ensure visible neighbor is present
        await renderAdjacentPanes();

        // Slide: current is at -33.333%; next is -66.666%; prev is 0%
        await animateTo(delta > 0 ? -66.666666 : 0);

        currentYear = target.year;
        currentMonth = target.month;
        await warmCache(currentYear, currentMonth);
        await renderAdjacentPanes();
        snapToCenter();

        const url = new URL(window.location.href);
        url.searchParams.set('view', 'month');
        url.searchParams.set('month', String(currentMonth));
        url.searchParams.set('year', String(currentYear));
        window.history.pushState({ month: currentMonth, year: currentYear }, '', url.toString());

        prevLink.removeAttribute('aria-disabled');
        nextLink.removeAttribute('aria-disabled');
        prevLink.classList.remove('pointer-events-none', 'opacity-60');
        nextLink.classList.remove('pointer-events-none', 'opacity-60');
        isAnimating = false;
    }

    prevLink.addEventListener('click', (e) => {
        e.preventDefault();
        go(-1);
    });

    nextLink.addEventListener('click', (e) => {
        e.preventDefault();
        go(1);
    });

    let pointerStartX = null;
    let pointerActive = false;
    slider.addEventListener('pointerdown', (e) => {
        if (e.target.closest('a, button, input, textarea, select')) return;
        pointerStartX = e.clientX;
        pointerActive = true;
        slider.setPointerCapture?.(e.pointerId);
    });
    slider.addEventListener('pointerup', (e) => {
        if (!pointerActive || pointerStartX == null) return;
        const delta = e.clientX - pointerStartX;
        pointerActive = false;
        pointerStartX = null;
        if (Math.abs(delta) > 60) {
            go(delta < 0 ? 1 : -1);
        }
    });
    slider.addEventListener('pointercancel', () => {
        pointerActive = false;
        pointerStartX = null;
    });

    slider.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') {
            e.preventDefault();
            go(-1);
        } else if (e.key === 'ArrowRight') {
            e.preventDefault();
            go(1);
        }
    });

    window.addEventListener('popstate', async () => {
        const params = new URLSearchParams(window.location.search);
        const view = params.get('view') || 'month';
        if (view !== 'month') return;
        const month = parseInt(params.get('month') || '', 10);
        const year = parseInt(params.get('year') || '', 10);
        if (!Number.isFinite(month) || !Number.isFinite(year)) return;
        currentMonth = month;
        currentYear = year;
        await warmCache(currentYear, currentMonth);
        await renderAdjacentPanes();
        snapToCenter();
    });

    // Initial: cache current pane from DOM, then warm ±2 and render.
    const initialPane = currentSlot.querySelector('[data-month-pane]');
    if (initialPane) {
        const key = ymKey(currentYear, currentMonth);
        // Serialize initial pane to HTML for cache consistency
        cache.set(key, initialPane.outerHTML);
        attachPreviewHandlers(currentSlot);
        attachDayCardNavigation(currentSlot);
    }
    if (track) {
        track.style.willChange = 'transform';
    }
    updateMonthHeader();
    warmCache(currentYear, currentMonth).then(() => {
        renderAdjacentPanes();
        snapToCenter();
    });
})();