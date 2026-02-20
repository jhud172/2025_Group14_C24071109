document.addEventListener("DOMContentLoaded", () => {
    const scheduleButton = document.getElementById("scheduleDrawerButton");
    const scheduleDrawer = document.getElementById("scheduleDrawer");
    const scheduleSearch = document.getElementById("schedule-search");
    const scheduleList = document.getElementById("schedule-list");

    const contextView = document.getElementById('schedule-context-view');
    const contextRange = document.getElementById('schedule-context-range');
    const contextSelectedDay = document.getElementById('schedule-context-selected-day');
    const contextExisting = document.getElementById('schedule-context-existing');

    const scopeButtons = Array.from(scheduleDrawer?.querySelectorAll('[data-schedule-scope]') || []);
    const customScopeRow = scheduleDrawer?.querySelector('[data-scope-custom]') || null;
    const customStartInput = document.getElementById('schedule-scope-start');
    const customEndInput = document.getElementById('schedule-scope-end');

    const previewToggle = document.getElementById('schedule-preview-toggle');
    const previewClearButton = document.getElementById('schedule-preview-clear');
    const applyScopeSelect = document.getElementById('schedule-apply-scope');
    const applyWeeksInput = document.getElementById('schedule-apply-weeks');
    const strategyButtons = Array.from(scheduleDrawer?.querySelectorAll('[data-deploy-strategy]') || []);
    const impactSummary = document.getElementById('schedule-impact-summary');
    const impactReviewButton = document.getElementById('schedule-impact-review');
    const impactApplyButton = document.getElementById('schedule-impact-apply');
    const simEnableToggle = document.getElementById('schedule-sim-enable');
    const simWeeksInput = document.getElementById('schedule-sim-weeks');
    const simRunButton = document.getElementById('schedule-sim-run');

    const filterType = document.getElementById('schedule-filter-type');
    const filterStatus = document.getElementById('schedule-filter-status');
    const filterFrequency = document.getElementById('schedule-filter-frequency');
    const filterFavourites = document.getElementById('schedule-filter-favourites');
    const filterRecent = document.getElementById('schedule-filter-recent');
    const sortSelect = document.getElementById('schedule-sort');

    const pinnedList = document.getElementById('schedule-pinned-list');
    const recentList = document.getElementById('schedule-recent-list');

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const csrfToken = csrfTokenMeta?.content;
    const csrfHeader = csrfHeaderMeta?.content || 'X-CSRF-TOKEN';

    const cardById = new Map();
    const scheduleCards = Array.from(scheduleDrawer?.querySelectorAll('.calendar-schedule-card[data-schedule-id], .calendar-schedule-card-compact[data-schedule-id]') || []);
    scheduleCards.forEach((card) => {
        const id = card.getAttribute('data-schedule-id');
        if (id) cardById.set(id, card);
    });

    let selectedDay = null;
    let contextScope = 'visible';
    let selectedScheduleId = null;
    let selectedStrategy = 'merge';
    let previewEnabled = true;
    const previewCache = new Map();
    const favouriteStorageKey = 'calendar.scheduleDrawer.favourites';
    const pinnedStorageKey = 'calendar.scheduleDrawer.pinned';
    const recentStorageKey = 'calendar.scheduleDrawer.recent';
    let draggedScheduleId = null;
    let lastFocusedElement = null;
    let undoTimeout = null;
    let searchFilterTimer = null;
    const scheduleMetadataCache = new Map();

    function getCurrentPane() {
        return document.querySelector('[data-month-pane-slot="current"] [data-month-pane]')
            || document.querySelector('[data-week-pane-slot="current"] [data-week-pane]')
            || document;
    }

    function getVisibleDateBounds() {
        const pane = getCurrentPane();
        const dates = Array.from(pane.querySelectorAll('.calendar-day-card[data-date]'))
            .map((card) => card.getAttribute('data-date'))
            .filter(Boolean)
            .sort();

        if (!dates.length) return null;
        return { start: dates[0], end: dates[dates.length - 1] };
    }

    function parseDate(dateIso) {
        if (!dateIso) return null;
        const date = new Date(`${dateIso}T00:00:00`);
        if (Number.isNaN(date.getTime())) return null;
        return date;
    }

    function getIsoDayNumber(dateIso) {
        const date = parseDate(dateIso);
        if (!date) return null;
        const jsDay = date.getDay();
        return jsDay === 0 ? 7 : jsDay;
    }

    function formatDateLabel(dateIso) {
        const date = parseDate(dateIso);
        if (!date) return dateIso || '—';
        return date.toLocaleDateString(undefined, { day: '2-digit', month: 'short', year: 'numeric' });
    }

    function clearPreview() {
        document.querySelectorAll('.calendar-preview-ghost').forEach((entry) => entry.remove());
        document.querySelectorAll('.calendar-day-card--preview-conflict').forEach((card) => {
            card.classList.remove('calendar-day-card--preview-conflict');
        });
        document.querySelectorAll('.calendar-day-card--simulated').forEach((card) => {
            card.classList.remove('calendar-day-card--simulated');
        });
    }

    function getListItemForSchedule(scheduleId) {
        return scheduleList?.querySelector(`li[data-schedule-id="${scheduleId}"]`) || null;
    }

    function loadSet(storageKey) {
        try {
            return new Set(JSON.parse(localStorage.getItem(storageKey) || '[]'));
        } catch {
            return new Set();
        }
    }

    function saveSet(storageKey, values) {
        localStorage.setItem(storageKey, JSON.stringify(Array.from(values)));
    }

    function loadRecent() {
        try {
            const parsed = JSON.parse(localStorage.getItem(recentStorageKey) || '[]');
            return Array.isArray(parsed) ? parsed : [];
        } catch {
            return [];
        }
    }

    function saveRecent(values) {
        localStorage.setItem(recentStorageKey, JSON.stringify(values.slice(0, 12)));
    }

    function trackRecent(scheduleId) {
        if (!scheduleId) return;
        const next = loadRecent().filter((id) => id !== scheduleId);
        next.unshift(scheduleId);
        saveRecent(next);
    }

    function typeLabelToFilterValue(typeRaw) {
        const value = (typeRaw || '').toLowerCase();
        if (value.includes('custom')) return 'custom';
        if (value.includes('rotation')) return 'rotational';
        return 'weekly';
    }

    function applyFiltersAndSort() {
        if (!scheduleList) return;
        const lis = Array.from(scheduleList.querySelectorAll('li[data-schedule-id]'));
        const favourites = loadSet(favouriteStorageKey);
        const recent = new Set(loadRecent());

        const selectedType = filterType?.value || 'all';
        const selectedStatus = filterStatus?.value || 'all';
        const selectedFrequency = filterFrequency?.value || 'all';
        const onlyFavourites = !!filterFavourites?.checked;
        const onlyRecent = !!filterRecent?.checked;

        lis.forEach((li) => {
            const scheduleId = li.getAttribute('data-schedule-id') || '';
            const card = li.querySelector('.calendar-schedule-card, .calendar-schedule-card-compact');
            const status = (li.getAttribute('data-schedule-status') || '').toLowerCase();
            const type = typeLabelToFilterValue(li.getAttribute('data-schedule-type'));
            const frequencyBucket = card?.getAttribute('data-frequency-bucket') || 'all';

            const matchType = selectedType === 'all' || selectedType === type;
            const matchStatus = selectedStatus === 'all' || selectedStatus === status;
            const matchFrequency = selectedFrequency === 'all' || selectedFrequency === frequencyBucket;
            const matchFavourite = !onlyFavourites || favourites.has(scheduleId);
            const matchRecent = !onlyRecent || recent.has(scheduleId);

            li.style.display = (matchType && matchStatus && matchFrequency && matchFavourite && matchRecent) ? '' : 'none';
        });

        const sortMode = sortSelect?.value || 'recentlyApplied';
        const sorted = lis.slice().sort((a, b) => {
            if (sortMode === 'mostUsed') {
                return Number(b.getAttribute('data-schedule-apply-count') || 0) - Number(a.getAttribute('data-schedule-apply-count') || 0);
            }

            if (sortMode === 'recentlyCreated') {
                return Number(b.getAttribute('data-schedule-id') || 0) - Number(a.getAttribute('data-schedule-id') || 0);
            }

            const aDate = a.getAttribute('data-schedule-latest-applied') || '';
            const bDate = b.getAttribute('data-schedule-latest-applied') || '';
            return bDate.localeCompare(aDate);
        });

        sorted.forEach((li) => scheduleList.appendChild(li));
    }

    function renderQuickAccessChips() {
        if (!pinnedList || !recentList) return;
        const pinned = Array.from(loadSet(pinnedStorageKey));
        const recent = loadRecent();

        const renderInto = (container, ids, emptyText) => {
            container.innerHTML = '';
            const availableIds = ids.filter((id) => !!getListItemForSchedule(id));
            if (!availableIds.length) {
                const empty = document.createElement('span');
                empty.className = 'calendar-quick-chip-empty';
                empty.textContent = emptyText;
                container.appendChild(empty);
                return;
            }

            availableIds.slice(0, 8).forEach((id) => {
                const li = getListItemForSchedule(id);
                if (!li) return;
                const name = li.getAttribute('data-schedule-name') || `Schedule ${id}`;
                const chip = document.createElement('button');
                chip.type = 'button';
                chip.className = 'calendar-quick-chip';
                chip.textContent = name;
                chip.addEventListener('click', () => {
                    activateSchedule(id, true).catch((error) => console.error(error));
                });
                container.appendChild(chip);
            });
        };

        renderInto(pinnedList, pinned, 'No pinned schedules');
        renderInto(recentList, recent, 'No recent schedules');
    }

    function applyPinnedUi() {
        const pinned = loadSet(pinnedStorageKey);
        scheduleDrawer?.querySelectorAll('[data-pin-toggle]').forEach((button) => {
            const id = button.getAttribute('data-schedule-id') || '';
            const isPinned = pinned.has(id);
            button.classList.toggle('is-pinned', isPinned);
            button.setAttribute('aria-pressed', String(isPinned));
        });
    }

    function togglePin(scheduleId) {
        if (!scheduleId) return;
        const pinned = loadSet(pinnedStorageKey);
        if (pinned.has(scheduleId)) pinned.delete(scheduleId);
        else pinned.add(scheduleId);
        saveSet(pinnedStorageKey, pinned);
        applyPinnedUi();
        renderQuickAccessChips();
    }

    function clearDropTargets() {
        document.querySelectorAll('.calendar-day-card--drop-target, .calendar-day-card--drop-hover').forEach((card) => {
            card.classList.remove('calendar-day-card--drop-target', 'calendar-day-card--drop-hover');
        });
    }

    function showDropTargets() {
        getCurrentPane().querySelectorAll('.calendar-day-card[data-date]').forEach((card) => {
            card.classList.add('calendar-day-card--drop-target');
        });
    }

    function bindDragToApply() {
        const handles = scheduleDrawer?.querySelectorAll('[data-schedule-drag]') || [];
        handles.forEach((handle) => {
            handle.addEventListener('dragstart', (event) => {
                const scheduleId = handle.getAttribute('data-schedule-id');
                if (!scheduleId) return;
                draggedScheduleId = scheduleId;

                event.dataTransfer.effectAllowed = 'copy';
                event.dataTransfer.setData('text/schedule-id', scheduleId);

                const dragBadge = document.createElement('div');
                dragBadge.className = 'calendar-drag-badge';
                dragBadge.textContent = 'Deploy schedule';
                document.body.appendChild(dragBadge);
                event.dataTransfer.setDragImage(dragBadge, 40, 18);
                setTimeout(() => dragBadge.remove(), 0);

                showDropTargets();
            });

            handle.addEventListener('dragend', () => {
                draggedScheduleId = null;
                clearDropTargets();
            });
        });

        document.addEventListener('dragover', (event) => {
            if (!draggedScheduleId || !scheduleDrawer?.classList.contains('open')) return;
            const dayCard = event.target.closest('.calendar-day-card[data-date]');
            if (!dayCard) return;
            event.preventDefault();
            dayCard.classList.add('calendar-day-card--drop-hover');
        });

        document.addEventListener('dragleave', (event) => {
            const dayCard = event.target.closest?.('.calendar-day-card[data-date]');
            if (!dayCard) return;
            dayCard.classList.remove('calendar-day-card--drop-hover');
        });

        document.addEventListener('drop', (event) => {
            if (!draggedScheduleId || !scheduleDrawer?.classList.contains('open')) return;
            const dayCard = event.target.closest('.calendar-day-card[data-date]');
            if (!dayCard) return;
            event.preventDefault();
            event.stopPropagation();

            selectedDay = dayCard.getAttribute('data-date');
            const choice = window.prompt('Drop scope: type week, forward, or weeks', 'forward');
            const scopeChoice = (choice || 'forward').trim().toLowerCase();

            if (applyScopeSelect) {
                applyScopeSelect.value = ['week', 'forward', 'weeks'].includes(scopeChoice) ? scopeChoice : 'forward';
            }

            activateSchedule(draggedScheduleId, true)
                .then(() => requestImpact())
                .then(() => {
                    const confirmApply = window.confirm(`Deploy schedule from ${formatDateLabel(selectedDay)} using ${applyScopeSelect?.value || 'forward'} scope?`);
                    if (confirmApply) {
                        return applyDeployment();
                    }
                    return null;
                })
                .catch((error) => {
                    console.error(error);
                    if (impactSummary) {
                        impactSummary.textContent = 'Drop deployment failed. Please retry from controls.';
                    }
                })
                .finally(() => {
                    clearDropTargets();
                    draggedScheduleId = null;
                });
        }, true);
    }

    function clearConflictHighlights() {
        document.querySelectorAll('.calendar-day-card--deploy-conflict').forEach((card) => {
            card.classList.remove('calendar-day-card--deploy-conflict');
        });
    }

    function updateScheduleSelectionUi() {
        scheduleCards.forEach((card) => {
            const isActive = card.getAttribute('data-schedule-id') === selectedScheduleId;
            card.classList.toggle('is-selected', isActive);
        });

        const enabled = !!selectedScheduleId;
        if (impactReviewButton) impactReviewButton.disabled = !enabled;
        if (impactApplyButton) impactApplyButton.disabled = true;
        if (simRunButton) simRunButton.disabled = !enabled || !simEnableToggle?.checked;
    }

    function renderHealthIndicators(card, metadata) {
        const container = card.querySelector('[data-health-indicators]');
        if (!container) return;
        container.innerHTML = '';

        const warnings = Array.isArray(metadata?.healthWarnings) ? metadata.healthWarnings : [];
        if (!warnings.length) return;

        warnings.forEach((warning) => {
            const badge = document.createElement('span');
            badge.className = 'calendar-health-warning';
            badge.title = warning.description || warning.label || 'Schedule warning';
            badge.textContent = warning.label || 'Warning';
            container.appendChild(badge);
        });
    }

    async function fetchSchedulePreview(scheduleId) {
        if (previewCache.has(scheduleId)) return previewCache.get(scheduleId);
        const res = await fetch(`/api/schedules/${scheduleId}/preview`, { credentials: 'same-origin' });
        if (!res.ok) throw new Error('Unable to load schedule preview');
        const data = await res.json();
        previewCache.set(scheduleId, data);
        return data;
    }

    function renderPreview(data) {
        clearPreview();
        if (!previewEnabled || !data || !Array.isArray(data.entries)) return;

        const groupedEntries = new Map();
        data.entries.forEach((entry) => {
            const day = Number(entry.dayOfWeek);
            if (!Number.isFinite(day)) return;
            if (!groupedEntries.has(day)) groupedEntries.set(day, []);

            const name = entry.exercise?.name || entry.customExercise?.name || 'Scheduled workout';
            groupedEntries.get(day).push(name);
        });

        getCurrentPane().querySelectorAll('.calendar-day-card[data-date]').forEach((card) => {
            const dayNumber = getIsoDayNumber(card.getAttribute('data-date'));
            const labels = groupedEntries.get(dayNumber) || [];
            if (!labels.length) return;

            const content = card.querySelector('.calendar-day-content');
            if (!content) return;

            const ghost = document.createElement('div');
            ghost.className = 'calendar-preview-ghost';
            const visibleLabels = labels.slice(0, 2).join(' • ');
            ghost.textContent = labels.length > 2 ? `${visibleLabels} +${labels.length - 2}` : visibleLabels;
            content.appendChild(ghost);

            const hasConflict = card.querySelector('.calendar-item[data-type="workout"], .calendar-item[data-type="occurrence"]') != null;
            if (hasConflict) {
                card.classList.add('calendar-day-card--preview-conflict');
            }
        });
    }

    function renderProjectedLayout(scheduleId, impact) {
        if (!impact || !scheduleId) return;
        const metadata = scheduleMetadataCache.get(scheduleId);
        if (!metadata) return;

        const activeDays = Array.isArray(metadata.activeDayIndexes) ? metadata.activeDayIndexes : [];
        if (!activeDays.length) return;

        const start = impact.windowStart;
        const end = impact.windowEnd;
        if (!start || !end) return;

        clearPreview();
        clearConflictHighlights();

        const startDate = parseDate(start);
        const endDate = parseDate(end);
        if (!startDate || !endDate) return;

        const rotationMode = (getListItemForSchedule(scheduleId)?.getAttribute('data-schedule-rotation') || 'weekly_repeat').replaceAll('_', ' ');

        getCurrentPane().querySelectorAll('.calendar-day-card[data-date]').forEach((card) => {
            const dateIso = card.getAttribute('data-date');
            const dateObj = parseDate(dateIso);
            if (!dateObj) return;
            if (dateObj < startDate || dateObj > endDate) return;

            const dayNumber = getIsoDayNumber(dateIso);
            if (!activeDays.includes(dayNumber)) return;

            const content = card.querySelector('.calendar-day-content');
            if (!content) return;

            const ghost = document.createElement('div');
            ghost.className = 'calendar-preview-ghost calendar-preview-ghost--sim';
            ghost.textContent = `Projected • ${rotationMode}`;
            content.appendChild(ghost);
            card.classList.add('calendar-day-card--simulated');
        });

        const conflictDates = Array.isArray(impact.conflictDates) ? impact.conflictDates : [];
        conflictDates.forEach((dateIso) => {
            const card = document.querySelector(`.calendar-day-card[data-date="${dateIso}"]`);
            if (card) card.classList.add('calendar-day-card--deploy-conflict');
        });
    }

    async function activateSchedule(scheduleId, withPreview = true) {
        if (!scheduleId) return;
        selectedScheduleId = scheduleId;
        trackRecent(scheduleId);
        updateScheduleSelectionUi();
        renderQuickAccessChips();
        if (!withPreview) return;

        try {
            const preview = await fetchSchedulePreview(scheduleId);
            renderPreview(preview);
        } catch (error) {
            console.error(error);
        }
    }

    function readApplyScope() {
        return applyScopeSelect?.value || 'forward';
    }

    function readWeeks() {
        const raw = Number(applyWeeksInput?.value || 4);
        if (!Number.isFinite(raw)) return 4;
        return Math.max(1, Math.min(52, Math.trunc(raw)));
    }

    function buildDeploymentPayload() {
        const bounds = getVisibleDateBounds();
        const scope = readApplyScope();
        const startDate = selectedDay || customStartInput?.value || bounds?.start;

        return {
            scope,
            selectedDate: selectedDay,
            startDate,
            weeks: readWeeks(),
            strategy: selectedStrategy
        };
    }

    function showApplyToast(message, undoAction, undoSeconds = 30) {
        const existing = document.getElementById('schedule-apply-toast');
        if (existing) existing.remove();

        const toast = document.createElement('div');
        toast.id = 'schedule-apply-toast';
        toast.className = 'calendar-apply-toast';
        toast.innerHTML = `
            <div class="calendar-apply-toast-text">${message}</div>
            <button type="button" class="calendar-apply-toast-undo">Undo</button>
        `;

        const undoButton = toast.querySelector('.calendar-apply-toast-undo');
        undoButton?.addEventListener('click', () => {
            if (undoTimeout) {
                window.clearTimeout(undoTimeout);
                undoTimeout = null;
            }
            toast.remove();
            undoAction?.();
        });

        document.body.appendChild(toast);
        undoTimeout = window.setTimeout(() => {
            toast.remove();
            window.location.reload();
        }, Math.max(5, undoSeconds) * 1000);
    }

    function ensureSelectedDayInVisibleRange() {
        const bounds = getVisibleDateBounds();
        if (!bounds) return;
        if (!selectedDay || selectedDay < bounds.start || selectedDay > bounds.end) {
            selectedDay = bounds.start;
        }
        if (customStartInput) {
            customStartInput.value = selectedDay;
        }
    }

    function renderImpact(impact) {
        if (!impactSummary || !impact || !impact.summary) return;
        const summary = impact.summary;
        const conflictDates = Array.isArray(impact.conflictDates) ? impact.conflictDates : [];
        const conflictLine = conflictDates.length
            ? `<br/>Conflicts on: ${conflictDates.slice(0, 6).map(formatDateLabel).join(', ')}${conflictDates.length > 6 ? '…' : ''}`
            : '<br/>No conflicts detected.';

        impactSummary.innerHTML = `
            Window: ${formatDateLabel(impact.windowStart)} → ${formatDateLabel(impact.windowEnd)}
            <br/>Added: ${summary.added ?? 0} • Replaced: ${summary.replaced ?? 0} • Skipped: ${summary.skipped ?? 0}
            <br/>Existing overlaps: ${summary.existingConflicts ?? 0}
            ${conflictLine}
        `;

        clearConflictHighlights();
        conflictDates.forEach((dateIso) => {
            const card = document.querySelector(`.calendar-day-card[data-date="${dateIso}"]`);
            if (card) {
                card.classList.add('calendar-day-card--deploy-conflict');
            }
        });

        if (impactApplyButton) {
            impactApplyButton.disabled = false;
            impactApplyButton.dataset.hasImpact = 'true';
        }
    }

    async function requestImpact() {
        if (!selectedScheduleId) return;
        const payload = buildDeploymentPayload();

        const res = await fetch(`/api/schedules/${selectedScheduleId}/deployment/impact`, {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                ...(csrfToken ? { [csrfHeader]: csrfToken } : {})
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('Impact request failed');
        const impact = await res.json();
        renderImpact(impact);
    }

    async function applyDeployment() {
        if (!selectedScheduleId) return;
        if (selectedStrategy === 'replace') {
            const proceed = window.confirm('Replace will remove existing entries in the selected window. Continue?');
            if (!proceed) return;
        }

        const payload = buildDeploymentPayload();

        const res = await fetch(`/api/schedules/${selectedScheduleId}/deployment/apply`, {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                ...(csrfToken ? { [csrfHeader]: csrfToken } : {})
            },
            body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error('Apply failed');
        const data = await res.json();

        const created = Number(data.created || 0);
        const replaced = Number(data.replaced || 0);
        const skipped = Number(data.skipped || 0);
        if (impactSummary) {
            impactSummary.textContent = `Applied successfully: ${created} added, ${replaced} replaced, ${skipped} skipped.`;
        }

        clearPreview();
        clearConflictHighlights();

        if (data.undoToken) {
            const token = data.undoToken;
            const undoSeconds = Number(data.undoExpiresInSeconds || 30);
            showApplyToast(
                `Schedule deployed. ${created} added, ${replaced} replaced, ${skipped} skipped.`,
                () => {
                    fetch(`/api/schedules/${selectedScheduleId}/deployment/undo`, {
                        method: 'POST',
                        credentials: 'same-origin',
                        headers: {
                            'Content-Type': 'application/json',
                            ...(csrfToken ? { [csrfHeader]: csrfToken } : {})
                        },
                        body: JSON.stringify({ undoToken: token })
                    }).then((undoRes) => {
                        if (!undoRes.ok) throw new Error('Undo failed');
                        window.location.reload();
                    }).catch((error) => {
                        console.error(error);
                    });
                },
                undoSeconds
            );
        } else {
            window.location.reload();
        }
    }

    function updateContextHeader() {
        const isWeek = document.querySelector('.calendar-view-btn.active[href*="view=week"]') != null;
        if (contextView) contextView.textContent = isWeek ? 'Week' : 'Month';

        const bounds = getVisibleDateBounds();
        if (contextRange) {
            contextRange.textContent = bounds ? `${formatDateLabel(bounds.start)} → ${formatDateLabel(bounds.end)}` : '—';
        }
        if (contextSelectedDay) {
            contextSelectedDay.textContent = selectedDay ? formatDateLabel(selectedDay) : 'None';
        }

        const pane = getCurrentPane();
        const entryCount = pane.querySelectorAll('.calendar-item').length;
        if (contextExisting) {
            contextExisting.textContent = entryCount > 0 ? `${entryCount} entries already scheduled` : 'No entries yet';
        }

        if (bounds && customStartInput && customEndInput && !customStartInput.value && !customEndInput.value) {
            customStartInput.value = bounds.start;
            customEndInput.value = bounds.end;
        }
    }

    function setContextScope(nextScope) {
        contextScope = nextScope;
        scopeButtons.forEach((button) => {
            button.classList.toggle('is-active', button.getAttribute('data-schedule-scope') === nextScope);
        });
        if (customScopeRow) {
            customScopeRow.hidden = nextScope !== 'custom';
        }
    }

    function loadFavourites() {
        try {
            return JSON.parse(localStorage.getItem(favouriteStorageKey) || '[]');
        } catch {
            return [];
        }
    }

    function saveFavourites(ids) {
        localStorage.setItem(favouriteStorageKey, JSON.stringify(ids));
    }

    function applyFavouriteUi() {
        const favourites = new Set(loadFavourites());
        scheduleDrawer?.querySelectorAll('[data-favourite-toggle]').forEach((button) => {
            const id = button.getAttribute('data-schedule-id');
            const isFavourite = id ? favourites.has(id) : false;
            button.textContent = isFavourite ? '★' : '☆';
            button.classList.toggle('is-favourite', isFavourite);
            button.setAttribute('aria-pressed', String(isFavourite));
        });
    }

    function toggleFavourite(scheduleId) {
        if (!scheduleId) return;
        const favourites = new Set(loadFavourites());
        if (favourites.has(scheduleId)) favourites.delete(scheduleId);
        else favourites.add(scheduleId);
        saveFavourites(Array.from(favourites));
        applyFavouriteUi();
    }

    async function hydrateScheduleMetadata() {
        const ids = Array.from(cardById.keys());
        if (!ids.length) return;

        const query = ids.map((id) => `ids=${encodeURIComponent(id)}`).join('&');
        const res = await fetch(`/api/schedules/metadata/batch?${query}`, { credentials: 'same-origin' });
        if (!res.ok) return;
        const data = await res.json();

        ids.forEach((id) => {
            const card = cardById.get(id);
            const metadata = data[id];
            if (!card || !metadata) return;
            scheduleMetadataCache.set(id, metadata);

            const sessionsPerWeek = card.querySelector('[data-meta-field="sessionsPerWeek"]');
            const activeDayLabels = card.querySelector('[data-meta-field="activeDayLabels"]');
            const restDays = card.querySelector('[data-meta-field="restDays"]');

            if (sessionsPerWeek) sessionsPerWeek.textContent = `${metadata.sessionsPerWeek ?? 0}/week`;
            if (card) {
                const perWeek = Number(metadata.sessionsPerWeek ?? 0);
                let bucket = 'low';
                if (perWeek >= 5) bucket = 'high';
                else if (perWeek >= 3) bucket = 'mid';
                card.setAttribute('data-frequency-bucket', bucket);
            }
            if (activeDayLabels) {
                const labels = Array.isArray(metadata.activeDayLabels) ? metadata.activeDayLabels : [];
                activeDayLabels.textContent = labels.length ? labels.join(', ') : '—';
            }
            if (restDays) restDays.textContent = String(metadata.restDays ?? '—');
            renderHealthIndicators(card, metadata);
        });

        applyFiltersAndSort();
    }

    function openScheduleDrawer() {
        if (!scheduleDrawer) return;
        lastFocusedElement = document.activeElement instanceof HTMLElement ? document.activeElement : null;
        scheduleDrawer.classList.add("open");
        scheduleDrawer.setAttribute("aria-hidden", "false");
        document.body.classList.add("calendar-schedule-open");
        document.body.style.overflow = "hidden";
        updateContextHeader();
        applyFavouriteUi();
        applyPinnedUi();
        renderQuickAccessChips();
        applyFiltersAndSort();
        if (selectedScheduleId && previewEnabled) {
            activateSchedule(selectedScheduleId, true).catch((error) => console.error(error));
        }
    }

    function closeScheduleDrawer() {
        if (!scheduleDrawer) return;
        scheduleDrawer.classList.remove("open");
        scheduleDrawer.setAttribute("aria-hidden", "true");
        document.body.classList.remove("calendar-schedule-open");
        document.body.style.overflow = "";
        clearPreview();
        clearConflictHighlights();
        if (lastFocusedElement && typeof lastFocusedElement.focus === 'function') {
            lastFocusedElement.focus();
        } else if (scheduleButton) {
            scheduleButton.focus();
        }
    }

    scheduleButton?.addEventListener("click", openScheduleDrawer);
    scheduleDrawer?.querySelector("[data-drawer-overlay]")?.addEventListener("click", closeScheduleDrawer);
    scheduleDrawer?.querySelector("[data-drawer-close]")?.addEventListener("click", closeScheduleDrawer);

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && scheduleDrawer?.classList.contains("open")) {
            closeScheduleDrawer();
        }
    });

    scheduleSearch?.addEventListener("input", (e) => {
        const query = e.target.value.toLowerCase().trim();
        if (searchFilterTimer) {
            window.clearTimeout(searchFilterTimer);
        }
        searchFilterTimer = window.setTimeout(() => {
            const items = scheduleList?.querySelectorAll("li");
            items?.forEach((item) => {
                const text = item.textContent.toLowerCase();
                item.style.display = text.includes(query) ? "" : "none";
            });
            applyFiltersAndSort();
        }, 80);
    });

    document.addEventListener('click', (event) => {
        const pinButton = event.target.closest('[data-pin-toggle]');
        if (pinButton) {
            event.preventDefault();
            togglePin(pinButton.getAttribute('data-schedule-id'));
            return;
        }

        const favouriteButton = event.target.closest('[data-favourite-toggle]');
        if (favouriteButton) {
            event.preventDefault();
            toggleFavourite(favouriteButton.getAttribute('data-schedule-id'));
            applyFiltersAndSort();
            return;
        }

        const duplicateButton = event.target.closest('[data-duplicate-schedule]');
        if (duplicateButton) {
            event.preventDefault();
            const scheduleId = duplicateButton.getAttribute('data-schedule-id');
            if (!scheduleId) return;
            duplicateButton.setAttribute('disabled', 'true');

            fetch(`/api/schedules/${scheduleId}/duplicate`, {
                method: 'POST',
                credentials: 'same-origin',
                headers: csrfToken ? { [csrfHeader]: csrfToken } : {}
            }).then((res) => {
                if (!res.ok) throw new Error('Failed to duplicate schedule');
                window.location.reload();
            }).catch((error) => {
                console.error(error);
                duplicateButton.removeAttribute('disabled');
            });
            return;
        }

        const deployButton = event.target.closest('[data-open-deploy]');
        if (deployButton) {
            event.preventDefault();
            const scheduleId = deployButton.getAttribute('data-schedule-id');
            activateSchedule(scheduleId, true).catch((error) => console.error(error));
            return;
        }
    });

    scheduleCards.forEach((card) => {
        const scheduleId = card.getAttribute('data-schedule-id');
        if (!scheduleId) return;

        card.addEventListener('mouseenter', () => {
            if (!previewEnabled) return;
            activateSchedule(scheduleId, true).catch((error) => console.error(error));
        });

        card.addEventListener('focusin', () => {
            if (!previewEnabled) return;
            activateSchedule(scheduleId, true).catch((error) => console.error(error));
        });

        card.addEventListener('click', () => {
            activateSchedule(scheduleId, previewEnabled).catch((error) => console.error(error));
        });
    });

    document.addEventListener('click', (event) => {
        if (!scheduleDrawer?.classList.contains('open')) return;
        const card = event.target.closest('.calendar-day-card[data-date]');
        if (!card) return;
        if (event.target.closest('button, input, textarea, select, form, a')) return;

        event.preventDefault();
        event.stopPropagation();
        selectedDay = card.getAttribute('data-date');
        if (customStartInput && selectedDay) {
            customStartInput.value = selectedDay;
        }
        setContextScope('day');
        updateContextHeader();
    }, true);

    scopeButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const scope = button.getAttribute('data-schedule-scope');
            if (!scope) return;
            setContextScope(scope);
            updateContextHeader();
        });
    });

    customStartInput?.addEventListener('change', updateContextHeader);
    customEndInput?.addEventListener('change', updateContextHeader);

    previewToggle?.addEventListener('change', () => {
        previewEnabled = !!previewToggle.checked;
        if (!previewEnabled) {
            clearPreview();
            return;
        }
        if (selectedScheduleId) {
            activateSchedule(selectedScheduleId, true).catch((error) => console.error(error));
        }
    });

    previewClearButton?.addEventListener('click', () => {
        selectedScheduleId = null;
        updateScheduleSelectionUi();
        clearPreview();
        if (impactSummary) {
            impactSummary.textContent = 'Select a schedule card to preview impact.';
        }
        clearConflictHighlights();
    });

    strategyButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const strategy = button.getAttribute('data-deploy-strategy') || 'merge';
            selectedStrategy = strategy;
            strategyButtons.forEach((candidate) => {
                candidate.classList.toggle('is-active', candidate === button);
            });
            if (impactApplyButton) impactApplyButton.disabled = true;
        });
    });

    applyScopeSelect?.addEventListener('change', () => {
        if (impactApplyButton) impactApplyButton.disabled = true;
    });

    applyWeeksInput?.addEventListener('input', () => {
        if (impactApplyButton) impactApplyButton.disabled = true;
    });

    simEnableToggle?.addEventListener('change', () => {
        if (simRunButton) simRunButton.disabled = !selectedScheduleId || !simEnableToggle.checked;
        if (!simEnableToggle.checked) {
            clearPreview();
            if (selectedScheduleId && previewEnabled) {
                activateSchedule(selectedScheduleId, true).catch((error) => console.error(error));
            }
        }
    });

    simRunButton?.addEventListener('click', () => {
        if (!selectedScheduleId || !simEnableToggle?.checked) return;
        const weeks = Math.max(1, Math.min(12, Number(simWeeksInput?.value || 6)));

        const payload = {
            ...buildDeploymentPayload(),
            scope: 'weeks',
            weeks,
            strategy: selectedStrategy
        };

        fetch(`/api/schedules/${selectedScheduleId}/deployment/impact`, {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/json',
                ...(csrfToken ? { [csrfHeader]: csrfToken } : {})
            },
            body: JSON.stringify(payload)
        }).then((res) => {
            if (!res.ok) throw new Error('Simulation failed');
            return res.json();
        }).then((impact) => {
            renderProjectedLayout(selectedScheduleId, impact);
            if (impactSummary) {
                const summary = impact.summary || {};
                impactSummary.innerHTML = `
                    Simulation: ${weeks} week(s) • ${formatDateLabel(impact.windowStart)} → ${formatDateLabel(impact.windowEnd)}
                    <br/>Projected adds: ${summary.added ?? 0} • Potential conflicts: ${summary.existingConflicts ?? 0}
                    <br/>Rotation: ${(getListItemForSchedule(selectedScheduleId)?.getAttribute('data-schedule-rotation') || 'weekly_repeat').replaceAll('_', ' ')}
                `;
            }
        }).catch((error) => {
            console.error(error);
            if (impactSummary) {
                impactSummary.textContent = 'Simulation could not be generated. Try a smaller range.';
            }
        });
    });

    [filterType, filterStatus, filterFrequency, sortSelect].forEach((control) => {
        control?.addEventListener('change', applyFiltersAndSort);
    });

    [filterFavourites, filterRecent].forEach((control) => {
        control?.addEventListener('change', applyFiltersAndSort);
    });

    impactReviewButton?.addEventListener('click', () => {
        requestImpact().catch((error) => {
            console.error(error);
            if (impactSummary) {
                impactSummary.textContent = 'Unable to calculate impact. Please try again.';
            }
        });
    });

    impactApplyButton?.addEventListener('click', () => {
        applyDeployment().catch((error) => {
            console.error(error);
            if (impactSummary) {
                impactSummary.textContent = 'Deployment failed. Please review conflicts and retry.';
            }
        });
    });

    [document.getElementById('month-prev'), document.getElementById('month-next'), document.getElementById('week-prev'), document.getElementById('week-next')]
        .filter(Boolean)
        .forEach((nav) => {
            nav.addEventListener('click', () => {
                clearPreview();
                clearConflictHighlights();
                setTimeout(() => {
                    if (scheduleDrawer?.classList.contains('open')) {
                        ensureSelectedDayInVisibleRange();
                        updateContextHeader();
                        if (selectedScheduleId && previewEnabled) {
                            activateSchedule(selectedScheduleId, true).catch((error) => console.error(error));
                        }
                    }
                }, 450);
            });
        });

    document.querySelectorAll("[data-heatmap-legend-toggle]").forEach((toggle) => {
        const wrapper = toggle.closest("[data-heatmap-legend-wrapper]");
        const legend = wrapper?.querySelector("[data-heatmap-legend]");
        if (!legend) return;

        toggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const isHidden = legend.classList.contains("hidden");
            legend.classList.toggle("hidden", !isHidden);
            toggle.setAttribute("aria-expanded", String(isHidden));
        });

        document.addEventListener("click", (event) => {
            if (!wrapper.contains(event.target)) {
                legend.classList.add("hidden");
                toggle.setAttribute("aria-expanded", "false");
            }
        });
    });

    setContextScope('visible');
    if (applyScopeSelect) applyScopeSelect.value = 'forward';
    updateScheduleSelectionUi();
    bindDragToApply();
    applyPinnedUi();
    renderQuickAccessChips();
    hydrateScheduleMetadata().catch((error) => console.error(error));
});
