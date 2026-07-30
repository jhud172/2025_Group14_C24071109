(function () {
    "use strict";

    const root = document.documentElement;
    const systemTheme = window.matchMedia?.("(prefers-color-scheme: dark)");

    const applyTheme = () => {
        const theme = root.getAttribute("data-theme") || "system";
        const useDarkTheme = theme === "dark" || (theme === "system" && systemTheme?.matches);
        root.classList.toggle("dark", Boolean(useDarkTheme));
        root.style.colorScheme = useDarkTheme ? "dark" : "light";
    };

    applyTheme();

    if (systemTheme) {
        if (typeof systemTheme.addEventListener === "function") {
            systemTheme.addEventListener("change", applyTheme);
        } else if (typeof systemTheme.addListener === "function") {
            systemTheme.addListener(applyTheme);
        }
    }
})();
