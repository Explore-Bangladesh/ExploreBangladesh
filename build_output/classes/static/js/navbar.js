let base64Image = null;

/**
 * Update navbar UI based on authentication status
 */
async function updateNavbar() {
    console.log('updateNavbar() called');
    const token = localStorage.getItem('token');
    const userMenuDiv = document.getElementById('userMenu');
    const loginBtn = document.getElementById('loginBtn');
    const signupBtn = document.getElementById('signupBtn');
    const avatarBtn = document.getElementById('avatarBtn');

    if (!loginBtn || !signupBtn || !userMenuDiv) {
        console.log('Navbar elements not found');
        return;
    }

    if (token) {
        console.log('User logged in - showing avatar menu');
        // Hide login/signup buttons
        loginBtn.classList.add('d-none');
        signupBtn.classList.add('d-none');
        
        // Show user menu
        userMenuDiv.classList.remove('d-none');
        userMenuDiv.style.display = 'block';

        // Load user profile
        try {
            const response = await fetch('/api/v1/profile/me', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const user = await response.json();
                const avatarImg = document.getElementById('avatarImg');
                if (avatarImg) {
                    avatarImg.src = user.image && user.image.startsWith('data:image')
                        ? user.image
                        : (user.image || 'Assets/default_user.jpg');
                }
            } else if (response.status === 401 || response.status === 403) {
                // Token invalid
                localStorage.removeItem('token');
                loginBtn.classList.remove('d-none');
                signupBtn.classList.remove('d-none');
                userMenuDiv.classList.add('d-none');
            }
        } catch (error) {
            console.error('Error loading profile:', error);
        }
        
        // Re-initialize dropdown after showing
        setTimeout(() => {
            initAvatarDropdown();
        }, 100);
        
    } else {
        console.log('No token - showing login buttons');
        loginBtn.classList.remove('d-none');
        signupBtn.classList.remove('d-none');
        userMenuDiv.classList.add('d-none');
        userMenuDiv.style.display = 'none';
    }
}

// Initialize avatar dropdown with proper click handlers
function initAvatarDropdown() {
    const avatarBtn = document.getElementById('avatarBtn');
    const dropdownMenu = document.getElementById('avatarDropdown');
    
    if (!avatarBtn || !dropdownMenu) {
        console.log('Avatar button or dropdown not found');
        return;
    }
    
    // Remove any existing event listeners
    const newAvatarBtn = avatarBtn.cloneNode(true);
    avatarBtn.parentNode.replaceChild(newAvatarBtn, avatarBtn);
    
    // Get fresh references
    const freshAvatarBtn = document.getElementById('avatarBtn');
    const freshDropdownMenu = document.getElementById('avatarDropdown');
    
    // Manual dropdown handler
    freshAvatarBtn.addEventListener('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        console.log('Avatar clicked - toggling dropdown');
        
        // Toggle the dropdown
        if (freshDropdownMenu.classList.contains('show')) {
            freshDropdownMenu.classList.remove('show');
            this.setAttribute('aria-expanded', 'false');
        } else {
            // Close any other open dropdowns
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                if (menu !== freshDropdownMenu) {
                    menu.classList.remove('show');
                }
            });
            freshDropdownMenu.classList.add('show');
            this.setAttribute('aria-expanded', 'true');
        }
    });
    
    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (!freshAvatarBtn.contains(e.target) && !freshDropdownMenu.contains(e.target)) {
            freshDropdownMenu.classList.remove('show');
            freshAvatarBtn.setAttribute('aria-expanded', 'false');
        }
    });
    
    console.log('Avatar dropdown initialized');
}

// Open profile modal
async function openProfileModal() {
    console.log('Opening profile modal...');
    const token = localStorage.getItem('token');

    if (!token) {
        alert('Please login first');
        return;
    }

    const modalElement = document.getElementById('profileModal');
    if (!modalElement) {
        console.error('Profile modal not found');
        alert('Profile modal not found');
        return;
    }

    try {
        const response = await fetch('/api/v1/profile/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const user = await response.json();
            console.log('Profile loaded:', user.email);

            // Populate form fields
            const modalFirstName = document.getElementById('modalFirstName');
            const modalLastName = document.getElementById('modalLastName');
            const modalEmail = document.getElementById('modalEmail');
            const modalPhone = document.getElementById('modalPhone');
            const modalDob = document.getElementById('modalDob');
            const modalCountry = document.getElementById('modalCountry');
            const modalNewsletter = document.getElementById('modalNewsletter');
            const modalAvatarImg = document.getElementById('modalAvatarImg');

            if (modalFirstName) modalFirstName.value = user.firstName || '';
            if (modalLastName) modalLastName.value = user.lastName || '';
            if (modalEmail) modalEmail.value = user.email || '';
            if (modalPhone) modalPhone.value = user.phone || '';
            if (modalDob) modalDob.value = user.dob || '';
            if (modalCountry) modalCountry.value = user.country || '';
            if (modalNewsletter) modalNewsletter.checked = user.subscribeNewsletter || false;

            if (modalAvatarImg) {
                if (user.image && user.image.startsWith('data:image')) {
                    modalAvatarImg.src = user.image;
                    base64Image = user.image;
                } else if (user.image) {
                    modalAvatarImg.src = user.image;
                } else {
                    modalAvatarImg.src = 'Assets/default_user.jpg';
                }
            }

            // Hide alerts
            const profileAlert = document.getElementById('profileAlert');
            const profileError = document.getElementById('profileError');
            if (profileAlert) profileAlert.classList.add('d-none');
            if (profileError) profileError.classList.add('d-none');

            // Show modal
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        } else {
            alert('Failed to load profile. Status: ' + response.status);
        }
    } catch (error) {
        console.error('Error loading profile:', error);
        alert('Error loading profile: ' + error.message);
    }
}

