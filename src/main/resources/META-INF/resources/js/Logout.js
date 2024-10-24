document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('logoutLink').addEventListener('click', function(event) {
        event.preventDefault();
        logout();
    });

    function logout() {
        const token = localStorage.getItem('token');
        if (token) {
            fetch('http://localhost:8080/Auth/Logout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (response.ok) {
                        // Remove the token from localStorage
                        localStorage.removeItem('token');
                        alert('You have been logged out successfully.');
                        // Redirect to the login page
                        window.location.href = 'login.html';
                    } else {
                        response.json().then(data => {
                            alert(`Logout failed: ${data.message}`);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error during logout:', error);
                    alert('Logout failed due to an error. Please try again.');
                });
        } else {
            alert('No user is currently logged in.');
            // Redirect to the login page
            window.location.href = 'login.html';
        }
    }
});