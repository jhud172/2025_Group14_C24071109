// Tab Switching
function switchTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.tcc-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelector(`.tcc-tab[data-tab="${tabName}"]`)?.classList.add('active');
    
    // Update tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.add('tcc-hidden');
    });
    document.getElementById(`tab-${tabName}`)?.classList.remove('tcc-hidden');
}

// Search Functionality
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("schedule-search");
    const cards = document.querySelectorAll("[data-schedule-name]");
    const emptyState = document.getElementById("schedule-search-empty");

    const runFilter = () => {
        const term = (input?.value || "").trim().toLowerCase();
        let visible = 0;

        cards.forEach(card => {
            const name = card.getAttribute("data-schedule-name") || "";
            const show = term.length === 0 || name.includes(term);
            card.classList.toggle("tcc-hidden", !show);
            if (show) {
                visible += 1;
            }
        });

        if (emptyState) {
            emptyState.classList.toggle("tcc-hidden", term.length === 0 || visible > 0);
        }
    };

    if (input) {
        input.addEventListener("input", runFilter);
    }

    document.querySelectorAll('[data-switch-tab]').forEach((button) => {
        button.addEventListener('click', () => {
            switchTab(button.getAttribute('data-switch-tab'));
        });
    });

    document.querySelectorAll('[data-open-create-modal]').forEach((button) => {
        button.addEventListener('click', showCreateModal);
    });

    document.querySelectorAll('[data-close-create-modal]').forEach((button) => {
        button.addEventListener('click', hideCreateModal);
    });

    document.querySelectorAll('[data-open-my-schedules]').forEach((button) => {
        button.addEventListener('click', () => {
            document.querySelector('.tcc-tab[data-tab="my-schedules"]')?.click();
        });
    });

    document.querySelectorAll('[data-open-schedule-preview]').forEach((button) => {
        button.addEventListener('click', () => showSchedulePreview(button));
    });

    document.querySelectorAll('[data-duplicate-schedule]').forEach((button) => {
        button.addEventListener('click', () => duplicateSchedule(button));
    });

    document.querySelectorAll('[data-template-navigation]').forEach((button) => {
        button.addEventListener('click', handleTemplateNavigation);
    });

    document.querySelectorAll('[data-close-preview-modal]').forEach((button) => {
        button.addEventListener('click', hidePreviewModal);
    });

    document.querySelectorAll('[data-overlay-close]').forEach((overlay) => {
        overlay.addEventListener('click', (event) => {
            if (event.target !== overlay) return;

            if (overlay.id === 'create-modal') {
                hideCreateModal();
                return;
            }

            if (overlay.id === 'preview-modal') {
                hidePreviewModal();
            }
        });
    });

    // Load schedule metadata and update insights
    loadScheduleMetadata();
});

// Load schedule metadata (frequency, days) using batch endpoint
async function loadScheduleMetadata() {
    const scheduleMetaElements = document.querySelectorAll('.schedule-meta');
    
    // Collect all schedule IDs
    const scheduleIds = Array.from(scheduleMetaElements)
        .map(el => el.getAttribute('data-schedule-id'))
        .filter(id => id);
    
    if (scheduleIds.length === 0) return;
    
    try {
        // Use batch endpoint to get all metadata in one request
        const params = new URLSearchParams();
        scheduleIds.forEach(id => params.append('ids', id));
        
        const response = await fetch(`/api/schedules/metadata/batch?${params.toString()}`);
        if (response.ok) {
            const batchData = await response.json();
            
            // Update each card with its metadata
            scheduleMetaElements.forEach(metaEl => {
                const scheduleId = metaEl.getAttribute('data-schedule-id');
                const data = batchData[scheduleId];
                
                if (data) {
                    const freqEl = metaEl.querySelector('.schedule-frequency');
                    const daysEl = metaEl.querySelector('.schedule-days');
                    
                    if (freqEl) {
                        freqEl.textContent = data.sessionsPerWeek ? `${data.sessionsPerWeek} sessions/week` : 'Custom';
                    }
                    if (daysEl) {
                        daysEl.textContent = data.activeDays ? `${data.activeDays} active days` : 'Not set';
                    }
                }
            });

            // Update insights bar using active schedule IDs
            updateInsightsFromBatch(batchData);
        }
    } catch (e) {
        // Silently fail - use placeholder text
        scheduleMetaElements.forEach(metaEl => {
            const freqEl = metaEl.querySelector('.schedule-frequency');
            const daysEl = metaEl.querySelector('.schedule-days');
            if (freqEl) freqEl.textContent = 'Custom schedule';
            if (daysEl) daysEl.textContent = 'View details';
        });
    }
}

// Collect active schedule IDs from the active panel data attributes
function getActiveScheduleIds() {
    return Array.from(document.querySelectorAll('.tcc-active-panel'))
        .map(panel => {
            const previewBtn = panel.querySelector('[data-schedule-id]');
            return previewBtn ? previewBtn.getAttribute('data-schedule-id') : null;
        })
        .filter(Boolean);
}

// Calculate rest days from API data (prefer explicit field, fallback to 7 minus active days)
function getRestDaysFromData(data) {
    if (data.restDays != null) return data.restDays;
    return Math.max(0, 7 - (data.activeDays || 0));
}

