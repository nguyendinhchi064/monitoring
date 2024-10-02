document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem('token');

    // Event listener for creating a new collection
    document.querySelector('.btn-create-collection').addEventListener('click', function () {
        const collectionName = document.getElementById('NewCollectionName').value.trim();
        if (!collectionName) {
            alert('Please enter a collection name.');
            return;
        }

        const apiUrl = 'http://localhost:8080/log/create'; // Adjust the endpoint if needed
        const data = { collectionName: collectionName };

        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    // Handle plain text response
                    return response.text();
                } else {
                    return response.text().then(text => { throw new Error(text); });
                }
            })
            .then(message => {
                console.log(message);
                alert(message);
                fetchCollections();  // Refresh the list of collections
            })
            .catch(error => {
                console.error('Error creating collection:', error);
                alert('Error creating collection: ' + error.message);
            });
    });


    // Function to fetch and update the list of collections
    function fetchCollections() {
        const collectionsUrl = 'http://localhost:8080/log/collections'; // Ensure this URL matches your actual API
        fetch(collectionsUrl, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to fetch collections');
                }
            })
            .then(collections => {
                console.log('Fetched collections:', collections);
                localStorage.setItem('collectionsFetched', 'true');
                populateCollectionsTable(collections);
            })
            .catch(error => {
                console.error('Error fetching collections:', error);
                alert('Error fetching collections: ' + error.message);
            });
    }

    // Function to populate the table with collections
    function populateCollectionsTable(collections) {
        const tableBody = document.getElementById('collections-table-body');
        tableBody.innerHTML = ''; // Clear the table before inserting new data
        collections.forEach(collectionName => {
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.textContent = collectionName;
            row.appendChild(cell);
            tableBody.appendChild(row)
        });
    }

    // Initial fetch of collections to populate the table on load
    fetchCollections();
});
