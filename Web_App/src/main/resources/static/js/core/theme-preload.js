(function () {
    "use strict";

    const theme = document.documentElement.getAttribute("data-theme");
    if (theme === "system"
        && window.matchMedia
        && window.matchMedia("(prefers-color-scheme: dark)").matches) {
        document.documentElement.classList.add("dark");
    }
})();
