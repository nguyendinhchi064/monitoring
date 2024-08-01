document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const response = await fetch('/users/me', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const user = await response.json();
        document.getElementById('profileUserName').innerText = user.username;
        document.getElementById('profileFullName').innerText = user.fullName;
        document.getElementById('profileEmail').innerText = user.email;
        document.getElementById('profilePhone').innerText = user.phone;
        document.getElementById('profileAddress').innerText = user.address;
    } else {
        const errorData = await response.json();
        document.getElementById('userProfileResponse').innerText = errorData.message;
    }
});


