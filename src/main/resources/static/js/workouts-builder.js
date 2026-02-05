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
