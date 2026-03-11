/**
 * vault.js – Training Vault client-side enhancements
 *
 * Handles: live search debounce, filter application,
 * confirm delete (with modal when available), flash fade-out,
 * sidebar collapse toggle.
 */
(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {

        // ── Confirm modal setup ────────────────────────────────────────
        var confirmOverlay  = document.getElementById("confirmOverlay");
        var confirmTitle    = document.getElementById("confirmTitle");
        var confirmMessage  = document.getElementById("confirmMessage");
        var confirmOk       = document.getElementById("confirmOk");
        var confirmCancel   = document.getElementById("confirmCancel");
        var pendingForm     = null;

        function showConfirm(title, message, form) {
            if (!confirmOverlay) {
                // Fallback when no modal is available
                if (confirm(message || "Are you sure?")) {
                    form._confirmed = true;
                    form.submit();
                }
                return;
            }
            if (confirmTitle)   confirmTitle.textContent   = title   || "Confirm";
            if (confirmMessage) confirmMessage.textContent = message || "Are you sure?";
            pendingForm = form;
            confirmOverlay.classList.add("show");
        }

        if (confirmOk) {
            confirmOk.addEventListener("click", function () {
                confirmOverlay.classList.remove("show");
                if (pendingForm) {
                    pendingForm._confirmed = true;
                    pendingForm.submit();
                    pendingForm = null;
                }
            });
        }

        if (confirmCancel) {
            confirmCancel.addEventListener("click", function () {
                confirmOverlay.classList.remove("show");
                pendingForm = null;
            });
        }

        if (confirmOverlay) {
            confirmOverlay.addEventListener("click", function (e) {
                if (e.target === confirmOverlay) {
                    confirmOverlay.classList.remove("show");
                    pendingForm = null;
                }
            });
        }

        // ── Wire delete-confirm forms ──────────────────────────────────
        document.querySelectorAll("[data-vault-confirm-delete]").forEach(function (form) {
            form.addEventListener("submit", function (e) {
                if (form._confirmed) return; // already confirmed
                e.preventDefault();
                var title   = form.getAttribute("data-confirm-title")   || "Delete note?";
                var message = form.getAttribute("data-confirm-message") || "This cannot be undone.";
                showConfirm(title, message, form);
            });
        });

        // Wire data-confirm forms (used in note-view.html)
        document.querySelectorAll("[data-confirm]").forEach(function (form) {
            form.addEventListener("submit", function (e) {
                if (form._confirmed) return;
                e.preventDefault();
                var title   = form.getAttribute("data-confirm-title")   || "Confirm";
                var message = form.getAttribute("data-confirm-message") || "Are you sure?";
                showConfirm(title, message, form);
            });
        });

        // ── Live Search ─────────────────────────────────────────────────
        var searchInput = document.getElementById("vaultSearch");
        if (searchInput) {
            var searchTimer = null;
            searchInput.addEventListener("input", function () {
                clearTimeout(searchTimer);
                searchTimer = setTimeout(function () {
                    var searchForm = document.getElementById("vaultSearchForm");
                    if (searchForm) searchForm.submit();
                }, 350);
            });
        }

        // ── Filter form auto-submit on change ────────────────────────────
        var filterForm = document.getElementById("vaultFilterForm");
        if (filterForm) {
            filterForm.querySelectorAll("select, input[type=checkbox]").forEach(function (el) {
                el.addEventListener("change", function () { filterForm.submit(); });
            });
            filterForm.querySelectorAll("input[type=date]").forEach(function (el) {
                el.addEventListener("change", function () { filterForm.submit(); });
            });
        }

        // ── Card hover actions – prevent link navigation on action btns ─
        document.querySelectorAll(".vault-card-actions form, .vault-card-actions a").forEach(function (el) {
            el.addEventListener("click", function (e) {
                e.stopPropagation();
            });
        });

        // ── Make vault cards fully clickable ──────────────────────────
        document.querySelectorAll(".vault-card").forEach(function (card) {
            var link = card.querySelector("a[data-card-link]");
            if (!link) return;
            var href = link.href;
            card.style.cursor = "pointer";
            card.addEventListener("click", function (e) {
                if (e.target.closest("a[href]:not([data-card-link]), button, input, select, textarea")) return;
                window.location.href = href;
            });
        });

        // ── Sidebar collapsible sections ──────────────────────────────
        document.querySelectorAll("[data-vault-collapse]").forEach(function (toggle) {
            var targetId = toggle.getAttribute("data-vault-collapse");
            var target   = document.getElementById(targetId);
            if (!target) return;
            toggle.addEventListener("click", function () {
                var isOpen = !target.classList.contains("hidden");
                target.classList.toggle("hidden", isOpen);
                var icon = toggle.querySelector("[data-collapse-icon]");
                if (icon) {
                    icon.style.transform = isOpen ? "rotate(-90deg)" : "";
                }
            });
        });

        // ── Flash fade-out ─────────────────────────────────────────────
        var flashEls = document.querySelectorAll("[data-vault-flash]");
        if (flashEls.length) {
            setTimeout(function () {
                flashEls.forEach(function (el) {
                    el.style.transition = "opacity 0.5s ease";
                    el.style.opacity    = "0";
                    setTimeout(function () { el.remove(); }, 600);
                });
            }, 6000);
        }

    });
}());
