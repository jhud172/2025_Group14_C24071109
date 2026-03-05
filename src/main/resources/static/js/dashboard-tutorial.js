/**
 * Dashboard Tutorial System
 * First-run experience for new users landing on AccountHomePage
 * Tracks completion via localStorage (onetoone.tutorial.completed)
 */

class DashboardTutorial {
    constructor() {
        this.storageKey = 'onetoone.tutorial.completed';
        this.currentStep = 0;
        this.isActive = false;
        this.targetElement = null;
        this.intersectionObserver = null;
        this.focusTrapLastFocusedElement = null;
        this.userRole = this.getUserRole();

        this.steps = this.getStepsForRole(this.userRole);
        this.totalSteps = this.steps.length;

        this.init();
    }

    getUserRole() {
        const overlay = document.getElementById('tutorial-overlay');
        const roleFromOverlay = overlay?.dataset?.userRole;
        const roleFromDashboard = document.querySelector('[data-dashboard-role]')?.dataset?.dashboardRole;
        const roleFromBody = document.body?.dataset?.userRole;
        const role = roleFromOverlay || roleFromDashboard || roleFromBody || 'CLIENT';
        return String(role).toUpperCase();
    }

    getStepsForRole(role) {
        if (role === 'TRAINER') {
            return [
                {
                    id: 'welcome-trainer',
                    title: 'Welcome to your coaching command center',
                    message: 'This quick flow highlights where to manage clients and programmes.',
                    targetSelector: '[aria-label="Next action"]',
                    highlight: true,
                    visitPath: '/trainer/clients',
                    visitLabel: 'Open clients',
                    ariaLabel: 'Step 1: Trainer welcome and overview.'
                },
                {
                    id: 'trainer-planning',
                    title: 'Plan and review sessions',
                    message: 'Use Calendar and Library to build structured client progressions.',
                    targetSelector: '[aria-label="This week at a glance"]',
                    highlight: true,
                    visitPath: '/trainer/library',
                    visitLabel: 'Open library',
                    ariaLabel: 'Step 2: Navigate to planning tools.'
                },
                {
                    id: 'trainer-done',
                    title: 'You are ready to coach',
                    message: 'Continue with your top priority action from this dashboard.',
                    targetSelector: '[aria-label="Next action"] .btn--primary',
                    highlight: true,
                    ariaLabel: 'Step 3: Trainer completion step.'
                }
            ];
        }

        if (role === 'GYM' || role === 'GYM_ADMIN') {
            return [
                {
                    id: 'welcome-gym',
                    title: 'Welcome to your gym dashboard',
                    message: 'We will walk through daily operations and team management.',
                    targetSelector: '[aria-label="Next action"]',
                    highlight: true,
                    visitPath: '/gym/trainers',
                    visitLabel: 'Open staff',
                    ariaLabel: 'Step 1: Gym welcome step.'
                },
                {
                    id: 'gym-operations',
                    title: 'Keep operations in flow',
                    message: 'Use the calendar and insights to keep member delivery consistent.',
                    targetSelector: '[aria-label="This week at a glance"]',
                    highlight: true,
                    visitPath: '/calendar',
                    visitLabel: 'Open calendar',
                    ariaLabel: 'Step 2: Gym operations step.'
                },
                {
                    id: 'gym-done',
                    title: 'All set',
                    message: 'You can start with the top action on this page or jump to staff tools.',
                    targetSelector: '[aria-label="Next action"] .btn--primary',
                    highlight: true,
                    ariaLabel: 'Step 3: Gym completion step.'
                }
            ];
        }

        return [
            {
                id: 'welcome',
                title: 'Welcome to your fitness command center!',
                message: "Let's show you around 🚀",
                targetSelector: '[aria-label="Next action"]',
                highlight: true,
                visitPath: '/calendar',
                visitLabel: 'Open calendar',
                ariaLabel: 'Step 1 of 3: Welcome. Introducing your fitness dashboard.'
            },
            {
                id: 'features',
                title: 'Track your progress with ease',
                message: 'Plan workouts, log activities, and see your streak in one place.',
                targetSelector: '[aria-label="This week at a glance"]',
                highlight: true,
                visitPath: '/workout',
                visitLabel: 'Open workout builder',
                ariaLabel: 'Step 2 of 3: Key features in context.'
            },
            {
                id: 'cta',
                title: 'Ready to get started?',
                message: 'Log your first workout and build your streak! 💪',
                targetSelector: '[aria-label="Next action"] .btn--primary',
                highlight: true,
                visitPath: '/exercise-log',
                visitLabel: 'Open workout log',
                ariaLabel: 'Step 3 of 3: Getting started with your first action.'
            }
        ];
    }

