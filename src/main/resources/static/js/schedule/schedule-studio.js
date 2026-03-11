/**
 * Schedule Studio (Training Flow Editor) - Interactive JavaScript
 * Handles drag-and-drop, inline editing, insights, and all UI interactions
 */

// ========================================
// State Management
// ========================================
let scheduleState = {
    entries: new Map(), // Map of day -> array of entries
    history: [],
    historyIndex: -1,
    unsavedChanges: false,
    draggedElement: null,
    draggedFrom: null
};

// ========================================
// Initialization
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    initializeScheduleName();
    loadExistingEntries();
    initializeDragAndDrop();
    initializeAddEntryButtons();
    initializeApplyModal();
    initializeAdvancedControls();
    initializeVersionControl();
    updateInsights();
    updateLastUpdatedTime();
    
    // Auto-save schedule name on blur
    const nameEl = document.getElementById('scheduleName');
    if (nameEl) {
        nameEl.addEventListener('blur', saveScheduleName);
        nameEl.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.blur();
            }
        });
    }
});

// ========================================
// Schedule Name Editing
// ========================================
function initializeScheduleName() {
    const nameEl = document.getElementById('scheduleName');
    if (!nameEl) return;
    
    // Make editable on click
    nameEl.addEventListener('click', function() {
        if (this.contentEditable === 'false') {
            this.contentEditable = 'true';
            this.focus();
            // Select all text
            const range = document.createRange();
            range.selectNodeContents(this);
            const sel = window.getSelection();
            sel.removeAllRanges();
            sel.addRange(range);
        }
    });
}

function saveScheduleName() {
    const nameEl = document.getElementById('scheduleName');
    if (!nameEl || nameEl.contentEditable === 'false') return;
    
    const newName = nameEl.textContent.trim();
    if (!newName) {
        nameEl.textContent = scheduleName;
        nameEl.contentEditable = 'false';
        return;
    }
    
    // Save via AJAX
    const formData = new FormData();
    formData.append('name', newName);
    formData.append('description', document.getElementById('scheduleDescription')?.value || '');
    
    fetch(`/schedules/${scheduleId}/update`, {
        method: 'POST',
        headers: {
            [csrfHeader]: csrfToken
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            nameEl.contentEditable = 'false';
            updateLastUpdatedTime();
            showNotification('Schedule name updated', 'success');
        } else {
            nameEl.textContent = scheduleName;
            showNotification('Failed to update name', 'error');
        }
    })
    .catch(() => {
        nameEl.textContent = scheduleName;
        showNotification('Network error', 'error');
    });
}

// ========================================
// Load Existing Entries
// ========================================
function loadExistingEntries() {
    if (!existingEntries || existingEntries.length === 0) {
        return;
    }
    
    existingEntries.forEach(entry => {
        const day = entry.dayOfWeek;
        const exerciseName = entry.exercise ? entry.exercise.name : 'Custom Exercise';
        const exerciseId = entry.exercise ? entry.exercise.id : null;
        
        addWorkoutToDay(day, exerciseId, exerciseName, entry.id);
    });
    
    saveState();
}

// ========================================
// Drag and Drop
// ========================================
function initializeDragAndDrop() {
    const dropZones = document.querySelectorAll('[data-drop-zone]');
    
    dropZones.forEach(zone => {
        zone.addEventListener('dragover', handleDragOver);
        zone.addEventListener('drop', handleDrop);
        zone.addEventListener('dragleave', handleDragLeave);
    });
}

function handleDragOver(e) {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    this.classList.add('drag-over');
}

function handleDragLeave(e) {
    if (e.target === this) {
        this.classList.remove('drag-over');
    }
}

