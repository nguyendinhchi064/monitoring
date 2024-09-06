document.addEventListener('DOMContentLoaded', () => {

    function setupChart(dataLabels, dataValues, latestTimestamps) {
        const ctx = document.getElementById('collectionDataChart').getContext('2d');
        if (window.DataTransferChart) {
            window.DataTransferChart.destroy();
        }
        window.DataTransferChart = new Chart(ctx, {
            type: 'bar',  // Changed from 'line' to 'bar'
            data: {
                labels: dataLabels,  // Only actual date labels
                datasets: [{
                    label: 'Data Transfers Over Time',
                    data: dataValues,
                    backgroundColor: 'rgba(7,86,228,0.2)',
                    borderColor: 'rgb(22,129,207)',
                    borderWidth: 1,
                    fill: true
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
                        min: 0,  // Explicitly set the minimum value to 0
                        ticks: {
                            beginAtZero: true,  // Enforce starting at 0
                            stepSize: 1,  // Ensure step size is reasonable for your data
                            callback: function(value) {
                                if (Number.isInteger(value)) {
                                    return value;  // Ensure Y-axis uses integers only
                                }
                            }
                        },
                        title: {
                            display: true,
                            text: 'Number of Data Transfers'
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
                                return `Transfers: ${count} (Latest at ${latestTime.toLocaleTimeString()})`;
                            }
                        }
                    }
                }
            }
        });
    }

    async function fetchData(collectionName) {
        const apiBaseUrl = 'http://localhost:8080/log';
        const token = localStorage.getItem('token');

        try {
            const response = await fetch(`${apiBaseUrl}/${collectionName}/data`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch data: ${response.status} ${response.statusText}`);
            }

            const data = await response.json();
            console.log('Fetched data:', data);  // Debug: log the entire response data

            // Check if no data is returned, to force Y-axis to start at 0
            if (data.length === 0) {
                setupChart([], [0], []); // Call setupChart with a 0 value for the Y-axis
                return;
            }

            // Group data by date and count the transfers per day
            const groupedData = data.reduce((acc, doc) => {
                const date = new Date(doc._id.date).toDateString();  // Extract the date only
                if (!acc[date]) {
                    acc[date] = { count: 0, latestTimestamp: new Date(doc._id.date) };
                }
                acc[date].count += 1;
                if (new Date(doc._id.date) > acc[date].latestTimestamp) {
                    acc[date].latestTimestamp = new Date(doc._id.date);  // Update with the latest timestamp
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
            console.error('Error fetching data:', error.message);
        }
    }

    document.querySelector('.btn-fetch-data').addEventListener('click', () => {
        const collectionName = document.getElementById('CollectionNameInput').value.trim();
        if (collectionName) {
            fetchData(collectionName);
        } else {
            alert('Please enter a collection name.');
        }
    });
});
