document.getElementById('registerForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const email = document.getElementById('email').value;

    const response = await fetch('/Auth/Register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password, confirmPassword, email })
    });

    if (response.ok) {
        window.location.href = 'login.html';
    } else {
        const errorText = await response.text();
        document.getElementById('registerResponse').innerText = errorText;
    }
});
