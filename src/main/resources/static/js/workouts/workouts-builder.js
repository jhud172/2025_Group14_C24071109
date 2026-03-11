document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("[data-workout-builder]");
    if (!form) {
        return;
    }

    const list = form.querySelector("[data-exercise-list]");
    const template = form.querySelector("[data-exercise-template]");
    const addBtn = form.querySelector("[data-add-exercise]");
    if (!list || !template || !addBtn) {
        return;
    }

    let index = list.children.length;

    const parseNumber = (value) => {
        if (value === undefined || value === null || value === "") {
            return null;
        }
        const parsed = parseInt(value, 10);
        return Number.isNaN(parsed) ? null : parsed;
    };

    const defaultSets = parseNumber(form.dataset.defaultSets);
    const defaultRepMin = parseNumber(form.dataset.defaultRepMin);
    const defaultRepMax = parseNumber(form.dataset.defaultRepMax);

    const applyDefaults = (row) => {
        const setsInput = row.querySelector("input[name$='.sets']");
        const repsInput = row.querySelector("input[name$='.reps']");
        if (setsInput && !setsInput.value && defaultSets != null) {
            setsInput.value = String(defaultSets);
        }
        if (repsInput) {
            if (!repsInput.value && defaultRepMin != null) {
                repsInput.value = String(defaultRepMin);
            }
            if (!repsInput.placeholder && defaultRepMin != null && defaultRepMax != null) {
                repsInput.placeholder = `${defaultRepMin}-${defaultRepMax}`;
            }
        }
    };

    const bindRow = (row) => {
        const select = row.querySelector("select");
        const nameInput = row.querySelector("input[name$='.exerciseName']");
        if (select && nameInput) {
            select.addEventListener("change", () => {
                const option = select.options[select.selectedIndex];
                if (option && option.value) {
                    nameInput.value = option.textContent.trim();
                }
            });
        }

        const removeBtn = row.querySelector("[data-remove-exercise]");
        if (removeBtn) {
            removeBtn.addEventListener("click", () => row.remove());
        }

        applyDefaults(row);
    };

    Array.from(list.children).forEach(bindRow);

    addBtn.addEventListener("click", () => {
        const html = template.innerHTML.replaceAll("__name__", `exercises[${index}]`);
        const wrapper = document.createElement("div");
        wrapper.innerHTML = html.trim();
        const row = wrapper.firstElementChild;
        if (!row) {
            return;
        }
        list.appendChild(row);
        bindRow(row);
        index += 1;
    });
});