// Save profile
async function saveProfile() {
    console.log('Saving profile...');
    const token = localStorage.getItem('token');
    const saveBtn = document.getElementById('saveProfileBtn');

    if (!saveBtn) return;

    const originalText = saveBtn.textContent;
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Saving...';

    const modalFirstName = document.getElementById('modalFirstName');
    const modalLastName = document.getElementById('modalLastName');
    const modalPhone = document.getElementById('modalPhone');
    const modalDob = document.getElementById('modalDob');
    const modalCountry = document.getElementById('modalCountry');
    const modalNewsletter = document.getElementById('modalNewsletter');

    const updatedData = {
        firstName: modalFirstName?.value || '',
        lastName: modalLastName?.value || '',
        phone: modalPhone?.value || '',
        dob: modalDob?.value || null,
        country: modalCountry?.value || '',
        subscribeNewsletter: modalNewsletter?.checked || false
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
            const avatarImg = document.getElementById('avatarImg');
            if (updatedUser.image && avatarImg) {
                avatarImg.src = updatedUser.image;
            }

            // Show success message
            const profileAlert = document.getElementById('profileAlert');
            if (profileAlert) {
                profileAlert.classList.remove('d-none');
                setTimeout(() => {
                    profileAlert.classList.add('d-none');
                }, 3000);
            }
        } else {
            alert('Failed to update profile');
        }
    } catch (error) {
        console.error('Error saving profile:', error);
        alert('Error updating profile: ' + error.message);
    } finally {
        saveBtn.disabled = false;
        saveBtn.textContent = originalText;
    }
}

// Logout function
async function logout() {
    console.log('Logout initiated');
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
            console.error('Logout API error:', error);
        } finally {
            // Clear all localStorage
            localStorage.clear();
            
            // Redirect to home page
            window.location.href = '/';
        }
    }
}

// Handle OAuth callback and show dropdown
function handleOAuthCallback() {
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    
    if (tokenFromUrl) {
        localStorage.setItem('token', tokenFromUrl);
        window.history.replaceState({}, document.title, window.location.pathname);
        updateNavbar();
        return true;
    }
    return false;
}

// DOM Content Loaded
document.addEventListener('DOMContentLoaded', async () => {
    console.log('DOM loaded - initializing navbar');
    
    // Handle OAuth callback
    handleOAuthCallback();
    
    // Update navbar
    await updateNavbar();
    
    // Profile link click handler
    const profileLink = document.getElementById('profileLink');
    if (profileLink) {
        profileLink.addEventListener('click', async (e) => {
            e.preventDefault();
            e.stopPropagation();
            console.log('Profile link clicked');
            const profileModal = document.getElementById('profileModal');
            if (profileModal) {
                await openProfileModal();
            } else {
                window.location.href = 'profile.html';
            }
            
            // Close dropdown after clicking
            const dropdownMenu = document.getElementById('avatarDropdown');
            if (dropdownMenu) {
                dropdownMenu.classList.remove('show');
            }
        });
    }
    
    // Logout link click handler
    const logoutLink = document.getElementById('logoutLink');
    if (logoutLink) {
        logoutLink.addEventListener('click', async (e) => {
            e.preventDefault();
            e.stopPropagation();
            console.log('Logout link clicked');
            await logout();
        });
    }
    
    // Image upload handler
    const imageUpload = document.getElementById('imageUpload');
    if (imageUpload) {
        imageUpload.addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    base64Image = e.target.result;
                    const modalAvatarImg = document.getElementById('modalAvatarImg');
                    const avatarImg = document.getElementById('avatarImg');
                    if (modalAvatarImg) modalAvatarImg.src = base64Image;
                    if (avatarImg) avatarImg.src = base64Image;
                };
                reader.readAsDataURL(file);
            }
        });
    }
    
    // Save profile button
    const saveProfileBtn = document.getElementById('saveProfileBtn');
    if (saveProfileBtn) {
        saveProfileBtn.addEventListener('click', async () => {
            await saveProfile();
        });
    }
    
    // Initialize dropdown again after everything loads
    setTimeout(() => {
        initAvatarDropdown();
    }, 500);
});
