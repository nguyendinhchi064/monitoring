const jwtToken = localStorage.getItem('token');

// Function to fetch the latest timestamp for the total collection bar chart
function updateCollectionLogsFooter() {
    fetch('/log/collectionLogs/latest', {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to fetch latest collectionLogs');
        })
        .then(data => {
            if (data && data.createdAt) {
                document.getElementById('collectionBarFooter').innerText = `Updated at: ${new Date(data.createdAt).toLocaleString()}`;
            } else {
                document.getElementById('collectionBarFooter').innerText = 'Updated at: Not available';
            }
        })
        .catch(error => {
            console.error('Error fetching latest collectionLogs:', error);
            document.getElementById('collectionBarFooter').innerText = 'Updated at: Error fetching data';
        });
}

// Function to fetch the latest timestamp for the data in each collection (pie chart)
function updateUserLatestFooter() {
    fetch('/log/user/latest', {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to fetch latest user collection data');
        })
        .then(data => {
            if (data && data.latestTime) {
                const formattedTime = new Date(data.latestTime).toLocaleString();
                document.getElementById('docPieFooter').innerText = `Updated at: ${formattedTime}`;
            } else {
                document.getElementById('docPieFooter').innerText = 'Updated at: Not available';
            }
        })
        .catch(error => {
            console.error('Error fetching latest user collection data:', error);
            document.getElementById('docPieFooter').innerText = 'Updated at: Error fetching data';
        });
}


// Function to fetch the latest timestamp for a specific collection bar chart (Documents of a specific collection)
function updateSpecificCollectionFooter(collectionName) {
    if (!collectionName) {
        document.getElementById('dataTransferFooter').innerText = 'Updated at: No collection name provided';
        return;
    }

    fetch(`/log/${collectionName}/latest`, {
        headers: {
            'Authorization': `Bearer ${jwtToken}`
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error(`Failed to fetch earliest data for collection: ${collectionName}`);
        })
        .then(data => {
            if (data && data.createdAt) {
                document.getElementById('dataTransferFooter').innerText = `Updated at: ${new Date(data.createdAt).toLocaleString()}`;
            } else {
                document.getElementById('dataTransferFooter').innerText = 'Updated at: Not available';
            }
        })
        .catch(error => {
            console.error(`Error fetching earliest data for collection: ${collectionName}`, error);
            document.getElementById('dataTransferFooter').innerText = 'Updated at: Error fetching data';
        });
}

// Attach event listener to fetch data for a specific collection when the button is clicked
document.querySelector('.btn-fetch-data').addEventListener('click', () => {
    const collectionName = document.getElementById('CollectionNameInput').value.trim();
    updateSpecificCollectionFooter(collectionName);
});

// Call functions to update the footers after the page loads
document.addEventListener('DOMContentLoaded', () => {
    updateCollectionLogsFooter(); // For total collection bar chart
    updateUserLatestFooter(); // For data in each collection pie chart
});
