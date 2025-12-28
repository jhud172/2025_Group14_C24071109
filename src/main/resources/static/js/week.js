document.addEventListener("DOMContentLoaded", () => {
    const preview = document.getElementById("preview-card");
    if (!preview) return;
    let hoverTimer = null;
    let closeTimer = null;
    let lockPosition = false;
    let mouseX = 0;
    let mouseY = 0;
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
        preview.classList.remove("hidden");
        preview.classList.remove("opacity-0");
        requestAnimationFrame(() => {
            preview.classList.add("opacity-100");
        });
        lockPosition = true;
        positionPreview(mouseX, mouseY);
    }
    function hidePreview() {
        preview.classList.remove("opacity-100");
        preview.classList.add("opacity-0");

        setTimeout(() => {
            preview.classList.add("hidden");
            lockPosition = false;
        }, 180);
    }
    document.querySelectorAll(".calendar-item").forEach(item => {
        item.addEventListener("mouseenter", () => {
            clearTimeout(closeTimer);
            hoverTimer = setTimeout(() => {
                renderPreview(item);
                showPreview();
            }, 500);
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
    function renderPreview(item) {
        const title = item.dataset.title || "Unknown";
        const time = item.dataset.time || "—";
        const notes = item.dataset.notes || "No notes";
        const isExercise = item.dataset.exercise === "true";
        const completed = item.dataset.completed === "true";
        const type = item.dataset.type;
        const id = item.dataset.id;
        let html = `
            <p class="font-bold text-lg mb-1">${title}</p>
            <p class="text-sm text-gray-700">Time: ${time}</p>
            <p class="text-sm text-gray-700 mb-2">${isExercise ? "Exercise Task" : "Custom Task"}</p>
            <p class="text-sm text-gray-600 italic mb-4">${notes}</p>
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
        positionPreview(mouseX, mouseY);
    }
});
