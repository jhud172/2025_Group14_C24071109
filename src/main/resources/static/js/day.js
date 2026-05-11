function toggleEdit(id) {
    const panel = document.getElementById("edit-" + id);
    if (!panel) return;
    panel.classList.toggle("hidden");
}

(function initAddTaskModal() {
    const openBtn = document.getElementById('open-add-task');
    const modal = document.getElementById('add-task-modal');
    const closeBtn = document.getElementById('close-add-task');
    if (!openBtn || !modal || !closeBtn) return;

    const backdrop = modal.querySelector('[data-modal-backdrop]');

    function open() {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');

        const firstInput = modal.querySelector('input[name="title"]');
        if (firstInput) firstInput.focus();
    }

    function close() {
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
        openBtn.focus();
    }

    openBtn.addEventListener('click', open);
    closeBtn.addEventListener('click', close);

    if (backdrop) {
        backdrop.addEventListener('click', close);
    }

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.classList.contains('hidden')) {
            close();
        }
    });

    const titleInput = modal.querySelector('input[name="title"]');
    const notesInput = modal.querySelector('textarea[name="notes"]');
    const exerciseInput = modal.querySelector('input[name="exercise"]');

    modal.addEventListener('click', (e) => {
        const btn = e.target && e.target.closest ? e.target.closest('[data-template]') : null;
        if (!btn) return;

        const t = btn.getAttribute('data-title') || '';
        const n = btn.getAttribute('data-notes') || '';
        const ex = (btn.getAttribute('data-exercise') || '').toLowerCase() === 'true';

        if (titleInput) titleInput.value = t;
        if (notesInput) notesInput.value = n;
        if (exerciseInput) exerciseInput.checked = ex;

        if (titleInput) titleInput.focus();
    });
})();

(function initTaskDrawer() {
    const drawer = document.getElementById('task-drawer');
    if (!drawer) return;

    const drawerBody = document.getElementById('task-drawer-body');
    const closeBtn = drawer.querySelector('[data-testid="task-drawer-close"]');
    const backdrop = drawer.querySelector('[data-testid="task-drawer-backdrop"]');

    function open(taskId) {
        if (!drawerBody) return;

        const content = document.getElementById('task-drawer-content-' + taskId);
        if (!content) return;

        drawerBody.innerHTML = content.innerHTML;
        drawer.classList.remove('hidden');
        drawer.setAttribute('aria-hidden', 'false');

        if (closeBtn) closeBtn.focus();
    }

    function close() {
        if (!drawerBody) return;
        drawer.classList.add('hidden');
        drawer.setAttribute('aria-hidden', 'true');
        drawerBody.innerHTML = '<p class="text-sm text-slate-600 dark:text-slate-300">Select a task to view details.</p>';
    }

    document.addEventListener('click', (e) => {
        const trigger = e.target && e.target.closest ? e.target.closest('[data-open-task-drawer]') : null;
        if (!trigger) return;
        const taskId = trigger.getAttribute('data-task-id');
        if (!taskId) return;

        e.preventDefault();
        open(taskId);
    });

    if (closeBtn) closeBtn.addEventListener('click', close);
    if (backdrop) backdrop.addEventListener('click', close);

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !drawer.classList.contains('hidden')) {
            close();
        }
    });
})();

(function initTasksConfigureJump() {
    const btn = document.getElementById('tasks-configure');
    const details = document.getElementById('tasks-config-details');
    if (!btn || !details) return;

    btn.addEventListener('click', () => {
        window.requestAnimationFrame(() => {
            const firstSelect = details.querySelector('select');
            if (firstSelect && typeof firstSelect.focus === 'function') {
                firstSelect.focus();
            }
        });
    });
})();

