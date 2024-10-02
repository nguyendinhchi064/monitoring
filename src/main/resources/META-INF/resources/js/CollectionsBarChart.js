document.addEventListener('DOMContentLoaded', () => {

    const chartCanvas = document.getElementById('collectionCreationChart');
    if (!chartCanvas) {
        console.log('Chart container not found. Skipping chart setup.');
        return;  // Exit if the chart container doesn't exist in the current HTML
    }

    function setupChart(dataLabels, dataValues) {
        const ctx = chartCanvas.getContext('2d');
        if (window.collectionChart) {
            window.collectionChart.destroy();
        }
        window.collectionChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: dataLabels,
                datasets: [{
                    label: 'Collections Created Over Time',
                    data: dataValues,
                    backgroundColor: 'rgba(7,86,228,0.2)',
                    borderColor: 'rgb(22,129,207)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Date'
                        }
                    },
                    y: {
                        beginAtZero: true, // Ensure Y-axis starts at 0
                        ticks: {
                            callback: function(value) {
                                return Number.isInteger(value) ? value : null; // Force integer ticks
                            }
                        },
                        title: {
                            display: true,
                            text: 'Number of Collections'
                        }
                    }
                }
            }
        });
    }

    async function fetchAndDisplayCollections() {
        const apiBaseUrl = 'http://localhost:8080/log';  // Adjust API URL
        const token = localStorage.getItem('token');

        try {
            const response = await fetch(`${apiBaseUrl}/collectionLogs`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch collections: ${response.status} ${response.statusText}`);
            }

            const logs = await response.json();

            // Group collections by creation date
            const groupedData = logs.reduce((acc, log) => {
                const date = new Date(log.createdAt).toDateString(); // Group by date
                acc[date] = (acc[date] || 0) + 1;
                return acc;
            }, {});

            // Ensure all dates, including dates without new collections
            const allDates = [];
            const startDate = new Date(Object.keys(groupedData)[0]);
            const endDate = new Date(Object.keys(groupedData)[Object.keys(groupedData).length - 1]);

            for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
                allDates.push(new Date(d).toDateString());
            }

            const dataValues = allDates.map(date => groupedData[date] || 0); // Ensure missing dates get 0
            setupChart(allDates, dataValues);

        } catch (error) {
            console.error('Error fetching collection logs:', error);
        }
    }

    fetchAndDisplayCollections(); // Fetch data and setup chart on page load
});
