let draggedItem = null;
let ghost = null;
let sourceZone = null;

function createItem(id, name) {
    const div = document.createElement("div");
    div.className = "scheduled-item bg-blue-50 p-2 rounded shadow border border-blue-300 text-sm cursor-move";
    div.draggable = true;
    div.dataset.id = id;
    div.dataset.name = name;
    div.innerHTML = `<span class="font-semibold text-blue-800">${name}</span>`;
    addDragHandlers(div);
    div.addEventListener("contextmenu", e => {
        e.preventDefault();
        div.remove();
    });

    return div;
}

function addDragHandlers(elem) {
    elem.addEventListener("dragstart", e => {
        draggedItem = elem;
        sourceZone = elem.parentElement;
        ghost = elem.cloneNode(true);
        ghost.style.opacity = "0.4";
        ghost.style.position = "absolute";
        ghost.style.top = "-9999px";
        document.body.appendChild(ghost);
        e.dataTransfer.setDragImage(ghost, 0, 0);
    });
    elem.addEventListener("dragend", () => {
        draggedItem = null;
        if (ghost) ghost.remove();
        ghost = null;
    });
}
document.querySelectorAll(".draggable-ex").forEach(ex => {
    addDragHandlers(ex);
    ex.addEventListener("dragstart", () => {
        // When dragging from left list, clone instead of move
        const clone = createItem(ex.dataset.id, ex.dataset.name);
        draggedItem = clone;
        sourceZone = null;
    });
});
document.querySelectorAll(".drop-zone").forEach(zone => {

    zone.addEventListener("dragover", e => {
        e.preventDefault();
    });

    zone.addEventListener("drop", () => {
        if (!draggedItem) return;
        if (!draggedItem.parentElement || draggedItem.parentElement.id === "exercise-list") {
            zone.appendChild(createItem(draggedItem.dataset.id, draggedItem.dataset.name));
        } else {
            zone.appendChild(draggedItem);
        }
    });
});

document.getElementById("saveForm").addEventListener("submit", () => {

    const output = {};

    document.querySelectorAll(".day-column").forEach(col => {
        const day = col.dataset.day;
        const items = Array.from(col.querySelectorAll(".scheduled-item"))
            .map(x => Number(x.dataset.id));

        output[day] = items;
    });

    document.getElementById("payloadField").value = JSON.stringify(output);
});

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("workout-search");
    const workoutItems = Array.from(document.querySelectorAll(".workout-item"));

    searchInput.addEventListener("input", () => {
        const q = searchInput.value.trim().toLowerCase();
        let shown = 0;

        workoutItems.forEach(item => {
            const name = item.dataset.name.toLowerCase();

            if (name.includes(q) && shown < 10) {
                item.style.display = "block";
                shown++;
            } else {
                item.style.display = "none";
            }
        });
    });
});