function handleDrop(e) {
    e.preventDefault();
    this.classList.remove('drag-over');
    
    const day = this.getAttribute('data-drop-zone');
    
    if (scheduleState.draggedElement) {
        const fromDay = scheduleState.draggedFrom;
        const workoutId = scheduleState.draggedElement.getAttribute('data-workout-id');
        const exerciseName = scheduleState.draggedElement.getAttribute('data-exercise-name');
        const entryId = scheduleState.draggedElement.getAttribute('data-entry-id');
        
        // Remove from old position
        scheduleState.draggedElement.remove();
        
        // Add to new position
        addWorkoutToDay(day, workoutId, exerciseName, entryId);
        
        saveState();
        updateInsights();
        scheduleState.unsavedChanges = true;
        
        // Save to server
        saveScheduleEntries();
    }
    
    scheduleState.draggedElement = null;
    scheduleState.draggedFrom = null;
}

function makeWorkoutDraggable(element, day) {
    element.setAttribute('draggable', 'true');
    
    element.addEventListener('dragstart', function(e) {
        scheduleState.draggedElement = this;
        scheduleState.draggedFrom = day;
        this.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
    });
    
    element.addEventListener('dragend', function(e) {
        this.classList.remove('dragging');
    });
}

// ========================================
// Add Entry Buttons
// ========================================
function initializeAddEntryButtons() {
    const addButtons = document.querySelectorAll('[data-add-for-day]');
    
    addButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const day = this.getAttribute('data-add-for-day');
            const dayName = getDayName(day);
            showAddEntryModal(day, dayName);
        });
    });
}

function showAddEntryModal(day, dayName) {
    const modal = document.getElementById('addEntryModal');
    const dayNameEl = document.getElementById('addEntryDayName');
    const dayValueEl = document.getElementById('addEntryDayValue');
    
    if (!modal) return;
    
    dayNameEl.textContent = dayName;
    dayValueEl.value = day;
    
    // Set order number to be the next in sequence
    const dayContent = document.querySelector(`[data-drop-zone="${day}"]`);
    const currentCount = dayContent ? dayContent.children.length : 0;
    document.getElementById('addEntryOrderValue').value = currentCount + 1;
    
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('show'), 10);
    
    // Close handlers
    const closeHandlers = [
        document.getElementById('closeAddEntryModalBtn'),
        document.getElementById('cancelAddEntryModalBtn')
    ];
    
    closeHandlers.forEach(btn => {
        if (btn) {
            btn.onclick = () => hideModal('addEntryModal');
        }
    });
    
    // Click outside to close
    modal.onclick = function(e) {
        if (e.target === modal) {
            hideModal('addEntryModal');
        }
    };
}

// ========================================
// Add/Remove Workouts
// ========================================
function addWorkoutToDay(day, exerciseId, exerciseName, entryId = null) {
    const dayContent = document.querySelector(`[data-drop-zone="${day}"]`);
    const dayColumn = document.querySelector(`[data-day="${day}"]`);
    if (!dayContent) return;
    
    const workoutChip = createWorkoutChip(exerciseId, exerciseName, entryId);
    makeWorkoutDraggable(workoutChip, day);
    
    dayContent.appendChild(workoutChip);
    
    // Update day count
    updateDayCount(day);
    updateInsights();
    
    if (dayColumn) {
        dayColumn.classList.add('has-workouts');
    }
}

function createWorkoutChip(exerciseId, exerciseName, entryId) {
    const chip = document.createElement('div');
    chip.className = 'flow-workout-chip animate-slide-in';
    chip.setAttribute('data-workout-id', exerciseId);
    chip.setAttribute('data-exercise-name', exerciseName);
    if (entryId) {
        chip.setAttribute('data-entry-id', entryId);
    }
    
    chip.innerHTML = `
        <div class="flow-workout-chip-header">
            <span class="flow-workout-chip-name">${exerciseName}</span>
            <div class="flow-workout-chip-actions">
                <button type="button" class="flow-workout-chip-btn expand-btn" title="Preview">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                    </svg>
                </button>
                <button type="button" class="flow-workout-chip-btn delete" title="Remove">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>
        </div>
        <div class="flow-workout-chip-preview" style="display: none;">
            <div class="flow-workout-chip-stat">
                <svg class="flow-workout-chip-stat-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
                <span>Strength Training</span>
            </div>
        </div>
    `;
    
    // Delete handler
    const deleteBtn = chip.querySelector('.delete');
    deleteBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        removeWorkoutChip(chip);
    });
    
    // Expand handler
    const expandBtn = chip.querySelector('.expand-btn');
    const preview = chip.querySelector('.flow-workout-chip-preview');
    expandBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        if (preview.style.display === 'none') {
            preview.style.display = 'block';
            preview.classList.add('expanded');
        } else {
            preview.style.display = 'none';
            preview.classList.remove('expanded');
        }
    });
    
    return chip;
}

