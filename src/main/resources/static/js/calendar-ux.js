document.addEventListener("DOMContentLoaded", () => {
    const drawerButton = document.getElementById("scheduleDrawerButton");
    const drawer = document.getElementById("scheduleDrawer");
    const overlay = drawer?.querySelector("[data-drawer-overlay]");
    const closeBtn = drawer?.querySelector("[data-drawer-close]");

    function openDrawer() {
        if (!drawer) return;
        drawer.classList.remove("hidden");
    }

    function closeDrawer() {
        if (!drawer) return;
        drawer.classList.add("hidden");
    }

    drawerButton?.addEventListener("click", openDrawer);
    overlay?.addEventListener("click", closeDrawer);
    closeBtn?.addEventListener("click", closeDrawer);

    document.addEventListener("click", (event) => {
        const card = event.target.closest(".calendar-day-card");
        if (!card) return;
        if (event.target.closest("a, button, input, textarea, select, .calendar-item")) {
            return;
        }
        const href = card.getAttribute("data-day-link");
        if (href) {
            window.location.href = href;
        }
    });

    document.querySelectorAll("[data-heatmap-legend-toggle]").forEach((toggle) => {
        const wrapper = toggle.closest("[data-heatmap-legend-wrapper]");
        const legend = wrapper?.querySelector("[data-heatmap-legend]");
        if (!legend) return;

        toggle.addEventListener("click", (event) => {
            event.stopPropagation();
            const isHidden = legend.classList.contains("hidden");
            legend.classList.toggle("hidden", !isHidden);
            toggle.setAttribute("aria-expanded", String(isHidden));
        });

        document.addEventListener("click", (event) => {
            if (!wrapper.contains(event.target)) {
                legend.classList.add("hidden");
                toggle.setAttribute("aria-expanded", "false");
            }
        });
    });
});
