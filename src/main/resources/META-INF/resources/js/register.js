document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    if (!registerForm) {
        console.error('Register form not found');
        return;
    }

    registerForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const email = document.getElementById('email').value;

        try {
            const response = await fetch('/Auth/Register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*'
                },
                body: JSON.stringify({ username, password, confirmPassword, email })
            });

            const result = await response.json();
            const registerResponse = document.getElementById('registerResponse');
            if (response.ok) {
                registerResponse.innerText = 'Registration successful!';
            } else {
                registerResponse.innerText = `Registration failed: ${result.message}`;
            }
        } catch (error) {
            console.error('Error:', error);
            const registerResponse = document.getElementById('registerResponse');
            registerResponse.innerText = 'Registration failed: Server error';
        }
    });
});
