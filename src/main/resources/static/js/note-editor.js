document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("noteForm");
    const editor = document.getElementById("editor");
    const contentHtml = document.getElementById("contentHtml");

    const headingSelect = document.getElementById("headingSelect");
    const fontSizeSelect = document.getElementById("fontSizeSelect");

    const linkBtn = document.getElementById("linkBtn");
    const highlightBtn = document.getElementById("highlightBtn");
    const clearBtn = document.getElementById("clearBtn");

    const initial = (colourInput.value || "slate").toLowerCase();
    document.querySelectorAll(".chip[data-note-colour]").forEach(c => {
        if ((c.getAttribute("data-note-colour") || "") === initial) {
            document.querySelectorAll(".chip[data-note-colour]").forEach(x => x.classList.remove("active"));
            c.classList.add("active");
        }
    });

    // Colour chips
    const colourInput = document.getElementById("noteColour");
    document.querySelectorAll(".chip[data-note-colour]").forEach(chip => {
        chip.addEventListener("click", () => {
            document.querySelectorAll(".chip[data-note-colour]").forEach(c => c.classList.remove("active"));
            chip.classList.add("active");
            colourInput.value = chip.getAttribute("data-note-colour") || "slate";
        });
    });

    // Toolbar commands (bold/italic/underline, lists, etc.)
    document.querySelectorAll(".tool[data-cmd]").forEach(btn => {
        btn.addEventListener("click", () => {
            const cmd = btn.getAttribute("data-cmd");
            editor.focus();
            document.execCommand(cmd, false, null);
        });
    });

    // Alignment
    document.querySelectorAll(".tool[data-align]").forEach(btn => {
        btn.addEventListener("click", () => {
            const align = btn.getAttribute("data-align");
            editor.focus();
            if (align === "left") document.execCommand("justifyLeft");
            if (align === "center") document.execCommand("justifyCenter");
            if (align === "right") document.execCommand("justifyRight");
        });
    });

    // Headings
    headingSelect.addEventListener("change", () => {
        editor.focus();
        const v = headingSelect.value;
        if (v === "p") document.execCommand("formatBlock", false, "p");
        else if (v === "blockquote") document.execCommand("formatBlock", false, "blockquote");
        else document.execCommand("formatBlock", false, v);
    });

    // Font size (execCommand uses 1–7)
    fontSizeSelect.addEventListener("change", () => {
        editor.focus();
        document.execCommand("fontSize", false, fontSizeSelect.value);
    });

    // Link
    linkBtn.addEventListener("click", () => {
        editor.focus();
        const url = prompt("Paste a link (https://...)");
        if (!url) return;
        document.execCommand("createLink", false, url);
    });

    // Highlight
    highlightBtn.addEventListener("click", () => {
        editor.focus();
        // Works in most browsers:
        document.execCommand("hiliteColor", false, "rgba(34,211,238,0.25)");
    });

    // Clear formatting
    clearBtn.addEventListener("click", () => {
        editor.focus();
        document.execCommand("removeFormat", false, null);
    });

    // Submit: put HTML into hidden input
    form.addEventListener("submit", () => {
        // Keep content clean-ish
        contentHtml.value = editor.innerHTML.trim();
    });

    // Keyboard shortcuts
    editor.addEventListener("keydown", (e) => {
        if (e.ctrlKey && e.key.toLowerCase() === "b") { e.preventDefault(); document.execCommand("bold"); }
        if (e.ctrlKey && e.key.toLowerCase() === "i") { e.preventDefault(); document.execCommand("italic"); }
        if (e.ctrlKey && e.key.toLowerCase() === "u") { e.preventDefault(); document.execCommand("underline"); }
    });
});
