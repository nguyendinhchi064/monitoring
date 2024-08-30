document.addEventListener('DOMContentLoaded', () => {
    const apiBaseUrl = 'http://localhost:8080/log';
    const token = localStorage.getItem('token');

    // Function to create a new collection
    async function createCollection(collectionName) {
        try {
            const response = await fetch(`${apiBaseUrl}/${collectionName}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                alert('Collection created successfully!');
                fetchCollections(); // Refresh the list of collections
            } else {
                const errorText = await response.text();
                alert('Failed to create collection. ' + errorText);
            }
        } catch (error) {
            console.error('Error creating collection:', error);
        }
    }

    // Function to fetch all collections and populate the table
    async function fetchCollections() {
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
        const tableBody = document.getElementById('collections-table-body');
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

    fetchCollections();

    // Event listener for the Submit button to create a new collection
    document.querySelector('.btn-create-collection').addEventListener('click', () => {
        const collectionName = document.getElementById('NewCollectionName').value.trim();
        if (collectionName) {
            createCollection(collectionName);
        } else {
            alert('Please enter a collection name.');
        }
    });

    // Fetch and display the list of collections when the page loads
    fetchCollections();
});
