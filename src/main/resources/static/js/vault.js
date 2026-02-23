/**
 * vault.js – Training Vault client-side enhancements
 *
 * Handles: live search debounce, filter application,
 * pin toggle feedback, delete confirm, view toggles.
 */
(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        // ── Live Search ─────────────────────────────────────────────
        const searchInput = document.getElementById("vaultSearch");
        if (searchInput) {
            let searchTimer = null;
            searchInput.addEventListener("input", function () {
                clearTimeout(searchTimer);
                searchTimer = setTimeout(function () {
                    submitVaultFilters();
                }, 350);
            });
        }

        // ── Filter form auto-submit on change ────────────────────────
        const filterForm = document.getElementById("vaultFilterForm");
        if (filterForm) {
            filterForm.querySelectorAll("select, input[type=checkbox]").forEach(function (el) {
                el.addEventListener("change", function () {
                    submitVaultFilters();
                });
            });
            filterForm.querySelectorAll("input[type=date]").forEach(function (el) {
                el.addEventListener("change", function () {
                    submitVaultFilters();
                });
            });
        }

        function submitVaultFilters() {
            if (filterForm) {
                filterForm.submit();
            }
        }

        // ── Confirm-delete for vault cards ────────────────────────────
        document.querySelectorAll("[data-vault-confirm-delete]").forEach(function (form) {
            form.addEventListener("submit", function (e) {
                if (!confirm("Delete this note? This cannot be undone.")) {
                    e.preventDefault();
                }
            });
        });

        // ── Card hover actions – prevent link navigation on action btns ─
        document.querySelectorAll(".vault-card-actions form, .vault-card-actions a").forEach(function (el) {
            el.addEventListener("click", function (e) {
                e.stopPropagation();
            });
        });

        // ── Filter chip removal ───────────────────────────────────────
        document.querySelectorAll("[data-remove-filter]").forEach(function (btn) {
            btn.addEventListener("click", function () {
                const param = btn.getAttribute("data-remove-filter");
                if (!filterForm) return;
                const input = filterForm.querySelector("[name=" + param + "]");
                if (input) {
                    if (input.type === "checkbox") {
                        input.checked = false;
                    } else {
                        input.value = "";
                    }
                }
                submitVaultFilters();
            });
        });

        // ── Sidebar collapsible sections ──────────────────────────────
        document.querySelectorAll("[data-vault-collapse]").forEach(function (toggle) {
            const targetId = toggle.getAttribute("data-vault-collapse");
            const target = document.getElementById(targetId);
            if (!target) return;
            toggle.addEventListener("click", function () {
                const isOpen = !target.classList.contains("hidden");
                target.classList.toggle("hidden", isOpen);
                const icon = toggle.querySelector("[data-collapse-icon]");
                if (icon) {
                    icon.style.transform = isOpen ? "rotate(-90deg)" : "";
                }
            });
        });

        // ── Flash fade-out ─────────────────────────────────────────────
        const flashEls = document.querySelectorAll("[data-vault-flash]");
        if (flashEls.length) {
            setTimeout(function () {
                flashEls.forEach(function (el) {
                    el.style.transition = "opacity 0.5s ease";
                    el.style.opacity = "0";
                    setTimeout(function () { el.remove(); }, 600);
                });
            }, 5000);
        }
    });
}());
