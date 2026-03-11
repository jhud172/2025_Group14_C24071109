/**
 * Workout Player — interactive in-workout experience
 * Features: AJAX set save, timers, set types, add/delete sets, add/reorder exercises
 */
(function () {
    'use strict';

    const boot = window.__wpBootstrap || {};
    const SESSION_ID = boot.sessionId;
    const CSRF_HEADER = boot.csrfHeader || 'X-CSRF-TOKEN';
    const CSRF_TOKEN = boot.csrfToken || '';

    if (!SESSION_ID) return;

    // ─── Utilities ──────────────────────────────────────────────────────────

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    async function apiFetch(url, body) {
        const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        if (CSRF_TOKEN) headers[CSRF_HEADER] = CSRF_TOKEN;
        const res = await fetch(url, {
            method: 'POST',
            headers,
            body: new URLSearchParams(body)
        });
        if (!res.ok) throw new Error('Request failed: ' + res.status);
        return res.json();
    }

    async function apiFetchJson(url, data) {
        const headers = { 'Content-Type': 'application/json' };
        if (CSRF_TOKEN) headers[CSRF_HEADER] = CSRF_TOKEN;
        const res = await fetch(url, {
            method: 'POST',
            headers,
            body: JSON.stringify(data)
        });
        if (!res.ok) throw new Error('Request failed: ' + res.status);
        return res.json();
    }

    // ─── Summary counters ───────────────────────────────────────────────────

    const elVolume = document.getElementById('wp-total-volume');
    const elSetsDone = document.getElementById('wp-sets-done');
    const elElapsed = document.getElementById('wp-elapsed');
    const elProgressFill = document.getElementById('wp-progress-fill');
    const elVolumeFooter = document.getElementById('wp-volume-footer');
    const elSetsFooter = document.getElementById('wp-sets-footer');

    function recalcSummary() {
        let totalVolume = 0;
        let setsDone = 0;
        let setsTotal = 0;
        document.querySelectorAll('[data-set-id]').forEach(function (row) {
            setsTotal++;
            const done = row.dataset.completed === 'true';
            const weight = parseFloat(row.querySelector('[data-field="weight"]')?.value || '0') || 0;
            const reps = parseInt(row.querySelector('[data-field="reps"]')?.value || '0', 10) || 0;
            if (done) {
                setsDone++;
                totalVolume += weight * reps;
            }
        });
        const pct = setsTotal > 0 ? Math.round((setsDone / setsTotal) * 100) : 0;
        if (elVolume) elVolume.textContent = totalVolume.toFixed(1);
        if (elSetsDone) elSetsDone.textContent = setsDone;
        if (elProgressFill) elProgressFill.style.width = pct + '%';
        if (elVolumeFooter) elVolumeFooter.textContent = totalVolume.toFixed(1) + ' kg×reps';
        if (elSetsFooter) elSetsFooter.textContent = setsDone + ' / ' + setsTotal + ' sets done';
    }

    // ─── Elapsed timer ──────────────────────────────────────────────────────

    const startTs = Date.now();
    setInterval(function () {
        const secs = Math.floor((Date.now() - startTs) / 1000);
        const m = String(Math.floor(secs / 60)).padStart(2, '0');
        const s = String(secs % 60).padStart(2, '0');
        if (elElapsed) elElapsed.textContent = m + ':' + s;
    }, 1000);

    // ─── Exercise timers (Cardio / Core) ────────────────────────────────────

    function bindExerciseTimer(card) {
        const panel = card.querySelector('[data-timer-display]')?.closest('.wp-timer-panel');
        if (!panel) return;
        const display = panel.querySelector('[data-timer-display]');
        const inputEl = panel.querySelector('[data-timer-input]');
        const startBtn = panel.querySelector('[data-timer-start]');
        const stopBtn = panel.querySelector('[data-timer-stop]');
        const resetBtn = panel.querySelector('[data-timer-reset]');

        let interval = null;
        let remaining = 0;
        let counting = false;

        function formatTime(s) {
            const m = String(Math.floor(s / 60)).padStart(2, '0');
            const ss = String(s % 60).padStart(2, '0');
            return m + ':' + ss;
        }

        function updateDisplay() {
            if (display) display.textContent = formatTime(remaining);
        }

        function stop() {
            if (interval) { clearInterval(interval); interval = null; }
            counting = false;
            startBtn?.classList.remove('hidden');
            stopBtn?.classList.add('hidden');
        }

        startBtn?.addEventListener('click', function () {
            const secs = parseInt(inputEl?.value || '0', 10);
            if (!counting) {
                if (secs > 0) remaining = secs;
                if (remaining <= 0) return;
            }
            counting = true;
            startBtn?.classList.add('hidden');
            stopBtn?.classList.remove('hidden');
            if (interval) clearInterval(interval);
            interval = setInterval(function () {
                remaining = Math.max(0, remaining - 1);
                updateDisplay();
                if (remaining <= 0) {
                    stop();
                    display?.classList.add('text-emerald-600');
                }
            }, 1000);
        });

        stopBtn?.addEventListener('click', stop);

        resetBtn?.addEventListener('click', function () {
            stop();
            const secs = parseInt(inputEl?.value || '0', 10);
            remaining = secs > 0 ? secs : 0;
            updateDisplay();
            display?.classList.remove('text-emerald-600');
        });

        inputEl?.addEventListener('input', function () {
            if (!counting) {
                remaining = parseInt(inputEl.value || '0', 10) || 0;
                updateDisplay();
            }
        });
    }

    // ─── Set row helpers ────────────────────────────────────────────────────

    function applySetTypeClass(row, type) {
        row.classList.remove('is-superset', 'is-dropset');
        if (type === 'SUPERSET') row.classList.add('is-superset');
        else if (type === 'DROPSET') row.classList.add('is-dropset');
    }

    function saveSet(row) {
        const setId = row.dataset.setId;
        if (!setId) return;
        row.classList.add('saving');
        const weight = row.querySelector('[data-field="weight"]')?.value || '';
        const reps = row.querySelector('[data-field="reps"]')?.value || '';
        const notes = row.querySelector('[data-field="notes"]')?.value || '';
        const completed = row.querySelector('[data-field="completed"]')?.checked ? 'true' : 'false';
        const setType = row.querySelector('[data-field="setType"]')?.value || 'NORMAL';

        applySetTypeClass(row, setType);

        apiFetch('/set-log/' + setId + '/api/update', {
            weight: weight,
            reps: reps,
            notes: notes,
            completed: completed,
            setType: setType
        }).then(function (data) {
            row.classList.remove('saving');
            row.classList.add('saved-flash');
            row.dataset.completed = data.completed ? 'true' : 'false';
            row.classList.toggle('is-done', !!data.completed);
            // Update card completion indicator
            const card = row.closest('[data-exercise-session-id]');
            if (card) {
                const tbody = card.querySelector('[data-set-tbody]');
                const allDone = tbody && [...tbody.querySelectorAll('[data-set-id]')]
                    .every(function (r) { return r.dataset.completed === 'true'; });
                card.classList.toggle('is-completed', !!allDone);
                rebuildCardCheckIcon(card, !!allDone);
            }
            recalcSummary();
            setTimeout(function () { row.classList.remove('saved-flash'); }, 600);
        }).catch(function () {
            row.classList.remove('saving');
        });
    }

    function rebuildCardCheckIcon(card, completed) {
        const header = card.querySelector('.wp-exercise-card-header');
        if (!header) return;
        let existing = header.querySelector('[data-check-icon]');
        if (completed && !existing) {
            const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            svg.setAttribute('data-check-icon', '');
            svg.setAttribute('class', 'h-5 w-5 shrink-0 text-emerald-500');
            svg.setAttribute('fill', 'none');
            svg.setAttribute('stroke', 'currentColor');
            svg.setAttribute('stroke-width', '2.5');
            svg.setAttribute('viewBox', '0 0 24 24');
            svg.setAttribute('aria-hidden', 'true');
            svg.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>';
            header.appendChild(svg);
        } else if (!completed && existing) {
            existing.remove();
        }
    }

    function bindSetRow(row) {
        // Apply initial set-type class
        applySetTypeClass(row, row.dataset.setType || 'NORMAL');

        // Auto-save on change
        row.querySelectorAll('[data-field]').forEach(function (field) {
            field.addEventListener('change', function () { saveSet(row); });
        });

        // Delete set
        row.querySelector('[data-delete-set]')?.addEventListener('click', function () {
            const setId = row.dataset.setId;
            if (!setId) return;
            apiFetch('/set-log/' + setId + '/api/delete', {}).then(function () {
                row.remove();
                renumberRows(row.closest('[data-set-tbody]'));
                recalcSummary();
            }).catch(function () { /* ignore */ });
        });
    }

    function renumberRows(tbody) {
        if (!tbody) return;
        tbody.querySelectorAll('[data-set-id]').forEach(function (row, idx) {
            const numEl = row.querySelector('[data-set-num]');
            if (numEl) numEl.textContent = idx + 1;
        });
    }

    // ─── Add set ────────────────────────────────────────────────────────────

    function bindAddSet(card) {
        const btn = card.querySelector('[data-add-set]');
        const esId = card.dataset.exerciseSessionId;
        if (!btn || !esId) return;
        btn.addEventListener('click', function () {
            btn.disabled = true;
            apiFetch('/exercise-session/' + esId + '/api/add-set', {}).then(function (data) {
                const tbody = card.querySelector('[data-set-tbody]');
                if (!tbody) { window.location.reload(); return; }
                const row = buildSetRow(data.setId, data.setNumber);
                tbody.appendChild(row);
                bindSetRow(row);
                recalcSummary();
            }).catch(function () { /* ignore */ }).finally(function () { btn.disabled = false; });
        });
    }

    function buildSetRow(setId, setNumber) {
        const tr = document.createElement('tr');
        tr.className = 'wp-set-row';
        tr.dataset.setId = setId;
        tr.dataset.setType = 'NORMAL';
        tr.dataset.completed = 'false';
        tr.innerHTML =
            '<td class="wp-set-num" data-set-num>' + escapeHtml(setNumber) + '</td>' +
            '<td class="px-2 py-2"><input type="number" step="0.5" min="0" class="wp-set-input" data-field="weight" aria-label="Weight" /></td>' +
            '<td class="px-2 py-2"><input type="number" min="0" class="wp-set-input" data-field="reps" aria-label="Reps" /></td>' +
            '<td class="px-2 py-2"><input type="text" class="wp-set-input" data-field="notes" placeholder="notes" aria-label="Notes" /></td>' +
            '<td class="px-2 py-2"><select class="wp-set-type-select" data-field="setType" aria-label="Set type">' +
                '<option value="NORMAL" selected>Normal</option>' +
                '<option value="SUPERSET">Superset</option>' +
                '<option value="DROPSET">Dropset</option>' +
            '</select></td>' +
            '<td class="px-2 py-2 text-center"><input type="checkbox" class="wp-set-done-checkbox" data-field="completed" aria-label="Mark set done" /></td>' +
            '<td class="px-1 py-2 text-right"><button type="button" class="wp-set-delete-btn" data-delete-set aria-label="Delete set">' +
                '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>' +
            '</button></td>';
        return tr;
    }

    // ─── Reorder exercises ──────────────────────────────────────────────────

    function bindReorder(card) {
        card.querySelectorAll('[data-reorder]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                const direction = btn.dataset.reorder;
                const esId = card.dataset.exerciseSessionId;
                btn.disabled = true;
                apiFetch('/workout-session/' + SESSION_ID + '/api/reorder', {
                    exerciseSessionId: esId,
                    direction: direction
                }).then(function (data) {
                    if (data.moved) {
                        // Re-order DOM nodes to reflect server state
                        const container = card.parentElement;
                        if (direction === 'up') {
                            const prev = card.previousElementSibling;
                            if (prev) container.insertBefore(card, prev);
                        } else {
                            const next = card.nextElementSibling;
                            if (next && next.nextElementSibling) {
                                container.insertBefore(card, next.nextElementSibling);
                            } else if (next) {
                                container.appendChild(card);
                            }
                        }
                        refreshReorderButtons();
                    }
                }).catch(function () { /* ignore */ }).finally(function () { btn.disabled = false; });
            });
        });
    }

    function refreshReorderButtons() {
        const cards = document.querySelectorAll('[data-exercise-session-id]');
        cards.forEach(function (card, idx) {
            const upBtn = card.querySelector('[data-reorder="up"]');
            const downBtn = card.querySelector('[data-reorder="down"]');
            if (upBtn) upBtn.disabled = idx === 0;
            if (downBtn) downBtn.disabled = idx === cards.length - 1;
        });
    }

    // ─── Add Exercise ───────────────────────────────────────────────────────

    // Tracks the last exercise card the user interacted with (for "after current" insertion)
    let lastActiveCardOrderIndex = null;

    const toggleBtn = document.getElementById('wp-add-ex-toggle');
    const addExBody = document.getElementById('wp-add-ex-body');
    const addExSearch = document.getElementById('wp-add-ex-search');
    const addExList = document.getElementById('wp-add-ex-list');

    toggleBtn?.addEventListener('click', function () {
        const open = addExBody?.classList.contains('hidden');
        addExBody?.classList.toggle('hidden', !open);
        toggleBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
        if (open) addExSearch?.focus();
    });

    addExSearch?.addEventListener('input', function () {
        const q = addExSearch.value.trim().toLowerCase();
        addExList?.querySelectorAll('.wp-exercise-option').forEach(function (opt) {
            const name = (opt.dataset.exerciseName || '').toLowerCase();
            const cat = (opt.dataset.exerciseCategory || '').toLowerCase();
            opt.style.display = (!q || name.includes(q) || cat.includes(q)) ? '' : 'none';
        });
    });

    addExList?.addEventListener('click', function (e) {
        const opt = e.target.closest('.wp-exercise-option');
        if (!opt) return;
        const exerciseId = opt.dataset.exerciseId;
        if (!exerciseId) return;

        // Read insertion position
        const positionRadio = document.querySelector('input[name="wp-add-ex-position"]:checked');
        const position = positionRadio ? positionRadio.value : 'end';

        const params = { exerciseId: exerciseId };
        if (position === 'after' && lastActiveCardOrderIndex !== null) {
            params.insertAfterOrderIndex = lastActiveCardOrderIndex;
        } else if (position === 'superset' && lastActiveCardOrderIndex !== null) {
            params.insertAfterOrderIndex = lastActiveCardOrderIndex;
            params.mode = 'SUPERSET';
            // share groupKey with current card
            const currentCard = document.querySelector('[data-exercise-session-id][data-order-index="' + lastActiveCardOrderIndex + '"]');
            if (currentCard) {
                const existingKey = currentCard.dataset.groupKey;
                if (existingKey && existingKey.trim()) {
                    params.groupKey = existingKey;
                } else {
                    params.groupKey = 'ss-' + lastActiveCardOrderIndex;
                    // Also set mode on current card
                    apiFetch('/exercise-session/' + currentCard.dataset.exerciseSessionId + '/api/set-mode', {
                        mode: 'SUPERSET',
                        groupKey: params.groupKey
                    }).then(function () {
                        currentCard.dataset.mode = 'SUPERSET';
                        currentCard.dataset.groupKey = params.groupKey;
                        applyModeBadge(currentCard, 'SUPERSET');
                    }).catch(function () { /* ignore */ });
                }
            }
        }

        apiFetch('/workout-session/' + SESSION_ID + '/api/add-exercise', params)
        .then(function (data) {
            // Find correct insertion point in DOM
            const container = document.getElementById('wp-exercises-container');
            if (!container) return;

            const card = buildExerciseCard(data);

            if (position === 'end' || lastActiveCardOrderIndex === null) {
                container.appendChild(card);
            } else {
                // insert after the card with the matching orderIndex
                const insertAfterCard = container.querySelector('[data-order-index="' + lastActiveCardOrderIndex + '"]');
                if (insertAfterCard && insertAfterCard.nextElementSibling) {
                    container.insertBefore(card, insertAfterCard.nextElementSibling);
                } else {
                    container.appendChild(card);
                }
            }

            initCard(card);
            refreshReorderButtons();
            recalcSummary();
            // Close panel
            addExBody?.classList.add('hidden');
            toggleBtn?.setAttribute('aria-expanded', 'false');
            if (addExSearch) addExSearch.value = '';
            // Scroll into view
            card.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }).catch(function () { /* ignore */ });
    });

    function buildExerciseCard(data) {
        const cat = data.exerciseCategory || '';
        const type = data.exerciseType || '';
        const mode = data.mode || 'NORMAL';
        const groupKey = data.groupKey || '';
        const isTimed = cat === 'Cardio' || cat === 'Core' || type === 'cardio';

        let badgeClass = 'wp-preflight-exercise-badge-default';
        if (cat === 'Cardio') badgeClass = 'wp-preflight-exercise-badge-cardio';
        else if (cat === 'Core') badgeClass = 'wp-preflight-exercise-badge-core';
        else if (cat === 'Strength') badgeClass = 'wp-preflight-exercise-badge-strength';

        const setId = data.setId || 0;

        const div = document.createElement('div');
        div.className = 'wp-exercise-card';
        div.dataset.exerciseSessionId = data.exerciseSessionId;
        div.dataset.exerciseName = data.exerciseName;
        div.dataset.exerciseCategory = cat;
        div.dataset.exerciseType = type;
        div.dataset.orderIndex = data.orderIndex;
        div.dataset.mode = mode;
        div.dataset.groupKey = groupKey;

        const modeBadge =
            mode === 'SUPERSET' ? '<span class="wp-exercise-category-badge wp-preflight-exercise-badge-superset" data-mode-badge title="Superset">SS</span>' :
            mode === 'DROPSET'  ? '<span class="wp-exercise-category-badge wp-preflight-exercise-badge-dropset" data-mode-badge title="Dropset">DS</span>' : '';

        div.innerHTML =
            '<div class="wp-exercise-card-header">' +
                '<div class="flex flex-col gap-0.5 shrink-0">' +
                    '<button type="button" class="wp-reorder-btn" data-reorder="up" aria-label="Move up">' +
                        '<svg class="h-3 w-3" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M4.5 15.75l7.5-7.5 7.5 7.5"/></svg>' +
                    '</button>' +
                    '<button type="button" class="wp-reorder-btn" data-reorder="down" aria-label="Move down">' +
                        '<svg class="h-3 w-3" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5"/></svg>' +
                    '</button>' +
                '</div>' +
                '<div class="flex-1 min-w-0">' +
                    '<span class="wp-exercise-name">' + escapeHtml(data.exerciseName) + '</span>' +
                '</div>' +
                modeBadge +
                '<span class="wp-exercise-category-badge ' + badgeClass + '">' + escapeHtml(cat) + '</span>' +
            '</div>' +
            (isTimed ?
                '<div class="wp-timer-panel">' +
                    '<div class="flex-1">' +
                        '<p class="text-[10px] font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">Timer</p>' +
                        '<div class="wp-timer-display" data-timer-display>00:00</div>' +
                    '</div>' +
                    '<div class="flex items-center gap-2 flex-wrap">' +
                        '<input type="number" min="1" max="3600" placeholder="secs" class="wp-timer-input" data-timer-input aria-label="Timer duration in seconds" />' +
                        '<button type="button" class="wp-timer-start" data-timer-start>Start</button>' +
                        '<button type="button" class="wp-timer-stop hidden" data-timer-stop>Stop</button>' +
                        '<button type="button" class="wp-timer-reset" data-timer-reset>Reset</button>' +
                    '</div>' +
                '</div>'
            : '') +
            '<div class="overflow-x-auto"><table class="wp-set-table">' +
                '<thead><tr>' +
                    '<th class="wp-set-th w-8">#</th>' +
                    '<th class="wp-set-th">Weight (kg)</th>' +
                    '<th class="wp-set-th">Reps</th>' +
                    '<th class="wp-set-th">Notes</th>' +
                    '<th class="wp-set-th w-20">Type</th>' +
                    '<th class="wp-set-th w-10 text-center">Done</th>' +
                    '<th class="wp-set-th w-8"></th>' +
                '</tr></thead>' +
                '<tbody data-set-tbody>' +
                (setId > 0 ?
                    '<tr class="wp-set-row" data-set-id="' + setId + '" data-set-type="NORMAL" data-completed="false">' +
                        '<td class="wp-set-num" data-set-num>1</td>' +
                        '<td class="px-2 py-2"><input type="number" step="0.5" min="0" class="wp-set-input" data-field="weight" aria-label="Weight" /></td>' +
                        '<td class="px-2 py-2"><input type="number" min="0" class="wp-set-input" data-field="reps" aria-label="Reps" /></td>' +
                        '<td class="px-2 py-2"><input type="text" class="wp-set-input" data-field="notes" placeholder="notes" aria-label="Notes" /></td>' +
                        '<td class="px-2 py-2"><select class="wp-set-type-select" data-field="setType" aria-label="Set type">' +
                            '<option value="NORMAL" selected>Normal</option>' +
                            '<option value="SUPERSET">Superset</option>' +
                            '<option value="DROPSET">Dropset</option>' +
                        '</select></td>' +
                        '<td class="px-2 py-2 text-center"><input type="checkbox" class="wp-set-done-checkbox" data-field="completed" aria-label="Mark set done" /></td>' +
                        '<td class="px-1 py-2 text-right"><button type="button" class="wp-set-delete-btn" data-delete-set aria-label="Delete set">' +
                            '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>' +
                        '</button></td>' +
                    '</tr>'
                : '') +
                '</tbody>' +
            '</table></div>' +
            '<div class="px-4 py-3 flex flex-wrap gap-2">' +
                '<button type="button" class="wp-add-set-btn" data-add-set>' +
                    '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15"/></svg>' +
                    ' Add set' +
                '</button>' +
                '<button type="button" class="wp-add-set-btn" data-add-drop-set aria-label="Add drop set">' +
                    '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5"/></svg>' +
                    ' Drop set' +
                '</button>' +
                '<button type="button" class="wp-add-set-btn" data-set-superset aria-label="Convert to superset">' +
                    '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M7.5 21L3 16.5m0 0L7.5 12M3 16.5h13.5m0-13.5L21 7.5m0 0L16.5 12M21 7.5H7.5"/></svg>' +
                    ' Superset' +
                '</button>' +
            '</div>';

        return div;
    }

    // ─── Mode badge helper ───────────────────────────────────────────────────

    function applyModeBadge(card, mode) {
        const header = card.querySelector('.wp-exercise-card-header');
        if (!header) return;
        let existing = header.querySelector('[data-mode-badge]');
        if (existing) existing.remove();
        if (mode === 'SUPERSET') {
            const span = document.createElement('span');
            span.setAttribute('data-mode-badge', '');
            span.className = 'wp-exercise-category-badge wp-preflight-exercise-badge-superset';
            span.title = 'Superset';
            span.textContent = 'SS';
            // Insert before category badge
            const catBadge = header.querySelector('.wp-exercise-category-badge:not([data-mode-badge])');
            if (catBadge) header.insertBefore(span, catBadge);
            else header.appendChild(span);
        } else if (mode === 'DROPSET') {
            const span = document.createElement('span');
            span.setAttribute('data-mode-badge', '');
            span.className = 'wp-exercise-category-badge wp-preflight-exercise-badge-dropset';
            span.title = 'Dropset';
            span.textContent = 'DS';
            const catBadge = header.querySelector('.wp-exercise-category-badge:not([data-mode-badge])');
            if (catBadge) header.insertBefore(span, catBadge);
            else header.appendChild(span);
        }
    }

    // ─── Drop set ─────────────────────────────────────────────────────────────

    function bindDropSet(card) {
        const btn = card.querySelector('[data-add-drop-set]');
        const esId = card.dataset.exerciseSessionId;
        if (!btn || !esId) return;
        btn.addEventListener('click', function () {
            btn.disabled = true;
            apiFetch('/exercise-session/' + esId + '/api/drop-set', {}).then(function (data) {
                const tbody = card.querySelector('[data-set-tbody]');
                if (!tbody) { window.location.reload(); return; }
                const row = buildSetRow(data.setId, data.setNumber);
                row.dataset.setType = 'DROPSET';
                // Pre-fill suggested weight/reps
                if (data.weight) {
                    const wInput = row.querySelector('[data-field="weight"]');
                    if (wInput) wInput.value = data.weight;
                }
                if (data.reps) {
                    const rInput = row.querySelector('[data-field="reps"]');
                    if (rInput) rInput.value = data.reps;
                }
                const typeSelect = row.querySelector('[data-field="setType"]');
                if (typeSelect) typeSelect.value = 'DROPSET';
                applySetTypeClass(row, 'DROPSET');
                tbody.appendChild(row);
                bindSetRow(row);
                recalcSummary();
            }).catch(function () { /* ignore */ }).finally(function () { btn.disabled = false; });
        });
    }

    // ─── Superset conversion ──────────────────────────────────────────────────

    function bindSetSuperset(card) {
        const btn = card.querySelector('[data-set-superset]');
        const esId = card.dataset.exerciseSessionId;
        if (!btn || !esId) return;
        btn.addEventListener('click', function () {
            const currentMode = card.dataset.mode || 'NORMAL';
            const newMode = currentMode === 'SUPERSET' ? 'NORMAL' : 'SUPERSET';
            const gKey = newMode === 'SUPERSET'
                ? (card.dataset.groupKey && card.dataset.groupKey.trim() ? card.dataset.groupKey : 'ss-' + card.dataset.orderIndex)
                : '';
            btn.disabled = true;
            apiFetch('/exercise-session/' + esId + '/api/set-mode', {
                mode: newMode,
                groupKey: gKey
            }).then(function (data) {
                card.dataset.mode = data.mode;
                card.dataset.groupKey = data.groupKey || '';
                applyModeBadge(card, data.mode);
                btn.title = data.mode === 'SUPERSET' ? 'Remove superset' : 'Convert to superset';
            }).catch(function () { /* ignore */ }).finally(function () { btn.disabled = false; });
        });
    }

    // ─── Init card ───────────────────────────────────────────────────────────

    function initCard(card) {
        bindExerciseTimer(card);
        bindAddSet(card);
        bindDropSet(card);
        bindSetSuperset(card);
        bindReorder(card);
        card.querySelectorAll('[data-set-id]').forEach(function (row) {
            bindSetRow(row);
        });
        // Track last active card for insertion position
        card.addEventListener('focusin', function () {
            const idx = parseInt(card.dataset.orderIndex, 10);
            if (!isNaN(idx)) lastActiveCardOrderIndex = idx;
        });
    }

    // ─── Bootstrap ──────────────────────────────────────────────────────────

    document.querySelectorAll('[data-exercise-session-id]').forEach(initCard);
    refreshReorderButtons();
    recalcSummary();

})();
