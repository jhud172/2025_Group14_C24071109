(function () {
    function shouldConfirm(message) {
        return message ? window.confirm(message) : true;
    }

    document.addEventListener("click", (event) => {
        const trigger = event.target && event.target.closest
            ? event.target.closest("a[data-confirm], button[data-confirm], input[data-confirm]")
            : null;

        if (!trigger) return;

        const message = trigger.getAttribute("data-confirm");
        if (!shouldConfirm(message)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }, true);

    document.addEventListener("submit", (event) => {
        const form = event.target;
        if (!(form instanceof HTMLFormElement)) return;

        const message = form.getAttribute("data-confirm");
        if (!message) return;

        if (!shouldConfirm(message)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }, true);
}());
