document.addEventListener('DOMContentLoaded', function() {
    const infoForm = document.getElementById('infoForm');
    if (!infoForm) {
        console.error('Info form not found');
        return;
    }

    infoForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const fullName = document.getElementById('fullName').value;
        const phone = document.getElementById('phone').value;
        const address = document.getElementById('address').value;

        try {
            const token = localStorage.getItem('token'); // Get the stored token

            const response = await fetch('/auth/updateInfo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': '*/*',
                    'Authorization': `Bearer ${token}` // Include the token in the Authorization header
                },
                body: JSON.stringify({ fullName, phone, address })
            });

            const result = await response.json();
            const infoResponse = document.getElementById('infoResponse');
            if (!infoResponse) {
                console.error('Info response element not found');
                return;
            }

            if (response.ok) {
                infoResponse.innerText = 'Information updated successfully!';
            } else {
                infoResponse.innerText = `Update failed: ${result.message}`;
            }
        } catch (error) {
            console.error('Error:', error);
            const infoResponse = document.getElementById('infoResponse');
            if (infoResponse) {
                infoResponse.innerText = 'Update failed: Server error';
            }
        }
    });
});
