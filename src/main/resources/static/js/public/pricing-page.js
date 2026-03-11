// FAQ: close others when one opens (optional UX)
(function() {
    var items = document.querySelectorAll('.faq-item');
    items.forEach(function(item) {
        item.addEventListener('toggle', function() {
            if (item.open) {
                items.forEach(function(other) {
                    if (other !== item) other.open = false;
                });
            }
        });
    });
}());
