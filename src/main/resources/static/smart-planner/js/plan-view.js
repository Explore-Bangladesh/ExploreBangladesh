/**
 * Plan View - JavaScript Module
 * Displays full travel plan with itinerary, budget, and insights
 */

let currentPlanId = null;
let currentPlan = null;

document.addEventListener('DOMContentLoaded', function() {
    // Load navbar
    loadNavBar();

    // Get plan ID from URL
    const params = new URLSearchParams(window.location.search);
    currentPlanId = params.get('planId');

    if (!currentPlanId) {
        showError('No plan ID provided');
        return;
    }

    // Load plan
    loadPlan();

    // Setup button listeners
    setupButtonListeners();
});

function loadPlan() {
    const loadingState = document.getElementById('loadingState');
    const errorState = document.getElementById('errorState');
    const planHeader = document.getElementById('planHeader');

    loadingState.style.display = 'block';

    fetch(`/api/planner/${currentPlanId}`, {
        headers: {
            'Authorization': 'Bearer ' + getAccessToken()
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to load plan');
        return response.json();
    })
    .then(plan => {
        currentPlan = plan;
        loadingState.style.display = 'none';

        // Display all sections
        displayPlanHeader(plan);
        displayItinerary(plan);
        displayBudget(plan);
        displayDestinationInfo(plan);
        displayInsights(plan);
    })
    .catch(error => {
        console.error('Error:', error);
        loadingState.style.display = 'none';
        showError(error.message);
    });
}

function displayPlanHeader(plan) {
    const header = document.getElementById('planHeader');
    const dateRange = plan.startDate + ' to ' + plan.endDate;
    const daysText = plan.durationDays === 1 ? 'day' : 'days';

    header.innerHTML = `
        <div class="plan-header-content">
            <h1>📍 ${plan.destination}</h1>
            <div class="plan-meta">
                <span class="badge">📅 ${dateRange}</span>
                <span class="badge">⏱️ ${plan.durationDays} ${daysText}</span>
                <span class="badge">💰 ${plan.budgetTier.replace('_', ' ').toUpperCase()}</span>
                <span class="badge">✈️ ${plan.travelStyle.toUpperCase()}</span>
            </div>
            <p class="plan-status">Status: <strong>${plan.status.toUpperCase()}</strong></p>
        </div>
    `;
}

function displayItinerary(plan) {
    const timeline = document.getElementById('itineraryTimeline');
    timeline.innerHTML = '';

    plan.dailyItineraries.forEach(day => {
        const dayCard = createDayCard(day);
        timeline.appendChild(dayCard);
    });
}

function createDayCard(day) {
    const card = document.createElement('div');
    card.className = 'day-card';

    const activities = day.activities.map(activity => `
        <div class="activity ${activity.activityType}">
            <div class="activity-time">
                ${activity.startTime || 'TBD'} - ${activity.endTime || 'TBD'}
            </div>
            <div class="activity-content">
                <h4>${activity.title}</h4>
                ${activity.description ? `<p>${activity.description}</p>` : ''}
                <div class="activity-meta">
                    <span class="tag">${activity.activityType}</span>
                    ${activity.estimatedCost ? `<span class="cost">৳${activity.estimatedCost}</span>` : ''}
                    ${activity.durationMinutes ? `<span class="duration">${activity.durationMinutes} min</span>` : ''}
                </div>
            </div>
        </div>
    `).join('');

    card.innerHTML = `
        <div class="day-header">
            <h3>📅 Day ${day.dayNumber} - ${day.date}</h3>
            <span class="day-theme">${day.theme}</span>
        </div>
        <div class="weather-info">
            🌤️ ${day.weatherCondition || 'Weather TBD'}
        </div>
        <div class="activities-list">
            ${activities}
        </div>
        <div class="day-summary">
            <span>👣 ~${day.estimatedSteps || 0} steps</span>
            <span>💰 ৳${day.estimatedCost || 0}</span>
        </div>
    `;

    return card;
}

function displayBudget(plan) {
    const budgetCard = document.getElementById('budgetDetails');
    const budget = plan.budgetBreakdown;

    budgetCard.innerHTML = `
        <div class="budget-breakdown">
            <div class="budget-item">
                <span>🏨 Accommodation</span>
                <span class="amount">৳${budget.accommodation || 0}</span>
            </div>
            <div class="budget-item">
                <span>🚗 Transport</span>
                <span class="amount">৳${budget.transport || 0}</span>
            </div>
            <div class="budget-item">
                <span>🎯 Attractions</span>
                <span class="amount">৳${budget.attractions || 0}</span>
            </div>
            <div class="budget-item">
                <span>🍽️ Food</span>
                <span class="amount">৳${budget.food || 0}</span>
            </div>
            <div class="budget-item">
                <span>🛍️ Shopping</span>
                <span class="amount">৳${budget.shopping || 0}</span>
            </div>
            <div class="budget-item">
                <span>📦 Miscellaneous</span>
                <span class="amount">৳${budget.miscellaneous || 0}</span>
            </div>
            <div class="budget-total">
                <strong>Total Estimated</strong>
                <strong class="total-amount">৳${plan.totalBudgetEstimate || 0}</strong>
            </div>
        </div>
    `;
}

function displayDestinationInfo(plan) {
    if (!plan.destinationInfo) return;

    const card = document.getElementById('destinationCard');
    const details = document.getElementById('destinationDetails');
    const info = plan.destinationInfo;

    card.style.display = 'block';
    details.innerHTML = `
        <div class="destination-info">
            <p><strong>Country:</strong> ${info.country}</p>
            <p><strong>Coordinates:</strong> ${info.latitude}, ${info.longitude}</p>
            <p><strong>Best Time:</strong> Month ${info.bestMonthFrom} - ${info.bestMonthTo}</strong></p>
            <p><strong>Safety Rating:</strong> ${info.safetyRating}/5</p>
            <p><strong>Language:</strong> ${info.language}</p>
            <p><strong>Description:</strong> ${info.description}</p>
        </div>
    `;
}

function displayInsights(plan) {
    const insightsDiv = document.getElementById('insights');
    insightsDiv.innerHTML = '';

    if (!plan.insights || plan.insights.length === 0) {
        insightsDiv.innerHTML = '<p>No insights yet</p>';
        return;
    }

    plan.insights.forEach(insight => {
        const insightEl = document.createElement('div');
        insightEl.className = `insight-item ${insight.severity}`;
        insightEl.innerHTML = `
            <h5>${insight.title}</h5>
            <p>${insight.description}</p>
            <span class="insight-type">${insight.insightType}</span>
        `;
        insightsDiv.appendChild(insightEl);
    });
}

function setupButtonListeners() {
    const editBtn = document.getElementById('editPlanBtn');
    const deleteBtn = document.getElementById('deletePlanBtn');

    if (editBtn) {
        editBtn.addEventListener('click', () => {
            alert('Edit functionality coming in Phase 2');
        });
    }

    if (deleteBtn) {
        deleteBtn.addEventListener('click', deletePlan);
    }
}

function deletePlan() {
    if (!confirm('Are you sure you want to delete this plan?')) return;

    fetch(`/api/planner/${currentPlanId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getAccessToken()
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to delete plan');
        alert('Plan deleted successfully');
        window.location.href = '/smart-planner/my-trips.html';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error deleting plan: ' + error.message);
    });
}

function showError(message) {
    const errorState = document.getElementById('errorState');
    const errorMessage = document.getElementById('errorMessage');
    errorState.style.display = 'block';
    errorMessage.textContent = message;
}

function getAccessToken() {
    return localStorage.getItem('accessToken') || '';
}

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
                    <li><a href="/smart-planner/smart-planner.html">Smart Planner</a></li>
                    <li><a href="/smart-planner/my-trips.html">My Trips</a></li>
                </ul>
                <div class="navbar-user">
                    <button id="userMenuBtn" class="user-menu-btn">👤 Profile</button>
                    <button id="logoutBtn" class="logout-btn">Logout</button>
                </div>
            </div>
        `;
        navbar.innerHTML = navHTML;

        document.getElementById('logoutBtn').addEventListener('click', () => {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            window.location.href = '/login.html';
        });

        document.getElementById('userMenuBtn').addEventListener('click', () => {
            window.location.href = '/profile.html';
        });
    }
}
