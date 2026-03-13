(function () {
    "use strict";

    function init() {
        initRailState();
        initActionHub();
        initWeekPreview();
        initUpcomingCountdown();
        initRevealCards();
        initProgressVisuals();
    }

    function initRailState() {
        const dashboardRoot = document.getElementById("dashboardRoot");
        if (!dashboardRoot) return;

        const desktopQuery = window.matchMedia("(min-width: 1028px)");
        let rafId = null;

        const applyRailState = () => {
            const shouldCenter = desktopQuery.matches && window.scrollY > 56;
            dashboardRoot.classList.toggle("dashboard-rails-centered", shouldCenter);
            rafId = null;
        };

        const onScroll = () => {
            if (rafId !== null) return;
            rafId = window.requestAnimationFrame(applyRailState);
        };

        window.addEventListener("scroll", onScroll, { passive: true });
        window.addEventListener("resize", onScroll);
        applyRailState();
    }

    function initActionHub() {
        const root = document.querySelector("[data-action-hub]");
        if (!root) return;

        const tabs = Array.from(root.querySelectorAll("[data-action-tab]"));
        const views = Array.from(root.querySelectorAll("[data-action-view]"));

        const setActiveView = (key) => {
            tabs.forEach((tab) => {
                const active = tab.dataset.actionTab === key;
                tab.classList.toggle("is-active", active);
                tab.setAttribute("aria-selected", active ? "true" : "false");
            });

            views.forEach((view) => {
                const active = view.dataset.actionView === key;
                view.classList.toggle("is-active", active);
            });
        };

        tabs.forEach((tab) => {
            tab.addEventListener("click", () => setActiveView(tab.dataset.actionTab));
        });

        setActiveView("recommended");
    }

    function initWeekPreview() {
        const dayButtons = Array.from(document.querySelectorAll("[data-week-day-button]"));
        const selectedLabel = document.getElementById("weekSelectedLabel");
        const selectedTitle = document.getElementById("weekSelectedTitle");
        const selectedSummary = document.getElementById("weekSelectedSummary");
        const taskList = document.getElementById("weekTaskList");
        const workoutList = document.getElementById("weekWorkoutList");
        const openDayLink = document.getElementById("weekOpenDayLink");
        const previewPanel = document.querySelector("[data-week-preview-panel]");
        const weekStrip = document.querySelector("[data-week-strip]");
        let previewSwapTimer = null;

        const parseTitles = (raw) => (raw || "")
            .split("|")
            .map((value) => value.trim())
            .filter((value) => value.length > 0);

        const fillList = (target, items, emptyText) => {
            if (!target) return;
            target.innerHTML = "";
            if (!items.length) {
                const empty = document.createElement("li");
                empty.className = "cd-week-preview-item";
                empty.textContent = emptyText;
                target.appendChild(empty);
                return;
            }

            items.forEach((item) => {
                const li = document.createElement("li");
                li.className = "cd-week-preview-item";
                li.textContent = item;
                target.appendChild(li);
            });
        };

        const setActiveDay = (button) => {
            if (!button) return;
            if (previewSwapTimer) {
                window.clearTimeout(previewSwapTimer);
                previewSwapTimer = null;
            }

            previewPanel?.classList.add("is-updating");
            dayButtons.forEach((el) => {
                el.setAttribute("aria-selected", "false");
                el.classList.remove("is-active");
            });

            button.setAttribute("aria-selected", "true");
            button.classList.add("is-active");

            previewSwapTimer = window.setTimeout(() => {
                const taskCount = Number(button.dataset.taskCount || "0");
                const workoutCount = Number(button.dataset.workoutCount || "0");
                const dayPath = button.dataset.dayPath || "/calendar";

                if (selectedLabel) selectedLabel.textContent = "Selected day";
                if (selectedTitle) selectedTitle.textContent = button.dataset.dayLabel || "Selected day";
                if (selectedSummary) selectedSummary.textContent = `${taskCount} tasks, ${workoutCount} workouts`;
                if (openDayLink) openDayLink.setAttribute("href", dayPath);

                fillList(taskList, parseTitles(button.dataset.taskTitles), "No tasks scheduled");
                fillList(workoutList, parseTitles(button.dataset.workoutTitles), "No workouts scheduled");

                previewPanel?.classList.remove("is-updating");
                previewSwapTimer = null;
            }, 140);
        };

        if (dayButtons.length) {
            const defaultButton = dayButtons.find((btn) => btn.dataset.daySelected === "true") || dayButtons[0];
            setActiveDay(defaultButton);

            dayButtons.forEach((button) => {
                button.addEventListener("click", () => setActiveDay(button));
                button.addEventListener("keydown", (event) => {
                    if (event.key === "Enter" || event.key === " ") {
                        event.preventDefault();
                        setActiveDay(button);
                    }
                });
            });
        }

        if (!weekStrip) return;
        let isDragging = false;
        let dragStartX = 0;
        let startScrollLeft = 0;

        weekStrip.addEventListener("pointerdown", (event) => {
            isDragging = true;
            dragStartX = event.clientX;
            startScrollLeft = weekStrip.scrollLeft;
            weekStrip.classList.add("dragging");
        });

        weekStrip.addEventListener("pointermove", (event) => {
            if (!isDragging) return;
            weekStrip.scrollLeft = startScrollLeft - (event.clientX - dragStartX);
        });

        ["pointerup", "pointerleave", "pointercancel"].forEach((eventName) => {
            weekStrip.addEventListener(eventName, () => {
                isDragging = false;
                weekStrip.classList.remove("dragging");
            });
        });
    }

    function initUpcomingCountdown() {
        const upcomingSection = document.getElementById("recommendedUpcomingCountdown");
        const upcomingCountdown = document.getElementById("recommendedUpcomingTimer");
        const upcomingRelative = document.getElementById("recommendedUpcomingRelative");
        if (!upcomingSection || !upcomingCountdown || !upcomingRelative) return;

        const countdownEnabled = upcomingSection.dataset.countdownEnabled === "true";
        const targetValue = upcomingSection.dataset.upcomingTarget;
        const targetDate = targetValue ? new Date(targetValue) : null;

        if (!countdownEnabled || !targetDate || Number.isNaN(targetDate.getTime())) {
            upcomingCountdown.textContent = "";
            return;
        }

        const renderCountdown = () => {
            const diffMs = targetDate.getTime() - Date.now();
            const totalSeconds = Math.max(0, Math.floor(diffMs / 1000));
            const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
            const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, "0");
            const seconds = String(totalSeconds % 60).padStart(2, "0");
            const minutesRemaining = Math.floor(totalSeconds / 60);

            upcomingCountdown.textContent = `${hours}:${minutes}:${seconds}`;

            if (totalSeconds === 0) {
                upcomingRelative.textContent = "Now due";
            } else if (minutesRemaining < 60) {
                upcomingRelative.textContent = `Due in ${minutesRemaining}m`;
            } else {
                const hrs = Math.floor(minutesRemaining / 60);
                const mins = minutesRemaining % 60;
                upcomingRelative.textContent = `Due in ${hrs}h ${mins}m`;
            }
        };

        renderCountdown();
        window.setInterval(renderCountdown, 1000);
    }

    function initRevealCards() {
        const cards = Array.from(document.querySelectorAll("[data-reveal-card]"));
        if (!cards.length) return;

        const coarsePointer = window.matchMedia("(hover: none), (pointer: coarse)");

        cards.forEach((card) => {
            const toggle = card.querySelector("[data-reveal-toggle]");
            if (!toggle) return;

            toggle.addEventListener("click", (event) => {
                event.preventDefault();
                const willOpen = !card.classList.contains("is-open");
                cards.forEach((item) => {
                    item.classList.remove("is-open");
                    const button = item.querySelector("[data-reveal-toggle]");
                    if (button) {
                        button.setAttribute("aria-expanded", "false");
                    }
                });

                if (willOpen) {
                    card.classList.add("is-open");
                    toggle.setAttribute("aria-expanded", "true");
                }
            });

            if (!coarsePointer.matches) {
                card.addEventListener("mouseleave", () => {
                    card.classList.remove("is-open");
                    toggle.setAttribute("aria-expanded", "false");
                });
            }
        });

        document.addEventListener("click", (event) => {
            if (event.target.closest("[data-reveal-card]")) return;
            cards.forEach((card) => {
                card.classList.remove("is-open");
                const toggle = card.querySelector("[data-reveal-toggle]");
                if (toggle) {
                    toggle.setAttribute("aria-expanded", "false");
                }
            });
        });
    }

    function initProgressVisuals() {
        const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        const roots = Array.from(document.querySelectorAll("[data-progress]"));
        if (!roots.length) return;

        const applyProgress = (root) => {
            const fill = root.querySelector(".cd-progress-bar__fill");
            if (!fill) return;
            const target = Math.max(0, Math.min(100, Number(root.dataset.progress || "0")));
            if (prefersReducedMotion) {
                fill.style.width = `${target}%`;
                return;
            }
            fill.style.width = "0%";
            window.requestAnimationFrame(() => {
                fill.style.width = `${target}%`;
            });
        };

        roots.forEach(applyProgress);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
