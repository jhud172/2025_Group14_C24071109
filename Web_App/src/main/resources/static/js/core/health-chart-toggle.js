(function () {
    const select = document.querySelector('[data-health-chart-select]');
    const bodyGraphs = document.getElementById('body-measurement-graphs');
    const cardioGraphs = document.getElementById('cardiovascular-graphs');

    if (!select || !bodyGraphs || !cardioGraphs) return;

    function updateCharts() {
        const showCardio = select.value === 'cardiovascular-health';
        bodyGraphs.classList.toggle('hidden', showCardio);
        cardioGraphs.classList.toggle('hidden', !showCardio);
    }

    select.addEventListener('change', updateCharts);
    updateCharts();
}());
