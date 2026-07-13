(function () {
    "use strict";

    const registrations = new Map();
    const activeByGroup = new Map();
    const DEFAULT_GROUP = "surface";

    function getGroup(name, fallbackGroup) {
        return registrations.get(name)?.group || fallbackGroup || DEFAULT_GROUP;
    }

    function syncBodyState() {
        if (!document.body) return;

        const surfaceOwner = activeByGroup.get(DEFAULT_GROUP);
        const modalOwner = activeByGroup.get("modal");

        if (surfaceOwner) document.body.dataset.overlayOwner = surfaceOwner;
        else delete document.body.dataset.overlayOwner;

        if (modalOwner) document.body.dataset.modalOwner = modalOwner;
        else delete document.body.dataset.modalOwner;
    }

    function announce(group, previous, current, reason) {
        document.dispatchEvent(new CustomEvent("one-to-one:overlay-change", {
            detail: { group, previous: previous || null, current: current || null, reason: reason || "state-change" }
        }));
    }

    function release(name, options) {
        const group = getGroup(name, options?.group);
        if (activeByGroup.get(group) !== name) return false;

        activeByGroup.delete(group);
        syncBodyState();
        announce(group, name, null, options?.reason || "closed");
        return true;
    }

    function open(name, options) {
        if (!name) return false;

        const group = getGroup(name, options?.group);
        const previous = activeByGroup.get(group);
        if (previous === name) {
            syncBodyState();
            return true;
        }

        if (previous) {
            activeByGroup.delete(group);
            syncBodyState();
            registrations.get(previous)?.close?.({
                reason: "replaced",
                restoreFocus: false,
                fromOverlayManager: true
            });
        }

        activeByGroup.set(group, name);
        syncBodyState();
        announce(group, previous, name, options?.reason || "opened");
        return true;
    }

    function close(name, options) {
        const registration = registrations.get(name);
        const group = getGroup(name, options?.group);
        if (activeByGroup.get(group) !== name) return false;

        activeByGroup.delete(group);
        syncBodyState();
        registration?.close?.({
            reason: options?.reason || "requested",
            restoreFocus: options?.restoreFocus !== false,
            fromOverlayManager: true
        });
        announce(group, name, null, options?.reason || "requested");
        return true;
    }

    function register(name, options) {
        if (!name || typeof options?.close !== "function") {
            throw new TypeError("Overlay registration requires a name and close callback.");
        }

        registrations.set(name, {
            group: options.group || DEFAULT_GROUP,
            close: options.close
        });
        syncBodyState();

        return function unregister() {
            release(name, { group: options.group, reason: "unregistered" });
            registrations.delete(name);
        };
    }

    window.OneToOneOverlay = Object.freeze({
        register,
        open,
        close,
        release,
        active(group) {
            return activeByGroup.get(group || DEFAULT_GROUP) || null;
        },
        isActive(name, group) {
            return activeByGroup.get(getGroup(name, group)) === name;
        }
    });
})();
