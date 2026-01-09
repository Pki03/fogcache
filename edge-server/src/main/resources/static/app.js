let cacheChart;

async function refresh() {
    const metrics = await fetch('/metrics').then(r => r.json());
    const hotkeys = await fetch('/admin/hotkeys').then(r => r.json());

    document.getElementById('metrics').innerHTML = `
        Requests: ${metrics.totalRequests}<br>
        Hits: ${metrics.cacheHits}<br>
        Misses: ${metrics.cacheMisses}<br>
        Origin Calls: ${metrics.originCalls}<br>
        Errors: ${metrics.errors}
    `;

    document.getElementById('hotkeys').innerHTML =
        hotkeys.map(k => `${k.key} : ${k.count}`).join('<br>');

    const data = {
        labels: ['Hits', 'Misses', 'Origin'],
        datasets: [{
            data: [metrics.cacheHits, metrics.cacheMisses, metrics.originCalls]
        }]
    };

    if (!cacheChart) {
        const ctx = document.getElementById('cacheChart');
        cacheChart = new Chart(ctx, { type: 'doughnut', data });
    } else {
        cacheChart.data = data;
        cacheChart.update();
    }
}

setInterval(refresh, 2000);
refresh();
