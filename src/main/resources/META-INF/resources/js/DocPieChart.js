document.addEventListener('DOMContentLoaded', () => {
    const ctx = document.getElementById('collectionPieChart').getContext('2d');
    let pieChart;

    // Function to fetch collection statistics and update the pie chart
    async function fetchCollectionStats() {
        const apiBaseUrl = 'http://localhost:8080/log';  // Adjust as per your actual API URL
        const token = localStorage.getItem('token');  // Ensure the token is available

        try {
            const response = await fetch(`${apiBaseUrl}/collectionStats`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error(`Failed to fetch collection stats: ${response.status} ${response.statusText}`);
            }

            const collectionStats = await response.json();
            const labels = collectionStats.map(stat => stat.collectionName);
            const data = collectionStats.map(stat => stat.documentCount);

            updatePieChart(labels, data);

        } catch (error) {
            console.error('Error fetching collection stats:', error);
        }
    }

    // Function to update the pie chart
    function updatePieChart(labels, data) {
        if (pieChart) {
            pieChart.destroy();  // Destroy the old chart before creating a new one
        }

        pieChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Document Count by Collection',
                    data: data,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(153, 102, 255, 0.2)',
                        'rgba(255, 159, 64, 0.2)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(75, 192, 192, 1)',
                        'rgba(153, 102, 255, 1)',
                        'rgba(255, 159, 64, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true
            }
        });
    }

    // Initial fetch to populate the pie chart when the page loads
    fetchCollectionStats();

    // Listen to localStorage changes (when triggered by other pages)
    window.addEventListener('storage', (event) => {
        if (event.key === 'collectionUpdated' || event.key === 'dataTransferred') {
            fetchCollectionStats();
            localStorage.removeItem(event.key);  // Clean up after the update
        }
    });
});
