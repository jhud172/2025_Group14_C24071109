document.addEventListener("DOMContentLoaded", () => {
    const drawerButton = document.getElementById("scheduleDrawerButton");
    const drawer = document.getElementById("scheduleDrawer");
    const overlay = drawer?.querySelector("[data-drawer-overlay]");
    const closeBtn = drawer?.querySelector("[data-drawer-close]");
    const panel = drawer?.querySelector("[data-drawer-panel]");

    function openDrawer() {
        if (!drawer || !panel) return;
        drawer.classList.remove("hidden");
        drawer.setAttribute("aria-hidden", "false");
        
        // Trigger animation after a brief delay to ensure the element is rendered
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                panel.style.transform = "translateX(0)";
                overlay.style.opacity = "1";
            });
        });
    }

    function closeDrawer() {
        if (!drawer || !panel) return;
        panel.style.transform = "translateX(100%)";
        overlay.style.opacity = "0";
        
        // Wait for animation to complete before hiding
        setTimeout(() => {
            drawer.classList.add("hidden");
            drawer.setAttribute("aria-hidden", "true");
        }, 300);
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
