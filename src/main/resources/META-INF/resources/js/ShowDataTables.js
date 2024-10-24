document.addEventListener('DOMContentLoaded', () => {
    const apiBaseUrl = 'http://localhost:8080/log';
    const token = localStorage.getItem('token');
    let dataTableInstance = null; // Store reference to DataTable instance

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

                // Clear the existing DataTable instance if it exists
                if (dataTableInstance) {
                    dataTableInstance.destroy(); // Clear any existing DataTable instance
                    dataTableInstance = null;
                }

                // Clear the table before adding new data
                clearTable();

                // Populate the table with new data
                populateDataTable(data);

                // Initialize the DataTable with new data
                dataTableInstance = new simpleDatatables.DataTable(document.querySelector('#collection-data-table'));
            } else {
                const errorText = await response.text();
                alert('Failed to fetch data. ' + errorText);
            }
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

    // Function to clear the table
    function clearTable() {
        const tableHeader = document.querySelector('#collection-data-table thead tr');
        const tableBody = document.getElementById('collection-data-body');

        if (tableHeader) {
            tableHeader.innerHTML = ''; // Clear the header
        } else {
            console.error('Error: Table header element not found.');
        }

        if (tableBody) {
            tableBody.innerHTML = ''; // Clear the body
        } else {
            console.error('Error: Table body element not found.');
        }
    }

    // Function to populate the table with fetched data
    function populateDataTable(data) {
        const tableHeader = document.querySelector('#collection-data-table thead tr');
        const tableBody = document.getElementById('collection-data-body');

        // Ensure tableBody exists before populating data
        if (!tableBody) {
            console.error('Error: Table body element not found.');
            return;
        }

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
