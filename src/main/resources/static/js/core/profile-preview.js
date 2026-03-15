(function () {
    "use strict";

    const OPEN_CLASS = "is-open";
    const BIO_COLLAPSED_CLASS = "is-collapsed";
    const BIO_EXPANDED_CLASS = "is-expanded";
    const INTERACTIVE_SELECTOR = [
        "a",
        "button",
        "input",
        "select",
        "textarea",
        "label",
        "[data-premium-badge-group]",
        "[data-profile-preview-ignore]"
    ].join(", ");

    function applyThemeScope(scope) {
        if (!(scope instanceof HTMLElement)) {
            return;
        }

        const nameColor = (scope.dataset.profileNameColor || "").trim();
        const copyColor = (scope.dataset.profileCopyColor || "").trim();

        if (nameColor) {
            scope.style.setProperty("--profile-preview-name-color", nameColor);
        } else {
            scope.style.removeProperty("--profile-preview-name-color");
        }

        if (copyColor) {
            scope.style.setProperty("--profile-preview-copy-color", copyColor);
        } else {
            scope.style.removeProperty("--profile-preview-copy-color");
        }

        scope.querySelectorAll("[data-profile-theme-banner]").forEach((banner) => {
            if (!(banner instanceof HTMLElement)) {
                return;
            }

            if (nameColor) {
                banner.style.setProperty("--profile-identity-name-color", nameColor);
            } else {
                banner.style.removeProperty("--profile-identity-name-color");
            }

            if (copyColor) {
                banner.style.setProperty("--profile-identity-handle-color", copyColor);
            } else {
                banner.style.removeProperty("--profile-identity-handle-color");
            }
        });
    }

    function setPreviewOpen(root, open) {
        if (!(root instanceof HTMLElement)) {
            return;
        }

        const trigger = root.querySelector("[data-profile-preview-trigger]");
        const panel = root.querySelector("[data-profile-preview-panel]");

        root.classList.toggle(OPEN_CLASS, open);

        if (trigger instanceof HTMLElement) {
            trigger.setAttribute("aria-expanded", open ? "true" : "false");
        }

        if (panel instanceof HTMLElement) {
            panel.setAttribute("aria-hidden", open ? "false" : "true");
        }
    }

    function initPreviewShell(root) {
        if (!(root instanceof HTMLElement) || root.dataset.profilePreviewReady === "true") {
            return;
        }

        const trigger = root.querySelector("[data-profile-preview-trigger]");
        const panel = root.querySelector("[data-profile-preview-panel]");
        if (!(trigger instanceof HTMLElement) || !(panel instanceof HTMLElement)) {
            return;
        }

        root.dataset.profilePreviewReady = "true";
        if (!root.classList.contains(OPEN_CLASS)) {
            setPreviewOpen(root, false);
        }

        const toggle = () => setPreviewOpen(root, !root.classList.contains(OPEN_CLASS));
        const togglePreservingScroll = () => {
            const scrollTop = window.scrollY;
            toggle();
            window.requestAnimationFrame(() => {
                if (Math.abs(window.scrollY - scrollTop) > 1) {
                    window.scrollTo({ top: scrollTop });
                }
            });
        };

        trigger.addEventListener("click", (event) => {
            if (event.target instanceof Element && event.target.closest(INTERACTIVE_SELECTOR)) {
                return;
            }
            event.preventDefault();
            togglePreservingScroll();
        });

        trigger.addEventListener("keydown", (event) => {
            if (event.key !== "Enter" && event.key !== " ") {
                if (event.key === "Escape") {
                    setPreviewOpen(root, false);
                }
                return;
            }
            event.preventDefault();
            togglePreservingScroll();
        });

        if (root.dataset.profilePreviewHover === "true") {
            root.addEventListener("mouseenter", () => setPreviewOpen(root, true));
            root.addEventListener("mouseleave", () => setPreviewOpen(root, false));
        }

        root.addEventListener("keydown", (event) => {
            if (event.key === "Escape") {
                setPreviewOpen(root, false);
            }
        });
    }

    function refreshBioBlock(block) {
        if (!(block instanceof HTMLElement)) {
            return;
        }

        const copy = block.querySelector("[data-profile-bio]");
        const toggle = block.querySelector("[data-profile-bio-toggle]");
        if (!(copy instanceof HTMLElement) || !(toggle instanceof HTMLElement)) {
            return;
        }

        const wasExpanded = block.classList.contains(BIO_EXPANDED_CLASS);
        const collapsedLines = Math.max(1, Number(block.dataset.bioCollapsedLines || "3"));
        const lineHeight = parseFloat(window.getComputedStyle(copy).lineHeight || "22");

        block.classList.remove(BIO_COLLAPSED_CLASS, BIO_EXPANDED_CLASS);

        const collapsedHeight = Math.ceil(Math.max(lineHeight * collapsedLines, 66));
        const fullHeight = Math.ceil(copy.scrollHeight);
        const needsToggle = fullHeight > collapsedHeight + 4;

        toggle.hidden = !needsToggle;
        toggle.setAttribute("aria-hidden", needsToggle ? "false" : "true");

        if (!needsToggle) {
            toggle.textContent = "Read more";
            toggle.setAttribute("aria-expanded", "false");
            return;
        }

        block.classList.add(wasExpanded ? BIO_EXPANDED_CLASS : BIO_COLLAPSED_CLASS);
        toggle.textContent = wasExpanded ? "Read less" : "Read more";
        toggle.setAttribute("aria-expanded", wasExpanded ? "true" : "false");
    }

    function initBioBlock(block) {
        if (!(block instanceof HTMLElement) || block.dataset.profileBioReady === "true") {
            refreshBioBlock(block);
            return;
        }

        const toggle = block.querySelector("[data-profile-bio-toggle]");
        if (!(toggle instanceof HTMLElement)) {
            return;
        }

        block.dataset.profileBioReady = "true";
        toggle.addEventListener("click", (event) => {
            event.preventDefault();

            const expand = !block.classList.contains(BIO_EXPANDED_CLASS);
            block.classList.toggle(BIO_EXPANDED_CLASS, expand);
            block.classList.toggle(BIO_COLLAPSED_CLASS, !expand);
            refreshBioBlock(block);
        });

        refreshBioBlock(block);
    }

    function refreshAll(scope) {
        const root = scope instanceof Element ? scope : document;
        const selectors = {
            theme: "[data-profile-theme-scope]",
            preview: "[data-profile-preview]",
            bio: "[data-profile-bio-block]"
        };

        const collect = (selector) => {
            const items = Array.from(root.querySelectorAll(selector));
            if (root instanceof Element && root.matches(selector)) {
                items.unshift(root);
            }
            return items;
        };

        collect(selectors.theme).forEach(applyThemeScope);
        collect(selectors.preview).forEach(initPreviewShell);
        collect(selectors.bio).forEach(initBioBlock);
    }

    document.addEventListener("click", (event) => {
        if (!(event.target instanceof Element)) {
            return;
        }

        document.querySelectorAll("[data-profile-preview].is-open").forEach((root) => {
            if (!(root instanceof HTMLElement) || root.contains(event.target)) {
                return;
            }
            setPreviewOpen(root, false);
        });
    });

    let refreshTimer = null;
    window.addEventListener("resize", () => {
        window.clearTimeout(refreshTimer);
        refreshTimer = window.setTimeout(() => refreshAll(document), 120);
    });

    window.OneToOneProfilePreview = {
        refreshAll
    };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", () => refreshAll(document));
    } else {
        refreshAll(document);
    }
})();