(function initClickOffDetailsMenus() {
    const menus = Array.from(document.querySelectorAll('details[data-click-off-details]'));
    if (!menus.length) return;

    function closeAll(except) {
        menus.forEach((details) => {
            if (except && details === except) return;
            if (details.open) details.open = false;
        });
    }

    menus.forEach((details) => {
        details.addEventListener('toggle', () => {
            if (!details.open) return;
            closeAll(details);
        });
    });

    document.addEventListener('click', (e) => {
        const target = e.target;
        const clickedInsideAny = menus.some((details) => details.contains(target));
        if (clickedInsideAny) return;
        closeAll();
    });

    document.addEventListener('keydown', (e) => {
        if (e.key !== 'Escape') return;
        closeAll();
    });
})();

(function initFadeInOnView() {
    const items = Array.from(document.querySelectorAll('[data-fade-in]'));
    if (!items.length) return;

    const reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reduceMotion) return;

    items.forEach((el) => {
        el.classList.add('transition-all', 'duration-500', 'ease-out', 'will-change-transform');

        const rect = el.getBoundingClientRect();
        const belowFold = rect.top > window.innerHeight * 0.9;
        if (belowFold) {
            el.classList.add('opacity-0', 'translate-y-2');
        }
    });

    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                const el = entry.target;
                el.classList.remove('opacity-0', 'translate-y-2');
                el.classList.add('opacity-100', 'translate-y-0');
                observer.unobserve(el);
            });
        },
        { threshold: 0.12, rootMargin: '0px 0px -10% 0px' }
    );

    items.forEach((el) => observer.observe(el));
})();

