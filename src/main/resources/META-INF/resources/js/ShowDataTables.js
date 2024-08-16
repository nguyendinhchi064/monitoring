document.addEventListener('DOMContentLoaded', () => {
    const apiBaseUrl = 'http://localhost:8080/log';
    const token = localStorage.getItem('token');

    // Function to fetch data from a specific collection and populate the table
    async function fetchCollectionData(collectionName) {
        try {
            const response = await fetch(`${apiBaseUrl}/${collectionName}/data`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                const data = await response.json();
                populateDataTable(data);
            } else {
                const errorText = await response.text();
                alert('Failed to fetch data. ' + errorText);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

    // Function to populate the table with fetched data
    function populateDataTable(data) {
        const tableHeader = document.querySelector('#collection-data-table thead tr');
        const tableBody = document.getElementById('collection-data-body');

        // Clear any existing data
        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        if (data.length > 0) {
            // Populate headers dynamically based on keys in the first document
            const headers = Object.keys(data[0]);
            headers.forEach(header => {
                const th = document.createElement('th');
                th.textContent = header;
                tableHeader.appendChild(th);
            });

            // Populate rows dynamically
            data.forEach(item => {
                const row = document.createElement('tr');
                headers.forEach(header => {
                    const cell = document.createElement('td');
                    cell.textContent = item[header];
                    row.appendChild(cell);
                });
                tableBody.appendChild(row);
            });
        } else {
            // Show a message if there is no data
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.textContent = 'No data found';
            cell.colSpan = 1;
            row.appendChild(cell);
            tableBody.appendChild(row);
        }

        // Initialize the DataTable
        new simpleDatatables.DataTable(document.querySelector('#collection-data-table'));
    }

    // Event listener for the Show button
    document.querySelector('.btn-fetch-data').addEventListener('click', () => {
        const collectionName = document.getElementById('CollectionName').value.trim();
        if (collectionName) {
            fetchCollectionData(collectionName);
        } else {
            alert('Please enter a collection name.');
        }
    });
});
