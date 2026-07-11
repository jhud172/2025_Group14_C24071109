/**
 * copy-code.js
 * Handles copy-to-clipboard buttons for trainer codes and gym codes on profile pages.
 * Expects elements with ids: copyTrainerCode / copyTrainerCodeLabel / trainerCodeVal
 * and/or copyGymCode / copyGymCodeLabel / gymCodeVal.
 */
(function () {
    function initCopyButton(btnId, labelId, valId) {
        var btn = document.getElementById(btnId);
        var lbl = document.getElementById(labelId);
        var val = document.getElementById(valId);
        if (!btn || !val) {
            return;
        }
        btn.addEventListener('click', function () {
            var text = val.textContent.trim();
            if (!navigator.clipboard) {
                return;
            }
            navigator.clipboard.writeText(text).then(function () {
                if (lbl) {
                    lbl.textContent = 'Copied!';
                    setTimeout(function () { lbl.textContent = 'Copy'; }, 2000);
                }
            });
        });
    }

    initCopyButton('copyTrainerCode', 'copyTrainerCodeLabel', 'trainerCodeVal');
    initCopyButton('copyGymCode', 'copyGymCodeLabel', 'gymCodeVal');
})();
