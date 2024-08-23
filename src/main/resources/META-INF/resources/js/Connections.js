document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('connection-form');

    // Function to decode JWT
    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            console.error('Failed to parse JWT:', e);
            return null;
        }
    }

    // Retrieve the token from localStorage
    const token = localStorage.getItem('token');  // Adjusted to match the key in your login script

    if (!token) {
        alert('JWT token is missing. Please log in again.');
        return;  // Stop execution since the token is not available
    }

    // Decode the token to extract the username
    const decodedToken = parseJwt(token);
    const username = decodedToken ? decodedToken.username : '';

    if (!username) {
        alert('Failed to retrieve username from token. Please ensure you are logged in.');
        return;
    }

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        const formData = {
            mysqlHost: document.getElementById('mysqlHost').value,
            mysqlPort: document.getElementById('mysqlPort').value,
            mysqlDb: document.getElementById('mysqlDb').value,
            mysqlUser: document.getElementById('mysqlUser').value,
            mysqlPassword: document.getElementById('mysqlPassword').value,
            tableName: document.getElementById('tableName').value,
            mongoConnectionString: 'mongodb://root:root@localhost:27018/?authSource=admin',
            mongoDatabase: username,  // Use the username as the MongoDB database name
            mongoCollection: document.getElementById('mongoCollection').value
        };

        try {
            const response = await fetch('/db_transfer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` // Include the token in the request header
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                const result = await response.json();
                alert('Data transfer successful!');
                console.log('Transferred Documents:', result);
            } else {
                const errorText = await response.text();
                alert('Error during data transfer: ' + errorText);
            }
        } catch (error) {
            alert('Unexpected error: ' + error.message);
        }
    });
});
