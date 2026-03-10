(function() {
    if (localStorage.getItem('legal-confirmed-terms') === '1') {
        document.getElementById('legal-confirm-pending').style.display = 'none';
        var msg = document.getElementById('legal-confirmed-msg');
        msg.style.display = 'flex';
    }
})();
function legalConfirm(key) {
    localStorage.setItem('legal-confirmed-' + key, '1');
    document.getElementById('legal-confirm-pending').style.display = 'none';
    var msg = document.getElementById('legal-confirmed-msg');
    msg.style.display = 'flex';
}