function removeWorkoutChip(chip) {
    const parent = chip.parentElement;
    const day = parent.getAttribute('data-drop-zone');
    
    chip.classList.add('animate-slide-out');
    setTimeout(() => {
        chip.remove();
        updateDayCount(day);
        updateInsights();
        saveState();
        scheduleState.unsavedChanges = true;
        saveScheduleEntries();
    }, 300);
}

function updateDayCount(day) {
    const dayContent = document.querySelector(`[data-drop-zone="${day}"]`);
    const dayColumn = document.querySelector(`[data-day="${day}"]`);
    const countBadge = dayColumn?.querySelector('.flow-day-count');
    
    if (!dayContent || !countBadge) return;
    
    const count = dayContent.children.length;
    countBadge.textContent = count;
    
    if (count > 0) {
        dayColumn.classList.add('has-workouts');
    } else {
        dayColumn.classList.remove('has-workouts');
    }
}

// ========================================
// Insights Panel
// ========================================
function updateInsights() {
    let totalSessions = 0;
    let activeDaysSet = new Set();
    
    // Count sessions and active days
    document.querySelectorAll('[data-drop-zone]').forEach(zone => {
        const day = zone.getAttribute('data-drop-zone');
        const count = zone.children.length;
        totalSessions += count;
        if (count > 0) {
            activeDaysSet.add(day);
        }
    });
    
    const activeDays = activeDaysSet.size;
    const restDays = 7 - activeDays;
    
    // Update metrics
    document.getElementById('totalSessions').textContent = totalSessions;
    document.getElementById('activeDays').textContent = activeDays;
    document.getElementById('restDays').textContent = restDays;
    document.getElementById('totalEntriesCount').textContent = totalSessions;
    
    // Update status badge
    const statusBadge = document.getElementById('statusBadge');
    if (statusBadge) {
        if (totalSessions > 0) {
            statusBadge.className = 'studio-status-badge applied';
            statusBadge.innerHTML = '<span class="studio-status-dot"></span><span>Active</span>';
        } else {
            statusBadge.className = 'studio-status-badge draft';
            statusBadge.innerHTML = '<span class="studio-status-dot"></span><span>Draft</span>';
        }
    }
    
    // Generate insights
    const insightMessages = document.getElementById('insightMessages');
    if (!insightMessages) return;
    
    insightMessages.innerHTML = '';
    
    // Warning: Too many consecutive training days
    if (activeDays >= 6) {
        insightMessages.innerHTML += `
            <div class="insight-warning">
                <svg class="insight-warning-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
                <span class="insight-warning-text">Limited rest days may increase injury risk. Consider adding recovery time.</span>
            </div>
        `;
    }
    
    // Success: Balanced schedule
    if (activeDays >= 3 && activeDays <= 5 && restDays >= 2) {
        insightMessages.innerHTML += `
            <div class="insight-success">
                <svg class="insight-success-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <span class="insight-success-text">Well-balanced schedule with good recovery time!</span>
            </div>
        `;
    }
    
    // Info: No workouts scheduled
    if (totalSessions === 0) {
        insightMessages.innerHTML += `
            <div class="insight-warning">
                <svg class="insight-warning-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <span class="insight-warning-text">No workouts scheduled yet. Start adding exercises to your days.</span>
            </div>
        `;
    }
}

