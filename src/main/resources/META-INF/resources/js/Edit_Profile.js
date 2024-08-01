document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');

    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('/users/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const user = await response.json();
            document.getElementById('profileUserName').innerText = user.username;
            document.getElementById('FullName').value = user.fullName;
            document.getElementById('email').value = user.email;
            document.getElementById('phone').value = user.phone;
            document.getElementById('address').value = user.address;
        } else {
            const errorData = await response.json();
            document.getElementById('editProfileResponse').innerText = errorData.message;
        }
    } catch (error) {
        document.getElementById('editProfileResponse').innerText = "An error occurred while fetching the profile data.";
    }
});

document.getElementById('editProfileForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const FullName = document.getElementById('FullName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('/users/update', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ FullName, email, phone, address })
        });

        if (response.ok) {
            window.location.href = 'UserProfile.html';
        } else {
            const errorData = await response.json();
            document.getElementById('editProfileResponse').innerText = errorData.message;
        }
    } catch (error) {
        document.getElementById('editProfileResponse').innerText = "An error occurred while updating the profile.";
    }
});
