document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (!loginForm) {
        console.error('Login form not found');
        return;
    }

    loginForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/Auth/Login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*'
                },
                body: JSON.stringify({ username, password })
            });

            let result;
            try {
                result = await response.json();
            } catch (e) {
                console.error('Failed to parse JSON response', e);
                result = { message: 'Failed to parse JSON response' };
            }

            const loginResponse = document.getElementById('loginResponse');
            if (!loginResponse) {
                console.error('Login response element not found');
                return;
            }

            if (response.ok) {
                localStorage.setItem('token', result.token);
                loginResponse.innerText = `Login successful!`;
                window.location.href = 'infoForm.html'
            } else {
                loginResponse.innerText = `Login failed: ${result.message}`;
            }
        } catch (error) {
            console.error('Error:', error);
            const loginResponse = document.getElementById('loginResponse');
            if (loginResponse) {
                loginResponse.innerText = 'Login failed: Server error';
            }
        }
    });
});
