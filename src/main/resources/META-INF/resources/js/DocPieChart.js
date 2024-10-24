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

    // Function to set up the pie chart using Plotly.js
    function setupPieChart(collectionLabels, collectionData) {
        // Generate dynamic colors for each collection
        const dynamicColors = collectionLabels.map(() => getRandomColor());

        const data = [{
            values: collectionData,
            labels: collectionLabels,
            type: 'pie',
            marker: {
                colors: dynamicColors
            },
            textinfo: 'label+percent',
            hoverinfo: 'label+percent+value'
        }];

        const layout = {
            title: 'Number of Documents per Collection',
            height: 400,
            width: 500,
            showlegend: true,
        };

        // Render the Plotly pie chart
        Plotly.newPlot('collectionPiePlotlyChart', data, layout);
    }

    // Function to fetch and display the collections data in the pie chart
    async function fetchAndDisplayCollections() {
        const apiBaseUrl = 'http://localhost:8080/log';
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
