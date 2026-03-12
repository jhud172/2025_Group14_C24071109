document.addEventListener("DOMContentLoaded", () => {
    const dashboardRoot = document.getElementById("dashboardRoot");

    if (dashboardRoot) {
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

    const dayButtons = Array.from(document.querySelectorAll("[data-week-day-button]"));
    const selectedLabel = document.getElementById("weekSelectedLabel");
    const selectedTitle = document.getElementById("weekSelectedTitle");
    const selectedSummary = document.getElementById("weekSelectedSummary");
    const taskList = document.getElementById("weekTaskList");
    const workoutList = document.getElementById("weekWorkoutList");
    const openDayLink = document.getElementById("weekOpenDayLink");
    const previewPanel = document.querySelector("[data-week-preview-panel]");
    let previewSwapTimer = null;

    function parseTitles(raw) {
        if (!raw) return [];
        return raw
            .split("|")
            .map((v) => v.trim())
            .filter((v) => v.length > 0);
    }

    function buildItem(label, index, type, dayPath) {
        const taskTimes = ["08:00", "12:30", "17:00", "20:00"];
        const workoutTimes = ["07:00", "13:00", "18:30", "21:00"];
        const times = type === "task" ? taskTimes : workoutTimes;
        const statuses = ["due", "due", "completed", "due"];
        const time = times[index % times.length];
        const status = statuses[index % statuses.length];
        const href = `${dayPath || "/calendar"}${type === "task" ? "#task-" : "#workout-"}${index + 1}`;

        const li = document.createElement("li");
        li.className =
            "rounded-lg border border-slate-200/80 bg-slate-50/70 px-2.5 py-1.5 dark:border-slate-700/70 dark:bg-slate-800/50";

        const link = document.createElement("a");
        link.href = href;
        link.className = "block text-sky-700 hover:text-sky-600 dark:text-sky-300 dark:hover:text-sky-200";
        link.textContent = `${label} - ${time} (${status})`;
        li.appendChild(link);
        return li;
    }

    function fillList(target, items, emptyText, type, dayPath) {
        if (!target) return;
        target.innerHTML = "";
        target.classList.remove("week-preview-list--scroll");
        if (items.length === 0) {
            const empty = document.createElement("li");
            empty.className =
                "rounded-lg border border-slate-200/80 bg-slate-50/70 px-2.5 py-1.5 dark:border-slate-700/70 dark:bg-slate-800/50";
            empty.textContent = emptyText;
            target.appendChild(empty);
            return;
        }

        items.forEach((item, idx) => target.appendChild(buildItem(item, idx, type, dayPath)));

        // Keep each column independent: once a list exceeds 2 entries, that list alone scrolls.
        if (items.length > 2) {
            target.classList.add("week-preview-list--scroll");
        }
    }

    function setActiveDay(button) {
        if (!button) return;

        if (previewSwapTimer) {
            window.clearTimeout(previewSwapTimer);
            previewSwapTimer = null;
        }

        if (previewPanel) previewPanel.classList.add("is-updating");

        dayButtons.forEach((el) => {
            el.setAttribute("aria-selected", "false");
            el.classList.remove("ring-2", "ring-sky-400/50", "border-sky-400/60");
            el.classList.remove("is-active");
        });

        button.setAttribute("aria-selected", "true");
        button.classList.add("ring-2", "ring-sky-400/50", "border-sky-400/60");
        button.classList.add("is-active");

        const dayLabel = button.dataset.dayLabel || "Selected day";
        const taskCount = Number(button.dataset.taskCount || "0");
        const workoutCount = Number(button.dataset.workoutCount || "0");
        const taskTitles = parseTitles(button.dataset.taskTitles || "");
        const workoutTitles = parseTitles(button.dataset.workoutTitles || "");
        const dayPath = button.dataset.dayPath || "/calendar";

        previewSwapTimer = window.setTimeout(() => {
            if (selectedLabel) selectedLabel.textContent = "Selected day";
            if (selectedTitle) selectedTitle.textContent = dayLabel;
            if (selectedSummary) selectedSummary.textContent = `${taskCount} tasks, ${workoutCount} workouts`;
            if (openDayLink) openDayLink.setAttribute("href", dayPath);

            fillList(taskList, taskTitles, "No tasks scheduled", "task", dayPath);
            fillList(workoutList, workoutTitles, "No workouts scheduled", "workout", dayPath);

            if (previewPanel) previewPanel.classList.remove("is-updating");
            previewSwapTimer = null;
        }, 170);
    }

    if (dayButtons.length) {
        const defaultButton =
            dayButtons.find((btn) => btn.dataset.daySelected === "true") || dayButtons[0];
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

    const weekStrip = document.querySelector("[data-week-strip]");
    if (weekStrip) {
        let isDragging = false;
        let dragStartX = 0;
        let startScrollLeft = 0;

        const beginDrag = (clientX) => {
            isDragging = true;
            dragStartX = clientX;
            startScrollLeft = weekStrip.scrollLeft;
            weekStrip.classList.add("dragging");
        };

        const moveDrag = (clientX) => {
            if (!isDragging) return;
            const delta = clientX - dragStartX;
            weekStrip.scrollLeft = startScrollLeft - delta;
        };

        const endDrag = () => {
            isDragging = false;
            weekStrip.classList.remove("dragging");
        };

        weekStrip.addEventListener("pointerdown", (event) => {
            beginDrag(event.clientX);
        });

        weekStrip.addEventListener("pointermove", (event) => {
            moveDrag(event.clientX);
        });

        weekStrip.addEventListener("pointerup", endDrag);
        weekStrip.addEventListener("pointerleave", endDrag);
        weekStrip.addEventListener("pointercancel", endDrag);
    }

    const upcomingSection = document.getElementById("recommendedUpcomingCountdown");
    const upcomingCountdown = document.getElementById("recommendedUpcomingTimer");
    const upcomingRelative = document.getElementById("recommendedUpcomingRelative");

    if (upcomingSection && upcomingCountdown && upcomingRelative) {
        const targetValue = upcomingSection.dataset.upcomingTarget;
        const targetDate = targetValue ? new Date(targetValue) : null;

        function applyUrgency(minutesRemaining) {
            upcomingSection.classList.remove(
                "border-emerald-300/80",
                "ring-emerald-200/80",
                "border-sky-300/80",
                "ring-sky-200/80",
                "border-amber-300/80",
                "ring-amber-200/80",
                "border-rose-300/90",
                "ring-rose-200/90",
                "animate-pulse"
            );

            if (minutesRemaining > 180) {
                upcomingSection.classList.add("border-emerald-300/80", "ring-emerald-200/80");
                return;
            }
            if (minutesRemaining > 60) {
                upcomingSection.classList.add("border-sky-300/80", "ring-sky-200/80");
                return;
            }
            if (minutesRemaining > 15) {
                upcomingSection.classList.add("border-amber-300/80", "ring-amber-200/80");
                return;
            }
            upcomingSection.classList.add("border-rose-300/90", "ring-rose-200/90", "animate-pulse");
        }

        function renderCountdown() {
            if (!targetDate || Number.isNaN(targetDate.getTime())) {
                upcomingCountdown.textContent = "";
                upcomingCountdown.classList.add("hidden");
                upcomingRelative.textContent = "This is recommended.";
                return;
            }

            upcomingCountdown.classList.remove("hidden");

            const now = new Date();
            const diffMs = targetDate.getTime() - now.getTime();
            const totalSeconds = Math.max(0, Math.floor(diffMs / 1000));
            const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
            const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, "0");
            const seconds = String(totalSeconds % 60).padStart(2, "0");

            upcomingCountdown.textContent = `${hours}:${minutes}:${seconds}`;

            const minsRemaining = Math.floor(totalSeconds / 60);
            applyUrgency(minsRemaining);

            if (totalSeconds === 0) {
                upcomingRelative.textContent = "Now due";
            } else if (minsRemaining < 60) {
                upcomingRelative.textContent = `Begins in ${minsRemaining}m`;
            } else {
                const hrs = Math.floor(minsRemaining / 60);
                const mins = minsRemaining % 60;
                upcomingRelative.textContent = `Begins in ${hrs}h ${mins}m`;
            }
        }

        renderCountdown();
        window.setInterval(renderCountdown, 1000);
    }

});
