(function () {
    "use strict";

    const form = document.getElementById("preferences-editor-form");
    const saveBar = document.querySelector("[data-pref-save-bar]");
    if (!form || !saveBar) {
        return;
    }

    function updatePillStyle(checkbox) {
        const pill = checkbox.closest(".pref-pill");
        if (!pill) {
            return;
        }
        const icon = pill.querySelector(".pref-pill-check");
        pill.classList.toggle("pref-pill--active", checkbox.checked);
        if (icon) {
            icon.classList.toggle("hidden", !checkbox.checked);
        }
    }

    function syncSingleSelect(select) {
        const category = select.dataset.category;
        const hiddenChecks = document.querySelector(`.pref-hidden-checks[data-category="${category}"]`);
        if (!hiddenChecks) {
            return;
        }

        hiddenChecks.querySelectorAll(".pref-hidden-checkbox").forEach((checkbox) => {
            checkbox.checked = checkbox.value === select.value;
        });
    }

    function toggleEquipmentOtherInput() {
        const checkbox = document.getElementById("equipment-other-checkbox");
        const container = document.getElementById("equipment-other-container");
        if (!checkbox || !container) {
            return;
        }
        container.classList.toggle("hidden", !checkbox.checked);
    }

    function enforceWeeklyMetricLimit(changed) {
        const boxes = Array.from(document.querySelectorAll(".weekly-metric-checkbox"));
        const limitMessage = document.getElementById("weekly-metric-limit-msg");
        const selected = boxes.filter((box) => box.checked);
        if (selected.length <= 6) {
            return;
        }
        changed.checked = false;
        updatePillStyle(changed);
        if (limitMessage) {
            limitMessage.classList.remove("hidden");
            window.setTimeout(() => limitMessage.classList.add("hidden"), 2200);
        }
    }

    let dirty = false;
    const markDirty = () => {
        dirty = true;
        saveBar.classList.remove("hidden");
    };

    form.querySelectorAll(".pref-pill-checkbox").forEach((checkbox) => {
        updatePillStyle(checkbox);
        checkbox.addEventListener("change", () => {
            updatePillStyle(checkbox);
            markDirty();
        });
    });

    form.querySelectorAll(".pref-single-select").forEach((select) => {
        syncSingleSelect(select);
        select.addEventListener("change", () => {
            syncSingleSelect(select);
            markDirty();
        });
    });

    form.querySelectorAll("input, select, textarea").forEach((field) => {
        field.addEventListener("change", markDirty);
        field.addEventListener("input", markDirty);
    });

    document.querySelectorAll(".weekly-metric-checkbox").forEach((checkbox) => {
        checkbox.addEventListener("change", () => enforceWeeklyMetricLimit(checkbox));
    });

    try {
        if (typeof SlimSelect !== "undefined") {
            new SlimSelect({
                select: "#selectElement",
                settings: {
                    searchText: "No conditions found",
                    searchPlaceholder: "Search conditions...",
                    searchHighlight: true,
                    allowDeselect: true
                }
            });
        }
    } catch (_) {
        // Ignore enhancement failures and fall back to the native multi-select.
    }

    const equipmentOtherCheckbox = document.getElementById("equipment-other-checkbox");
    if (equipmentOtherCheckbox) {
        equipmentOtherCheckbox.addEventListener("change", () => {
            toggleEquipmentOtherInput();
            markDirty();
        });
    }
    toggleEquipmentOtherInput();

    form.addEventListener("submit", () => {
        dirty = false;
        saveBar.classList.add("hidden");
    });
})();
