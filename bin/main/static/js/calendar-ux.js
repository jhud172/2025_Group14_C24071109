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

    document.querySelectorAll(".calendar-day-card").forEach(card => {
        card.addEventListener("click", (event) => {
            if (event.target.closest("a, button, input, textarea, select, .calendar-item")) {
                return;
            }
            const href = card.getAttribute("data-day-link");
            if (href) {
                window.location.href = href;
            }
        });
    });
});