// Update insights bar with real data from batch API response
function updateInsightsFromBatch(batchData) {
    const activeIds = getActiveScheduleIds();
    if (activeIds.length === 0) return;

    let totalSessions = 0;
    let totalRestDays = 0;
    let count = 0;

    activeIds.forEach(id => {
        const data = batchData[id];
        if (data) {
            totalSessions += (data.sessionsPerWeek || 0);
            totalRestDays += getRestDaysFromData(data);
            count++;
        }
    });

    const weeklyFreqEl = document.getElementById('weekly-frequency');
    const restDaysEl = document.getElementById('rest-days');

    if (weeklyFreqEl && count > 0) {
        weeklyFreqEl.textContent = totalSessions;
    }
    if (restDaysEl && count > 0) {
        restDaysEl.textContent = Math.round(totalRestDays / count);
    }
}

// Create Modal Functions
function showCreateModal() {
    document.getElementById('create-modal')?.classList.remove('tcc-hidden');
    document.body.style.overflow = 'hidden';
}

function hideCreateModal() {
    document.getElementById('create-modal')?.classList.add('tcc-hidden');
    document.body.style.overflow = '';
}

function handleTemplateNavigation() {
    const templatesTab = document.querySelector('.tcc-tab[data-tab=templates]');
    if (templatesTab) {
        templatesTab.click();
        hideCreateModal();
    }
}

function getCsrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-CSRF-TOKEN';
    return token ? { [header]: token } : {};
}

// Preview Modal Functions
function showSchedulePreview(button) {
    const scheduleId = button.getAttribute('data-schedule-id');
    const modal = document.getElementById('preview-modal');
    
    if (!modal) return;
    
    modal.classList.remove('tcc-hidden');
    document.body.style.overflow = 'hidden';
    
    // Load preview data
    loadSchedulePreview(scheduleId);
}

function hidePreviewModal() {
    document.getElementById('preview-modal')?.classList.add('tcc-hidden');
    document.body.style.overflow = '';
}

async function loadSchedulePreview(scheduleId) {
    const contentEl = document.getElementById('preview-content');
    const titleEl = document.getElementById('preview-title');
    
    if (!contentEl) return;
    
    try {
        const response = await fetch(`/api/schedules/${scheduleId}/preview`);
        if (response.ok) {
            const data = await response.json();
            
            if (titleEl) {
                titleEl.textContent = data.name || 'Schedule Preview';
            }
            
            // Render weekly preview
            contentEl.innerHTML = renderWeeklyPreview(data);
        } else {
            contentEl.innerHTML = `
                <div class="text-center py-8">
                    <p class="text-slate-600 dark:text-slate-400">Preview not available</p>
                    <p class="text-sm text-slate-500 dark:text-slate-500 mt-2">This schedule needs to be configured first.</p>
                </div>
            `;
        }
    } catch (e) {
        contentEl.innerHTML = `
            <div class="text-center py-8">
                <p class="text-rose-600 dark:text-rose-400">Failed to load preview</p>
                <p class="text-sm text-slate-500 dark:text-slate-500 mt-2">Please try again later.</p>
            </div>
        `;
    }
}

function renderWeeklyPreview(data) {
    const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    
    let html = '<div class="tcc-preview-week-grid">';
    
    days.forEach((day, index) => {
        const dayNum = index + 1;
        const dayEntries = (data.entries || []).filter(e => e.dayOfWeek === dayNum);
        
        html += `
            <div class="tcc-preview-day">
                <div class="tcc-preview-day-header">${day}</div>
                <div class="tcc-preview-day-content">
        `;
        
        if (dayEntries.length > 0) {
            dayEntries.forEach(entry => {
                const exerciseName = entry.customExercise?.name || entry.exercise?.name || 'Exercise';
                html += `<div class="tcc-preview-workout">${exerciseName}</div>`;
            });
        } else {
            html += `<div class="tcc-preview-rest">Rest day</div>`;
        }
        
        html += `
                </div>
            </div>
        `;
    });
    
    html += '</div>';
    return html;
}

// Duplicate Schedule Function
async function duplicateSchedule(button) {
    const scheduleId = button.getAttribute('data-schedule-id');
    
    if (!confirm('Duplicate this schedule? A copy will be created with "(Copy)" appended to the name.')) {
        return;
    }
    
    button.disabled = true;
    button.innerHTML = '<span class="inline-block w-4 h-4 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin"></span>';
    
    try {
        const response = await fetch(`/api/schedules/${scheduleId}/duplicate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders(),
            },
        });
        
        if (response.ok) {
            // Reload page to show the new schedule
            window.location.reload();
        } else {
            alert('Failed to duplicate schedule. Please try again.');
            button.disabled = false;
            button.innerHTML = `
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                </svg>
                Duplicate
            `;
        }
    } catch (e) {
        alert('An error occurred. Please try again.');
        button.disabled = false;
        button.innerHTML = `
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
            </svg>
            Duplicate
        `;
    }
}

// Keyboard shortcuts
document.addEventListener('keydown', (e) => {
    // Escape key closes modals
    if (e.key === 'Escape') {
        hideCreateModal();
        hidePreviewModal();
    }
    
    // Ctrl/Cmd + K opens search
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.getElementById('schedule-search')?.focus();
    }
});
