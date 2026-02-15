document.addEventListener("DOMContentLoaded", () => {
    // ===== SCHEDULE SHELF (Left-side sliding panel) =====
    const scheduleButton = document.getElementById("scheduleDrawerButton");
    const scheduleShelf = document.getElementById("scheduleShelf");
    
    function openScheduleShelf() {
        if (!scheduleShelf) return;
        scheduleShelf.classList.add("open");
        document.body.style.overflow = "hidden"; // Prevent scrolling when open
    }
    
    function closeScheduleShelf() {
        if (!scheduleShelf) return;
        scheduleShelf.classList.remove("open");
        document.body.style.overflow = "";
    }
    
    scheduleButton?.addEventListener("click", openScheduleShelf);
    scheduleShelf?.querySelector(".calendar-schedule-overlay")?.addEventListener("click", closeScheduleShelf);
    scheduleShelf?.querySelector(".calendar-schedule-close")?.addEventListener("click", closeScheduleShelf);
    
    // Close on Escape key
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && scheduleShelf?.classList.contains("open")) {
            closeScheduleShelf();
        }
    });
    
    // ===== MODE INFO MODAL =====
    const modeInfoBtn = document.getElementById("modeInfoBtn");
    const modeInfoModal = document.getElementById("modeInfoModal");
    const closeModeModalBtn = document.getElementById("closeModeModal");
    
    function openModeInfoModal() {
        if (!modeInfoModal) return;
        modeInfoModal.classList.add("open");
        document.body.style.overflow = "hidden";
    }
    
    function closeModeInfoModal() {
        if (!modeInfoModal) return;
        modeInfoModal.classList.remove("open");
        document.body.style.overflow = "";
    }
    
    modeInfoBtn?.addEventListener("click", openModeInfoModal);
    closeModeModalBtn?.addEventListener("click", closeModeInfoModal);
    modeInfoModal?.querySelector(".calendar-mode-modal")?.addEventListener("click", (e) => {
        if (e.target === modeInfoModal) {
            closeModeInfoModal();
        }
    });
    
    // Close on Escape key
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && modeInfoModal?.classList.contains("open")) {
            closeModeInfoModal();
        }
    });
    
    // ===== SCHEDULE SEARCH =====
    const scheduleSearch = document.getElementById("schedule-search");
    const scheduleList = document.getElementById("schedule-list");
    
    scheduleSearch?.addEventListener("input", (e) => {
        const query = e.target.value.toLowerCase().trim();
        const items = scheduleList?.querySelectorAll("li");
        
        items?.forEach(item => {
            const text = item.textContent.toLowerCase();
            if (text.includes(query)) {
                item.style.display = "";
            } else {
                item.style.display = "none";
            }
        });
    });

    // ===== DAY CARD CLICKABLE =====
    document.addEventListener("click", (event) => {
        const card = event.target.closest(".calendar-day-card");
        if (!card) return;
        // Don't trigger if clicking on interactive elements
        if (event.target.closest("a, button, input, textarea, select, .calendar-item, .calendar-grouped-item")) {
            return;
        }
        const href = card.getAttribute("data-day-link");
        if (href) {
            window.location.href = href;
        }
    });

    // ===== HEATMAP LEGEND TOGGLE =====
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
