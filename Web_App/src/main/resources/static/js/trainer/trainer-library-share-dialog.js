document.addEventListener("DOMContentLoaded", () => {
    const dialogs = Array.from(document.querySelectorAll("[data-share-dialog]"));
    if (dialogs.length === 0) {
        return;
    }

    dialogs.forEach((dialog) => {
        const key = dialog.getAttribute("data-share-dialog");
        if (!key) {
            return;
        }

        document.querySelectorAll(`[data-share-dialog-open="${key}"]`).forEach((button) => {
            button.addEventListener("click", () => {
                if (typeof dialog.showModal === "function") {
                    dialog.showModal();
                }
            });
        });

        document.querySelectorAll(`[data-share-dialog-close="${key}"]`).forEach((button) => {
            button.addEventListener("click", () => {
                if (typeof dialog.close === "function") {
                    dialog.close();
                }
            });
        });

        dialog.addEventListener("click", (event) => {
            if (event.target === dialog && typeof dialog.close === "function") {
                dialog.close();
            }
        });
    });
});
