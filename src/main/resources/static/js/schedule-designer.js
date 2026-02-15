/**
 * Training Structure Designer JavaScript
 * Premium schedule builder with advanced features
 */

// State management
const ScheduleDesigner = {
    scheduleType: 'WEEKLY',
    rotationMode: 'WEEKLY_REPEAT',
    customDayCount: 7,
    currentDays: [],
    draggedItem: null,
    sourceZone: null,
    ghost: null,
    templateId: null,
    
    /**
     * Initialize the schedule designer
     */
    init() {
        this.setupEventListeners();
        this.generateScheduleGrid();
        this.updateStats();
    },
    
    /**
     * Setup all event listeners
     */
    setupEventListeners() {
        // Schedule type selection
        document.querySelectorAll('input[name="scheduleType"]').forEach(radio => {
            radio.addEventListener('change', (e) => this.handleScheduleTypeChange(e.target.value));
        });
        
        // Rotation mode selection
        document.querySelectorAll('input[name="rotationMode"]').forEach(radio => {
            radio.addEventListener('change', (e) => this.handleRotationModeChange(e.target.value));
        });
        
        // Custom day count
        const applyCustomDaysBtn = document.getElementById('applyCustomDays');
        if (applyCustomDaysBtn) {
            applyCustomDaysBtn.addEventListener('click', () => this.applyCustomDayCount());
        }
        
        // Template controls
        const showTemplatesBtn = document.getElementById('showTemplatesBtn');
        const hideTemplatesBtn = document.getElementById('hideTemplatesBtn');
        const templateLibrary = document.getElementById('templateLibrary');
        
        if (showTemplatesBtn) {
            showTemplatesBtn.addEventListener('click', () => {
                templateLibrary.classList.toggle('hidden');
            });
        }
        
        if (hideTemplatesBtn) {
            hideTemplatesBtn.addEventListener('click', () => {
                templateLibrary.classList.add('hidden');
            });
        }
        
        // Template selection
        document.querySelectorAll('.template-select-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const templateId = e.target.dataset.templateId;
                this.applyTemplate(templateId);
            });
        });
        
        // Advanced controls
        const clearAllBtn = document.getElementById('clearAllBtn');
        if (clearAllBtn) {
            clearAllBtn.addEventListener('click', () => this.clearAll());
        }
        
        const duplicateScheduleBtn = document.getElementById('duplicateScheduleBtn');
        if (duplicateScheduleBtn) {
            duplicateScheduleBtn.addEventListener('click', () => this.duplicateSchedule());
        }
        
        // Workout search
        const searchInput = document.getElementById('workout-search');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.searchWorkouts(e.target.value));
        }
        
        // Draggable workout items from sidebar
        this.setupSidebarDragHandlers();
        
        // Form submission
        const saveForm = document.getElementById('saveForm');
        if (saveForm) {
            saveForm.addEventListener('submit', (e) => this.handleSaveForm(e));
        }
        
        // Warn before leaving with unsaved changes
        let hasChanges = false;
        const scheduleGrid = document.getElementById('scheduleGrid');
        if (scheduleGrid) {
            const observer = new MutationObserver(() => {
                hasChanges = true;
            });
            observer.observe(scheduleGrid, { childList: true, subtree: true });
        }
        
        window.addEventListener('beforeunload', (e) => {
            if (hasChanges) {
                e.preventDefault();
                e.returnValue = '';
            }
        });
        
        saveForm?.addEventListener('submit', () => {
            hasChanges = false;
        });
    },
    
    /**
     * Handle schedule type change
     */
    handleScheduleTypeChange(type) {
        this.scheduleType = type;
        document.getElementById('scheduleTypeField').value = type;
        
        // Show/hide custom day controls
        const customDayControls = document.getElementById('customDayControls');
        const rotationModeSection = document.getElementById('rotationModeSection');
        
        if (type === 'CUSTOM') {
            customDayControls?.classList.remove('hidden');
            rotationModeSection?.classList.remove('hidden');
        } else {
            customDayControls?.classList.add('hidden');
            rotationModeSection?.classList.add('hidden');
        }
        
        this.generateScheduleGrid();
        this.updateStats();
    },
    
    /**
     * Handle rotation mode change
     */
    handleRotationModeChange(mode) {
        this.rotationMode = mode;
        document.getElementById('rotationModeField').value = mode;
    },
    
    /**
     * Apply custom day count
     */
    applyCustomDayCount() {
        const input = document.getElementById('customDayInput');
        const count = parseInt(input.value);
        
        if (count >= 1 && count <= 14) {
            this.customDayCount = count;
            document.getElementById('customDayCountField').value = count;
            this.generateScheduleGrid();
            this.updateStats();
        }
    },
    
    /**
     * Generate the schedule grid based on current type
     */
    generateScheduleGrid() {
        const container = document.getElementById('scheduleGrid');
        if (!container) return;
        
        // Preserve current workouts
        const currentWorkouts = this.extractCurrentWorkouts();
        
        // Clear grid
        container.innerHTML = '';
        
        // Update grid classes
        container.className = 'schedule-grid';
        if (this.scheduleType === 'CUSTOM') {
            container.classList.add('custom-schedule');
        } else if (this.scheduleType === 'DAILY') {
            container.classList.add('daily-schedule');
        }
        
        // Generate days
        const dayLabels = this.getDayLabels();
        this.currentDays = dayLabels;
        
        dayLabels.forEach((label, index) => {
            const dayCard = this.createDayCard(label, index + 1);
            container.appendChild(dayCard);
        });
        
        // Restore workouts
        this.restoreWorkouts(currentWorkouts);
        
        // Setup drop zones
        this.setupDropZones();
    },
    
    /**
     * Get day labels based on schedule type
     */
    getDayLabels() {
        switch (this.scheduleType) {
            case 'WEEKLY':
                return ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
            case 'DAILY':
                return ['Daily Routine'];
            case 'CUSTOM':
                return Array.from({ length: this.customDayCount }, (_, i) => `Day ${i + 1}`);
            default:
                return [];
        }
    },
    
    /**
     * Create a day card element
     */
    createDayCard(label, dayIndex) {
        const card = document.createElement('div');
        card.className = 'schedule-day-card';
        card.dataset.day = label;
        card.dataset.dayIndex = dayIndex;
        
        card.innerHTML = `
            <div class="schedule-day-card-header">
                <div class="schedule-day-header-content">
                    <h3 class="schedule-day-title">${label}</h3>
                    <span class="schedule-day-badge">
                        <span class="schedule-day-badge-dot"></span>
                        <span class="count">0</span>
                    </span>
                </div>
            </div>
            <div class="schedule-drop-zone" data-day="${label}">
                <div class="schedule-day-rest-state">
                    <svg class="schedule-day-rest-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"/>
                    </svg>
                    <div class="schedule-day-rest-label">Rest Day</div>
                    <div class="schedule-day-rest-action">Click to mark as active recovery</div>
                </div>
            </div>
        `;
        
        return card;
    },
    
    /**
     * Safely escape text for insertion into HTML.
     */
    escapeHtml(text) {
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    },
    
    /**
     * Create a workout chip element
     */
    createWorkoutChip(id, name) {
        const chip = document.createElement('div');
        chip.className = 'schedule-workout-chip group';
        chip.draggable = true;
        chip.dataset.id = id;
        chip.dataset.name = name;
        
        const safeName = this.escapeHtml(String(name));
        
        chip.innerHTML = `
            <svg class="schedule-workout-chip-drag-handle w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
            <div class="schedule-workout-chip-content">
                <div class="schedule-workout-chip-name">${safeName}</div>
                <div class="schedule-workout-chip-meta">Tap to preview</div>
            </div>
            <div class="schedule-workout-chip-actions">
                <button type="button" class="schedule-workout-chip-action duplicate-workout" title="Duplicate">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                    </svg>
                </button>
                <button type="button" class="schedule-workout-chip-action schedule-workout-chip-remove" title="Remove">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>
        `;
        
        this.addWorkoutChipHandlers(chip);
        return chip;
    },
    
    /**
     * Add event handlers to workout chip
     */
    addWorkoutChipHandlers(chip) {
        // Drag handlers
        chip.addEventListener('dragstart', (e) => {
            this.draggedItem = chip;
            this.sourceZone = chip.parentElement;
            chip.classList.add('dragging');
            
            // Create ghost element
            this.ghost = chip.cloneNode(true);
            this.ghost.style.opacity = '0.6';
            this.ghost.style.position = 'absolute';
            this.ghost.style.top = '-9999px';
            document.body.appendChild(this.ghost);
            e.dataTransfer.setDragImage(this.ghost, 0, 0);
        });
        
        chip.addEventListener('dragend', () => {
            if (this.draggedItem) {
                this.draggedItem.classList.remove('dragging');
            }
            this.draggedItem = null;
            this.sourceZone = null;
            if (this.ghost) {
                this.ghost.remove();
                this.ghost = null;
            }
            this.updateStats();
        });
        
        // Remove button
        const removeBtn = chip.querySelector('.schedule-workout-chip-remove');
        if (removeBtn) {
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                chip.remove();
                this.updateStats();
            });
        }
        
        // Duplicate button
        const duplicateBtn = chip.querySelector('.duplicate-workout');
        if (duplicateBtn) {
            duplicateBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                const newChip = this.createWorkoutChip(chip.dataset.id, chip.dataset.name);
                chip.parentElement.insertBefore(newChip, chip.nextSibling);
                this.updateStats();
            });
        }
        
        // Context menu (right-click)
        chip.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            chip.remove();
            this.updateStats();
        });
    },
    
    /**
     * Setup sidebar drag handlers
     */
    setupSidebarDragHandlers() {
        document.querySelectorAll('.draggable-ex').forEach(item => {
            item.addEventListener('dragstart', (e) => {
                // Clone the workout item instead of moving it
                const clone = this.createWorkoutChip(item.dataset.id, item.dataset.name);
                this.draggedItem = clone;
                this.sourceZone = null; // Indicates it's from sidebar
                
                // Create ghost
                this.ghost = item.cloneNode(true);
                this.ghost.style.opacity = '0.6';
                this.ghost.style.position = 'absolute';
                this.ghost.style.top = '-9999px';
                document.body.appendChild(this.ghost);
                e.dataTransfer.setDragImage(this.ghost, 0, 0);
            });
            
            item.addEventListener('dragend', () => {
                if (this.ghost) {
                    this.ghost.remove();
                    this.ghost = null;
                }
            });
        });
    },
    
    /**
     * Setup drop zone handlers
     */
    setupDropZones() {
        document.querySelectorAll('.schedule-drop-zone').forEach(zone => {
            zone.addEventListener('dragover', (e) => {
                e.preventDefault();
                zone.classList.add('drag-over');
            });
            
            zone.addEventListener('dragleave', (e) => {
                if (e.target === zone) {
                    zone.classList.remove('drag-over');
                }
            });
            
            zone.addEventListener('drop', (e) => {
                e.preventDefault();
                zone.classList.remove('drag-over');
                
                if (!this.draggedItem) return;
                
                // Hide rest state
                const restState = zone.querySelector('.schedule-day-rest-state');
                if (restState) {
                    restState.style.display = 'none';
                }
                
                // If from sidebar, append the cloned chip
                // If from another zone, move the chip
                if (this.sourceZone === null) {
                    // From sidebar - the draggedItem is already a new chip
                    zone.appendChild(this.draggedItem);
                } else {
                    // From another zone - move it
                    zone.appendChild(this.draggedItem);
                    
                    // Show rest state in source if empty
                    if (this.sourceZone && this.sourceZone.children.length === 1) {
                        const sourceRestState = this.sourceZone.querySelector('.schedule-day-rest-state');
                        if (sourceRestState) {
                            sourceRestState.style.display = '';
                        }
                    }
                }
                
                this.updateDayCardState(zone.closest('.schedule-day-card'));
                if (this.sourceZone) {
                    this.updateDayCardState(this.sourceZone.closest('.schedule-day-card'));
                }
                
                this.updateStats();
            });
        });
    },
    
    /**
     * Update day card visual state
     */
    updateDayCardState(card) {
        if (!card) return;
        
        const dropZone = card.querySelector('.schedule-drop-zone');
        const chips = dropZone.querySelectorAll('.schedule-workout-chip');
        const count = chips.length;
        const badge = card.querySelector('.schedule-day-badge .count');
        const restState = card.querySelector('.schedule-day-rest-state');
        
        if (badge) {
            badge.textContent = count;
        }
        
        if (count > 0) {
            card.classList.add('has-workouts');
            if (restState) {
                restState.style.display = 'none';
            }
        } else {
            card.classList.remove('has-workouts');
            if (restState) {
                restState.style.display = '';
            }
        }
    },
    
    /**
     * Update schedule statistics and intelligence
     */
    updateStats() {
        const allChips = document.querySelectorAll('.schedule-workout-chip');
        const totalWorkouts = allChips.length;
        
        const daysWithWorkouts = new Set();
        document.querySelectorAll('.schedule-drop-zone').forEach(zone => {
            const chips = zone.querySelectorAll('.schedule-workout-chip');
            if (chips.length > 0) {
                daysWithWorkouts.add(zone.dataset.day);
            }
        });
        
        const activeDays = daysWithWorkouts.size;
        const totalDays = this.currentDays.length;
        const restDays = totalDays - activeDays;
        const frequency = totalDays > 0 ? Math.round((activeDays / totalDays) * 100) : 0;
        const avgPerDay = activeDays > 0 ? (totalWorkouts / activeDays).toFixed(1) : '0.0';
        
        // Update stat displays
        document.getElementById('totalWorkouts').textContent = totalWorkouts;
        document.getElementById('activeDays').textContent = activeDays;
        document.getElementById('restDays').textContent = restDays;
        document.getElementById('frequency').textContent = frequency + '%';
        document.getElementById('avgPerDay').textContent = avgPerDay;
        
        // Update all day card states
        document.querySelectorAll('.schedule-day-card').forEach(card => {
            this.updateDayCardState(card);
        });
        
        // Generate insights
        this.generateInsights(totalWorkouts, activeDays, restDays, frequency);
    },
    
    /**
     * Generate intelligent insights
     */
    generateInsights(totalWorkouts, activeDays, restDays, frequency) {
        const container = document.getElementById('intelligenceInsights');
        if (!container) return;
        
        container.innerHTML = '';
        
        const insights = [];
        
        // Training frequency insight
        if (frequency >= 80) {
            insights.push({
                type: 'warning',
                title: 'High Training Frequency',
                description: `You're training ${activeDays} days. Ensure adequate recovery between sessions.`
            });
        } else if (frequency >= 50 && frequency < 80) {
            insights.push({
                type: 'success',
                title: 'Balanced Training Schedule',
                description: `Great balance with ${activeDays} training days and ${restDays} rest days.`
            });
        } else if (frequency > 0) {
            insights.push({
                type: 'info',
                title: 'Light Training Load',
                description: `You have ${activeDays} training days. Consider adding more for better results.`
            });
        }
        
        // Consecutive days warning
        const consecutiveDays = this.findConsecutiveTrainingDays();
        if (consecutiveDays >= 4) {
            insights.push({
                type: 'warning',
                title: 'Consecutive Training Days Detected',
                description: `You have ${consecutiveDays} consecutive training days. Consider adding a rest day.`
            });
        }
        
        // Rest day recommendation
        if (restDays === 0 && this.scheduleType === 'WEEKLY') {
            insights.push({
                type: 'warning',
                title: 'No Rest Days',
                description: 'Consider adding at least 1-2 rest days per week for recovery.'
            });
        }
        
        // Empty schedule
        if (totalWorkouts === 0) {
            insights.push({
                type: 'info',
                title: 'Get Started',
                description: 'Start building your schedule by dragging workouts onto days or selecting a template.'
            });
        }
        
        // Render insights
        insights.forEach(insight => {
            const insightEl = document.createElement('div');
            insightEl.className = `intelligence-insight ${insight.type}`;
            insightEl.innerHTML = `
                <svg class="intelligence-insight-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    ${insight.type === 'warning' 
                        ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>'
                        : insight.type === 'success'
                        ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>'
                        : '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>'
                    }
                </svg>
                <div class="intelligence-insight-content">
                    <div class="intelligence-insight-title">${insight.title}</div>
                    <div class="intelligence-insight-description">${insight.description}</div>
                </div>
            `;
            container.appendChild(insightEl);
        });
    },
    
    /**
     * Find consecutive training days
     */
    findConsecutiveTrainingDays() {
        let maxConsecutive = 0;
        let current = 0;
        
        const zones = Array.from(document.querySelectorAll('.schedule-drop-zone'));
        zones.forEach(zone => {
            const hasWorkouts = zone.querySelectorAll('.schedule-workout-chip').length > 0;
            if (hasWorkouts) {
                current++;
                maxConsecutive = Math.max(maxConsecutive, current);
            } else {
                current = 0;
            }
        });
        
        return maxConsecutive;
    },
    
    /**
     * Search workouts in sidebar
     */
    searchWorkouts(query) {
        const workoutItems = document.querySelectorAll('.workout-item');
        const searchTerm = query.trim().toLowerCase();
        
        workoutItems.forEach(item => {
            const name = item.dataset.name.toLowerCase();
            item.style.display = name.includes(searchTerm) ? '' : 'none';
        });
    },
    
    /**
     * Extract current workouts before regenerating grid
     */
    extractCurrentWorkouts() {
        const workouts = {};
        
        document.querySelectorAll('.schedule-drop-zone').forEach(zone => {
            const dayLabel = zone.dataset.day;
            const chips = Array.from(zone.querySelectorAll('.schedule-workout-chip'));
            
            workouts[dayLabel] = chips.map(chip => ({
                id: chip.dataset.id,
                name: chip.dataset.name
            }));
        });
        
        return workouts;
    },
    
    /**
     * Restore workouts after regenerating grid
     */
    restoreWorkouts(workouts) {
        Object.entries(workouts).forEach(([dayLabel, items]) => {
            const zone = document.querySelector(`.schedule-drop-zone[data-day="${dayLabel}"]`);
            if (zone) {
                items.forEach(item => {
                    const chip = this.createWorkoutChip(item.id, item.name);
                    zone.appendChild(chip);
                });
            }
        });
    },
    
    /**
     * Apply a template
     */
    applyTemplate(templateId) {
        this.templateId = templateId;
        document.getElementById('templateIdField').value = templateId;
        
        // Hide template library
        document.getElementById('templateLibrary')?.classList.add('hidden');
        
        // Show success message
        this.showNotification('Template applied! You can now customize it.');
    },
    
    /**
     * Clear all workouts
     */
    clearAll() {
        // TODO: Replace with custom modal for better UX and accessibility
        if (!confirm('Are you sure you want to clear all workouts from the schedule?')) {
            return;
        }
        
        document.querySelectorAll('.schedule-workout-chip').forEach(chip => chip.remove());
        this.updateStats();
        this.showNotification('Schedule cleared');
    },
    
    /**
     * Duplicate entire schedule
     */
    duplicateSchedule() {
        const currentWorkouts = this.extractCurrentWorkouts();
        
        // Create a copy with modified name
        const nameInput = document.getElementById('scheduleName');
        if (nameInput) {
            const currentName = nameInput.value;
            nameInput.value = currentName ? `${currentName} (Copy)` : 'Copy';
        }
        
        this.showNotification('Schedule duplicated. Modify the name and save as new.');
    },
    
    /**
     * Handle form submission
     */
    handleSaveForm(e) {
        const output = {};
        
        document.querySelectorAll('.schedule-day-card').forEach(card => {
            const dayLabel = card.dataset.day;
            const dayIndex = card.dataset.dayIndex;
            const dropZone = card.querySelector('.schedule-drop-zone');
            const chips = Array.from(dropZone.querySelectorAll('.schedule-workout-chip'));
            
            const workoutIds = chips.map(chip => Number(chip.dataset.id));
            
            // Use day label as key for compatibility
            const key = this.scheduleType === 'WEEKLY' 
                ? this.getDayAbbreviation(dayLabel)
                : dayLabel;
            
            output[key] = workoutIds;
        });
        
        document.getElementById('payloadField').value = JSON.stringify(output);
        
        // Show success indicator
        const timestamp = document.getElementById('saveTimestamp');
        if (timestamp) {
            timestamp.classList.remove('hidden');
            document.getElementById('lastSavedText').textContent = 'Saving...';
        }
        
        return true;
    },
    
    /**
     * Get day abbreviation for backend compatibility
     */
    getDayAbbreviation(fullDay) {
        const map = {
            'Monday': 'Mon',
            'Tuesday': 'Tue',
            'Wednesday': 'Wed',
            'Thursday': 'Thu',
            'Friday': 'Fri',
            'Saturday': 'Sat',
            'Sunday': 'Sun'
        };
        return map[fullDay] || fullDay;
    },
    
    /**
     * Show notification toast
     */
    showNotification(message) {
        // Simple notification - could be enhanced with a toast library
        const toast = document.createElement('div');
        toast.className = 'fixed top-4 right-4 bg-emerald-600 text-white px-4 py-3 rounded-lg shadow-lg z-50 animate-slideIn';
        toast.textContent = message;
        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.remove();
        }, 3000);
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    ScheduleDesigner.init();
});

// Add CSS for notification animation
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    .animate-slideIn {
        animation: slideIn 0.3s ease-out;
    }
`;
document.head.appendChild(style);
