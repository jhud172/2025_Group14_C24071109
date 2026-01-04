document.addEventListener("DOMContentLoaded", () => {
    const overlay = document.getElementById("confirmOverlay");
    const titleEl = document.getElementById("confirmTitle");
    const msgEl = document.getElementById("confirmMessage");
    const cancelBtn = document.getElementById("confirmCancel");
    const confirmBtn = document.getElementById("confirmOk");

    if (!overlay) return;

    let pendingForm = null;

    const open = (title, msg, form) => {
        pendingForm = form;
        titleEl.textContent = title || "Confirm";
        msgEl.textContent = msg || "Are you sure?";
        overlay.classList.add("show");
        document.body.style.overflow = "hidden";
        confirmBtn.focus();
    };

    const close = () => {
        overlay.classList.remove("show");
        document.body.style.overflow = "";
        pendingForm = null;
    };

    cancelBtn.addEventListener("click", close);
    overlay.addEventListener("click", (e) => {
        if (e.target === overlay) close();
    });
    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") close();
    });

    confirmBtn.addEventListener("click", () => {
        if (pendingForm) pendingForm.submit();
    });

    document.querySelectorAll("form[data-confirm]").forEach(form => {
        form.addEventListener("submit", (e) => {
            e.preventDefault();
            const title = form.getAttribute("data-confirm-title");
            const msg = form.getAttribute("data-confirm-message");
            open(title, msg, form);
        });
    });
});
