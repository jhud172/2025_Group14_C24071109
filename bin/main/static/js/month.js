(function initMonthView() {
    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfParamMeta = document.querySelector('meta[name="_csrf_param"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfParam = csrfParamMeta ? csrfParamMeta.content : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.content : 'X-CSRF-TOKEN';

    let hoverTimer = null;
    let closeTimer = null;

    const preview = document.getElementById('preview-card');
    const logModal = document.getElementById('calendar-log-modal');
    const logForm = document.getElementById('calendar-log-form');
    const logStatus = document.getElementById('calendar-log-status');
    const logTitle = document.getElementById('calendar-log-title');
    const logTaskFields = logModal?.querySelector('[data-log-task-fields]') || null;
    const logWorkoutFields = logModal?.querySelector('[data-log-workout-fields]') || null;
    const logWorkoutLink = document.getElementById('calendar-log-workout-link');
    const logActions = logModal?.querySelector('.calendar-log-actions') || null;
    let activeItem = null;

    function positionPreviewAtItem(item) {
        if (!preview || !item) return;
        const offset = 16;
        const itemRect = item.getBoundingClientRect();
        const previewRect = preview.getBoundingClientRect();

        let left = itemRect.right + offset;
        let top = itemRect.top;

        if (left + previewRect.width > window.innerWidth - offset) {
            left = itemRect.left - previewRect.width - offset;
        }
        left = Math.max(offset, left);

        const maxTop = window.innerHeight - previewRect.height - offset;
        top = Math.min(Math.max(itemRect.top, offset), Math.max(offset, maxTop));

        preview.style.left = left + 'px';
        preview.style.top = top + 'px';
    }

    function showPreview(item) {
        if (!preview) return;
        preview.classList.remove('hidden');
        preview.classList.remove('opacity-0');
        requestAnimationFrame(() => {
            positionPreviewAtItem(item);
            preview.classList.add('opacity-100');
        });
    }

    function hidePreview() {
        if (!preview) return;
        preview.classList.remove('opacity-100');
        preview.classList.add('opacity-0');

        setTimeout(() => {
            preview.classList.add('hidden');
            activeItem = null;
        }, 180);
    }

    function getCompletedMessage(item) {
        const messages = [
            'Completed. Nice work keeping the streak alive.',
            'Done and dusted. Keep the momentum going.',
            'Completed. Small wins add up fast.',
            'Checked off. You are on track today.'
        ];
        const seed = `${item.dataset.id || ''}${item.dataset.date || ''}`;
        let hash = 0;
        for (let i = 0; i < seed.length; i += 1) {
            hash = (hash * 31 + seed.charCodeAt(i)) % messages.length;
        }
        return messages[hash] || messages[0];
    }

    function buildPreviewHtml(item) {
        const type = item.dataset.type;
        const completed = item.dataset.completed;
        const title = item.dataset.title || 'Untitled';
        const time = item.dataset.time || '—';
        const notes = item.dataset.notes || 'No description';
        const isCompleted = completed === 'true';

        let html = `
            <p class="mb-1 text-lg font-semibold text-slate-900 dark:text-slate-100">${title}</p>
            <p class="text-xs uppercase tracking-widest text-slate-500 dark:text-slate-400">${type === 'occurrence' ? 'Schedule' : 'Task'}</p>
            <p class="mt-2 text-sm text-slate-700 dark:text-slate-300">Time: ${time}</p>
            <p class="mb-4 text-sm text-slate-500 dark:text-slate-400">${notes}</p>
        `;

        if (isCompleted) {
            html += `
                <div class="mt-3 rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm font-semibold text-emerald-700 dark:border-emerald-900/40 dark:bg-emerald-950/30 dark:text-emerald-200">
                    ✓ Completed
                </div>
                <p class="mt-2 text-xs text-slate-500 dark:text-slate-400">${getCompletedMessage(item)}</p>
            `;
        }

        if (type === 'task') {
            if (!isCompleted) {
                html += `
                    <div class="calendar-preview-actions">
                        <button type="button" class="calendar-preview-action" data-preview-action="complete">Mark completed</button>
                        <button type="button" class="calendar-preview-action calendar-preview-action--ghost" data-preview-action="log">Log completion</button>
                    </div>
                `;
            }
        } else if (type === 'occurrence') {
            const actionLabel = isCompleted ? 'Review workout log' : 'Complete workout';
            const actionType = isCompleted ? 'workout-review' : 'workout-log';
            html += `
                <div class="calendar-preview-actions">
                    <button type="button" class="calendar-preview-action" data-preview-action="${actionType}">${actionLabel}</button>
                </div>
            `;
        }

        return html;
    }

    let previewDelegated = false;
    function bindPreviewDelegation() {
        if (previewDelegated || !preview || !csrfToken || !csrfParam) return;
        previewDelegated = true;

        document.addEventListener('mouseover', (event) => {
            const item = event.target.closest('.calendar-item');
            if (!item) return;
            if (activeItem === item) return;
            clearTimeout(closeTimer);
            activeItem = item;
            hoverTimer = setTimeout(() => {
                preview.innerHTML = buildPreviewHtml(item);
                showPreview(item);
            }, 600);
        });

        document.addEventListener('mouseout', (event) => {
            const item = event.target.closest('.calendar-item');
            if (!item) return;
            const related = event.relatedTarget;
            if (related && (related.closest?.('.calendar-item') === item || related.closest?.('#preview-card'))) return;
            clearTimeout(hoverTimer);
            closeTimer = setTimeout(() => {
                if (!preview.matches(':hover')) hidePreview();
            }, 200);
        });
    }

    function attachPreviewHandlers() {
        bindPreviewDelegation();
    }

    function setLogStatus(message, isError = false) {
        if (!logStatus) return;
        logStatus.textContent = message || '';
        logStatus.classList.toggle('is-error', isError);
        logStatus.classList.toggle('is-visible', !!message);
    }

    function openLogModal(item, mode) {
        if (!logModal || !logForm) return;
        const isWorkout = mode === 'workout';
        if (logTaskFields) logTaskFields.classList.toggle('hidden', isWorkout);
        if (logWorkoutFields) logWorkoutFields.classList.toggle('hidden', !isWorkout);
        if (logActions) logActions.classList.toggle('hidden', isWorkout);
        logForm.dataset.mode = isWorkout ? 'workout' : 'task';

        const title = item.dataset.title || 'Task';
        if (logTitle) logTitle.textContent = isWorkout ? 'Complete workout' : `Log ${title}`;

        logForm.taskId.value = item.dataset.id || '';
        logForm.date.value = item.dataset.date || '';
        logForm.title.value = item.dataset.title || '';
        logForm.time.value = item.dataset.time || '';
        logForm.notes.value = item.dataset.notes || '';
        logForm.exercise.value = item.dataset.exercise || 'false';
        logForm.completed.value = item.dataset.completed || 'false';

        if (logWorkoutLink) {
            const occId = item.dataset.id;
            logWorkoutLink.href = occId ? `/exercise-log/add-occurrence?occId=${occId}` : '#';
        }

        setLogStatus('');
        logModal.classList.remove('hidden');
        logModal.setAttribute('aria-hidden', 'false');
        const focusTarget = isWorkout ? logWorkoutLink : logForm.querySelector('textarea, select, input');
        if (focusTarget) focusTarget.focus();
    }

    function closeLogModal() {
        if (!logModal) return;
        logModal.classList.add('hidden');
        logModal.setAttribute('aria-hidden', 'true');
        setLogStatus('');
    }

    function buildLogEntry(formData) {
        const summary = formData.get('summary') || '';
        const effort = formData.get('effort') || '';
        const mood = formData.get('mood') || '';
        const extra = formData.get('logNotes') || '';
        const stamped = new Date().toLocaleString();
        const parts = [`Completion log (${stamped})`];
        if (summary) parts.push(`Summary: ${summary}`);
        if (effort) parts.push(`Effort: ${effort}/5`);
        if (mood) parts.push(`Mood: ${mood}`);
        if (extra) parts.push(`Notes: ${extra}`);
        return parts.join('\n');
    }

    async function postForm(action, body) {
        const headers = {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'X-Requested-With': 'XMLHttpRequest'
        };
        if (csrfToken) headers[csrfHeader] = csrfToken;
        return fetch(action, {
            method: 'POST',
            headers,
            body: body.toString()
        });
    }

    function updateTaskCompletionUi(taskId, completed) {
        const items = document.querySelectorAll(
            `.calendar-item[data-type="task"][data-id="${taskId}"], .calendar-grouped-item[data-type="task"][data-id="${taskId}"]`
        );
        items.forEach((el) => {
            el.classList.toggle('completed', completed);
            el.dataset.completed = String(completed);
            if (el.classList.contains('calendar-item')) {
                let check = el.querySelector('.calendar-item-check');
                if (completed && !check) {
                    check = document.createElement('div');
                    check.className = 'calendar-item-check';
                    check.textContent = '✓';
                    el.appendChild(check);
                }
                if (!completed && check) {
                    check.remove();
                }
            }
        });
    }

    async function toggleTaskCompletion(item) {
        const taskId = item.dataset.id;
        const date = item.dataset.date;
        if (!taskId || !date) return;
        const nextCompleted = item.dataset.completed !== 'true';
        item.dataset.completed = String(nextCompleted);
        updateTaskCompletionUi(taskId, nextCompleted);
        if (preview && activeItem && activeItem.dataset.id === taskId) {
            preview.innerHTML = buildPreviewHtml(activeItem);
        }

        const body = new URLSearchParams();
        body.append('taskId', taskId);
        if (csrfParam && csrfToken) body.append(csrfParam, csrfToken);

        try {
            const res = await postForm(`/calendar/day/${date}/toggle-complete`, body);
            if (!res.ok) {
                throw new Error(`Toggle failed: ${res.status}`);
            }
        } catch (error) {
            console.error(error);
            const revert = !nextCompleted;
            item.dataset.completed = String(revert);
            updateTaskCompletionUi(taskId, revert);
            if (preview && activeItem && activeItem.dataset.id === taskId) {
                preview.innerHTML = buildPreviewHtml(activeItem);
            }
        }
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

    function handlePreviewAction(actionEl, event) {
        if (!actionEl || !activeItem) return;
        event.preventDefault();
        event.stopPropagation();
        const action = actionEl.getAttribute('data-preview-action');
        if (action === 'complete') {
            toggleTaskCompletion(activeItem).catch((error) => console.error(error));
        }
        if (action === 'log') {
            openLogModal(activeItem, 'task');
        }
        if (action === 'workout-log') {
            openLogModal(activeItem, 'workout');
        }
        if (action === 'workout-review') {
            window.location.href = '/exercise-log/list';
        }
    }

    preview?.addEventListener('click', (event) => {
        const actionEl = event.target.closest('[data-preview-action]');
        if (!actionEl) return;
        handlePreviewAction(actionEl, event);
    });

    document.addEventListener('click', (event) => {
        const actionEl = event.target.closest('[data-preview-action]');
        if (!actionEl) return;
        handlePreviewAction(actionEl, event);
    }, true);

    if (logModal) {
        const backdrop = logModal.querySelector('[data-log-backdrop]');
        const closeBtn = logModal.querySelector('[data-log-close]');
        const cancelBtn = logModal.querySelector('[data-log-cancel]');
        if (backdrop) backdrop.addEventListener('click', closeLogModal);
        if (closeBtn) closeBtn.addEventListener('click', closeLogModal);
        if (cancelBtn) cancelBtn.addEventListener('click', closeLogModal);
        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape' && !logModal.classList.contains('hidden')) {
                closeLogModal();
            }
        });
    }

    if (logForm) {
        logForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            if (!activeItem) return;
            if (logForm.dataset.mode === 'workout') return;

            const formData = new FormData(logForm);
            const taskId = formData.get('taskId');
            const date = formData.get('date');
            if (!taskId || !date) return;

            const logEntry = buildLogEntry(formData);
            const existingNotes = (formData.get('notes') || '').toString().trim();
            const combinedNotes = existingNotes ? `${existingNotes}\n\n${logEntry}` : logEntry;
            const title = formData.get('title') || '';
            const time = formData.get('time') || '';
            const exercise = formData.get('exercise') === 'true' ? 'true' : 'false';

            try {
                setLogStatus('Saving log...');
                const editBody = new URLSearchParams();
                editBody.append('title', String(title));
                editBody.append('time', String(time));
                editBody.append('notes', combinedNotes);
                editBody.append('exercise', exercise);
                if (csrfParam && csrfToken) editBody.append(csrfParam, csrfToken);
                await postForm(`/calendar/task/${taskId}/edit-inline`, editBody);

                if (activeItem.dataset.completed !== 'true') {
                    await toggleTaskCompletion(activeItem);
                }

                activeItem.dataset.notes = combinedNotes;
                if (preview) preview.innerHTML = buildPreviewHtml(activeItem);
                setLogStatus('Log saved.');
                setTimeout(closeLogModal, 600);
            } catch (error) {
                console.error(error);
                setLogStatus('Unable to save log. Please try again.', true);
            }
        });
    }

    window.addEventListener('resize', () => {
        if (!preview || preview.classList.contains('hidden') || !activeItem) return;
        positionPreviewAtItem(activeItem);
    });

    const monthPane = document.querySelector('[data-month-pane]');
    const paneYear = parseInt(monthPane?.getAttribute('data-year') || '', 10);
    const paneMonth = parseInt(monthPane?.getAttribute('data-month') || '', 10);
    const currentYear = Number.isFinite(paneYear) ? paneYear : new Date().getFullYear();
    const currentMonth = Number.isFinite(paneMonth) ? paneMonth : new Date().getMonth() + 1;
    const heatmapCache = new Map();

    const monthNameEl = document.getElementById('month-name');
    const monthYearEl = document.getElementById('month-year');
    const monthRedirectInput = document.getElementById('month-redirect');
    const jumpTodayBtn = document.getElementById('month-jump-today');
    const jumpDateInput = document.getElementById('month-jump-date');
    const jumpDateBtn = document.getElementById('month-jump-date-go');
    const jumpNextWorkoutBtn = document.getElementById('month-jump-next-workout');
    const jumpTaskInput = document.getElementById('month-jump-task');
    const jumpTaskBtn = document.getElementById('month-jump-task-go');
    const jumpStatusEl = document.getElementById('month-jump-status');
    const jumpControls = [jumpTodayBtn, jumpDateInput, jumpDateBtn, jumpNextWorkoutBtn, jumpTaskInput, jumpTaskBtn].filter(Boolean);
    let jumpActionInProgress = false;

    function setMonthHeader(year, month) {
        if (monthNameEl) {
            monthNameEl.textContent = new Date(year, month - 1, 1).toLocaleString('en-GB', { month: 'long' });
        }
        if (monthYearEl) {
            monthYearEl.textContent = String(year);
        }
        if (monthRedirectInput) {
            monthRedirectInput.value = `/calendar?view=month&month=${month}&year=${year}`;
        }
    }

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
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    }

    function parseIsoDateInput(value) {
        if (!value || !/^\d{4}-\d{2}-\d{2}$/.test(value)) return null;
        const [year, month, day] = value.split('-').map((part) => parseInt(part, 10));
        if (!Number.isFinite(year) || !Number.isFinite(month) || !Number.isFinite(day)) return null;
        return new Date(year, month - 1, day);
    }

    function getCurrentPane() {
        return monthPane;
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

    function findDateCardInCurrentPane(dateIso) {
        const pane = getCurrentPane();
        if (!pane) return null;
        return pane.querySelector(`.calendar-day-card[data-date="${dateIso}"]`);
    }

    function sortedCardsInPane(pane) {
        return Array.from(pane.querySelectorAll('.calendar-day-card[data-date]')).sort((a, b) => {
            return (a.dataset.date || '').localeCompare(b.dataset.date || '');
        });
    }

    function findNextWorkoutCard(pane, fromDateIso) {
        const cards = sortedCardsInPane(pane);
        return cards.find((card) => {
            const date = card.dataset.date || '';
            if (date < fromDateIso) return false;
            return !!card.querySelector('.calendar-item[data-type="occurrence"]');
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
        const targetYear = parsed.getFullYear();
        const targetMonth = parsed.getMonth() + 1;
        if (targetYear !== currentYear || targetMonth !== currentMonth) {
            const url = new URL('/calendar', window.location.origin);
            url.searchParams.set('view', 'month');
            url.searchParams.set('month', String(targetMonth));
            url.searchParams.set('year', String(targetYear));
            url.searchParams.set('jumpDate', dateIso);
            window.location.href = url.toString();
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
        const pane = getCurrentPane();
        if (!pane) return;
        const fromDateIso = toLocalIsoDate(new Date());
        const targetCard = findNextWorkoutCard(pane, fromDateIso);
        if (!targetCard) {
            setJumpStatus('No upcoming workout found in this month.', 'error');
            return;
        }
        highlightJumpCard(targetCard);
        setJumpStatus('Jumped to next workout.', 'success');
    }

    async function jumpToTask() {
        const query = (jumpTaskInput?.value || '').trim().toLowerCase();
        if (!query) {
            setJumpStatus('Please enter a task name to search.', 'error');
            return;
        }
        const pane = getCurrentPane();
        if (!pane) return;
        const fromDateIso = toLocalIsoDate(new Date());
        const targetCard = findTaskCardByQuery(pane, query, fromDateIso);
        if (!targetCard) {
            setJumpStatus('No matching task found in this month.', 'error');
            return;
        }
        highlightJumpCard(targetCard);
        setJumpStatus('Jumped to matching task.', 'success');
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

    attachPreviewHandlers(document);
    attachDayCardNavigation(document);
    setMonthHeader(currentYear, currentMonth);
    updateHeatmapForPane(monthPane);

    const urlParams = new URLSearchParams(window.location.search);
    const jumpDateFromUrl = urlParams.get('jumpDate');
    if (jumpDateFromUrl) {
        runJumpAction(() => jumpToDate(jumpDateFromUrl));
    }
})();
