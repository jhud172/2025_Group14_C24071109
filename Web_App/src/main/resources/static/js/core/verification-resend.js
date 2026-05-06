(function () {
    "use strict";

    const resendForms = document.querySelectorAll("[data-verification-resend-form]");
    if (!resendForms.length) {
        return;
    }

    resendForms.forEach((form) => {
        const button = form.querySelector("[data-verification-resend-button]");
        const label = form.querySelector("[data-verification-resend-label]");
        const countdown = form.querySelector("[data-verification-resend-countdown]");
        const baseLabel = form.dataset.baseLabel || "Resend code";
        let remaining = Number.parseInt(form.dataset.cooldownSeconds || "0", 10);
        if (!Number.isFinite(remaining) || remaining < 0) {
            remaining = 0;
        }

        const render = () => {
            if (label) {
                label.textContent = baseLabel;
            }

            if (countdown) {
                countdown.hidden = remaining <= 0;
                countdown.textContent = remaining > 0 ? remaining + "s" : "";
            }

            if (button) {
                button.disabled = remaining > 0;
                button.classList.toggle("is-disabled", remaining > 0);
            }
        };

        render();

        if (remaining > 0) {
            const intervalId = window.setInterval(() => {
                remaining -= 1;
                if (remaining <= 0) {
                    remaining = 0;
                    window.clearInterval(intervalId);
                }
                render();
            }, 1000);
        }

        form.addEventListener("submit", (event) => {
            if (remaining > 0) {
                event.preventDefault();
                return;
            }
            remaining = 30;
            render();
        });
    });
})();
