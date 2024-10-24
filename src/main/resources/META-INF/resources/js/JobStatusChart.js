document.addEventListener('DOMContentLoaded', () => {
    function fetchJobStatusData() {
        const jwtToken = localStorage.getItem('token');

        fetch('/db_transfer/jobStatusCounts', {
            headers: {
                'Authorization': `Bearer ${jwtToken}`
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Failed to fetch job status counts');
            })
            .then(data => {
                // Plotly Pie Chart for Job Status Overview
                const jobStatusData = [{
                    values: [data.completed, data.failed],
                    labels: ['Completed', 'Failed'],
                    type: 'pie',
                    marker: {
                        colors: ['green', 'red']
                    },
                    textinfo: 'label+percent',
                    hoverinfo: 'label+percent+value'
                }];

                const layout = {
                    title: 'Job Status Overview',
                    height: 400,
                    width: 500
                };

                Plotly.newPlot('jobStatusPlotlyChart', jobStatusData, layout);
            })
            .catch(error => {
                console.error('Error fetching job status data:', error);
            });
    }

    fetchJobStatusData();
});
