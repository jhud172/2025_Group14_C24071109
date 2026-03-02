// Day View Enhancements - Smart, Interactive Features

(function() {
    'use strict';
    
    // ==================== Smart Completion Animations ====================
    function animateCompletionDots() {
        const dots = document.querySelectorAll('.completion-dot');
        dots.forEach((dot, index) => {
            dot.style.animationDelay = `${index * 0.05}s`;
        });
    }
    
    // ==================== Quick Stats Display ====================
    function initQuickStats() {
        const tasksPanel = document.querySelector('[data-testid="tasks-panel"]');
        const workoutsPanel = document.querySelector('[data-testid="workouts-panel"]');
        
        if (!tasksPanel && !workoutsPanel) return;
        
        // Count tasks
        const totalTasks = document.querySelectorAll('[data-task-item]').length;
        const completedTasks = document.querySelectorAll('[data-task-item][data-task-completed="true"]').length || 
                              document.querySelectorAll('[data-task-item].bg-green-50').length;
        const lateTasks = document.querySelectorAll('[data-task-item][data-task-late="true"]').length ||
                         document.querySelectorAll('[data-task-item].bg-red-50').length;
        
        // Add quick stats if there are tasks
        if (totalTasks > 0 && tasksPanel) {
            const header = tasksPanel.querySelector('header');
            if (header && !document.getElementById('task-quick-stats')) {
                const statsDiv = document.createElement('div');
                statsDiv.id = 'task-quick-stats';
                statsDiv.className = 'mt-2 flex gap-2 text-xs';
                statsDiv.innerHTML = `
                    <span class="inline-flex items-center gap-1 rounded-full bg-slate-100 px-2 py-0.5 text-slate-700 dark:bg-slate-800 dark:text-slate-300">
                        <svg class="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
                            <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                            <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
                        </svg>
                        ${totalTasks} total
                    </span>
                    ${completedTasks > 0 ? `
                        <span class="inline-flex items-center gap-1 rounded-full bg-green-100 px-2 py-0.5 text-green-800 dark:bg-green-950/30 dark:text-green-200">
                            <svg class="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                            </svg>
                            ${completedTasks} done
                        </span>
                    ` : ''}
                    ${lateTasks > 0 ? `
                        <span class="inline-flex items-center gap-1 rounded-full bg-red-100 px-2 py-0.5 text-red-800 dark:bg-red-950/30 dark:text-red-200">
                            <svg class="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                            </svg>
                            ${lateTasks} late
                        </span>
                    ` : ''}
                `;
                header.appendChild(statsDiv);
            }
        }
    }
    
    // ==================== Time-of-Day Theme ====================
    var TIME_THEME_KEY = 'day-time-theme-enabled';

    function isTimeThemeEnabled() {
        var stored = localStorage.getItem(TIME_THEME_KEY);
        // Default on if not set
        return stored !== 'false';
    }

    function applyTimeOfDayTheme() {
        if (!isTimeThemeEnabled()) {
            // Remove any existing time theme
            document.documentElement.removeAttribute('data-time-theme');
            var header = document.querySelector('[data-testid="day-hub-header"]');
            if (header) header.removeAttribute('data-time-theme');
            return;
        }
        var hour = new Date().getHours();
        var theme;
        if (hour >= 5 && hour < 12) {
            theme = 'morning';
        } else if (hour >= 12 && hour < 17) {
            theme = 'afternoon';
        } else if (hour >= 17 && hour < 21) {
            theme = 'evening';
        } else {
            theme = 'night';
        }
        document.documentElement.setAttribute('data-time-theme', theme);
        var header = document.querySelector('[data-testid="day-hub-header"]');
        if (header) header.setAttribute('data-time-theme', theme);
    }

    // ==================== Time-Theme Toggle Button ====================
    function initTimeThemeToggle() {
        var btn = document.getElementById('time-theme-toggle-btn');
        if (!btn) return;
        var enabled = isTimeThemeEnabled();
        btn.setAttribute('aria-pressed', String(enabled));
        btn.querySelector('.ttt-label').textContent = enabled ? 'Time theme: on' : 'Time theme: off';

        btn.addEventListener('click', function() {
            var next = !isTimeThemeEnabled();
            localStorage.setItem(TIME_THEME_KEY, String(next));
            btn.setAttribute('aria-pressed', String(next));
            btn.querySelector('.ttt-label').textContent = next ? 'Time theme: on' : 'Time theme: off';
            applyTimeOfDayTheme();
        });
    }

    // ==================== Smart Time Greetings ====================
    function addTimeGreeting() {
        const header = document.querySelector('[data-testid="day-hub-header"]');
        if (!header) return;
        
        const timeTheme = document.documentElement.getAttribute('data-time-theme') || header.getAttribute('data-time-theme');
        const greetings = {
            morning: { emoji: '🌅', text: 'Good morning!', color: 'text-amber-600 dark:text-amber-400' },
            afternoon: { emoji: '☀️', text: 'Good afternoon!', color: 'text-blue-600 dark:text-blue-400' },
            midday: { emoji: '☀️', text: 'Good afternoon!', color: 'text-blue-600 dark:text-blue-400' },
            evening: { emoji: '🌆', text: 'Good evening!', color: 'text-indigo-600 dark:text-indigo-400' },
            night: { emoji: '🌙', text: 'Good night!', color: 'text-emerald-600 dark:text-emerald-400' }
        };
        
        const greeting = greetings[timeTheme] || greetings.afternoon;
        
        if (!document.getElementById('time-greeting')) {
            const greetingDiv = document.createElement('div');
            greetingDiv.id = 'time-greeting';
            greetingDiv.className = `mb-3 flex items-center gap-2 ${greeting.color}`;
            greetingDiv.innerHTML = `
                <span class="text-2xl" aria-hidden="true">${greeting.emoji}</span>
                <span class="text-sm font-semibold">${greeting.text}</span>
            `;
            
            const firstChild = header.querySelector('.flex.flex-col');
            if (firstChild) {
                firstChild.insertBefore(greetingDiv, firstChild.firstChild);
            }
        }
    }
    
    // ==================== Motivational Messages ====================
    function addMotivationalMessage() {
        const completionSection = document.querySelector('[data-testid="day-hub-header"] section[aria-label="Day completion"]');
        if (!completionSection) return;
        
        const percentageText = completionSection.querySelector('p.text-sm.font-semibold span');
        if (!percentageText) return;
        
        const percentage = parseInt(percentageText.textContent) || 0;
        
        const messages = [
            { range: [0, 20], text: "Let's get started! 💪", color: "text-slate-600 dark:text-slate-400" },
            { range: [21, 40], text: "Making progress! 🎯", color: "text-blue-600 dark:text-blue-400" },
            { range: [41, 60], text: "Halfway there! 🚀", color: "text-indigo-600 dark:text-indigo-400" },
            { range: [61, 80], text: "Almost done! ⭐", color: "text-amber-600 dark:text-amber-400" },
            { range: [81, 99], text: "Final stretch! 🔥", color: "text-orange-600 dark:text-orange-400" },
            { range: [100, 100], text: "Perfect day! 🎉", color: "text-emerald-600 dark:text-emerald-400" }
        ];
        
        const message = messages.find(m => percentage >= m.range[0] && percentage <= m.range[1]);
        
        if (message && !document.getElementById('motivation-message')) {
            const msgDiv = document.createElement('p');
            msgDiv.id = 'motivation-message';
            msgDiv.className = `mt-2 text-xs font-medium ${message.color}`;
            msgDiv.textContent = message.text;
            completionSection.appendChild(msgDiv);
        }
    }
    
    // ==================== Smart Task Highlighting ====================
    function highlightUpcomingTasks() {
        const now = new Date();
        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();
        const currentTime = currentHour * 60 + currentMinute;
        
        document.querySelectorAll('[data-task-item]').forEach(task => {
            const timeStr = task.getAttribute('data-task-time');
            if (!timeStr) return;
            
            const [hours, minutes] = timeStr.split(':').map(Number);
            const taskTime = hours * 60 + minutes;
            const timeDiff = taskTime - currentTime;
            
            // Highlight tasks within next 30 minutes
            if (timeDiff > 0 && timeDiff <= 30) {
                task.classList.add('ring-2', 'ring-blue-400/50', 'dark:ring-blue-500/50');
                
                if (!task.querySelector('.upcoming-badge')) {
                    const badge = document.createElement('span');
                    badge.className = 'upcoming-badge absolute top-2 right-2 inline-flex items-center gap-1 rounded-full bg-blue-100 px-2 py-0.5 text-xs font-semibold text-blue-800 dark:bg-blue-950/40 dark:text-blue-200';
                    badge.innerHTML = `
                        <svg class="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                        </svg>
                        Soon
                    `;
                    const taskItem = task.querySelector('[data-testid="task-list-item"]');
                    if (taskItem) {
                        taskItem.style.position = 'relative';
                        taskItem.appendChild(badge);
                    }
                }
            }
        });
    }
    
    // ==================== Smooth Scroll to Current Time ====================
    function scrollToCurrentTime() {
        const now = new Date();
        const currentHour = now.getHours();
        
        // Only auto-scroll during waking hours
        if (currentHour < 6 || currentHour > 23) return;
        
        const tasks = Array.from(document.querySelectorAll('[data-task-item]'));
        const currentTimeTask = tasks.find(task => {
            const timeStr = task.getAttribute('data-task-time');
            if (!timeStr) return false;
            
            const [hours] = timeStr.split(':').map(Number);
            return hours === currentHour;
        });
        
        if (currentTimeTask) {
            setTimeout(() => {
                currentTimeTask.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }, 500);
        }
    }
    
    // ==================== Keyboard Shortcuts ====================
    function initKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            // Don't trigger shortcuts when typing in inputs
            if (e.target.matches('input, textarea, select')) return;
            
            // Ctrl/Cmd + N = New Task
            if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
                e.preventDefault();
                const addTaskBtn = document.getElementById('open-add-task');
                if (addTaskBtn) addTaskBtn.click();
            }
            
            // ? = Show keyboard shortcuts help
            if (e.key === '?') {
                e.preventDefault();
                showKeyboardHelp();
            }
        });
    }
    
    function showKeyboardHelp() {
        if (document.getElementById('keyboard-help-modal')) return;
        
        const modal = document.createElement('div');
        modal.id = 'keyboard-help-modal';
        modal.className = 'fixed inset-0 z-[70] flex items-center justify-center bg-slate-950/70 backdrop-blur-sm';
        modal.innerHTML = `
            <div class="mx-4 w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-xl dark:border-slate-800 dark:bg-slate-950">
                <div class="flex items-start justify-between">
                    <h3 class="text-lg font-semibold text-slate-900 dark:text-slate-100">Keyboard Shortcuts</h3>
                    <button class="close-help-btn rounded-lg bg-slate-100 px-3 py-1 text-sm font-medium text-slate-900 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-100 dark:hover:bg-slate-700">Close</button>
                </div>
                <div class="mt-4 space-y-2 text-sm">
                    <div class="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                        <span class="text-slate-700 dark:text-slate-200">New Task</span>
                        <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">Ctrl+N</kbd>
                    </div>
                    <div class="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                        <span class="text-slate-700 dark:text-slate-200">Previous Day</span>
                        <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">←</kbd>
                    </div>
                    <div class="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                        <span class="text-slate-700 dark:text-slate-200">Next Day</span>
                        <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">→</kbd>
                    </div>
                    <div class="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                        <span class="text-slate-700 dark:text-slate-200">Show Help</span>
                        <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">?</kbd>
                    </div>
                    <div class="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                        <span class="text-slate-700 dark:text-slate-200">Switch tabs (Tasks/Workouts/Timeline/Overview)</span>
                        <div class="flex gap-1">
                            <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">1</kbd>
                            <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">2</kbd>
                            <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">3</kbd>
                            <kbd class="rounded bg-white px-2 py-1 text-xs font-semibold text-slate-900 shadow dark:bg-slate-800 dark:text-slate-100">4</kbd>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        
        function closeModal() {
            modal.remove();
            document.removeEventListener('keydown', handleEscape);
        }
        
        function handleEscape(e) {
            if (e.key === 'Escape') {
                closeModal();
            }
        }
        
        modal.addEventListener('click', (e) => {
            if (e.target === modal) closeModal();
        });
        
        const closeBtn = modal.querySelector('.close-help-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', closeModal);
        }
        
        document.addEventListener('keydown', handleEscape);
    }
    
    // ==================== Focus Mode ====================
    function initFocusMode() {
        const btn = document.getElementById('focus-mode-btn');
        if (!btn) return;

        const wrapper = document.getElementById('day-main-content');
        if (!wrapper) return;

        btn.addEventListener('click', () => {
            const isActive = wrapper.getAttribute('data-focus-mode') === 'true';
            const next = !isActive;
            wrapper.setAttribute('data-focus-mode', String(next));
            btn.setAttribute('aria-pressed', String(next));
            btn.querySelector('.focus-mode-label').textContent = next ? 'Exit focus' : 'Focus mode';

            if (next) {
                const timelineTab = document.getElementById('tab-btn-timeline');
                if (timelineTab) timelineTab.click();
            }
        });
    }

    // ==================== Section Tabs ====================
    function initTabs() {
        const tabNav = document.querySelector('[role="tablist"][aria-label="Day view sections"]');
        if (!tabNav) return;

        const tabs = Array.from(tabNav.querySelectorAll('[role="tab"]'));
        const panels = tabs.map(t => document.getElementById('tab-panel-' + t.dataset.tabTarget));
        const pill = document.getElementById('day-tab-pill');

        function updatePill(activeTab) {
            if (!pill || !activeTab) return;
            const navRect = tabNav.getBoundingClientRect();
            const tabRect = activeTab.getBoundingClientRect();
            const scrollLeft = tabNav.scrollLeft || 0;
            const left = tabRect.left - navRect.left + scrollLeft;
            pill.style.width = tabRect.width + 'px';
            pill.style.transform = 'translateX(' + left + 'px)';
        }

        function activateTab(tab, pushHash) {
            tabs.forEach((t, i) => {
                const isActive = t === tab;
                t.setAttribute('aria-selected', isActive ? 'true' : 'false');
                t.setAttribute('tabindex', isActive ? '0' : '-1');
                t.classList.toggle('is-active', isActive);
                if (panels[i]) {
                    if (isActive) {
                        panels[i].classList.remove('hidden');
                        // Force re-animation
                        void panels[i].offsetWidth;
                    } else {
                        panels[i].classList.add('hidden');
                    }
                }
            });
            updatePill(tab);
            // URL hash persistence
            const hash = tab.dataset.tabTarget;
            if (hash && pushHash !== false) {
                try {
                    history.replaceState(null, '', '#' + hash);
                } catch(e) {
                    // history API may fail in sandboxed iframes; safe to ignore
                }
            }
        }

        tabs.forEach(tab => {
            tab.addEventListener('click', () => activateTab(tab));

            // Arrow key navigation
            tab.addEventListener('keydown', (e) => {
                let idx = tabs.indexOf(tab);
                if (e.key === 'ArrowRight') {
                    e.preventDefault();
                    idx = (idx + 1) % tabs.length;
                    tabs[idx].focus();
                    activateTab(tabs[idx]);
                } else if (e.key === 'ArrowLeft') {
                    e.preventDefault();
                    idx = (idx - 1 + tabs.length) % tabs.length;
                    tabs[idx].focus();
                    activateTab(tabs[idx]);
                } else if (e.key === 'Home') {
                    e.preventDefault();
                    tabs[0].focus();
                    activateTab(tabs[0]);
                } else if (e.key === 'End') {
                    e.preventDefault();
                    tabs[tabs.length - 1].focus();
                    activateTab(tabs[tabs.length - 1]);
                } else if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    activateTab(tab);
                }
            });
        });

        // Keyboard shortcut: 1-4 to switch tabs
        document.addEventListener('keydown', (e) => {
            if (e.target.matches('input, textarea, select, [contenteditable]')) return;
            const num = parseInt(e.key);
            if (num >= 1 && num <= tabs.length && !e.ctrlKey && !e.metaKey) {
                activateTab(tabs[num - 1]);
                tabs[num - 1].focus();
            }
        });

        // Shortcuts button
        const shortcutsBtn = document.getElementById('day-shortcuts-btn');
        if (shortcutsBtn) shortcutsBtn.addEventListener('click', showKeyboardHelp);

        // Empty-state alias buttons
        document.querySelectorAll('[data-open-add-task-alias]').forEach(btn => {
            btn.addEventListener('click', () => {
                const addBtn = document.getElementById('open-add-task');
                if (addBtn) addBtn.click();
            });
        });

        // Stat pill navigation: clicking "Tasks left" / "Workouts left" switches to the relevant tab
        document.querySelectorAll('[data-stat-tab-target]').forEach((btn) => {
            const target = btn.getAttribute('data-stat-tab-target');
            const tab = target ? tabs.find(t => t.dataset.tabTarget === target) : null;
            if (!tab) return;
            btn.addEventListener('click', () => {
                activateTab(tab);
                tab.focus();
                tab.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            });
        });

        // Check URL hash for initial tab
        const hash = location.hash.replace('#', '');
        const hashTab = hash ? tabs.find(t => t.dataset.tabTarget === hash) : null;
        if (hashTab) {
            activateTab(hashTab, false);
        }

        // Initialize pill position after paint
        requestAnimationFrame(() => {
            const activeTab = tabs.find(t => t.getAttribute('aria-selected') === 'true') || tabs[0];
            if (activeTab) updatePill(activeTab);
        });

        // Update pill on resize (debounced)
        let resizeTimer;
        window.addEventListener('resize', () => {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(() => {
                const activeTab = tabs.find(t => t.getAttribute('aria-selected') === 'true') || tabs[0];
                if (activeTab) updatePill(activeTab);
            }, 100);
        });
    }

    // ==================== Task Filter ====================
    function initTaskFilter() {
        const filterInput = document.getElementById('task-filter-input');
        const filterChips = document.querySelectorAll('.task-filter-chip');
        let activeStatus = 'all';

        function applyFilter() {
            const query = filterInput ? filterInput.value.trim().toLowerCase() : '';
            const tasks = document.querySelectorAll('[data-task-item]');
            tasks.forEach(task => {
                const title = (task.getAttribute('data-task-title') || '').toLowerCase();
                const status = task.getAttribute('data-task-status') || 'todo';
                const matchesQuery = !query || title.includes(query);

                let matchesStatus = true;
                if (activeStatus === 'todo') {
                    matchesStatus = status === 'todo';
                } else if (activeStatus === 'done') {
                    matchesStatus = status === 'done';
                } else if (activeStatus === 'late') {
                    matchesStatus = status === 'late';
                }
                task.style.display = (matchesQuery && matchesStatus) ? '' : 'none';
            });
        }

        if (filterInput) {
            filterInput.addEventListener('input', applyFilter);
        }

        filterChips.forEach(chip => {
            chip.addEventListener('click', () => {
                activeStatus = chip.dataset.filter || 'all';
                filterChips.forEach(c => {
                    c.classList.toggle('is-active', c === chip);
                    c.setAttribute('aria-pressed', c === chip ? 'true' : 'false');
                });
                applyFilter();
            });
        });
    }

    // ==================== Timeline View ====================
    function buildTimeline() {
        const container = document.getElementById('day-timeline');
        if (!container) return;

        const isToday = container.getAttribute('data-is-today') === 'true';
        const now = new Date();
        const dayMainContent = document.getElementById('day-main-content');
        const pageDate = dayMainContent ? (dayMainContent.getAttribute('data-date') || '') : '';

        // Collect tasks with times
        const taskItems = Array.from(document.querySelectorAll('[data-task-item]')).map(el => ({
            id: el.getAttribute('data-task-id') || '',
            title: el.getAttribute('data-task-title') || 'Task',
            time: el.getAttribute('data-task-time') || '',
            completed: el.getAttribute('data-task-status') === 'done',
            type: 'task'
        })).filter(t => t.time);

        // Collect workout items
        const workoutItems = Array.from(document.querySelectorAll('[data-workout-item]')).map(el => ({
            title: el.getAttribute('data-workout-name') || 'Workout',
            time: '',
            type: 'workout'
        }));

        const occurrenceItems = Array.from(document.querySelectorAll('[data-occurrence-item]')).map(el => ({
            title: el.getAttribute('data-occurrence-title') || 'Workout',
            time: '',
            type: 'workout',
            completed: el.getAttribute('data-completed') === 'true'
        }));

        // Build hour grid 6-23
        let html = '<div class="timeline-grid" role="list" aria-label="Day timeline">';

        const currentHour = now.getHours();
        const currentMinute = now.getMinutes();

        for (let h = 6; h <= 23; h++) {
            const isNowHour = isToday && h === currentHour;
            html += `<div class="timeline-hour-row${isNowHour ? ' timeline-now-indicator' : ''}" role="listitem">`;
            if (isNowHour) {
                html += `<div class="timeline-now-dot" aria-hidden="true"></div>`;
            }
            html += `<div class="timeline-hour-label" aria-hidden="true">${String(h).padStart(2, '0')}:00</div>`;
            html += `<div class="timeline-slot">`;

            // Tasks in this hour
            taskItems.filter(t => {
                const [th] = t.time.split(':').map(Number);
                return th === h;
            }).forEach(t => {
                const doneClass = t.completed ? ' is-done' : '';
                const checkIcon = '<svg viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M5 13l4 4L19 7" stroke-linecap="round" stroke-linejoin="round"/></svg>';
                const qcBtn = t.id ? `<button type="button" class="timeline-qc-btn${t.completed ? ' is-done' : ''}" data-quick-complete data-task-id="${escapeHtml(t.id)}" data-task-date="${escapeHtml(pageDate)}" aria-label="${t.completed ? 'Mark incomplete' : 'Mark complete'}" title="${t.completed ? 'Mark incomplete' : 'Mark complete'}">${checkIcon}</button>` : '';
                html += `<div class="timeline-event timeline-event--task${doneClass}" role="listitem">
                    <span class="font-medium">${escapeHtml(t.title)}</span>
                    <span class="ml-auto text-[10px] opacity-70 mr-1">${t.time}</span>
                    ${qcBtn}
                </div>`;
            });

            // Workouts in morning slot (7am) if unscheduled
            if (h === 7) {
                workoutItems.forEach(w => {
                    html += `<div class="timeline-event timeline-event--workout" role="listitem">
                        <svg class="h-3.5 w-3.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/></svg>
                        <span class="font-medium">${escapeHtml(w.title)}</span>
                        <span class="ml-auto text-[10px] opacity-70">Scheduled</span>
                    </div>`;
                });
                occurrenceItems.forEach(o => {
                    const doneClass = o.completed ? ' is-done' : '';
                    html += `<div class="timeline-event timeline-event--workout${doneClass}" role="listitem">
                        <svg class="h-3.5 w-3.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/></svg>
                        <span class="font-medium">${escapeHtml(o.title)}</span>
                        <span class="ml-auto text-[10px] opacity-70">Workout</span>
                    </div>`;
                });
            }

            html += `</div></div>`;
        }
        html += '</div>';

        // Add legend
        html = `<div class="mb-3 flex flex-wrap items-center gap-3 text-xs text-slate-500 dark:text-slate-400">
            <span class="flex items-center gap-1.5"><span class="h-3 w-3 rounded-sm border-l-2 border-l-blue-500 bg-blue-50"></span>Task</span>
            <span class="flex items-center gap-1.5"><span class="h-3 w-3 rounded-sm border-l-2 border-l-emerald-500 bg-emerald-50"></span>Workout</span>
            ${isToday ? '<span class="flex items-center gap-1.5"><span class="inline-block h-2 w-4 bg-red-500 opacity-70 rounded-full"></span>Now</span>' : ''}
        </div>` + html;

        container.innerHTML = html;

        // Auto-scroll to current hour if today
        if (isToday) {
            const nowRow = container.querySelector('.timeline-now-indicator');
            if (nowRow) {
                const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
                setTimeout(() => nowRow.scrollIntoView({
                    behavior: reducedMotion ? 'auto' : 'smooth',
                    block: 'center'
                }), 300);
            }
        }
    }

    function escapeHtml(str) {
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }

    // ==================== Timed Focus Countdown ====================
    function initTimedFocusCountdown() {
        const section = document.querySelector('[data-testid="timed-focus"]');
        if (!section) return;

        // Find nearest upcoming task with a time (from task items)
        const now = new Date();
        const currentMinutes = now.getHours() * 60 + now.getMinutes();

        let nearest = null;
        let nearestMinutes = Infinity;

        document.querySelectorAll('[data-task-item]').forEach(el => {
            const timeStr = el.getAttribute('data-task-time');
            const status = el.getAttribute('data-task-status');
            if (!timeStr || status === 'done') return;
            const [h, m] = timeStr.split(':').map(Number);
            const taskMinutes = h * 60 + m;
            const diff = taskMinutes - currentMinutes;
            if (diff >= 0 && diff < nearestMinutes) {
                nearestMinutes = diff;
                nearest = { timeStr, minutes: taskMinutes, diff };
            }
        });

        if (!nearest) return;

        function getNowMinutes() {
            const t = new Date();
            return t.getHours() * 60 + t.getMinutes();
        }

        // Check if currently active (within 30-min window)
        const isActive = currentMinutes >= nearest.minutes - 5 && currentMinutes <= nearest.minutes + 30;
        if (isActive) {
            section.setAttribute('data-timed-focus-active', 'true');
        }

        // Show countdown only if within 60 minutes
        if (nearest.diff > 60) return;

        // Create countdown element
        let countdownEl = section.querySelector('.timed-focus-countdown');
        if (!countdownEl) {
            countdownEl = document.createElement('span');
            countdownEl.className = 'timed-focus-countdown block mt-1';
            const valueEl = section.querySelector('[data-testid="timed-focus-value"]');
            if (valueEl) valueEl.after(countdownEl);
        }

        function updateCountdown() {
            const now = new Date();
            const diffSec = nearest.minutes * 60 - (now.getHours() * 3600 + now.getMinutes() * 60 + now.getSeconds());
            if (diffSec <= 0) {
                countdownEl.textContent = 'Now';
                return;
            }
            const m = Math.floor(diffSec / 60);
            const s = diffSec % 60;
            countdownEl.textContent = 'In ' + m + 'm ' + String(s).padStart(2, '0') + 's';
        }

        updateCountdown();
        const timer = setInterval(() => {
            updateCountdown();
            if (getNowMinutes() > nearest.minutes + 30) {
                clearInterval(timer);
                if (countdownEl) countdownEl.remove();
                section.removeAttribute('data-timed-focus-active');
            }
        }, 1000);
    }

    // ==================== AI Optimise Modal ====================
    function initAiOptimiseModal() {
        const btn = document.getElementById('ai-optimise-btn');
        const modal = document.getElementById('ai-optimise-modal');
        const cancelBtn = document.getElementById('ai-optimise-cancel');
        const confirmBtn = document.getElementById('ai-optimise-confirm');
        const badge = document.getElementById('ai-optimised-badge');
        const dontShowCheck = document.getElementById('ai-optimise-dont-show');

        if (!btn || !modal) return;

        const dateAttr = btn.getAttribute('data-date');
        // Server already sets disabled/badge for previously optimised dates.
        // JS only needs to handle the "skip warning" preference from localStorage.
        const storageKey = 'ai-optimise-skip-warning';

        // If already optimised from server, nothing more to do for init
        if (btn.getAttribute('data-optimised') === 'true') {
            return;
        }

        // Pre-populate "don't show" checkbox from server preference
        const serverHideWarning = btn.getAttribute('data-hide-warning') === 'true';
        if (serverHideWarning && dontShowCheck) {
            dontShowCheck.checked = true;
            localStorage.setItem(storageKey, '1');
        }

        function openModal() {
            modal.classList.remove('hidden');
            modal.setAttribute('aria-hidden', 'false');
            if (cancelBtn) cancelBtn.focus();
            document.addEventListener('keydown', handleEscape);
        }

        function closeModal() {
            modal.classList.add('hidden');
            modal.setAttribute('aria-hidden', 'true');
            document.removeEventListener('keydown', handleEscape);
            btn.focus();
        }

        function handleEscape(e) {
            if (e.key === 'Escape') closeModal();
        }

        function applyOptimisedState(dayTheme) {
            btn.disabled = true;
            btn.setAttribute('aria-disabled', 'true');
            btn.classList.add('opacity-50', 'cursor-not-allowed');
            if (badge) badge.classList.remove('hidden');
            // Lock the daily focus select
            const focusSelect = document.getElementById('daily-focus-select');
            if (focusSelect) {
                focusSelect.disabled = true;
                focusSelect.setAttribute('title', 'Locked by AI Optimise');
            }
            // Apply day theme CSS class to main container
            if (dayTheme) {
                applyDayThemeClass(dayTheme);
            }
        }

        btn.addEventListener('click', () => {
            if (localStorage.getItem(storageKey) === '1') {
                performOptimise(false);
            } else {
                openModal();
            }
        });

        if (cancelBtn) cancelBtn.addEventListener('click', closeModal);

        if (confirmBtn) {
            confirmBtn.addEventListener('click', () => {
                const dontShow = dontShowCheck && dontShowCheck.checked;
                if (dontShow) {
                    localStorage.setItem(storageKey, '1');
                }
                closeModal();
                performOptimise(dontShow);
            });
        }

        // Close on backdrop click
        modal.addEventListener('click', (e) => {
            if (e.target === modal) closeModal();
        });

        function performOptimise(dontShowAgain) {
            const csrf = document.querySelector('input[name="_csrf"]');
            const csrfVal = csrf ? csrf.value : null;

            const body = new URLSearchParams();
            body.set('dontShowAgain', dontShowAgain ? 'true' : 'false');
            if (csrfVal) body.set('_csrf', csrfVal);

            fetch('/calendar/day/' + dateAttr + '/optimise', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: body.toString()
            })
            .then(res => {
                if (res.ok) return res.json();
                if (res.status === 409) {
                    // Already optimised (server-side check)
                    applyOptimisedState(null);
                }
                return null;
            })
            .then(data => {
                if (data && data.dayTheme) {
                    applyOptimisedState(data.dayTheme);
                }
            })
            .catch(err => {
                console.error('AI Optimise request failed:', err);
            });
        }
    }

    // ==================== Quick Complete Toggle ====================
    function initQuickComplete() {
        // Get CSRF token from any existing form in the page
        function getCsrf() {
            const inp = document.querySelector('input[name="_csrf"]');
            return inp ? inp.value : null;
        }

        function getCsrfParam() {
            const inp = document.querySelector('input[name="_csrf"]');
            const name = inp ? inp.getAttribute('name') : '_csrf';
            return name;
        }

        function chipLabel(isDone, status) {
            if (isDone) return 'Done';
            return status === 'late' ? 'Late' : 'To do';
        }

        document.addEventListener('click', (e) => {
            const btn = e.target && e.target.closest ? e.target.closest('[data-quick-complete]') : null;
            if (!btn) return;
            e.preventDefault();
            e.stopPropagation();

            const taskId = btn.getAttribute('data-task-id');
            const date = btn.getAttribute('data-task-date');
            if (!taskId || !date) return;

            const csrf = getCsrf();
            if (!csrf) return;

            const taskItem = btn.closest('[data-task-item]');
            const isDone = btn.classList.contains('is-done');
            const originalStatus = taskItem ? (taskItem.getAttribute('data-task-status') || 'todo') : 'todo';
            const newDone = !isDone;

            // Optimistic UI update
            btn.classList.toggle('is-done', newDone);
            btn.setAttribute('aria-pressed', newDone ? 'true' : 'false');
            if (taskItem) {
                taskItem.setAttribute('data-task-status', newDone ? 'done' : originalStatus);
                taskItem.classList.toggle('bg-green-50', newDone);
                taskItem.classList.toggle('dark:bg-green-950/20', newDone);
                const chip = taskItem.querySelector('[data-testid="task-status-chip"]');
                if (chip) chip.textContent = chipLabel(newDone, originalStatus);
                // Flash animation on completion
                if (newDone) {
                    taskItem.classList.remove('just-completed');
                    void taskItem.offsetWidth;
                    taskItem.classList.add('just-completed');
                    taskItem.addEventListener('animationend', () => taskItem.classList.remove('just-completed'), { once: true });
                }
            }

            // Also sync timeline quick-complete button for the same task
            document.querySelectorAll(`.timeline-qc-btn[data-task-id="${taskId}"]`).forEach(tlBtn => {
                tlBtn.classList.toggle('is-done', newDone);
            });

            const body = new URLSearchParams();
            body.append('taskId', taskId);
            body.append(getCsrfParam(), csrf);

            fetch('/calendar/day/' + date + '/toggle-complete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: body.toString()
            }).catch(() => {
                // Revert on network/server error
                btn.classList.toggle('is-done', isDone);
                btn.setAttribute('aria-pressed', isDone ? 'true' : 'false');
                if (taskItem) {
                    taskItem.setAttribute('data-task-status', originalStatus);
                    taskItem.classList.toggle('bg-green-50', isDone);
                    taskItem.classList.toggle('dark:bg-green-950/20', isDone);
                    const chip = taskItem.querySelector('[data-testid="task-status-chip"]');
                    if (chip) chip.textContent = chipLabel(isDone, originalStatus);
                }
                document.querySelectorAll(`.timeline-qc-btn[data-task-id="${taskId}"]`).forEach(tlBtn => {
                    tlBtn.classList.toggle('is-done', isDone);
                });
            });
        });
    }

    // ==================== Day Theme Helper ====================
    function applyDayThemeClass(theme) {
        const container = document.getElementById('day-main-content');
        if (container) {
            container.setAttribute('data-day-theme', theme);
        }
        document.body.classList.remove('day-theme-professional', 'day-theme-futuristic', 'day-theme-clean');
        document.body.classList.add('day-theme-' + theme);
    }

    // ==================== Apply Server-Side Day Theme ====================
    function applyServerDayTheme() {
        const container = document.getElementById('day-main-content');
        if (!container) return;
        const theme = container.getAttribute('data-day-theme');
        if (theme) {
            applyDayThemeClass(theme);
        }
    }

    // ==================== Initialize Everything ====================
    function init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
            return;
        }
        
        applyTimeOfDayTheme();
        initTimeThemeToggle();
        animateCompletionDots();
        initQuickStats();
        addTimeGreeting();
        addMotivationalMessage();
        highlightUpcomingTasks();
        scrollToCurrentTime();
        initKeyboardShortcuts();
        initFocusMode();
        initTabs();
        initTaskFilter();
        buildTimeline();
        initTimedFocusCountdown();
        initAiOptimiseModal();
        initQuickComplete();
        applyServerDayTheme();
        
        // Refresh upcoming task highlights every minute
        setInterval(highlightUpcomingTasks, 60000);
    }
    
    init();
})();
