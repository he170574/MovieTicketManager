let chart = null;

function createChart(labels, values) {
    const ctx = document.getElementById('ticketsChart').getContext('2d');
    chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Tickets Sold',
                data: values,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
                fill: false // Ensures the area under the line is not filled
            }]
        },
        options: {
            scales: {
                y: {
                    ticks: {
                        callback: function (value, index, values) {
                            return Number.isInteger(value) ? value : '';
                        },
                        beginAtZero: true
                    }
                }
            }
        }
    });
}

function showChart() {
    let from = $("#from").val();
    let to = $("#to").val();
    fetch(`/get-ticket-statistic?from=${from}&to=${to}`)
        .then(response => response.json())
        .then(result => {
            let data = Object.entries(result.data);
            data.sort((a, b) => (new Date(a[0]) - new Date(b[0])))
            let labels = data.map(e => e[0]);
            let values = data.map(e => e[1]);
            let totalTickets = values.reduce((acc, val) => acc + val, 0);
            $("#totalTicketsSold").text(`Total Tickets Sold: ${totalTickets}`);
            if (!chart) {
                createChart(labels, values);
            } else {
                chart.data.datasets[0].data = values;
                chart.data.labels = labels;
                chart.update();
            }
        })
}

$("#to").val(moment().startOf("day").format("YYYY-MM-DD"))
$("#from").val(moment().subtract(7, "day").format("YYYY-MM-DD"))
showChart();