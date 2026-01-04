const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfParam = document.querySelector('meta[name="_csrf_param"]').content;

let hoverTimer = null;
let closeTimer = null;

let preview = document.getElementById("preview-card");
let lockPosition = false;

let mouseX = 0, mouseY = 0;

document.addEventListener("mousemove", e => {
    mouseX = e.clientX;
    mouseY = e.clientY;

    if (!lockPosition) {
        positionPreview(mouseX, mouseY);
    }
});

function positionPreview(x, y) {
    const offset = 18;
    const rect = preview.getBoundingClientRect();

    let left = x + offset;
    let top = y + offset;

    if (left + rect.width > window.innerWidth) {
        left = x - rect.width - offset;
    }
    if (top + rect.height > window.innerHeight) {
        top = y - rect.height - offset;
    }

    preview.style.left = left + "px";
    preview.style.top = top + "px";
}

function showPreview() {
    preview.classList.remove("hidden", "hidden-state");
    requestAnimationFrame(() => preview.classList.add("showing"));
    lockPosition = true;
    positionPreview(mouseX, mouseY);
}

function hidePreview() {
    preview.classList.remove("showing");
    preview.classList.add("hidden-state");

    setTimeout(() => {
        preview.classList.add("hidden");
        lockPosition = false;
    }, 180);
}

document.querySelectorAll(".calendar-item").forEach(item => {
    item.addEventListener("mouseenter", () => {
        clearTimeout(closeTimer);

        hoverTimer = setTimeout(() => {

            const type = item.dataset.type;
            const id = item.dataset.id;
            const completed = item.dataset.completed;

            let html = `
                <p class="font-bold text-lg mb-1">${item.dataset.title}</p>
                <p class="text-sm text-gray-700">Time: ${item.dataset.time ?? '—'}</p>
                <p class="text-sm text-gray-600 italic mb-4">${item.dataset.notes || 'No description'}</p>
            `;
            if (completed === "true") {
                html += `
                    <div class="mt-3 text-green-700 font-semibold flex items-center gap-2">
                        <span class="text-xl">✓</span> Completed
                    </div>
                `;
            } else if (type === "exercise") {
                html += `
                    <a href="/exercise-log/add-calendar?taskId=${id}"
                        class="mt-3 px-3 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow block text-center">
                        Complete Exercise Log
                    </a>
                `;
            } else if (type === "occurrence") {
                html += `
                    <a href="/exercise-log/add-occurrence?occId=${id}"
                        class="mt-3 px-3 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow block text-center">
                        Complete Scheduled Exercise
                    </a>
                `;
            } else if (type === "task") {
                html += `
                    <form method="post" action="/calendar/day/${item.dataset.date}/toggle-complete">
                        <input type="hidden" name="taskId" value="${id}">
                        <input type="hidden" name="${csrfParam}" value="${csrfToken}">
                        <button class="mt-3 px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg shadow">
                            Mark Completed
                        </button>
                    </form>
                `;
            }

            preview.innerHTML = html;
            preview.style.left = (mouseX + 18) + "px";
            preview.style.top = (mouseY + 18) + "px";

            showPreview();

        }, 600);
    });

    item.addEventListener("mouseleave", () => {
        clearTimeout(hoverTimer);
        closeTimer = setTimeout(() => {
            if (!preview.matches(":hover")) hidePreview();
        }, 200);
    });
});

preview.addEventListener("mouseenter", () => clearTimeout(closeTimer));
preview.addEventListener("mouseleave", () => {
    closeTimer = setTimeout(() => hidePreview(), 250);
});