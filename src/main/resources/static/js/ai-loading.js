(function () {
    "use strict";
    var SPINNER_HTML = '<svg class="animate-spin h-4 w-4 mr-1.5 inline shrink-0" fill="none" viewBox="0 0 24 24" aria-hidden="true">'
        + '<circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>'
        + '<path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>'
        + '</svg>Loading\u2026';
    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll("form[action*='/ai/']").forEach(function (form) {
            form.addEventListener("submit", function () {
                var btn = form.querySelector("button[type='submit'], button:not([type='button']):not([type='reset'])");
                if (!btn || btn.disabled) return;
                btn.disabled = true;
                btn.innerHTML = SPINNER_HTML;
            });
        });
    });
}());