// ========================================
// Apply to Calendar Modal
// ========================================
function initializeApplyModal() {
    const applyBtn = document.getElementById('applyToCalendarBtn');
    if (!applyBtn) return;
    
    applyBtn.addEventListener('click', showApplyModal);
    
    // Set default start date to today
    const startDateInput = document.getElementById('startDate');
    if (startDateInput) {
        const today = new Date().toISOString().split('T')[0];
        startDateInput.value = today;
    }
    
    // Radio button interactions
    const radioOptions = document.querySelectorAll('[data-radio-group="mode"]');
    radioOptions.forEach(option => {
        option.addEventListener('click', function() {
            radioOptions.forEach(opt => opt.classList.remove('selected'));
            this.classList.add('selected');
            this.querySelector('input').checked = true;
        });
    });
    
    // Close handlers
    const closeHandlers = [
        document.getElementById('closeModalBtn'),
        document.getElementById('cancelModalBtn')
    ];
    
    closeHandlers.forEach(btn => {
        if (btn) {
            btn.addEventListener('click', () => hideModal('applyModal'));
        }
    });
}

function showApplyModal() {
    const modal = document.getElementById('applyModal');
    if (!modal) return;
    
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('show'), 10);
    
    // Click outside to close
    modal.onclick = function(e) {
        if (e.target === modal) {
            hideModal('applyModal');
        }
    };
}

function hideModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    
    modal.classList.remove('show');
    setTimeout(() => {
        modal.style.display = 'none';
        modal.onclick = null;
    }, 300);
}

// ========================================
// Advanced Controls
// ========================================
function initializeAdvancedControls() {
    const btn = document.getElementById('advancedControlsBtn');
    const dropdown = document.getElementById('advancedControlsDropdown');
    
    if (!btn || !dropdown) return;
    
    btn.addEventListener('click', function(e) {
        e.stopPropagation();
        dropdown.classList.toggle('show');
    });
    
    // Close when clicking outside
    document.addEventListener('click', function() {
        dropdown.classList.remove('show');
    });
    
    // Duplicate schedule
    document.getElementById('duplicateScheduleBtn')?.addEventListener('click', duplicateSchedule);
    
    // Shift schedule
    document.getElementById('shiftScheduleBtn')?.addEventListener('click', shiftSchedule);
    
    // Auto-balance
    document.getElementById('autoBalanceBtn')?.addEventListener('click', autoBalanceWorkouts);
    
    // Clear all
    document.getElementById('clearAllBtn')?.addEventListener('click', clearAllWorkouts);
}

function duplicateSchedule() {
    if (!confirm('Create a copy of this schedule?')) return;
    
    showNotification('Duplicate functionality coming soon', 'info');
}

function shiftSchedule() {
    const direction = prompt('Shift workouts forward or backward? (Enter "forward" or "backward")');
    if (!direction) return;
    
    showNotification('Shift functionality coming soon', 'info');
}

function autoBalanceWorkouts() {
    if (!confirm('Distribute workouts evenly across the week?')) return;
    
    showNotification('Auto-balance functionality coming soon', 'info');
}

function clearAllWorkouts() {
    if (!confirm('Remove all workouts from this schedule? This cannot be undone.')) return;
    
    document.querySelectorAll('[data-drop-zone]').forEach(zone => {
        zone.innerHTML = '';
    });
    
    // Update all day counts
    for (let day = 1; day <= 7; day++) {
        updateDayCount(day.toString());
    }
    
    updateInsights();
    saveState();
    scheduleState.unsavedChanges = true;
    saveScheduleEntries();
    
    showNotification('All workouts cleared', 'success');
}

// ========================================
// Version Control (Undo/Reset)
// ========================================
function initializeVersionControl() {
    document.getElementById('undoBtn')?.addEventListener('click', undo);
    document.getElementById('resetBtn')?.addEventListener('click', resetToSaved);
}

