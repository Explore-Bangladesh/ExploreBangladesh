let base64Image = null;

document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    const authActionsDiv = document.getElementById('authActions');
    const userMenuDiv = document.getElementById('userMenu');
    const loginBtn = document.getElementById('loginBtn');
    const signupBtn = document.getElementById('signupBtn');

    if (token) {
        // User is logged in - show avatar instead of login/signup buttons
        loginBtn.classList.add('d-none');
        signupBtn.classList.add('d-none');
        userMenuDiv.classList.remove('d-none');

        // Load and display user profile
        try {
            const response = await fetch('/api/v1/profile/me', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const user = await response.json();
                document.getElementById('avatarImg').src = user.image && user.image.startsWith('data:image') 
                    ? user.image 
                    : (user.image || 'https://via.placeholder.com/40');
            } else if (response.status === 401 || response.status === 403) {
                // Token invalid, clear localStorage and show login buttons
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                loginBtn.classList.remove('d-none');
                signupBtn.classList.remove('d-none');
                userMenuDiv.classList.add('d-none');
            }
        } catch (error) {
            console.error('Error loading profile:', error);
        }
    } else {
        // User not logged in - show login/signup buttons
        loginBtn.classList.remove('d-none');
        signupBtn.classList.remove('d-none');
        userMenuDiv.classList.add('d-none');
    }

    // Profile modal event listeners
    document.getElementById('profileLink').addEventListener('click', async (e) => {
        e.preventDefault();
        await openProfileModal();
    });

    document.getElementById('avatarBtn').addEventListener('click', () => {
        // Dropdown will open automatically via Bootstrap
    });

    // Image upload handler
    document.getElementById('imageUpload').addEventListener('change', function(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                base64Image = e.target.result;
                document.getElementById('modalAvatarImg').src = base64Image;
                document.getElementById('avatarImg').src = base64Image;
            };
            reader.readAsDataURL(file);
        }
    });

    // Save profile button
    document.getElementById('saveProfileBtn').addEventListener('click', async () => {
        await saveProfile();
    });

    // Logout handler
    document.getElementById('logoutLink').addEventListener('click', async (e) => {
        e.preventDefault();
        await logout();
    });

    // Allow pressing Enter in profile form fields
    document.getElementById('profileForm').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            document.getElementById('saveProfileBtn').click();
        }
    });
});

async function openProfileModal() {
    const token = localStorage.getItem('token');
    const modal = new bootstrap.Modal(document.getElementById('profileModal'));

    try {
        const response = await fetch('/api/v1/profile/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const user = await response.json();
            
            // Populate form fields
            document.getElementById('modalFirstName').value = user.firstName || '';
            document.getElementById('modalLastName').value = user.lastName || '';
            document.getElementById('modalEmail').value = user.email || '';
            document.getElementById('modalPhone').value = user.phone || '';
            document.getElementById('modalDob').value = user.dob || '';
            document.getElementById('modalCountry').value = user.country || '';
            document.getElementById('modalNewsletter').checked = user.subscribeNewsletter || false;

            // Set profile image
            if (user.image && user.image.startsWith('data:image')) {
                document.getElementById('modalAvatarImg').src = user.image;
                base64Image = user.image;
            } else if (user.image) {
                document.getElementById('modalAvatarImg').src = user.image;
            }

            // Hide alerts
            document.getElementById('profileAlert').classList.add('d-none');
            document.getElementById('profileError').classList.add('d-none');

            modal.show();
        } else {
            console.error('Failed to load profile');
        }
    } catch (error) {
        console.error('Error loading profile:', error);
    }
}

async function saveProfile() {
    const token = localStorage.getItem('token');
    const saveBtn = document.getElementById('saveProfileBtn');
    const originalText = saveBtn.textContent;

    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving...';

    const updatedData = {
        firstName: document.getElementById('modalFirstName').value,
        lastName: document.getElementById('modalLastName').value,
        phone: document.getElementById('modalPhone').value,
        dob: document.getElementById('modalDob').value || null,
        country: document.getElementById('modalCountry').value,
        subscribeNewsletter: document.getElementById('modalNewsletter').checked
    };

    if (base64Image) {
        updatedData.image = base64Image;
    }

    try {
        const response = await fetch('/api/v1/profile/me', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            const updatedUser = await response.json();
            
            // Update navbar avatar
            if (updatedUser.image) {
                document.getElementById('avatarImg').src = updatedUser.image.startsWith('data:image') 
                    ? updatedUser.image 
                    : updatedUser.image;
            }

            // Show success message
            document.getElementById('profileAlert').classList.remove('d-none');
            document.getElementById('profileError').classList.add('d-none');

            // Dismiss after 3 seconds
            setTimeout(() => {
                document.getElementById('profileAlert').classList.add('d-none');
            }, 3000);
        } else {
            showProfileError('Failed to update profile');
        }
    } catch (error) {
        console.error('Error saving profile:', error);
        showProfileError('Error updating profile: ' + error.message);
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = originalText;
    }
}

function showProfileError(message) {
    const errorDiv = document.getElementById('profileError');
    document.getElementById('profileErrorMsg').textContent = message;
    errorDiv.classList.remove('d-none');
    document.getElementById('profileAlert').classList.add('d-none');

    setTimeout(() => {
        errorDiv.classList.add('d-none');
    }, 5000);
}

async function logout() {
    const token = localStorage.getItem('token');

    if (confirm('Are you sure you want to logout?')) {
        try {
            await fetch('/api/v1/auth/logout', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
        } catch (error) {
            console.error('Error logging out:', error);
        } finally {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/';
        }
    }
}
