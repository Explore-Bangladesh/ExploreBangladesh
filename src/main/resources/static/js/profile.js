let base64Image = null;

document.addEventListener('DOMContentLoaded', async () => {
    // Use auth.js utilities to get token
    const token = getAccessToken();
    
    if (!token) {
        redirectToLogin();
        return;
    }

    try {
        // Fetch personal profile details using authenticatedFetch
        const response = await authenticatedFetch('/api/v1/profile/me', {
            method: 'GET'
        });

        if (response.ok) {
            const user = await response.json();
            populateForm(user);
        } else {
            console.error('Failed to load profile');
            if (response.status === 401 || response.status === 403) {
                logout();
            }
        }
    } catch (error) {
        console.error('Error fetching profile:', error);
    }

    // Handle Image Upload Selection
    document.getElementById('imageUpload').addEventListener('change', function(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                base64Image = e.target.result;
                document.getElementById('profilePreview').src = base64Image;
            };
            reader.readAsDataURL(file);
        }
    });

    // Handle Form Submission
    document.getElementById('profileForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        
        const saveBtn = document.getElementById('saveBtn');
        saveBtn.disabled = true;
        saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving...';
        
        const updatedData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            phone: document.getElementById('phone').value,
            dob: document.getElementById('dob').value || null,
            country: document.getElementById('country').value,
            subscribeNewsletter: document.getElementById('subscribeNewsletter').checked,
        };

        if (base64Image) {
            updatedData.image = base64Image;
        }

        try {
            const res = await authenticatedFetch('/api/v1/profile/me', {
                method: 'PUT',
                body: JSON.stringify(updatedData)
            });

            if (res.ok) {
                const updatedUser = await res.json();
                populateForm(updatedUser);
                showAlert('alertMessage', 3000);
            } else {
                showAlert('errorMessage', 3000);
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            showAlert('errorMessage', 3000);
        } finally {
            saveBtn.disabled = false;
            saveBtn.innerHTML = 'Save Changes';
        }
    });

    // Handle Logout
    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        logout(); // Uses auth.js logout function
    });
});

function populateForm(user) {
    document.getElementById('email').value = user.email || '';
    document.getElementById('firstName').value = user.firstName || '';
    document.getElementById('lastName').value = user.lastName || '';
    document.getElementById('phone').value = user.phone || '';
    
    if (user.dob) {
        document.getElementById('dob').value = user.dob;
    }
    
    document.getElementById('country').value = user.country || '';
    document.getElementById('subscribeNewsletter').checked = user.subscribeNewsletter || false;
    
    if (user.image && user.image.startsWith('data:image')) {
        document.getElementById('profilePreview').src = user.image;
        base64Image = user.image;
    } else if (user.image) {
        document.getElementById('profilePreview').src = user.image;
    } else {
        document.getElementById('profilePreview').src = 'Assets/default_user.jpg';
    }
}

function showAlert(id, duration) {
    const alert = document.getElementById(id);
    alert.style.display = 'block';
    setTimeout(() => {
        alert.style.display = 'none';
    }, duration);
}