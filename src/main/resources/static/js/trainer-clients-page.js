document.addEventListener("DOMContentLoaded", () => {
    const search = document.getElementById("clientSearch");
    const rows = document.querySelectorAll("[data-client-row]");
    if (!search || !rows.length) {
        return;
    }
    search.addEventListener("input", () => {
        const query = search.value.toLowerCase();
        rows.forEach((row) => {
            const text = row.textContent.toLowerCase();
            row.classList.toggle("hidden", query && !text.includes(query));
        });
    });
});
