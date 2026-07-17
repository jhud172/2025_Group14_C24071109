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
                    tab.tabIndex = active ? 0 : -1;
                });
                views.forEach((view) => {
                    const active = view.dataset.goalView === key;
                    view.classList.toggle("is-active", active);
                    view.hidden = !active;
                });
            };
            tabs.forEach((tab, index) => {
                tab.addEventListener("click", () => setActive(tab.dataset.goalTab));
                tab.addEventListener("keydown", (event) => {
                    let nextIndex = null;
                    if (event.key === "ArrowRight") nextIndex = (index + 1) % tabs.length;
                    if (event.key === "ArrowLeft") nextIndex = (index - 1 + tabs.length) % tabs.length;
                    if (event.key === "Home") nextIndex = 0;
                    if (event.key === "End") nextIndex = tabs.length - 1;
                    if (nextIndex === null) return;
                    event.preventDefault();
                    const nextTab = tabs[nextIndex];
                    setActive(nextTab.dataset.goalTab);
                    nextTab.focus();
                });
            });
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
