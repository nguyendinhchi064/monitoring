document.addEventListener('DOMContentLoaded', () => {

    function setupChart(dataLabels, dataValues, latestTimestamps) {
        const ctx = document.getElementById('collectionCreationChart').getContext('2d');
        if (window.collectionChart) {
            window.collectionChart.destroy();
        }

        window.collectionChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: dataLabels,  // Only actual date labels
                datasets: [{
                    label: 'Collections Created Over Time',
                    data: dataValues,
                    backgroundColor: 'rgba(7,86,228,0.2)',
                    borderColor: 'rgb(22,129,207)',
                    borderWidth: 1,
                    fill: true,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                }]
            },
            options: {
                scales: {
                    x: {
                        type: 'category',
                        title: {
                            display: true,
                            text: 'Date'
                        }
                    },
                    y: {
                        min: 0,  // Ensure the Y-axis starts at 0
                        ticks: {
                            beginAtZero: true,  // Enforce starting at 0
                            stepSize: 1  // Ensure step size is reasonable for your data
                        },
                        title: {
                            display: true,
                            text: 'Number of Collections'
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function(tooltipItem) {
                                const date = tooltipItem.label;
                                const count = tooltipItem.raw;
                                const latestTime = latestTimestamps[tooltipItem.dataIndex];
                                return `Collections: ${count} (Latest at ${latestTime.toLocaleTimeString()})`;
                            }
                        }
                    }
                }
            }
        });
    }

    async function fetchAndDisplayCollections() {
        const apiBaseUrl = 'http://localhost:8080/log';  // Adjust the API URL as needed
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
            console.log('Fetched collection logs:', logs);  // Debug: log the collection logs

            // Check if no data is returned, to force Y-axis to start at 0
            if (logs.length === 0) {
                setupChart([], [0], []);  // Call setupChart with a 0 value for the Y-axis
                return;
            }

            // Group data by date and count the collections created per day
            const groupedData = logs.reduce((acc, log) => {
                const date = new Date(log.createdAt).toDateString();  // Extract the date only
                if (!acc[date]) {
                    acc[date] = { count: 0, latestTimestamp: new Date(log.createdAt) };
                }
                acc[date].count += 1;
                if (new Date(log.createdAt) > acc[date].latestTimestamp) {
                    acc[date].latestTimestamp = new Date(log.createdAt);  // Update with the latest timestamp
                }
                return acc;
            }, {});

            // Get all dates between the earliest and latest dates in the dataset
            const allDates = [];
            const startDate = new Date(Object.keys(groupedData)[0]);
            const endDate = new Date(Object.keys(groupedData)[Object.keys(groupedData).length - 1]);
            for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
                allDates.push(new Date(d).toDateString());
            }

            // Ensure all dates have a count, even if it's 0
            const counts = allDates.map(date => groupedData[date] ? groupedData[date].count : 0);
            const latestTimestamps = allDates.map(date => groupedData[date] ? groupedData[date].latestTimestamp : new Date(startDate));

            setupChart(allDates, counts, latestTimestamps);

        } catch (error) {
            console.error('Error fetching collection logs:', error);
        }
    }

    // Event listener for fetching collections when button is clicked
    document.querySelector('.btn-fetch-data').addEventListener('click', () => {
        const collectionName = document.getElementById('CollectionNameInput').value.trim();
        if (collectionName) {
            fetchAndDisplayCollections(collectionName);
        } else {
            alert('Please enter a collection name.');
        }
    });

    // Initial fetch to display the collections chart on page load
    fetchAndDisplayCollections();
});