function saveState() {
    const state = captureCurrentState();
    
    // If we're not at the end of history, remove everything after current position
    if (scheduleState.historyIndex < scheduleState.history.length - 1) {
        scheduleState.history = scheduleState.history.slice(0, scheduleState.historyIndex + 1);
    }
    
    scheduleState.history.push(state);
    scheduleState.historyIndex = scheduleState.history.length - 1;
    
    // Enable/disable undo button
    const undoBtn = document.getElementById('undoBtn');
    if (undoBtn) {
        undoBtn.disabled = scheduleState.historyIndex <= 0;
    }
}

function captureCurrentState() {
    const state = {};
    
    document.querySelectorAll('[data-drop-zone]').forEach(zone => {
        const day = zone.getAttribute('data-drop-zone');
        const workouts = Array.from(zone.children).map(chip => ({
            exerciseId: chip.getAttribute('data-workout-id'),
            exerciseName: chip.getAttribute('data-exercise-name'),
            entryId: chip.getAttribute('data-entry-id')
        }));
        state[day] = workouts;
    });
    
    return state;
}

function restoreState(state) {
    // Clear all days
    document.querySelectorAll('[data-drop-zone]').forEach(zone => {
        zone.innerHTML = '';
    });
    
    // Restore workouts
    Object.keys(state).forEach(day => {
        state[day].forEach(workout => {
            addWorkoutToDay(day, workout.exerciseId, workout.exerciseName, workout.entryId);
        });
    });
    
    updateInsights();
}

function undo() {
    if (scheduleState.historyIndex <= 0) return;
    
    scheduleState.historyIndex--;
    const state = scheduleState.history[scheduleState.historyIndex];
    restoreState(state);
    
    const undoBtn = document.getElementById('undoBtn');
    if (undoBtn) {
        undoBtn.disabled = scheduleState.historyIndex <= 0;
    }
    
    scheduleState.unsavedChanges = true;
    saveScheduleEntries();
    
    showNotification('Undo successful', 'success');
}

function resetToSaved() {
    if (!confirm('Reset schedule to last saved state? All unsaved changes will be lost.')) return;
    
    // Reload the page to get fresh data
    location.reload();
}

// ========================================
// Save Schedule Entries
// ========================================
function saveScheduleEntries() {
    const entries = [];
    
    document.querySelectorAll('[data-drop-zone]').forEach(zone => {
        const day = zone.getAttribute('data-drop-zone');
        const workouts = Array.from(zone.children);
        
        workouts.forEach((chip, index) => {
            entries.push({
                dayOfWeek: parseInt(day),
                orderNumber: index,
                exerciseId: chip.getAttribute('data-workout-id'),
                entryId: chip.getAttribute('data-entry-id')
            });
        });
    });
    
    // Save via AJAX (simplified - actual implementation would depend on backend)
    console.log('Saving entries:', entries);
    updateLastUpdatedTime();
}

// ========================================
// Utility Functions
// ========================================
function getDayName(dayNum) {
    const days = {
        '1': 'Monday',
        '2': 'Tuesday',
        '3': 'Wednesday',
        '4': 'Thursday',
        '5': 'Friday',
        '6': 'Saturday',
        '7': 'Sunday'
    };
    return days[dayNum] || 'Day';
}

function updateLastUpdatedTime() {
    const timeEl = document.getElementById('lastUpdatedTime');
    if (!timeEl) return;
    
    const now = new Date();
    const timeStr = now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    timeEl.textContent = `Today at ${timeStr}`;
}

function showNotification(message, type = 'info') {
    // Simple notification - could be enhanced with a toast library
    const notifColors = {
        success: 'bg-emerald-500',
        error: 'bg-red-500',
        info: 'bg-blue-500',
        warning: 'bg-amber-500'
    };
    
    const notif = document.createElement('div');
    notif.className = `fixed bottom-4 right-4 ${notifColors[type]} text-white px-4 py-3 rounded-lg shadow-lg z-50 animate-slide-in`;
    notif.textContent = message;
    
    document.body.appendChild(notif);
    
    setTimeout(() => {
        notif.classList.add('animate-slide-out');
        setTimeout(() => notif.remove(), 300);
    }, 3000);
}
