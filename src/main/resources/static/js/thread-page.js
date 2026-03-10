(function () {
    const patterns = [
        /\bpaypal\b/i,
        /paypal\.me/i,
        /\bvenmo\b/i,
        /venmo\.com/i,
        /\bcashapp\b/i,
        /\bcashtag\b/i,
        /\bzelle\b/i,
        /\brevolut\b/i,
        /\bwise\b/i,
        /\bbacs\b/i,
        /\bfaster\s+payments\b/i,
        /\bwire\s+transfer\b/i,
        /\bbank\s+transfer\b/i,
        /\bsort\s+code\b/i,
        /\baccount\s+number\b/i,
        /\biban\b/i,
        /\bswift\b/i,
        /\brouting\s+number\b/i,
        /\bpay\s+me\b/i,
        /\bpay\s+via\b/i,
        /\bsend\s+money\b/i,
        /\bmeet\s+and\s+pay\b/i,
        /\bpay\s+on\s+meet\b/i,
        /\bcash\b/i,
        /(pay|send|transfer|paypal|cashapp|venmo|revolut|wise)\s*(to)?\s*@\w{2,}/i
    ];
    const sortCodeDigits = /\b(?:\d[\s-]*){6}\b/;
    const accountDigits = /\b(?:\d[\s-]*){8}\b/;
    const ibanPattern = /\b[A-Z]{2}\d{2}[A-Z0-9]{10,30}\b/;

    const textarea = document.getElementById("bodyText");
    const warning = document.getElementById("offPlatformWarning");
    const sendButton = document.getElementById("sendMessageButton");
    const form = textarea ? textarea.closest("form") : null;
    if (!textarea || !warning || !sendButton || !form) return;

    function normalize(value) {
        return (value || "").toLowerCase();
    }

    function collapse(value) {
        return normalize(value).replace(/[^a-z0-9]/g, "");
    }

    function containsOffPlatform(value) {
        const text = normalize(value);
        if (!text.trim()) return false;
        if (patterns.some((regex) => regex.test(text))) return true;

        const collapsed = collapse(text);
        const collapsedHits = [
            "paypal",
            "venmo",
            "cashapp",
            "revolut",
            "wise",
            "banktransfer",
            "sortcode",
            "accountnumber",
            "iban",
            "swift",
            "meetandpay"
        ];
        if (collapsedHits.some((token) => collapsed.includes(token))) return true;

        if (sortCodeDigits.test(text) || accountDigits.test(text)) return true;

        const compact = text.replace(/\s+/g, "").toUpperCase();
        if (ibanPattern.test(compact)) return true;

        return false;
    }

    function updateWarning() {
        const hit = containsOffPlatform(textarea.value);
        warning.classList.toggle("hidden", !hit);
        sendButton.disabled = hit;
        if (hit) {
            sendButton.classList.add("opacity-50", "cursor-not-allowed");
        } else {
            sendButton.classList.remove("opacity-50", "cursor-not-allowed");
        }
        return hit;
    }

    textarea.addEventListener("input", updateWarning);
    form.addEventListener("submit", function (event) {
        if (updateWarning()) {
            event.preventDefault();
        }
    });
    updateWarning();
})();
