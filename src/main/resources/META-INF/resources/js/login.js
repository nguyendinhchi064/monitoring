document.getElementById('loginForm').addEventListener('submit', async (event) => {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/Auth/Login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    });
    const responseData = await response.json();
    if (response.status === 428) {
        localStorage.setItem('token', responseData.token);
        window.location.href = 'infoForm.html';
    } else if (response.ok) {
        localStorage.setItem('token', responseData.token);
        window.location.href = 'MainPage.html';
    } else {
        const errorText = await response.json();
        document.getElementById('loginResponse').innerText = errorText.message;
    }
});
