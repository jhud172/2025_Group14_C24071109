function toggleEdit(id) {
    const panel = document.getElementById("edit-" + id);
    if (!panel) return;
    panel.classList.toggle("hidden");
}
