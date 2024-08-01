document.getElementById('infoForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const fullName = document.getElementById('fullName').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const token = localStorage.getItem('token');

    if (!token) {
        document.getElementById('infoResponse').innerText = "User not authenticated. Please log in.";
        return;
    }

    const response = await fetch('/Auth/updateInfo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept':'*/*',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ fullName, phone, address })
    });

    if (response.ok) {
        window.location.href = 'UserProfile.html';
    } else {
        try {
            const errorData = await response.json();
            document.getElementById('infoResponse').innerText = errorData.message;
        } catch (error) {
            document.getElementById('infoResponse').innerText = "An unexpected error occurred.";
        }
    }
});
