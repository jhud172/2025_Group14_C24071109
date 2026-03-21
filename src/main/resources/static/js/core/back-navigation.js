(function () {
    document.addEventListener("click", (event) => {
        const trigger = event.target && event.target.closest
            ? event.target.closest("[data-history-back]")
            : null;

        if (!trigger) return;

        event.preventDefault();
        window.history.back();
    });
}());
