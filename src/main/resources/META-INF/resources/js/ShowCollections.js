document.addEventListener('DOMContentLoaded', () => {
    const apiBaseUrl = 'http://localhost:8080/log';
    const token = localStorage.getItem('token');

    // Function to fetch all collections and populate the table
    async function FetchCollections() {
        try {
            const response = await fetch(`${apiBaseUrl}/collections`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                const collections = await response.json();
                populateCollectionsTable(collections);
            } else {
                const errorText = await response.text();
                alert('Failed to fetch collections. ' + errorText);
            }
        } catch (error) {
            console.error('Error fetching collections:', error);
        }
    }

    // Function to populate the table with the list of collections
    function populateCollectionsTable(collections) {
        const tableBody = document.getElementById('Collections-table-body');
        tableBody.innerHTML = ''; // Clear the table before inserting new data

        collections.forEach(collectionName => {
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.textContent = collectionName;
            row.appendChild(cell);
            tableBody.appendChild(row);
        });

        // Initialize DataTable after populating the table
        const table = new simpleDatatables.DataTable("#collections-table", {
            searchable: true,
            fixedHeight: true
        });
    }

    FetchCollections();
});
