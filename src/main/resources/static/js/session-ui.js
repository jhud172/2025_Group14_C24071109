/**
 * session-ui.js — Workout Session UI controller
 * Handles FLOW (one exercise per screen) and PROFESSIONAL (two-column) templates.
 * Communicates with backend via AJAX for set persistence.
 */
(function () {
    'use strict';

    const boot = window.__sessionBootstrap || {};
    const SESSION_ID   = boot.sessionId;
    const LAYOUT       = (boot.layoutType || 'FLOW').toUpperCase();
    const CSRF_HEADER  = boot.csrfHeader || 'X-CSRF-TOKEN';
    const CSRF_TOKEN   = boot.csrfToken  || '';

    if (!SESSION_ID) return;

    // ─── SVG Icons ───────────────────────────────────────────────────────────

    var SVG_CHEVRON_RIGHT = '<svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5"/></svg>';

    // ─── DOM helpers ────────────────────────────────────────────────────────

    function $(sel, ctx) { return (ctx || document).querySelector(sel); }
    function $$(sel, ctx) { return Array.from((ctx || document).querySelectorAll(sel)); }

    function escHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    // ─── AJAX ────────────────────────────────────────────────────────────────

    function apiFetch(url, method, body) {
        var isGet = (method || 'POST').toUpperCase() === 'GET';
        var headers = {};
        if (!isGet) headers['Content-Type'] = 'application/x-www-form-urlencoded';
        if (CSRF_TOKEN && !isGet) headers[CSRF_HEADER] = CSRF_TOKEN;
        return fetch(url, {
            method: method || 'POST',
            headers: headers,
            body: (!isGet && body) ? new URLSearchParams(body).toString() : undefined
        }).then(function (res) {
            if (!res.ok) throw new Error('Request failed: ' + res.status);
            return res.json();
        });
    }

    // ─── Elapsed timer ──────────────────────────────────────────────────────

    var startTs = Date.now();

    function formatTime(secs) {
        var m = String(Math.floor(secs / 60)).padStart(2, '0');
        var s = String(secs % 60).padStart(2, '0');
        return m + ':' + s;
    }

    function startElapsedTimer(displays) {
        startTs = Date.now();
        setInterval(function () {
            var secs = Math.floor((Date.now() - startTs) / 1000);
            var txt = formatTime(secs);
            displays.forEach(function (el) { if (el) el.textContent = txt; });
        }, 1000);
    }

    // ─── Volume + sets counter ───────────────────────────────────────────────

    function recalcStats() {
        var totalVolume = 0;
        var setsDone = 0;
        var setsTotal = 0;
        $$('[data-set-id]').forEach(function (row) {
            setsTotal++;
            if (row.dataset.completed === 'true') {
                setsDone++;
                var w = parseFloat(row.querySelector('[data-field="weight"]')?.value || 0) || 0;
                var r = parseInt(row.querySelector('[data-field="reps"]')?.value || 0, 10) || 0;
                totalVolume += w * r;
            }
        });

        var volTxt = totalVolume.toFixed(1);
        var elVolFlow   = $('#wsu-flow-volume');
        var elSetsDone  = $('#wsu-flow-sets-done');
        var elVolFooter = $('#wsu-flow-vol-footer');
        var elProVol    = $('#wsu-pro-volume');

        if (elVolFlow)   elVolFlow.textContent   = volTxt;
        if (elSetsDone)  elSetsDone.textContent   = setsDone;
        if (elVolFooter) elVolFooter.textContent  = volTxt + ' kg\u00d7reps';
        if (elProVol)    elProVol.textContent     = volTxt;

        // Summary panel values (updated once on finish)
        var elSumVol  = $('#wsu-sum-volume');
        var elSumSets = $('#wsu-sum-sets');
        if (elSumVol)  elSumVol.textContent  = volTxt + ' kg';
        if (elSumSets) elSumSets.textContent = setsDone + ' / ' + setsTotal;

        return { totalVolume, setsDone, setsTotal };
    }

    // ─── Set persistence helpers ─────────────────────────────────────────────

    function persistSetUpdate(setId, reps, weight, rpe) {
        return apiFetch('/workout-session-sets/' + setId, 'POST', {
            reps: reps != null ? reps : '',
            weight: weight != null ? weight : '',
            rpe: rpe != null ? rpe : ''
        });
    }

    function persistCompleteSet(setId) {
        return apiFetch('/workout-session-sets/' + setId + '/complete', 'POST');
    }

    function persistAddSet(exerciseId, reps, weight, rpe) {
        return apiFetch('/workout-sessions/' + SESSION_ID + '/exercises/' + exerciseId + '/sets', 'POST', {
            reps: reps != null ? reps : '',
            weight: weight != null ? weight : '',
            rpe: rpe != null ? rpe : ''
        });
    }

    // ─── Build a set row HTML ────────────────────────────────────────────────

    function buildSetRow(setData, index) {
        var idx = index + 1;
        var done = !!setData.completedAt;
        var weight = setData.weight != null ? setData.weight : '';
        var reps   = setData.reps   != null ? setData.reps   : '';
        var rpe    = setData.rpe    != null ? setData.rpe    : '';
        return '<div class="wsu-flow-set-row' + (done ? ' wsu-set-done' : '') + '"'
            + ' data-set-id="' + escHtml(String(setData.id)) + '"'
            + ' data-completed="' + (done ? 'true' : 'false') + '">'
            + '<span class="wsu-set-label">Set ' + idx + '</span>'
            + '<div class="wsu-set-inputs">'
            + '<label class="wsu-set-field"><span>Weight</span>'
            + '<input type="number" step="0.5" min="0" class="wsu-set-input" data-field="weight" value="' + escHtml(String(weight)) + '" placeholder="kg"/></label>'
            + '<label class="wsu-set-field"><span>Reps</span>'
            + '<input type="number" min="0" class="wsu-set-input" data-field="reps" value="' + escHtml(String(reps)) + '" placeholder="—"/></label>'
            + '<label class="wsu-set-field"><span>RPE</span>'
            + '<input type="number" step="0.5" min="1" max="10" class="wsu-set-input" data-field="rpe" value="' + escHtml(String(rpe)) + '" placeholder="—"/></label>'
            + '</div>'
            + '<button type="button" class="wsu-set-complete-btn' + (done ? ' wsu-set-complete-btn--done' : '') + '">'
            + (done ? '\u2713 Done' : 'Complete set')
            + '</button>'
            + '</div>';
    }

    // ─── Bind set row events ─────────────────────────────────────────────────

    function bindSetRow(row) {
        var setId = row.dataset.setId;

        // Auto-save on input blur
        $$('[data-field]', row).forEach(function (input) {
            input.addEventListener('change', function () {
                var reps   = row.querySelector('[data-field="reps"]')?.value   || null;
                var weight = row.querySelector('[data-field="weight"]')?.value || null;
                var rpe    = row.querySelector('[data-field="rpe"]')?.value    || null;
                persistSetUpdate(setId, reps, weight, rpe).then(recalcStats).catch(console.error);
            });
        });

        // Complete set button
        var completeBtn = row.querySelector('.wsu-set-complete-btn');
        if (completeBtn) {
            completeBtn.addEventListener('click', function () {
                var reps   = row.querySelector('[data-field="reps"]')?.value   || null;
                var weight = row.querySelector('[data-field="weight"]')?.value || null;
                var rpe    = row.querySelector('[data-field="rpe"]')?.value    || null;
                // Save first, then complete
                persistSetUpdate(setId, reps, weight, rpe)
                    .then(function () { return persistCompleteSet(setId); })
                    .then(function (data) {
                        row.dataset.completed = 'true';
                        row.classList.add('wsu-set-done');
                        completeBtn.classList.add('wsu-set-complete-btn--done');
                        completeBtn.textContent = '\u2713 Done';
                        recalcStats();
                        // Show rest timer if enabled (default 90s)
                        showRestTimer(90);
                    })
                    .catch(console.error);
            });
        }
    }

    function bindAllSetRows() {
        $$('[data-set-id]').forEach(bindSetRow);
    }

    // ─── Add set button handling ─────────────────────────────────────────────

    function bindAddSetButtons() {
        $$('.wsu-add-set-btn, .wsu-pro-quick-add').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var exId = btn.dataset.exerciseId;
                if (!exId) return;
                persistAddSet(exId, null, null, null)
                    .then(function (setData) {
                        // Find the container for this exercise
                        var container = document.querySelector('[data-exercise-id="' + exId + '"].wsu-flow-sets')
                            || document.querySelector('#wsu-pro-sets-container');
                        if (container) {
                            var index = container.querySelectorAll('[data-set-id]').length;
                            container.insertAdjacentHTML('beforeend', buildSetRow(setData, index));
                            var newRow = container.lastElementChild;
                            bindSetRow(newRow);

                            // Remove empty state message if present
                            var noSets = container.querySelector('.wsu-flow-no-sets');
                            if (noSets) noSets.remove();
                        }
                        recalcStats();
                    })
                    .catch(console.error);
            });
        });
    }

    // ─── REST TIMER ──────────────────────────────────────────────────────────

    var restInterval = null;
    var restRemaining = 0;
    var restTotal = 0;
    var CIRCUMFERENCE = 326.7; // 2 * PI * 52

    function showRestTimer(seconds) {
        var overlay = $('#wsu-rest-overlay');
        if (!overlay) return;
        restRemaining = seconds;
        restTotal     = seconds;
        updateRestDisplay();
        overlay.classList.remove('wsu-hidden');
        clearInterval(restInterval);
        restInterval = setInterval(function () {
            restRemaining--;
            if (restRemaining <= 0) {
                restRemaining = 0;
                clearInterval(restInterval);
                hideRestTimer();
            }
            updateRestDisplay();
        }, 1000);
    }

    function hideRestTimer() {
        var overlay = $('#wsu-rest-overlay');
        if (overlay) overlay.classList.add('wsu-hidden');
        clearInterval(restInterval);
    }

    function updateRestDisplay() {
        var el = $('#wsu-rest-time');
        if (el) el.textContent = formatTime(restRemaining);
        var ring = $('#wsu-rest-ring-fill');
        if (ring && restTotal > 0) {
            var frac   = restRemaining / restTotal;
            var offset = CIRCUMFERENCE * (1 - frac);
            ring.style.strokeDashoffset = offset;
        }
    }

    function bindRestControls() {
        var skip   = $('#wsu-rest-skip');
        var minus  = $('#wsu-rest-minus');
        var plus   = $('#wsu-rest-plus');
        if (skip)  skip.addEventListener('click',  hideRestTimer);
        if (minus) minus.addEventListener('click', function () {
            restRemaining = Math.max(0, restRemaining - 15);
            updateRestDisplay();
        });
        if (plus)  plus.addEventListener('click',  function () {
            restRemaining += 15;
            restTotal      = Math.max(restRemaining, restTotal);
            updateRestDisplay();
        });
    }

    // ─── FINISH / SUMMARY ────────────────────────────────────────────────────

    function finishSession() {
        apiFetch('/workout-sessions/' + SESSION_ID + '/complete', 'POST')
            .then(function (data) {
                var stats = recalcStats();
                var elapsed = Math.floor((Date.now() - startTs) / 1000);
                var elDur = $('#wsu-sum-duration');
                if (elDur) elDur.textContent = formatTime(elapsed);

                // Hide active panel, show summary
                $$('#wsu-flow, #wsu-professional').forEach(function (el) { el.classList.add('wsu-hidden'); });
                var summary = $('#wsu-summary');
                if (summary) summary.classList.remove('wsu-hidden');
            })
            .catch(console.error);
    }

    function bindFinishButtons() {
        var flowBtn = $('#wsu-flow-finish-btn');
        var proBtn  = $('#wsu-pro-finish-btn');
        if (flowBtn) flowBtn.addEventListener('click', finishSession);
        if (proBtn)  proBtn.addEventListener('click',  finishSession);
    }

    // ─── START SCREEN ────────────────────────────────────────────────────────

    function showTemplate() {
        var startScreen = $('#wsu-start-screen');
        if (startScreen) startScreen.classList.add('wsu-hidden');

        if (LAYOUT === 'PROFESSIONAL') {
            var proEl = $('#wsu-professional');
            if (proEl) {
                proEl.classList.remove('wsu-hidden');
                initProfessional();
            }
        } else {
            // FLOW (default)
            var flowEl = $('#wsu-flow');
            if (flowEl) {
                flowEl.classList.remove('wsu-hidden');
                initFlow();
            }
        }

        bindAllSetRows();
        bindAddSetButtons();
        bindRestControls();
        bindFinishButtons();
        startElapsedTimer([
            $('#wsu-flow-elapsed'),
            $('#wsu-pro-elapsed')
        ]);
        recalcStats();
    }

    function initStartScreen() {
        var startScreen = $('#wsu-start-screen');
        var startBtn = $('#wsu-start-btn');
        if (!startScreen) return;

        if (startBtn) {
            startBtn.addEventListener('click', function () {
                startBtn.disabled = true;
                startBtn.textContent = 'Starting\u2026';
                showTemplate();
            });
        }
    }

    // ─── FLOW template ───────────────────────────────────────────────────────

    var flowCurrentIdx = 0;

    function updateFlowSlide() {
        var slides = $$('.wsu-flow-slide');
        if (!slides.length) return;

        // Move the slides container
        var container = $('#wsu-flow-slides');
        if (container) {
            container.style.transform = 'translateX(-' + (flowCurrentIdx * 100) + '%)';
        }

        // Counter
        var counter = $('#wsu-flow-counter');
        if (counter) counter.textContent = (flowCurrentIdx + 1) + ' / ' + slides.length;

        // Progress
        var fill = $('#wsu-flow-progress-fill');
        if (fill) {
            var pct = slides.length > 1 ? Math.round((flowCurrentIdx / (slides.length - 1)) * 100) : 100;
            fill.style.width = pct + '%';
            fill.closest('[role="progressbar"]')?.setAttribute('aria-valuenow', pct);
        }

        // Prev/Next buttons
        var prevBtn = $('#wsu-flow-prev');
        var nextBtn = $('#wsu-flow-next');
        if (prevBtn) prevBtn.disabled = flowCurrentIdx === 0;
        if (nextBtn) {
            if (flowCurrentIdx >= slides.length - 1) {
                nextBtn.disabled = true;
                nextBtn.textContent = 'Last exercise';
            } else {
                nextBtn.disabled = false;
                nextBtn.innerHTML = 'Next ' + SVG_CHEVRON_RIGHT;
            }
        }
    }

    function initFlow() {
        var slides = $$('.wsu-flow-slide');
        if (!slides.length) return;

        // Ensure slides stretch full width inside the wrapper
        slides.forEach(function (s) { s.style.minWidth = '100%'; });

        var prevBtn = $('#wsu-flow-prev');
        var nextBtn = $('#wsu-flow-next');
        var backBtn = $('#wsu-flow-back');

        if (prevBtn) prevBtn.addEventListener('click', function () {
            if (flowCurrentIdx > 0) { flowCurrentIdx--; updateFlowSlide(); }
        });
        if (nextBtn) nextBtn.addEventListener('click', function () {
            if (flowCurrentIdx < slides.length - 1) { flowCurrentIdx++; updateFlowSlide(); }
        });
        if (backBtn) backBtn.addEventListener('click', function () {
            var flowEl = $('#wsu-flow');
            if (flowEl) flowEl.classList.add('wsu-hidden');
            var startEl = $('#wsu-start-screen');
            if (startEl) startEl.classList.remove('wsu-hidden');
        });

        updateFlowSlide();
    }

    // ─── PROFESSIONAL template ────────────────────────────────────────────────

    var proCurrentExerciseId = null;

    function renderProSets(exerciseId) {
        var editorContent = $('#wsu-pro-editor-content');
        var placeholder   = $('#wsu-pro-placeholder');
        var setsContainer = $('#wsu-pro-sets-container');
        var titleEl       = $('#wsu-pro-ex-title');
        var catEl         = $('#wsu-pro-ex-cat');
        var addBtn        = $('#wsu-pro-add-set-btn');

        if (!setsContainer) return;

        // Find exercise data from the DOM
        var exerciseItem = document.querySelector('.wsu-pro-exercise-item[data-exercise-id="' + exerciseId + '"]');
        if (!exerciseItem) return;

        var name = exerciseItem.dataset.exerciseName || 'Exercise';
        var demoUrl = exerciseItem.dataset.demoUrl || '';

        if (placeholder) placeholder.classList.add('wsu-hidden');
        if (editorContent) editorContent.classList.remove('wsu-hidden');
        if (titleEl) titleEl.textContent = name;

        // Render demo link in the editor header
        var editorHeader = editorContent.querySelector('.wsu-pro-editor-header');
        var existingDemo = editorHeader ? editorHeader.querySelector('.wsu-demo-btn') : null;
        if (existingDemo) existingDemo.remove();
        if (demoUrl && editorHeader) {
            var demoLink = document.createElement('a');
            demoLink.href = demoUrl;
            demoLink.target = '_blank';
            demoLink.rel = 'noopener noreferrer';
            demoLink.className = 'wsu-demo-btn';
            demoLink.title = 'Watch exercise demo';
            demoLink.innerHTML = '<svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M5.25 5.653c0-.856.917-1.398 1.667-.986l11.54 6.347a1.125 1.125 0 010 1.972l-11.54 6.347a1.125 1.125 0 01-1.667-.986V5.653z"/></svg> Demo';
            editorHeader.appendChild(demoLink);
        }

        // Re-render sets from server data on exercise selection
        apiFetch('/workout-sessions/' + SESSION_ID + '/data', 'GET')
            .catch(function () { return null; })
            .then(function (sessionData) {
                if (!sessionData) return;
                var exercises = sessionData.exercises || [];
                var ex = exercises.find(function (e) { return String(e.id) === String(exerciseId); });
                if (!ex) return;

                if (catEl) catEl.textContent = ex.exerciseCategory || '';

                var sets = ex.sets || [];
                setsContainer.innerHTML = sets.length
                    ? sets.map(function (s, i) { return buildSetRow(s, i); }).join('')
                    : '<p class="wsu-flow-no-sets">No sets yet — add one below.</p>';

                $$('[data-set-id]', setsContainer).forEach(bindSetRow);

                // Wire add set button for this exercise
                if (addBtn) {
                    addBtn.dataset.exerciseId = exerciseId;
                    var newBtn = addBtn.cloneNode(true);
                    addBtn.parentNode.replaceChild(newBtn, addBtn);
                    newBtn.addEventListener('click', function () {
                        persistAddSet(exerciseId, null, null, null)
                            .then(function (setData) {
                                var noSets = setsContainer.querySelector('.wsu-flow-no-sets');
                                if (noSets) noSets.remove();
                                var index = setsContainer.querySelectorAll('[data-set-id]').length;
                                setsContainer.insertAdjacentHTML('beforeend', buildSetRow(setData, index));
                                bindSetRow(setsContainer.lastElementChild);
                                // Update sets count in sidebar
                                updateProExerciseSetCount(exerciseId);
                                recalcStats();
                            })
                            .catch(console.error);
                    });
                }
            });
    }

    function updateProExerciseSetCount(exerciseId) {
        var countEl = document.querySelector('.wsu-pro-ex-sets-count[data-ex-id="' + exerciseId + '"]');
        if (!countEl) return;
        var setsContainer = $('#wsu-pro-sets-container');
        if (!setsContainer) return;
        var count = setsContainer.querySelectorAll('[data-set-id]').length;
        countEl.textContent = count + ' set' + (count === 1 ? '' : 's');
    }

    function initProfessional() {
        var list  = $('#wsu-pro-exercise-list');
        var backBtn = $('#wsu-pro-back');

        if (backBtn) backBtn.addEventListener('click', function () {
            var proEl = $('#wsu-professional');
            if (proEl) proEl.classList.add('wsu-hidden');
            var startEl = $('#wsu-start-screen');
            if (startEl) startEl.classList.remove('wsu-hidden');
        });

        if (!list) return;

        $$('.wsu-pro-exercise-item', list).forEach(function (item) {
            item.addEventListener('click', function (e) {
                // Don't trigger if clicking the quick-add button
                if (e.target.closest('.wsu-pro-quick-add')) return;

                $$('.wsu-pro-exercise-item').forEach(function (i) {
                    i.classList.remove('wsu-pro-exercise-item--active');
                });
                item.classList.add('wsu-pro-exercise-item--active');
                proCurrentExerciseId = item.dataset.exerciseId;
                renderProSets(proCurrentExerciseId);
            });
        });

        // Select first exercise by default
        var first = list.querySelector('.wsu-pro-exercise-item');
        if (first) {
            proCurrentExerciseId = first.dataset.exerciseId;
            renderProSets(proCurrentExerciseId);
        }
    }

    // ─── Init ────────────────────────────────────────────────────────────────

    document.addEventListener('DOMContentLoaded', function () {
        initStartScreen();
    });

}());
