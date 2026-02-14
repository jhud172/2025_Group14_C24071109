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
        const completedTasks = document.querySelectorAll('[data-task-item].bg-green-50, [data-task-item].dark\\:bg-green-950\\/20').length;
        const lateTasks = document.querySelectorAll('[data-task-item].bg-red-50, [data-task-item].dark\\:bg-red-950\\/20').length;
        
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
                    <button class="rounded-lg bg-slate-100 px-3 py-1 text-sm font-medium text-slate-900 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-100 dark:hover:bg-slate-700" onclick="this.closest('#keyboard-help-modal').remove()">Close</button>
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
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.remove();
        });
        
        document.addEventListener('keydown', function closeOnEscape(e) {
            if (e.key === 'Escape') {
                modal.remove();
                document.removeEventListener('keydown', closeOnEscape);
            }
        });
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
        
        // Refresh upcoming task highlights every minute
        setInterval(highlightUpcomingTasks, 60000);
    }
    
    init();
})();
