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
    
    // ==================== Smart Time Greetings ====================
    function addTimeGreeting() {
        const header = document.querySelector('[data-testid="day-hub-header"]');
        if (!header) return;
        
        const timeTheme = header.getAttribute('data-time-theme');
        const greetings = {
            morning: { emoji: '🌅', text: 'Good morning!', color: 'text-amber-600 dark:text-amber-400' },
            midday: { emoji: '☀️', text: 'Good afternoon!', color: 'text-blue-600 dark:text-blue-400' },
            evening: { emoji: '🌆', text: 'Good evening!', color: 'text-indigo-600 dark:text-indigo-400' },
            night: { emoji: '🌙', text: 'Good night!', color: 'text-emerald-600 dark:text-emerald-400' }
        };
        
        const greeting = greetings[timeTheme] || greetings.midday;
        
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
    
    // ==================== Add Help Button ====================
    function addHelpButton() {
        const header = document.querySelector('[data-testid="day-hub-header"]');
        if (!header || document.getElementById('keyboard-help-btn')) return;
        
        const helpBtn = document.createElement('button');
        helpBtn.id = 'keyboard-help-btn';
        helpBtn.className = 'action-hint absolute right-4 top-4 flex h-8 w-8 items-center justify-center rounded-full border border-slate-300 bg-white text-slate-600 shadow-sm hover:bg-slate-50 hover:text-slate-900 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-emerald-500 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-slate-100';
        helpBtn.setAttribute('aria-label', 'Show keyboard shortcuts');
        helpBtn.innerHTML = `
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
        `;
        helpBtn.addEventListener('click', showKeyboardHelp);
        header.appendChild(helpBtn);
    }
    
    // ==================== Section Tabs ====================
    function initTabs() {
        const tabNav = document.querySelector('[role="tablist"][aria-label="Day view sections"]');
        if (!tabNav) return;

        const tabs = Array.from(tabNav.querySelectorAll('[role="tab"]'));
        const panels = tabs.map(t => document.getElementById('tab-panel-' + t.dataset.tabTarget));

        function activateTab(tab) {
            tabs.forEach((t, i) => {
                const isActive = t === tab;
                t.setAttribute('aria-selected', isActive ? 'true' : 'false');
                t.setAttribute('tabindex', isActive ? '0' : '-1');
                t.classList.toggle('is-active', isActive);
                if (panels[i]) {
                    panels[i].classList.toggle('hidden', !isActive);
                }
            });
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
                html += `<div class="timeline-event timeline-event--task${doneClass}" role="listitem">
                    ${t.completed ? '<svg class="h-3.5 w-3.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20" aria-hidden="true"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/></svg>' : '<svg class="h-3.5 w-3.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>'}
                    <span class="font-medium">${escapeHtml(t.title)}</span>
                    <span class="ml-auto text-[10px] opacity-70">${t.time}</span>
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

    // ==================== Initialize Everything ====================
    function init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
            return;
        }
        
        animateCompletionDots();
        initQuickStats();
        addTimeGreeting();
        addMotivationalMessage();
        highlightUpcomingTasks();
        scrollToCurrentTime();
        initKeyboardShortcuts();
        addHelpButton();
        initTabs();
        initTaskFilter();
        buildTimeline();
        
        // Refresh upcoming task highlights every minute
        setInterval(highlightUpcomingTasks, 60000);
    }
    
    init();
})();
