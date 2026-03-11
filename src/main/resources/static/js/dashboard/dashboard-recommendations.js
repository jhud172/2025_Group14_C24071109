/**
 * Dashboard Recommendations - Time-based action suggestions
 * Handles tab switching and meal time recommendations
 */

(function () {
    "use strict";

    // Meal time windows (24-hour format as decimal hours)
    const MEAL_TIMES = {
        breakfast: { start: 7, end: 9.5, label: "Breakfast" }, // 7:00 - 9:30
        lunch: { start: 11.5, end: 14.5, label: "Lunch" }, // 11:30 - 14:30
        dinner: { start: 18.5, end: 20.5, label: "Dinner" }, // 18:30 - 20:30
    };

    /**
     * Get current time as decimal hours (e.g., 14.75 for 2:45 PM)
     */
    function getCurrentTimeInDecimalHours() {
        const now = new Date();
        return now.getHours() + now.getMinutes() / 60;
    }

    /**
     * Get current meal type if within meal window
     */
    function getCurrentMealType() {
        const hours = getCurrentTimeInDecimalHours();

        for (const [mealKey, meal] of Object.entries(MEAL_TIMES)) {
            if (hours >= meal.start && hours < meal.end) {
                return meal.label;
            }
        }
        return null;
    }

    /**
     * Get next upcoming meal
     */
    function getNextMealLabel() {
        const hours = getCurrentTimeInDecimalHours();

        const meals = [
            { label: "Breakfast", time: MEAL_TIMES.breakfast.start },
            { label: "Lunch", time: MEAL_TIMES.lunch.start },
            { label: "Dinner", time: MEAL_TIMES.dinner.start },
        ];

        for (const meal of meals) {
            if (hours < meal.time) {
                return meal.label;
            }
        }

        // If past all meals, next is breakfast tomorrow
        return "Breakfast";
    }

    /**
     * Update recommended view based on current time and task/workout data
     */
    function updateRecommendedActions() {
        const currentMeal = getCurrentMealType();
        const nextMeal = getNextMealLabel();

        // Get section with data attributes
        const section = document.querySelector('[data-has-tasks][data-has-workouts]');
        const hasTasks = section?.dataset.hasTasks === "true";
        const hasWorkouts = section?.dataset.hasWorkouts === "true";

        // Get reference elements
        const logMealBtn = document.getElementById("recommendedLogMealBtn");
        const addTaskBtn = document.getElementById("recommendedAddTaskBtn");
        const startWorkoutBtn = document.getElementById("recommendedStartWorkoutBtn");
        const trackIntakeCard = document.getElementById("recommendedTrackIntakeCard");
        const trackDayCard = document.getElementById("recommendedTrackDayCard");
        const mealTypeLabel = document.getElementById("mealTypeLabel");
        const mealTimeLabel = document.getElementById("mealTimageLabel");

        // Hide all elements
        [logMealBtn, addTaskBtn, startWorkoutBtn, trackIntakeCard, trackDayCard].forEach((el) => {
            if (el) el.classList.add("hidden");
        });

        // Build list of recommendations in priority order: Tasks > Meals > Workouts
        const recommendations = [];

        // Priority 1: Tasks (if available and not already completed)
        if (hasTasks) {
            recommendations.push({
                type: "task",
                button: addTaskBtn,
                priority: 100,
            });
        }

        // Priority 2: Meals (always available, priority varies by time)
        const mealInWindow = currentMeal !== null;
        const mealPriority = mealInWindow ? 50 : 30; // Higher priority during meal windows

        recommendations.push({
            type: "meal",
            button: logMealBtn,
            card: trackIntakeCard,
            mealLabel: currentMeal || nextMeal,
            isCurrent: mealInWindow,
            priority: mealPriority,
        });

        // Priority 3: Workouts (if available)
        if (hasWorkouts) {
            recommendations.push({
                type: "workout",
                button: startWorkoutBtn,
                card: trackDayCard,
                priority: 10,
            });
        }

        // Sort by priority (highest first)
        recommendations.sort((a, b) => b.priority - a.priority);

        // Show top 2 recommendations
        for (let i = 0; i < Math.min(2, recommendations.length); i++) {
            const item = recommendations[i];

            if (item.type === "meal") {
                logMealBtn?.classList.remove("hidden");
                trackIntakeCard?.classList.remove("hidden");

                if (mealTypeLabel) {
                    mealTypeLabel.textContent = `Log ${item.mealLabel}`;
                }
                if (mealTimeLabel) {
                    if (item.isCurrent) {
                        mealTimeLabel.textContent = `It's ${item.mealLabel.toLowerCase()} time! Log your meal to stay on track.`;
                    } else {
                        mealTimeLabel.textContent = `Prepare ahead: plan your ${item.mealLabel.toLowerCase()} now.`;
                    }
                }
            } else if (item.button) {
                item.button.classList.remove("hidden");
                if (item.card) {
                    item.card.classList.remove("hidden");
                }
            }
        }
    }

    /**
     * Handle tab switching
     */
    function setupTabSwitching() {
        const tabs = document.querySelectorAll(".action-filter-tab");
        const views = document.querySelectorAll(".action-view");

        tabs.forEach((tab) => {
            tab.addEventListener("click", () => {
                const filter = tab.dataset.filter;

                // Update tab states
                tabs.forEach((t) => {
                    t.classList.remove("active");
                    t.setAttribute("aria-selected", "false");
                    t.classList.add("border-transparent", "text-slate-600", "hover:text-slate-900");
                    t.classList.remove(
                        "border-emerald-500",
                        "text-emerald-700",
                        "dark:text-emerald-300"
                    );
                });

                // Activate clicked tab
                tab.classList.add("active");
                tab.setAttribute("aria-selected", "true");
                tab.classList.remove("border-transparent", "text-slate-600", "hover:text-slate-900");
                tab.classList.add("border-emerald-500", "text-emerald-700", "dark:text-emerald-300");

                // Update view visibility
                views.forEach((view) => {
                    if (view.dataset.view === filter) {
                        view.classList.remove("hidden");
                        view.classList.add("active");
                    } else {
                        view.classList.add("hidden");
                        view.classList.remove("active");
                    }
                });
            });
        });
    }

    /**
     * Initialize recommendations on page load
     */
    function init() {
        // Set up tab switching
        setupTabSwitching();

        // Update recommended actions based on time
        updateRecommendedActions();

        // Update every minute in case meal times change
        setInterval(updateRecommendedActions, 60000);
    }

    // Initialize when DOM is ready
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();