    init() {
        // Set up intersection observer for scroll-into-view
        this.createIntersectionObserver();

        // Handle keyboard events (ESC to skip)
        document.addEventListener('keydown', (e) => {
            if (this.isActive && e.key === 'Escape') {
                this.skipTutorial();
            }
            // Tab focus trap
            if (this.isActive && e.key === 'Tab') {
                this.handleTabTrap(e);
            }
        });

        // Handle arrow navigation
        document.addEventListener('keydown', (e) => {
            if (!this.isActive) return;
            if (e.key === 'ArrowRight' && this.currentStep < this.totalSteps - 1) {
                e.preventDefault();
                this.nextStep();
            } else if (e.key === 'ArrowLeft' && this.currentStep > 0) {
                e.preventDefault();
                this.prevStep();
            }
        });

        // Check if user is new and should see tutorial
        this.checkAndStartTutorial();

        // Backdrop click skips tutorial
        const overlay = this.getOverlay();
        const backdrop = overlay?.querySelector('.tutorial-backdrop');
        if (backdrop) {
            backdrop.addEventListener('click', () => {
                if (this.isActive) {
                    this.skipTutorial();
                }
            });
        }
    }

    createIntersectionObserver() {
        const options = {
            root: null,
            rootMargin: '50px',
            threshold: 0.1
        };

        this.intersectionObserver = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (!entry.isIntersecting && this.isActive) {
                    // Element is out of view, scroll into view smoothly
                    setTimeout(() => {
                        entry.target.scrollIntoView({
                            behavior: 'smooth',
                            block: 'center'
                        });
                    }, 100);
                }
            });
        }, options);
    }

    checkAndStartTutorial() {
        // Check if tutorial has been completed
        const isCompleted = localStorage.getItem(this.storageKey);

        // Start tutorial for new users (this would be triggered on first visit)
        // In production, backend should also pass this flag
        if (!isCompleted && this.shouldShowTutorialForNewUser()) {
            setTimeout(() => {
                this.startTutorial();
            }, 500);
        }
    }

    shouldShowTutorialForNewUser() {
        // Check if this is truly a new user
        // In a real implementation, the backend would indicate this
        const userData = this.getUserData();
        
        // Show if: first visit ever OR no activity in 7 days
        if (!userData) {
            return true; // New user - first visit
        }

        // Check if low activity (7 days with 0 logs)
        if (userData.logsThisWeekCount === 0 && userData.daysSinceCreation >= 7) {
            return 'low-activity'; // Return trigger type
        }

        return false;
    }

    getUserData() {
        // This would be passed from backend in data attributes
        // For now, we'll check if any log data exists
        const logsElement = document.querySelector('[data-user-logs-count]');
        if (!logsElement) {
            return null;
        }

        return {
            logsThisWeekCount: parseInt(logsElement.dataset.userLogsCount) || 0,
            daysSinceCreation: parseInt(logsElement.dataset.daysSinceCreation) || 0
        };
    }

    startTutorial() {
        if (this.isActive) return;

        this.isActive = true;
        this.currentStep = 0;
        this.focusTrapLastFocusedElement = document.activeElement;

        // Show overlay
        const overlay = this.getOverlay();
        overlay.classList.remove('hidden');

        // Force layout recalculation
        overlay.offsetHeight;

        overlay.classList.add('tutorial-visible');

        // Show first step
        this.showStep(0);

        // Manage focus
        this.setInitialFocus();
    }

    showStep(stepIndex) {
        if (stepIndex < 0 || stepIndex >= this.totalSteps) return;

        this.currentStep = stepIndex;
        const step = this.steps[stepIndex];

        // Get overlay elements
        const overlay = this.getOverlay();
        const tooltip = overlay.querySelector('[data-tutorial-tooltip]');
        const progressBar = overlay.querySelector('[data-tutorial-progress]');
        const stepNumber = overlay.querySelector('[data-step-number]');
        const stepTitle = overlay.querySelector('[data-step-title]');
        const stepMessage = overlay.querySelector('[data-step-message]');
        const buttonsContainer = overlay.querySelector('[data-tutorial-buttons]');
        const highlightBox = overlay.querySelector('[data-tutorial-highlight]');

        // Update progress
        if (progressBar) {
            const progress = ((stepIndex + 1) / this.totalSteps) * 100;
            progressBar.style.width = `${progress}%`;
        }

        if (stepNumber) {
            stepNumber.textContent = `${stepIndex + 1} of ${this.totalSteps}`;
        }

        // Update content
        if (stepTitle) stepTitle.textContent = step.title;
        if (stepMessage) stepMessage.textContent = step.message;

        // Announce to screen readers
        const announcementElement = overlay.querySelector('[data-tutorial-announcement]');
        if (announcementElement) {
            announcementElement.textContent = step.ariaLabel;
        }

        // Update buttons
        this.setupButtons(buttonsContainer, step, stepIndex);

        // Highlight target element
        this.highlightTarget(step.targetSelector, highlightBox);

        // Ensure element is in view
        this.scrollTargetIntoView(step.targetSelector);

        // Add animation
        tooltip.classList.remove('tutorial-step-enter');
        tooltip.offsetHeight; // Trigger reflow
        tooltip.classList.add('tutorial-step-enter');

        // Set focus to first interactive element in tooltip
        this.setStepFocus();
    }

    highlightTarget(selector, highlightBox) {
        const target = document.querySelector(selector);

        if (!target) {
            // Hide highlight if target not found
            if (highlightBox) {
                highlightBox.style.display = 'none';
            }
            return;
        }

        this.targetElement = target;

        // Add highlight ring to target
        target.classList.add('tutorial-highlight-ring');

        // Position highlight box
        if (highlightBox) {
            const rect = target.getBoundingClientRect();
            const padding = 8;

            highlightBox.style.display = 'block';
            highlightBox.style.left = `${rect.left - padding}px`;
            highlightBox.style.top = `${rect.top - padding}px`;
            highlightBox.style.width = `${rect.width + padding * 2}px`;
            highlightBox.style.height = `${rect.height + padding * 2}px`;

            // Set up observer for this element
            if (this.intersectionObserver) {
                this.intersectionObserver.observe(target);
            }
        }
    }

    scrollTargetIntoView(selector) {
        const target = document.querySelector(selector);
        if (target) {
            // Only scroll if target is out of view
            const rect = target.getBoundingClientRect();
            if (rect.top < 0 || rect.bottom > window.innerHeight) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'center'
                });
            }
        }
    }

    setupButtons(container, step, stepIndex) {
        if (!container) return;

        container.innerHTML = '';

        if (stepIndex > 0) {
            const backButton = document.createElement('button');
            backButton.type = 'button';
            backButton.className = 'btn btn--secondary btn--sm';
            backButton.textContent = '← Back';
            backButton.addEventListener('click', () => this.prevStep());
            container.appendChild(backButton);
        }

        const skipButton = document.createElement('button');
        skipButton.type = 'button';
        skipButton.className = 'btn btn--secondary btn--sm';
        skipButton.textContent = 'Skip';
        skipButton.addEventListener('click', () => this.skipTutorial());
        container.appendChild(skipButton);

        if (step.visitPath) {
            const visitButton = document.createElement('button');
            visitButton.type = 'button';
            visitButton.className = 'btn btn--secondary btn--sm';
            visitButton.textContent = step.visitLabel || 'Open page';
            visitButton.addEventListener('click', () => this.openStepPage(step.visitPath));
            container.appendChild(visitButton);
        }

        const isLast = stepIndex === this.totalSteps - 1;
        const continueButton = document.createElement('button');
        continueButton.type = 'button';
        continueButton.className = 'btn btn--primary btn--sm';
        continueButton.textContent = isLast ? 'Done ✓' : 'Continue →';
        continueButton.addEventListener('click', () => {
            if (isLast) {
                this.completeTutorial();
                return;
            }
            this.nextStep();
        });
        container.appendChild(continueButton);
    }

    openStepPage(path) {
        if (!path || typeof path !== 'string') return;
        if (!path.startsWith('/')) return;
        window.location.assign(path);
    }

    nextStep() {
        if (this.currentStep < this.totalSteps - 1) {
            this.clearHighlight();
            this.showStep(this.currentStep + 1);
        }
    }

    prevStep() {
        if (this.currentStep > 0) {
            this.clearHighlight();
            this.showStep(this.currentStep - 1);
        }
    }

    skipTutorial() {
        this.clearHighlight();
        this.closeTutorial();
        // Don't mark as completed - user can retrigger with help button
    }

    completeTutorial() {
        // Mark tutorial as completed in localStorage
        localStorage.setItem(this.storageKey, 'true');

        // Show celebration
        this.showCelebration();

        // Close after celebration
        setTimeout(() => {
            this.closeTutorial();
        }, 2000);
    }

    showCelebration() {
        const overlay = this.getOverlay();
        const tooltip = overlay.querySelector('[data-tutorial-tooltip]');

        // Add celebration class for animation
        tooltip.classList.add('tutorial-celebration');

        // Create mini confetti burst
        this.confetti();
    }

    confetti() {
        // Simple CSS-based confetti using multiple elements
        const container = this.getOverlay();
        if (!container) return;

        for (let i = 0; i < 20; i++) {
            const confetto = document.createElement('div');
            confetto.className = 'tutorial-confetto';
            confetto.style.left = Math.random() * 100 + '%';
            confetto.style.setProperty('--delay', Math.random() * 0.2 + 's');
            confetto.style.setProperty('--duration', Math.random() * 0.5 + 0.5 + 's');
            confetto.style.setProperty('--angle', Math.random() * 360 + 'deg');
            container.appendChild(confetto);

            // Remove after animation
            setTimeout(() => {
                confetto.remove();
            }, 1000);
        }
    }

    closeTutorial() {
        this.isActive = false;

        const overlay = this.getOverlay();
        overlay.classList.remove('tutorial-visible');

        // Remove highlight rings
        this.clearHighlight();

        // Clean up observers
        if (this.intersectionObserver) {
            this.intersectionObserver.disconnect();
        }

        // Restore focus
        if (this.focusTrapLastFocusedElement) {
            this.focusTrapLastFocusedElement.focus();
        }

        // Hide after transition
        setTimeout(() => {
            overlay.classList.add('hidden');
        }, 300);
    }

    clearHighlight() {
        // Remove highlight ring from all elements
        document.querySelectorAll('.tutorial-highlight-ring').forEach((el) => {
            el.classList.remove('tutorial-highlight-ring');
        });

        // Hide highlight box
        const highlightBox = this.getOverlay().querySelector('[data-tutorial-highlight]');
        if (highlightBox) {
            highlightBox.style.display = 'none';
        }

        // Disconnect observer
        if (this.intersectionObserver && this.targetElement) {
            this.intersectionObserver.unobserve(this.targetElement);
        }
    }

    getOverlay() {
        return document.getElementById('tutorial-overlay');
    }

    setInitialFocus() {
        const overlay = this.getOverlay();
        const firstButton = overlay.querySelector('[data-tutorial-buttons] button');
        if (firstButton) {
            setTimeout(() => {
                firstButton.focus();
            }, 100);
        }
    }

    setStepFocus() {
        const overlay = this.getOverlay();
        const firstButton = overlay.querySelector('[data-tutorial-buttons] button');
        if (firstButton) {
            firstButton.focus();
        }
    }

    handleTabTrap(e) {
        const overlay = this.getOverlay();
        const focusableElements = overlay.querySelectorAll(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );

        if (focusableElements.length === 0) return;

        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        const activeElement = document.activeElement;

        if (e.shiftKey) {
            // Shift + Tab
            if (activeElement === firstElement) {
                e.preventDefault();
                lastElement.focus();
            }
        } else {
            // Tab
            if (activeElement === lastElement) {
                e.preventDefault();
                firstElement.focus();
            }
        }
    }

    // Public method to manually start/restart tutorial
    static startTutorial() {
        const instance = window.dashboardTutorial || new DashboardTutorial();
        instance.startTutorial();
    }

    // Public method to reset completion status
    static resetTutorial() {
        localStorage.removeItem('onetoone.tutorial.completed');
        const instance = window.dashboardTutorial || new DashboardTutorial();
        instance.startTutorial();
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    window.dashboardTutorial = new DashboardTutorial();
});

// Global access for navbar help button
window.startTutorial = () => DashboardTutorial.startTutorial();
window.resetTutorial = () => DashboardTutorial.resetTutorial();
