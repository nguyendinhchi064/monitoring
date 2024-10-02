 // Function to decode JWT
    function parseJwt(token) {
    try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
}).join(''));
    return JSON.parse(jsonPayload);
} catch (e) {
    console.error('Invalid token:', e);
    return null;
}
}

    // Get the token from localStorage
    const token = localStorage.getItem('token'); // Adjust the key if it's named differently

    if (token) {
    // Parse the token to extract the username
    const decodedToken = parseJwt(token);
    const username = decodedToken ? decodedToken.username : 'Unknown User'; // Adjust the key if the username is stored under a different key

    // Display the username in the "Logged in as:" section
    document.querySelector('.sb-sidenav-footer .small').textContent = 'Logged in as: ' + username;
} else {
    // If no token is found, display a default message
    document.querySelector('.sb-sidenav-footer .small').textContent = 'Logged in as: Guest';
}
