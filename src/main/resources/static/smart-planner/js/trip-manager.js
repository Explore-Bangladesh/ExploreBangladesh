/**
 * Trip Manager Module
 * Handles loading, displaying, and managing user's travel plans
 */

let allTrips = [];
let currentFilter = 'all';
const API_BASE = '/api/planner';

/**
 * Initialize the page
 */
document.addEventListener('DOMContentLoaded', async function() {
    console.log('Initializing My Trips page...');
    
    // Load navbar
    loadNavBar();
    
    // Load trips
    await loadAllTrips();
});

/**
 * Load all trips from the API
 */
async function loadAllTrips() {
    try {
        showLoadingState();
        
        const token = getAccessToken();
        if (!token) {
            console.warn('🔴 No access token found. Redirecting to login.');
            window.location.href = '/login.html';
            return;
        }

        console.log('🔑 Found token, calling /api/planner/my-plans');

        const response = await fetch(`${API_BASE}/my-plans`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log(`📡 Response status: ${response.status}`);

        if (response.status === 401) {
            console.warn('🔴 Unauthorized (401). Token might be expired. Redirecting to login.');
            window.location.href = '/login.html';
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error('🔴 HTTP error! status:', response.status, 'body:', errorText);
            throw new Error(`HTTP error! status: ${response.status}: ${errorText}`);
        }

        const data = await response.json();
        
        console.log('📦 API Response:', data);
        
        // Handle backend response format: { status, message, data: [...], totalPlans }
        if (data.data && Array.isArray(data.data)) {
            allTrips = data.data;
            console.log('✅ Found data.data array with', allTrips.length, 'trips');
        } else if (data.success && data.data) {
            allTrips = Array.isArray(data.data) ? data.data : [];
            console.log('✅ Found data.success format with', allTrips.length, 'trips');
        } else if (data.plans) {
            allTrips = Array.isArray(data.plans) ? data.plans : [];
            console.log('✅ Found data.plans array with', allTrips.length, 'trips');
        } else {
            console.warn('⚠️ No trips found in response. Response structure:', Object.keys(data));
            console.warn('Full response object:', JSON.stringify(data));
            allTrips = [];
        }

        console.log(`✅ Loaded ${allTrips.length} trips total`);
        if (allTrips.length > 0) {
            console.log('📋 Trip details:', allTrips);
        }
        
        if (allTrips.length === 0) {
            showEmptyState();
        } else {
            displayAllTrips();
            setupFilters();
        }
    } catch (error) {
        console.error('🔴 Error loading trips:', error);
        showErrorState('Failed to load your trips. Please refresh the page.');
    }
}

/**
 * Display all trips in the grid
 */
function displayAllTrips() {
    const grid = document.getElementById('tripsGrid');
    const emptyState = document.getElementById('emptyState');
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');

    if (loadingState) loadingState.style.display = 'none';
    if (emptyState) emptyState.style.display = 'none';
    if (errorState) errorState.style.display = 'none';
    
    if (grid) {
        grid.style.display = 'grid';
        grid.innerHTML = '';

        allTrips.forEach(trip => {
            const card = createTripCard(trip);
            grid.appendChild(card);
        });
    }
}

/**
 * Create a trip card element
 */
function createTripCard(trip) {
    const card = document.createElement('div');
    card.className = 'trip-card';
    card.dataset.tripId = trip.planId;

    // Format dates and values
    const startDate = formatDate(trip.startDate);
    const endDate = formatDate(trip.endDate);
    const status = trip.status || 'draft';
    const budgetTier = trip.budgetTier || 'mid_range';
    const durationDays = trip.durationDays || 0;
    const totalBudget = trip.totalBudget || 0;

    card.innerHTML = `
        <div class="trip-card__header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px;">
            <div style="font-size: 1.6em; font-weight: bold;">🌍 ${escapeHtml(trip.destination)}</div>
            <div style="font-size: 0.9em; opacity: 0.9;">${startDate}</div>
        </div>
        <div class="trip-card__body" style="padding: 20px;">
            <div style="display: inline-block; padding: 5px 12px; border-radius: 20px; font-size: 0.85em; font-weight: 600; margin-bottom: 15px; background: ${getStatusColor(status)};">
                ${getStatusEmoji(status)} ${capitalizeFirst(status)}
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 15px;">
                <div style="text-align: center;">
                    <div style="font-size: 0.85em; color: #666; margin-bottom: 5px;">Duration</div>
                    <div style="font-size: 1.1em; font-weight: bold; color: #333;">${durationDays} days</div>
                </div>
                <div style="text-align: center;">
                    <div style="font-size: 0.85em; color: #666; margin-bottom: 5px;">Budget Tier</div>
                    <div style="font-size: 1.1em; font-weight: bold; color: #333;">${formatBudgetTier(budgetTier)}</div>
                </div>
            </div>

            <div style="display: flex; gap: 10px; margin-top: 15px;">
                <a href="plan-view-v2.html?planId=${trip.planId}&planType=${trip.planType || 'manual'}" class="trip-card__btn trip-card__btn--primary" style="flex: 1; padding: 10px; background: #667eea; color: white; text-align: center; border-radius: 6px; text-decoration: none; cursor: pointer; font-weight: 500; transition: background 0.3s;">
                    👁️ View
                </a>
                <button class="trip-card__btn trip-card__btn--danger" onclick="deleteTrip(${trip.planId})" style="flex: 1; padding: 10px; background: #dc3545; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background 0.3s;">
                    🗑️ Delete
                </button>
            </div>
        </div>
    `;

    // Add hover effects
    card.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-8px)';
        this.style.boxShadow = '0 8px 25px rgba(0, 0, 0, 0.15)';
    });

    card.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
        this.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
    });

    return card;
}