(function initDayViewAjaxPreferenceForms() {
    const forms = Array.from(document.querySelectorAll('form[data-ajax-form]'));
    if (!forms.length) return;

    function postFormWithoutReload(form) {
        const method = (form.getAttribute('method') || 'post').toUpperCase();
        const action = form.getAttribute('action');
        if (!action) return Promise.reject(new Error('Missing form action'));

        const formData = new FormData(form);
        const body = new URLSearchParams();
        for (const [key, value] of formData.entries()) {
            body.append(key, String(value));
        }

        return fetch(action, {
            method,
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: body.toString()
        });
    }

    function captureOriginalOrder(selector, attrName) {
        const items = Array.from(document.querySelectorAll(selector));
        items.forEach((el, idx) => {
            if (!el.dataset[attrName]) {
                el.dataset[attrName] = String(idx);
            }
        });
    }

    function normalizeText(s) {
        return (s || '').toString().trim().toLowerCase();
    }

    function sortByOrdering(items, ordering) {
        if (ordering === 'ALPHABETICAL') {
            return items.sort((a, b) => {
                const at = normalizeText(a.dataset.taskTitle);
                const bt = normalizeText(b.dataset.taskTitle);
                if (at < bt) return -1;
                if (at > bt) return 1;
                const atime = normalizeText(a.dataset.taskTime);
                const btime = normalizeText(b.dataset.taskTime);
                return atime.localeCompare(btime);
            });
        }

        // Chronological (time asc; empty time last; title tiebreak)
        return items.sort((a, b) => {
            const at = (a.dataset.taskTime || '').trim();
            const bt = (b.dataset.taskTime || '').trim();
            const aHas = at.length > 0;
            const bHas = bt.length > 0;
            if (aHas && bHas) {
                if (at < bt) return -1;
                if (at > bt) return 1;
            } else if (aHas && !bHas) {
                return -1;
            } else if (!aHas && bHas) {
                return 1;
            }
            const an = normalizeText(a.dataset.taskTitle);
            const bn = normalizeText(b.dataset.taskTitle);
            if (an < bn) return -1;
            if (an > bn) return 1;
            return 0;
        });
    }

    function applyTaskPreferences(form) {
        const tasksRoot = document.getElementById('tasks-list-root');
        if (!tasksRoot) return;

        const ordering = form.querySelector('select[name="ordering"]')?.value || 'CHRONOLOGICAL';
        const layout = form.querySelector('select[name="layout"]')?.value || 'COMBINED_LIST';

        const allTaskItems = Array.from(tasksRoot.querySelectorAll('[data-task-item]'));
        if (!allTaskItems.length) return;

        sortByOrdering(allTaskItems, ordering);

        const combinedScrollClass = 'mt-4 max-h-[32rem] overflow-x-hidden overflow-y-auto rounded-xl border border-slate-200 bg-white scrollbar-thin scrollbar-track-transparent scrollbar-thumb-slate-300 dark:border-slate-800 dark:bg-slate-950 dark:scrollbar-thumb-slate-700';
        const separatedScrollClass = 'mt-3 max-h-[24rem] overflow-x-hidden overflow-y-auto rounded-xl border border-slate-200 bg-white scrollbar-thin scrollbar-track-transparent scrollbar-thumb-slate-300 dark:border-slate-800 dark:bg-slate-950 dark:scrollbar-thumb-slate-700';
        const headingClass = 'mt-6 text-sm font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400';

        // Clear current layout nodes (we rebuild only the layout portion, not the empty-state banner)
        tasksRoot.innerHTML = '';

        if (layout === 'SEPARATED_BY_CATEGORY') {
            const otherHeading = document.createElement('h3');
            otherHeading.className = headingClass;
            otherHeading.textContent = 'Other tasks';

            const otherScroll = document.createElement('div');
            otherScroll.className = separatedScrollClass;
            otherScroll.setAttribute('data-task-list', 'other');

            const exerciseHeading = document.createElement('h3');
            exerciseHeading.className = headingClass;
            exerciseHeading.textContent = 'Exercise tasks';

            const exerciseScroll = document.createElement('div');
            exerciseScroll.className = separatedScrollClass;
            exerciseScroll.setAttribute('data-task-list', 'exercise');

            allTaskItems.forEach((item) => {
                const isExercise = (item.dataset.taskExercise || '').toLowerCase() === 'true';
                (isExercise ? exerciseScroll : otherScroll).appendChild(item);
            });

            tasksRoot.appendChild(otherHeading);
            tasksRoot.appendChild(otherScroll);
            tasksRoot.appendChild(exerciseHeading);
            tasksRoot.appendChild(exerciseScroll);
            return;
        }

        const combinedScroll = document.createElement('div');
        combinedScroll.className = combinedScrollClass;
        combinedScroll.setAttribute('data-task-list', 'combined');
        allTaskItems.forEach((item) => combinedScroll.appendChild(item));
        tasksRoot.appendChild(combinedScroll);
    }

    function applyWorkoutPreferences(form) {
        const workoutsRoot = document.getElementById('workouts-list-root');
        if (!workoutsRoot) return;

        const ordering = form.querySelector('select[name="ordering"]')?.value || 'SCHEDULE_ORDER';
        const items = Array.from(workoutsRoot.querySelectorAll('[data-workout-item]'));
        if (!items.length) return;

        captureOriginalOrder('#workouts-list-root [data-workout-item]', 'originalIndex');

        if (ordering === 'ALPHABETICAL') {
            items.sort((a, b) => {
                const an = normalizeText(a.dataset.workoutName);
                const bn = normalizeText(b.dataset.workoutName);
                if (an < bn) return -1;
                if (an > bn) return 1;
                return 0;
            });
        } else {
            items.sort((a, b) => {
                const ai = parseInt(a.dataset.originalIndex || '0', 10);
                const bi = parseInt(b.dataset.originalIndex || '0', 10);
                return ai - bi;
            });
        }

        items.forEach((el) => workoutsRoot.appendChild(el));
    }

    // Capture initial order so we can restore schedule order.
    captureOriginalOrder('#workouts-list-root [data-workout-item]', 'originalIndex');

    forms.forEach((form) => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();

            postFormWithoutReload(form)
                .then((res) => {
                    if (!res.ok) return;

                    const action = form.getAttribute('action') || '';
                    if (action.includes('/task-preferences')) {
                        applyTaskPreferences(form);
                    } else if (action.includes('/workout-preferences')) {
                        applyWorkoutPreferences(form);
                    }
                })
                .catch(() => {
                    // fail silently; non-js fallback still works on normal submits
                });
        });
    });
})();
