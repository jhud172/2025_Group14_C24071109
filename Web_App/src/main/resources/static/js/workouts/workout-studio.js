/**
 * Workout Studio - Premium Workout Creation Interface
 * Handles mode switching, exercise library, favourites, builder, and suggestions
 */

(function() {
    'use strict';

    // ============================================
    // STATE MANAGEMENT
    // ============================================
    const state = {
        currentMode: 'my-workouts',
        favourites: new Set(),
        builder: {
            exercises: [],
            draggedItem: null
        },
        suggestions: {
            shown: 3,
            total: 0,
            isPremium: false
        },
        library: {
            searchQuery: '',
            activeFilters: new Set(),
            exercises: []
        }
    };
    let customExerciseOpener = null;

    // ============================================
    // MODE SWITCHING
    // ============================================
    function initModeNavigation() {
        const tabs = document.querySelectorAll('.studio-mode-tab');
        const panels = document.querySelectorAll('.studio-mode-panel');

        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const targetMode = tab.dataset.mode;
                switchMode(targetMode, tabs, panels);
            });
        });

        document.querySelectorAll('[data-open-builder]').forEach((button) => {
            button.addEventListener('click', () => {
                switchMode('builder', tabs, panels);
            });
        });
    }

    function switchMode(targetMode, tabs, panels) {
        state.currentMode = targetMode;

        // Update tabs
        tabs.forEach(tab => {
            if (tab.dataset.mode === targetMode) {
                tab.classList.add('active');
            } else {
                tab.classList.remove('active');
            }
        });

        // Update panels with fade animation
        panels.forEach(panel => {
            if (panel.dataset.mode === targetMode) {
                panel.classList.remove('hidden');
                panel.classList.add('active', 'animate-fade-in');
            } else {
                panel.classList.remove('active');
                panel.classList.add('hidden');
            }
        });

        // Load mode-specific data
        loadModeData(targetMode);
    }

    function loadModeData(mode) {
        switch(mode) {
            case 'library':
                loadExerciseLibrary();
                break;
            case 'builder':
                updateBuilderPreview();
                break;
            case 'my-workouts':
                // Already loaded from server
                break;
        }
    }

    // ============================================
    // EXERCISE LIBRARY
    // ============================================
    function initExerciseLibrary() {
        const searchInput = document.querySelector('.library-search-input');
        const filterTags = document.querySelectorAll('.library-filter-tag');

        if (searchInput) {
            searchInput.addEventListener('input', debounce(handleLibrarySearch, 300));
        }

        filterTags.forEach(tag => {
            tag.addEventListener('click', () => toggleFilter(tag));
        });

        // Load exercises from server
        loadExerciseLibrary();
    }

    function handleLibrarySearch(e) {
        state.library.searchQuery = e.target.value.toLowerCase();
        filterExerciseLibrary();
    }

    function toggleFilter(tag) {
        const filterValue = tag.dataset.filter;
        
        if (state.library.activeFilters.has(filterValue)) {
            state.library.activeFilters.delete(filterValue);
            tag.classList.remove('active');
        } else {
            state.library.activeFilters.add(filterValue);
            tag.classList.add('active');
        }
        
        filterExerciseLibrary();
    }

    function filterExerciseLibrary() {
        const cards = document.querySelectorAll('.exercise-card');
        
        cards.forEach(card => {
            const name = card.dataset.name?.toLowerCase() || '';
            const category = card.dataset.category?.toLowerCase() || '';
            const difficulty = card.dataset.difficulty || '';
            const type = card.dataset.type?.toLowerCase() || '';

            let matches = true;

            // Search query
            if (state.library.searchQuery) {
                matches = name.includes(state.library.searchQuery) || 
                         category.includes(state.library.searchQuery);
            }

            // Filters
            if (matches && state.library.activeFilters.size > 0) {
                matches = Array.from(state.library.activeFilters).some(filter => {
                    return category === filter || 
                           difficulty === filter || 
                           type === filter;
                });
            }

            card.style.display = matches ? '' : 'none';
        });
    }

    async function loadExerciseLibrary() {
        try {
            const response = await fetch('/api/exercises/all');
            if (response.ok) {
                const exercises = await response.json();
                state.library.exercises = exercises;
                renderExerciseLibrary(exercises);
                loadFavourites();
            }
        } catch (error) {
            console.error('Failed to load exercise library:', error);
        }
    }

    function renderExerciseLibrary(exercises) {
        const grid = document.querySelector('.library-grid');
        if (!grid) return;

        grid.innerHTML = exercises.map(exercise => createExerciseCard(exercise)).join('');
        
        // Reattach event listeners
        initExerciseCardActions();
    }

    function createExerciseCard(exercise) {
        const difficultyClass = `difficulty-${exercise.difficulty || 2}`;
        const difficultyLabel = ['', 'Beginner', 'Intermediate', 'Advanced', 'Expert'][exercise.difficulty || 2];
        
        return `
            <div class="exercise-card group" 
                 data-id="${exercise.id}"
                 data-name="${exercise.name}"
                 data-category="${exercise.category || ''}"
                 data-difficulty="${exercise.difficulty || 2}"
                 data-type="${exercise.type || ''}">
                <div class="exercise-card-image-wrapper">
                    ${exercise.imageUrl ? 
                        `<img src="${exercise.imageUrl}" alt="${exercise.name}" class="exercise-card-image">` :
                        `<div class="exercise-card-image flex items-center justify-center text-4xl text-slate-300">💪</div>`
                    }
                    <div class="exercise-card-difficulty ${difficultyClass}">${difficultyLabel}</div>
                    <div class="exercise-card-favourite-btn" data-exercise-id="${exercise.id}">
                        <svg class="exercise-card-favourite-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                        </svg>
                    </div>
                </div>
                <div class="exercise-card-body">
                    <div class="exercise-card-header">
                        <h3 class="exercise-card-title">${exercise.name}</h3>
                    </div>
                    <span class="exercise-card-category">${exercise.category || 'General'}</span>
                    <p class="exercise-card-description">${exercise.description || 'No description available'}</p>
                    <div class="exercise-card-footer">
                        <span class="exercise-card-type">${exercise.type || 'strength'}</span>
                        <button class="exercise-card-add-btn" data-exercise-id="${exercise.id}" title="Add to workout">
                            <svg class="exercise-card-add-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    function initExerciseCardActions() {
        // Favourite buttons
        document.querySelectorAll('.exercise-card-favourite-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                toggleFavourite(btn);
            });
        });

        // Add to workout buttons
        document.querySelectorAll('.exercise-card-add-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                addExerciseToBuilder(btn.dataset.exerciseId);
            });
        });
    }

    // ============================================
    // FAVOURITES SYSTEM
    // ============================================
    async function loadFavourites() {
        try {
            const response = await fetch('/api/favourites');
            if (response.ok) {
                const favourites = await response.json();
                favourites.forEach(fav => {
                    if (fav.exerciseId) {
                        state.favourites.add(fav.exerciseId);
                    }
                });
                updateFavouriteUI();
            }
        } catch (error) {
            console.error('Failed to load favourites:', error);
        }
    }

    async function toggleFavourite(btn) {
        const exerciseId = parseInt(btn.dataset.exerciseId);
        const isFavourited = state.favourites.has(exerciseId);

        try {
            if (isFavourited) {
                // Remove favourite
                const response = await fetch(`/api/favourites/${exerciseId}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': getCsrfToken()
                    }
                });
                
                if (response.ok) {
                    state.favourites.delete(exerciseId);
                    btn.classList.remove('favourited');
                    showFeedback('Removed from favourites', 'info');
                }
            } else {
                // Add favourite
                const response = await fetch('/api/favourites', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': getCsrfToken()
                    },
                    body: JSON.stringify({ exerciseId: exerciseId })
                });
                
                if (response.ok) {
                    state.favourites.add(exerciseId);
                    btn.classList.add('favourited');
                    showFeedback('Added to favourites', 'success');
                }
            }
        } catch (error) {
            console.error('Failed to toggle favourite:', error);
            showFeedback('Failed to update favourite', 'error');
        }
    }

    function updateFavouriteUI() {
        document.querySelectorAll('.exercise-card-favourite-btn').forEach(btn => {
            const exerciseId = parseInt(btn.dataset.exerciseId);
            if (state.favourites.has(exerciseId)) {
                btn.classList.add('favourited');
            } else {
                btn.classList.remove('favourited');
            }
        });
    }

    // ============================================
    // WORKOUT BUILDER
    // ============================================
    function initWorkoutBuilder() {
        const addBtn = document.querySelector('[data-add-exercise]');
        if (addBtn) {
            addBtn.addEventListener('click', addExerciseRow);
        }

        // Remove buttons
        initExerciseRemoveButtons();

        // Drag and drop
        initDragAndDrop();

        // Auto-save on input (debounced)
        const inputs = document.querySelectorAll('.builder-input, .builder-textarea, .builder-exercise-input, .builder-exercise-select');
        inputs.forEach(input => {
            input.addEventListener('input', debounce(updateBuilderPreview, 500));
        });
    }

    function addExerciseRow() {
        const template = document.querySelector('[data-exercise-template]');
        const list = document.querySelector('[data-exercise-list]');
        
        if (!template || !list) return;

        const clone = template.content.cloneNode(true);
        const index = list.children.length;
        
        // Update field names
        clone.querySelectorAll('[name*="__name__"]').forEach(field => {
            field.name = field.name.replace('__name__', `exercises[${index}]`);
        });

        list.appendChild(clone);
        
        // Reattach remove listeners
        initExerciseRemoveButtons();
        
        // Animate in
        const newItem = list.lastElementChild;
        newItem.classList.add('animate-bounce-in');
        
        updateBuilderPreview();
    }

    function initExerciseRemoveButtons() {
        document.querySelectorAll('[data-remove-exercise]').forEach(btn => {
            btn.removeEventListener('click', removeExerciseRow);
            btn.addEventListener('click', removeExerciseRow);
        });
    }

    function removeExerciseRow(e) {
        const item = e.target.closest('.builder-exercise-item');
        if (item) {
            item.style.opacity = '0';
            item.style.transform = 'scale(0.9)';
            setTimeout(() => {
                item.remove();
                reindexExercises();
                updateBuilderPreview();
            }, 200);
        }
    }

    function reindexExercises() {
        const list = document.querySelector('[data-exercise-list]');
        if (!list) return;

        const items = list.querySelectorAll('.builder-exercise-item');
        items.forEach((item, index) => {
            item.querySelectorAll('[name^="exercises["]').forEach(field => {
                field.name = field.name.replace(/exercises\[\d+\]/, `exercises[${index}]`);
            });
        });
    }

    function updateBuilderPreview() {
        const exerciseItems = document.querySelectorAll('.builder-exercise-item');
        const previewList = document.querySelector('.builder-preview-list');
        const exerciseCount = document.querySelector('.builder-stat-value[data-stat="exercises"]');
        const durationEstimate = document.querySelector('.builder-stat-value[data-stat="duration"]');

        if (exerciseCount) {
            exerciseCount.textContent = exerciseItems.length;
        }

        // Calculate estimated duration (sets × 45s + rest)
        let totalSeconds = 0;
        exerciseItems.forEach(item => {
            const sets = parseInt(item.querySelector('[name*="sets"]')?.value) || 0;
            const rest = parseInt(item.querySelector('[name*="restSeconds"]')?.value) || 60;
            totalSeconds += (sets * 45) + (sets * rest);
        });
        
        if (durationEstimate) {
            const minutes = Math.round(totalSeconds / 60);
            durationEstimate.textContent = minutes;
        }

        // Update preview list
        if (previewList) {
            previewList.innerHTML = '';
            exerciseItems.forEach(item => {
                const name = item.querySelector('[name*="exerciseName"]')?.value || 'Unnamed';
                const sets = item.querySelector('[name*="sets"]')?.value || '0';
                const reps = item.querySelector('[name*="reps"]')?.value || '0';

                const previewItem = document.createElement('div');
                previewItem.className = 'builder-preview-item';

                const nameSpan = document.createElement('span');
                nameSpan.className = 'builder-preview-exercise-name';
                nameSpan.textContent = name;

                const setsRepsSpan = document.createElement('span');
                setsRepsSpan.className = 'builder-preview-exercise-sets';
                setsRepsSpan.textContent = `${sets}×${reps}`;

                previewItem.appendChild(nameSpan);
                previewItem.appendChild(setsRepsSpan);

                previewList.appendChild(previewItem);
            });
        }
    }

    function addExerciseToBuilder(exerciseId) {
        const exercise = state.library.exercises.find(ex => ex.id == exerciseId);
        if (!exercise) return;

        // Switch to builder mode
        const builderTab = document.querySelector('[data-mode="builder"]');
        if (builderTab) {
            builderTab.click();
        }

        // Add exercise row
        setTimeout(() => {
            addExerciseRow();
            
            // Populate with exercise data
            const list = document.querySelector('[data-exercise-list]');
            const lastItem = list?.lastElementChild;
            
            if (lastItem) {
                const selectField = lastItem.querySelector('[name*="exerciseRef"]');
                const nameField = lastItem.querySelector('[name*="exerciseName"]');
                
                if (selectField) {
                    selectField.value = `e:${exercise.id}`;
                }
                if (nameField) {
                    nameField.value = exercise.name;
                }
                
                // Highlight with emerald effect
                lastItem.classList.add('emerald-highlight');
                setTimeout(() => lastItem.classList.remove('emerald-highlight'), 1000);
                
                updateBuilderPreview();
                showFeedback(`Added ${exercise.name} to workout`, 'success');
            }
        }, 100);
    }

    function initDragAndDrop() {
        // TODO: Implement drag-and-drop for exercise reordering
        // Tracking: Future enhancement for desktop power users
        // Basic implementation deferred to maintain focus on core functionality
        const list = document.querySelector('[data-exercise-list]');
        if (!list) return;
    }

    // ============================================
    // CUSTOM EXERCISE PANEL
    // ============================================
    function initCustomExercisePanel() {
        const openBtn = document.querySelector('[data-open-custom-exercise]');
        const closeBtn = document.querySelector('.custom-exercise-close-btn');
        const overlay = document.querySelector('.custom-exercise-overlay');
        const panel = document.querySelector('.custom-exercise-panel');

        if (openBtn) {
            openBtn.addEventListener('click', () => {
                customExerciseOpener = document.activeElement;
                overlay?.classList.add('active');
                panel?.classList.add('active');
                overlay?.setAttribute('aria-hidden', 'false');
                panel?.setAttribute('aria-hidden', 'false');
                panel?.removeAttribute('inert');
                closeBtn?.focus();
            });
        }

        if (closeBtn) {
            closeBtn.addEventListener('click', closeCustomExercisePanel);
        }

        if (overlay) {
            overlay.addEventListener('click', closeCustomExercisePanel);
        }

        document.addEventListener('keydown', event => {
            if (event.key === 'Escape' && panel?.classList.contains('active')) {
                closeCustomExercisePanel();
            }
        });
    }

    function closeCustomExercisePanel() {
        const overlay = document.querySelector('.custom-exercise-overlay');
        const panel = document.querySelector('.custom-exercise-panel');
        
        overlay?.classList.remove('active');
        panel?.classList.remove('active');
        overlay?.setAttribute('aria-hidden', 'true');
        panel?.setAttribute('aria-hidden', 'true');
        panel?.setAttribute('inert', '');
        if (customExerciseOpener instanceof HTMLElement) {
            customExerciseOpener.focus();
        }
    }

    // ============================================
    // UTILITIES
    // ============================================
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    function getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content || 
               document.querySelector('input[name="_csrf"]')?.value || '';
    }

    function showFeedback(message, type = 'success') {
        // Simple toast notification
        const toast = document.createElement('div');
        toast.className = `fixed bottom-6 right-6 z-50 rounded-xl px-4 py-3 text-sm font-semibold text-white shadow-lg animate-slide-in-right ${
            type === 'success' ? 'bg-emerald-500' :
            type === 'error' ? 'bg-rose-500' :
            'bg-blue-500'
        }`;
        toast.textContent = message;
        
        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }

    // ============================================
    // INITIALIZATION
    // ============================================
    function init() {
        initModeNavigation();
        initExerciseLibrary();
        initWorkoutBuilder();
        initCustomExercisePanel();
        
        // Load initial data
        updateBuilderPreview();
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