/**
 * Setup filter buttons
 */
function setupFilters() {
    const filterBtns = document.querySelectorAll('[data-filter]');
    
    filterBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            // Update active state
            filterBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // Filter and display
            const filter = this.dataset.filter;
            currentFilter = filter;
            
            const filtered = filter === 'all' 
                ? allTrips 
                : allTrips.filter(t => (t.status || 'draft') === filter);
            
            displayFilteredTrips(filtered);
        });
    });
}

/**
 * Display filtered trips
 */
function displayFilteredTrips(trips) {
    const grid = document.getElementById('tripsGrid');
    const emptyState = document.getElementById('emptyState');
    
    if (trips.length === 0) {
        if (grid) grid.style.display = 'none';
        if (emptyState) emptyState.style.display = 'block';
        return;
    }

    if (emptyState) emptyState.style.display = 'none';
    if (grid) {
        grid.style.display = 'grid';
        grid.innerHTML = '';

        trips.forEach(trip => {
            const card = createTripCard(trip);
            grid.appendChild(card);
        });
    }
}

/**
 * Delete a trip
 */
async function deleteTrip(planId) {
    if (!confirm('Are you sure you want to delete this trip? This action cannot be undone.')) {
        return;
    }

    try {
        const token = getAccessToken();
        
        const response = await fetch(`${API_BASE}/${planId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to delete trip: ${response.statusText}`);
        }

        console.log(`Trip ${planId} deleted successfully`);
        
        // Remove from local array
        allTrips = allTrips.filter(t => t.planId !== planId);
        
        // Refresh display
        if (allTrips.length === 0) {
            showEmptyState();
        } else {
            displayAllTrips();
            setupFilters();
        }
        
        // Show success message
        alert('Trip deleted successfully!');
    } catch (error) {
        console.error('Error deleting trip:', error);
        alert('Failed to delete trip. Please try again.');
    }
}

/**
 * Show loading state
 */
function showLoadingState() {
    const loadingState = document.getElementById('loadingState');
    const grid = document.getElementById('tripsGrid');
    const emptyState = document.getElementById('emptyState');
    const errorState = document.getElementById('errorState');

    if (loadingState) loadingState.style.display = 'block';
    if (grid) grid.style.display = 'none';
    if (emptyState) emptyState.style.display = 'none';
    if (errorState) errorState.style.display = 'none';
}

/**
 * Show empty state
 */
function showEmptyState() {
    const emptyState = document.getElementById('emptyState');
    const grid = document.getElementById('tripsGrid');
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');

    if (loadingState) loadingState.style.display = 'none';
    if (grid) grid.style.display = 'none';
    if (errorState) errorState.style.display = 'none';
    if (emptyState) emptyState.style.display = 'block';
}

/**
 * Show error state
 */
function showErrorState(message = 'An error occurred') {
    const errorState = document.getElementById('errorState');
    const errorMessage = document.getElementById('errorMessage');
    const loadingState = document.getElementById('loadingState');
    const grid = document.getElementById('tripsGrid');
    const emptyState = document.getElementById('emptyState');

    if (loadingState) loadingState.style.display = 'none';
    if (grid) grid.style.display = 'none';
    if (emptyState) emptyState.style.display = 'none';
    
    if (errorMessage) errorMessage.textContent = message;
    if (errorState) errorState.style.display = 'block';
}

/**
 * Utility function to get access token
 */
function getAccessToken() {
    return localStorage.getItem('token') || sessionStorage.getItem('token') || '';
}

/**
 * Format date string
 */
function formatDate(dateString) {
    if (!dateString) return 'Not set';
    
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric'
        });
    } catch (e) {
        return dateString;
    }
}

/**
 * Get status emoji
 */
function getStatusEmoji(status) {
    const emojis = {
        'draft': '📝',
        'active': '✅',
        'completed': '🎉'
    };
    return emojis[status] || '📌';
}

/**
 * Get status color
 */
function getStatusColor(status) {
    const colors = {
        'draft': '#f0f0f0',
        'active': '#d4edda',
        'completed': '#d1ecf1'
    };
    return colors[status] || '#f0f0f0';
}

/**
 * Format budget tier
 */
function formatBudgetTier(tier) {
    const tiers = {
        'economy': '🏨 Economy',
        'mid_range': '⭐ Mid-Range',
        'luxury': '💎 Luxury'
    };
    return tiers[tier] || tier;
}

/**
 * Capitalize first letter
 */
function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

/**
 * Escape HTML special characters
 */
function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

/**
 * Load navigation bar
 */
function loadNavBar() {
    const navbar = document.getElementById('navbar');
    if (navbar) {
        const navHTML = `
            <div class="navbar-container">
                <div class="navbar-logo">
                    <a href="/index.html">🌍 Explore Bangladesh</a>
                </div>
                <ul class="nav-menu">
                    <li><a href="/places.html">Places</a></li>
                    <li><a href="/hotels.html">Hotels</a></li>
                    <li><a href="/flights.html">Flights</a></li>
                    <li><a href="/cars.html">Cars</a></li>
                    <li><a href="/guides.html">Guides</a></li>
                    <li><a href="smart-planner.html">Smart Planner</a></li>
                    <li><a href="my-trips.html" class="active">My Trips</a></li>
                </ul>
                <div class="navbar-user">
                    <button id="userMenuBtn" class="user-menu-btn">👤 Profile</button>
                    <button id="logoutBtn" class="logout-btn">Logout</button>
                </div>
            </div>
        `;
        navbar.innerHTML = navHTML;

        document.getElementById('logoutBtn').addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            window.location.href = '/login.html';
        });

        document.getElementById('userMenuBtn').addEventListener('click', () => {
            window.location.href = '/profile.html';
        });
    }
}
