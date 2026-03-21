(function () {
    function navigateWithQuery(element) {
        const param = element.getAttribute("data-auto-query-param");
        if (!param) return false;

        const url = new URL(window.location.href);
        const value = element.value == null ? "" : String(element.value);

        if (value === "") {
            url.searchParams.delete(param);
        } else {
            url.searchParams.set(param, value);
        }

        window.location.href = url.toString();
        return true;
    }

    document.addEventListener("change", (event) => {
        const element = event.target && event.target.closest
            ? event.target.closest("[data-auto-submit], [data-auto-query-param]")
            : null;

        if (!element) return;

        if (navigateWithQuery(element)) {
            return;
        }

        const form = element.form || element.closest("form");
        if (form && typeof form.requestSubmit === "function") {
            form.requestSubmit();
            return;
        }
        if (form) {
            form.submit();
        }
    });
}());
