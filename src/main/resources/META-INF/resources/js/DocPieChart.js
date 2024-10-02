document.addEventListener('DOMContentLoaded', () => {
    // Function to generate random colors for the collections
    function getRandomColor() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }

    // Function to set up the pie chart
    function setupPieChart(collectionLabels, collectionData) {
        const ctx = document.getElementById('collectionPieChart').getContext('2d');
        if (window.pieChart) {
            window.pieChart.destroy();
        }

        // Generate dynamic colors for each collection
        const dynamicColors = collectionLabels.map(() => getRandomColor());

        window.pieChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: collectionLabels,
                datasets: [{
                    label: 'Number of Documents per Collection',
                    data: collectionData,
                    backgroundColor: dynamicColors,
                    borderColor: dynamicColors.map(color => color.replace(/0.2/, '1')),
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(tooltipItem) {
                                const label = tooltipItem.label || '';
                                const value = tooltipItem.raw;
                                return `${label}: ${value} documents`;
                            }
                        }
                    }
                }
            }
        });
    }

    // Function to fetch and display the collections data in the pie chart
    async function fetchAndDisplayCollections() {
        const apiBaseUrl = 'http://localhost:8080/log'; // Adjust API URL if needed
        const token = localStorage.getItem('token');

        try {
            const response = await fetch(`${apiBaseUrl}/collectionStats`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch collection data: ${response.status} ${response.statusText}`);
            }

            const collections = await response.json();
            console.log('Fetched collections:', collections); // Debug: log the fetched data

            const collectionLabels = collections.map(col => col.collectionName);  // Adjust according to your data structure
            const collectionData = collections.map(col => col.documentCount);     // Adjust according to your data structure

            setupPieChart(collectionLabels, collectionData);  // Set up the chart with the new data

        } catch (error) {
            console.error('Error fetching collection data:', error.message);
        }
    }

    // Initial fetch of collections to populate the pie chart on load
    fetchAndDisplayCollections();

});


