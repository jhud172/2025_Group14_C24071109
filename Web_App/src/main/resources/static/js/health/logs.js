const cards = Array.from(document.querySelectorAll(".log-card"));
const searchInput = document.getElementById("log-search");
const sortSelect = document.getElementById("sort-select");
const paginationContainer = document.getElementById("pagination-controls");
const preview = document.getElementById("preview-panel");
const closeBtn = document.getElementById("close-preview");
const pageSize = 6;
let currentPage = 1;
function renderLogs() {
    let query = searchInput.value.toLowerCase();
    let filtered = cards.filter(c => c.innerText.toLowerCase().includes(query));
    filtered.sort((a, b) => {
        let dateA = new Date(a.dataset.date);
        let dateB = new Date(b.dataset.date);
        return sortSelect.value === "newest" ? dateB - dateA : dateA - dateB;
    });
    let totalPages = Math.ceil(filtered.length / pageSize);
    let start = (currentPage - 1) * pageSize;
    let paginated = filtered.slice(start, start + pageSize);
    document.getElementById("log-grid").innerHTML = "";
    paginated.forEach(card => document.getElementById("log-grid").appendChild(card));
    renderPagination(totalPages);
}
function renderPagination(total) {
    paginationContainer.innerHTML = "";
    for (let i = 1; i <= total; i++) {
        let btn = document.createElement("button");
        btn.innerText = i;
        btn.className = "px-3 py-1 rounded-lg border shadow bg-white hover:bg-gray-200 transition";
        if (i === currentPage) btn.classList.add("bg-blue-200");
        btn.onclick = () => {
            currentPage = i;
            renderLogs();
        };
        paginationContainer.appendChild(btn);
    }
}
cards.forEach(card => {
    card.addEventListener("click", () => {
        document.getElementById("preview-date").innerText = card.dataset.date;
        document.getElementById("preview-before").innerText = card.dataset.before;
        document.getElementById("preview-after").innerText = card.dataset.after;
        document.getElementById("preview-confidence").innerText = card.dataset.confidence;
        document.getElementById("preview-view-btn")
            .href = "/exercise-log/view/" + card.dataset.id;
        preview.classList.remove("hidden");
        preview.classList.remove("translate-x-full");
    });
});
closeBtn.addEventListener("click", () => {
    preview.classList.add("translate-x-full");
    setTimeout(() => preview.classList.add("hidden"), 300);
});
searchInput.addEventListener("input", renderLogs);
sortSelect.addEventListener("change", renderLogs);
renderLogs();
