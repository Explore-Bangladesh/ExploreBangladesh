/**
 * Authentication helper utility
 * Handles token management for both traditional login and OAuth2
 */

const AUTH_TOKEN_KEY = 'token';  // Changed to 'token' to match navbar.js and login.html
const REFRESH_TOKEN_KEY = 'refreshToken';
const USER_KEY = 'user';
const LOGIN_TIME_KEY = 'loginTime';
const REMEMBER_EMAIL_KEY = 'rememberEmail';

/**
 * Store tokens after successful authentication
 * Called after OAuth2 login redirects back from backend
 */
function storeAuthTokens(accessToken, user = null) {
    if (accessToken) {
        localStorage.setItem(AUTH_TOKEN_KEY, accessToken);
        localStorage.setItem(LOGIN_TIME_KEY, String(Date.now()));
        if (user) {
            localStorage.setItem(USER_KEY, JSON.stringify(user));
        }
    }
}

/**
 * Get the stored access token
 */
function getAccessToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

/**
 * Get the refresh token from cookies
 * (Refresh token is stored in HTTP-only cookie)
 */
function getRefreshToken() {
    return getCookieValue('REFRESH_TOKEN');
}

/**
 * Get a user's email from stored session
 */
function getStoredUser() {
    const userJson = localStorage.getItem(USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
}

/**
 * Utility: Get cookie value by name
 */
function getCookieValue(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

/**
 * Make an authenticated API request
 * Automatically includes Authorization header with access token
 */
async function authenticatedFetch(url, options = {}) {
    const token = getAccessToken();
    
    if (!token) {
        console.error('No access token found. User not authenticated.');
        redirectToLogin();
        throw new Error('User not authenticated');
    }

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };

    const response = await fetch(url, {
        ...options,
        headers
    });

    // If 401, token might be expired - try refresh
    if (response.status === 401) {
        console.warn('Access token expired, attempting refresh...');
        const refreshed = await refreshAccessToken();
        if (refreshed) {
            // Retry the request with new token
            return authenticatedFetch(url, options);
        } else {
            redirectToLogin();
            throw new Error('Token refresh failed');
        }
    }

    return response;
}

/**
 * Attempt to refresh the access token using refresh token
 * (stored in HTTP-only cookie)
 */
async function refreshAccessToken() {
    try {
        const response = await fetch('/api/v1/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include' // Include cookies (refresh token)
        });

        if (response.ok) {
            const data = await response.json();
            storeAuthTokens(data.accessToken, data.user);
            return true;
        }
        return false;
    } catch (error) {
        console.error('Token refresh failed:', error);
        return false;
    }
}

/**
 * Check if user is logged in
 */
function isLoggedIn() {
    return !!getAccessToken();
}

/**
 * Logout the user
 */
function logout() {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(LOGIN_TIME_KEY);
    
    // Optional: Call backend logout endpoint
    fetch('/api/v1/auth/logout', {
        method: 'POST',
        credentials: 'include'
    }).catch(err => console.log('Logout request error:', err));

    redirectToLogin();
}

/**
 * Redirect to login page
 */
function redirectToLogin() {
    window.location.href = '/login.html';
}

/**
 * Handle OAuth2 redirect callback
 * After Google redirects back to app with tokens in URL
 */
function handleOAuth2Callback() {
    // Extract token from URL params (OAuth2 backend appends ?token=...)
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');
    
    if (tokenFromUrl) {
        console.log('OAuth2 token detected in URL');
        storeAuthTokens(tokenFromUrl);
        // Remove token from URL for security & cleanliness
        window.history.replaceState({}, document.title, window.location.pathname);
        console.log('OAuth2 token stored successfully in localStorage');

        // IMPORTANT: Show userMenu immediately after storing token
        const userMenuDiv = document.getElementById('userMenu');
        const loginBtn = document.getElementById('loginBtn');
        const signupBtn = document.getElementById('signupBtn');
        
        if (userMenuDiv) {
            userMenuDiv.classList.remove('d-none');
            userMenuDiv.style.display = 'block';
            console.log('User menu shown');
        }
        if (loginBtn) loginBtn.classList.add('d-none');
        if (signupBtn) signupBtn.classList.add('d-none');

        // Initialize Bootstrap Dropdown for avatar button
        setTimeout(() => {
            const avatarBtn = document.getElementById('avatarBtn');
            if (avatarBtn && typeof bootstrap !== 'undefined') {
                try {
                    let dropdown = bootstrap.Dropdown.getInstance(avatarBtn);
                    if (!dropdown) {
                        dropdown = new bootstrap.Dropdown(avatarBtn);
                        console.log('Bootstrap Dropdown initialized');
                    }
                } catch (e) {
                    console.error('Error initializing Bootstrap Dropdown:', e);
                }
            }
        }, 100);

        // Update navbar with profile image
        setTimeout(() => {
            if (typeof updateNavbar === 'function') {
                updateNavbar();
                console.log('Navbar updated after OAuth2 login');
            }
        }, 150);

        return true;
    }

    return false;
}

/**
 * Auto-logout after 24 hours if remember-me is not enabled
 */
function checkAutoLogout() {
    const token = getAccessToken();
    const rememberEmail = localStorage.getItem(REMEMBER_EMAIL_KEY);

    if (token && !rememberEmail) {
        const loginTime = localStorage.getItem(LOGIN_TIME_KEY);
        if (loginTime) {
            const hoursSinceLogin = (Date.now() - parseInt(loginTime)) / (1000 * 60 * 60);
            if (hoursSinceLogin > 24) {
                logout();
            }
        }
    }
}

/**
 * Initialize auth on page load
 * - Check if user is logged in
 * - Redirect unauthorized users
 * - Set up auto-logout
 */
function initializeAuth(requireLogin = false) {
    const isLoggedInUser = isLoggedIn();

    if (requireLogin && !isLoggedInUser) {
        redirectToLogin();
        return false;
    }

    checkAutoLogout();
    return isLoggedInUser;
}

// Auto-initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    // Handle OAuth2 callback - check for token in URL
    const handled = handleOAuth2Callback();

    if (handled) {
        console.log('OAuth2 callback processed successfully');
    }

    checkAutoLogout();
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        storeAuthTokens,
        getAccessToken,
        getRefreshToken,
        getStoredUser,
        authenticatedFetch,
        refreshAccessToken,
        isLoggedIn,
        logout,
        redirectToLogin,
        handleOAuth2Callback,
        checkAutoLogout,
        initializeAuth
    };
}
