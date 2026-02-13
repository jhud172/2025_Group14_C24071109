let draggedItem = null;
let ghost = null;
let sourceZone = null;

function createItem(id, name) {
    const div = document.createElement("div");
    div.className = "schedule-workout-item";
    div.draggable = true;
    div.dataset.id = id;
    div.dataset.name = name;
    div.innerHTML = `
        <div class="flex items-center justify-between gap-2">
            <span class="schedule-workout-item-name">${name}</span>
            <span class="schedule-workout-item-remove text-xs">✕</span>
        </div>
    `;
    addDragHandlers(div);
    div.addEventListener("contextmenu", e => {
        e.preventDefault();
        div.remove();
    });
    
    // Also allow clicking the X to remove
    const removeBtn = div.querySelector('.schedule-workout-item-remove');
    if (removeBtn) {
        removeBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            div.remove();
        });
    }

    return div;
}

function addDragHandlers(elem) {
    elem.addEventListener("dragstart", e => {
        draggedItem = elem;
        sourceZone = elem.parentElement;
        elem.classList.add('dragging');
        ghost = elem.cloneNode(true);
        ghost.style.opacity = "0.4";
        ghost.style.position = "absolute";
        ghost.style.top = "-9999px";
        document.body.appendChild(ghost);
        e.dataTransfer.setDragImage(ghost, 0, 0);
    });
    elem.addEventListener("dragend", () => {
        if (draggedItem) {
            draggedItem.classList.remove('dragging');
        }
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

document.querySelectorAll(".schedule-drop-zone").forEach(zone => {

    zone.addEventListener("dragover", e => {
        e.preventDefault();
        zone.classList.add('drag-over');
    });

    zone.addEventListener("dragleave", e => {
        if (e.target === zone) {
            zone.classList.remove('drag-over');
        }
    });

    zone.addEventListener("drop", (e) => {
        e.preventDefault();
        zone.classList.remove('drag-over');
        
        if (!draggedItem) return;
        if (!draggedItem.parentElement || draggedItem.parentElement.id === "workout-list") {
            zone.appendChild(createItem(draggedItem.dataset.id, draggedItem.dataset.name));
        } else {
            zone.appendChild(draggedItem);
        }
    });
});

document.getElementById("saveForm").addEventListener("submit", () => {

    const output = {};

    document.querySelectorAll(".schedule-day-column").forEach(col => {
        const day = col.dataset.day;
        const items = Array.from(col.querySelectorAll(".schedule-workout-item"))
            .map(x => Number(x.dataset.id));

        output[day] = items;
    });

    document.getElementById("payloadField").value = JSON.stringify(output);
});

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("workout-search");
    const workoutItems = Array.from(document.querySelectorAll(".workout-item"));

    if (searchInput && workoutItems.length > 0) {
        searchInput.addEventListener("input", () => {
            const q = searchInput.value.trim().toLowerCase();

            workoutItems.forEach(item => {
                const name = item.dataset.name.toLowerCase();
                if (name.includes(q)) {
                    item.style.display = "block";
                } else {
                    item.style.display = "none";
                }
            });
        });
    }
});