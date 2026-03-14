(function () {
    "use strict";

    function initTabs() {
        const roots = Array.from(document.querySelectorAll("[data-goal-tabs]"));
        roots.forEach((root) => {
            const tabs = Array.from(root.querySelectorAll("[data-goal-tab]"));
            const views = Array.from(root.querySelectorAll("[data-goal-view]"));
            const setActive = (key) => {
                tabs.forEach((tab) => {
                    const active = tab.dataset.goalTab === key;
                    tab.classList.toggle("is-active", active);
                    tab.setAttribute("aria-selected", active ? "true" : "false");
                });
                views.forEach((view) => {
                    view.classList.toggle("is-active", view.dataset.goalView === key);
                });
            };
            tabs.forEach((tab) => tab.addEventListener("click", () => setActive(tab.dataset.goalTab)));
            setActive(tabs[0]?.dataset.goalTab || "week");
        });
    }

    function initProgress() {
        document.querySelectorAll(".goal-progress").forEach((bar) => {
            const fill = bar.querySelector("span");
            if (!fill) return;
            const target = Math.max(0, Math.min(100, Number(bar.dataset.progress || "0")));
            requestAnimationFrame(() => {
                fill.style.width = `${target}%`;
            });
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => {
            initTabs();
            initProgress();
        });
    } else {
        initTabs();
        initProgress();
    }
})();
