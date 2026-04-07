/**
 * Phase 2: Enhanced Plan View - Detailed Itinerary Display
 * Displays intelligent itinerary with timeline, budget breakdown, and insights
 */

document.addEventListener('DOMContentLoaded', async function() {
    console.log('📄 [PAGE LOAD] Loading Phase 2 Enhanced Plan View...');
    
    // Get parameters from URL
    const params = new URLSearchParams(window.location.search);
    const planId = params.get('planId');
    const planType = params.get('planType');  // ✅ Get plan type from URL
    const isPreview = params.get('preview') === 'true';
    
    console.log('🔗 URL Parameters: planId=' + planId + ', planType=' + planType + ', preview=' + isPreview);
    
    try {
        let plan = null;
        let isRawAIFormat = false;  // ✅ Track if plan is raw AI format
        
        if (isPreview) {
            // Preview mode: Load from localStorage
            console.log('👁️ [PREVIEW MODE] Loading plan from localStorage...');
            const previewData = localStorage.getItem('previewPlan');
            const formData = localStorage.getItem('formData');
            
            if (!previewData) {
                showError('Preview data not found. Please generate a plan first.');
                return;
            }
            
            plan = JSON.parse(previewData);
            console.log('✅ Preview plan loaded from localStorage');
            
            // ✅ Preview is always AI format from Gemini, mark for adapter
            isRawAIFormat = true;
            
            // Show Save Plan button in preview mode
            showSavePlanButton(formData ? JSON.parse(formData) : null);
        } else if (planId) {
            // Saved plan mode: Load from API
            console.log('📡 [SAVED MODE] Loading plan from API...');
            plan = await loadPlanFromAPI(planId, planType);  // ✅ Pass planType to API loader
            console.log('✅ Saved plan loaded from API');
            
            // ✅ If this is an AI-generated plan, it's in raw format (no need to apply adapter)
            isRawAIFormat = (planType === 'ai_generated');
            
            if (isRawAIFormat) {
                console.log('🤖 [RAW AI FORMAT] Plan is raw AI format - will apply adapter for display');
            } else {
                console.log('📋 [MANUAL FORMAT] Plan is manual format - will use as-is');
            }
        } else {
            showError('No plan specified. Please create a plan first.');
            return;
        }
        
        // ✅ Apply AI adapter to raw AI format (both preview and saved AI plans)
        if (isRawAIFormat && isAIPlan(plan)) {
            console.log('🤖 [AI ADAPTER] Converting raw AI response to display format...');
            plan = adaptAIResponse(plan);
        }
        
        // Display the plan
        console.log('📊 [DISPLAY] Rendering plan...');
        displayPlanDetails(plan);
        console.log('✅ Plan displayed successfully');
        
    } catch (error) {
        console.error('Error loading plan:', error);
        showError('Failed to load plan details. Please try again.');
    }
});

/**
 * Load plan from API (for saved plans)
 * ✅ UPDATED: Now returns RAW JSON for AI plans (no deser ialization)
 * Backend separation: AI plans return raw Map, manual plans return parsed DTOs
 */
async function loadPlanFromAPI(planId, planType) {
    try {
        const token = getAccessToken();
        
        // ✅ Determine which endpoint to use based on plan type
        let endpoint;
        if (planType === 'ai_generated') {
            endpoint = `/api/planner/v2/ai/${planId}`;
            console.log('🤖 [API CALL] Fetching AI plan (will receive RAW JSON): ' + endpoint);
        } else if (planType === 'manual') {
            endpoint = `/api/planner/v2/manual/${planId}`;
            console.log('📋 [API CALL] Fetching MANUAL plan: ' + endpoint);
        } else {
            // Fallback: Use generic endpoint (checks both tables)
            endpoint = `/api/planner/v2/${planId}`;
            console.log('🔄 [API CALL] Fetching plan (auto-detect type): ' + endpoint);
        }
        
        const response = await fetch(endpoint, {
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });
        
        console.log('📡 [API RESPONSE] Status: ' + response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        
        // ✅ For AI plans, backend returns raw JSON (as Map)
        if (planType === 'ai_generated') {
            console.log('📦 [RAW AI FORMAT] Received raw JSON from backend - will apply adapter for display');
        } else {
            console.log('📦 [PARSED FORMAT] Received plan data');
        }
        
        return data.data;
    } catch (error) {
        console.error('Error loading plan:', error);
        throw error;
    }
}

/**
 * Check if a plan is AI-generated
 */
function isAIPlan(plan) {
    if (!plan) return false;
    // Check for AI-specific fields that indicate this is from AIGeneratedPlanDTO
    return plan.planStatus === 'ai_generated' || 
           plan.aiInsights !== undefined ||
           (plan.dailyItineraries && plan.dailyItineraries[0] && 
            (plan.dailyItineraries[0].dayTitle !== undefined || 
             plan.dailyItineraries[0].estimatedDailyBudget !== undefined));
}

/**
 * Show Save Plan button in preview mode
 * ✅ FIXED: Passes FULL plan data (not just formData) to prevent regeneration
 */
function showSavePlanButton(formData) {
    const saveBtnContainer = document.getElementById('savePlanContainer');
    if (!saveBtnContainer) {
        // Create container if it doesn't exist
        const container = document.createElement('div');
        container.id = 'savePlanContainer';
        container.style.cssText = 'text-align: center; margin: 20px 0; padding: 20px; background: #e8f5e9; border-radius: 8px;';
        
        const button = document.createElement('button');
        button.id = 'savePlanBtn';
        button.textContent = '💾 Save This Plan to My Trips';
        button.style.cssText = 'padding: 12px 30px; background: #4CAF50; color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; font-weight: bold;';
        button.onclick = () => {
            // ✅ Get FULL plan data from localStorage
            const fullPlan = localStorage.getItem('previewPlan') 
                ? JSON.parse(localStorage.getItem('previewPlan')) 
                : null;
            savePlanToDatabase(formData, fullPlan);
        };
        
        container.appendChild(button);
        
        // Insert before dailyTimelineContainer if it exists, otherwise append to body
        const timelineContainer = document.getElementById('dailyTimelineContainer');
        if (timelineContainer && timelineContainer.parentNode) {
            timelineContainer.parentNode.insertBefore(container, timelineContainer);
        } else {
            const planContainer = document.querySelector('.plan-container');
            if (planContainer) {
                planContainer.insertBefore(container, planContainer.firstChild);
            } else {
                document.body.appendChild(container);
            }
        }
    } else {
        saveBtnContainer.style.display = 'block';
    }
}

/**
 * Save the preview plan to database
 * ✅ FIXED: Sends FULL plan data + formData to prevent regeneration
 */
async function savePlanToDatabase(formData, fullPlan) {
    if (!formData) {
        console.error('No form data available');
        alert('Error: Could not save plan. Form data missing.');
        return;
    }
    
    if (!fullPlan) {
        console.warn('⚠️ Full plan data missing, will use formData only');
    }
    
    const saveBtn = document.getElementById('savePlanBtn');
    const originalText = saveBtn.textContent;
    saveBtn.disabled = true;
    saveBtn.textContent = '⏳ Saving...';
    
    try {
        const token = getAccessToken();
        if (!token) {
            throw new Error('Not authenticated');
        }
        
        // ✅ Create save payload with BOTH form data AND full plan
        const savePayload = {
            // Basic plan information
            destination: formData.destination,
            durationDays: formData.durationDays,
            budgetTier: formData.budgetTier,
            travelStyle: formData.travelStyle,
            startDate: formData.startDate || null,
            // ✅ CRITICAL: Full AI-generated plan data (to avoid regeneration)
            fullPlanData: fullPlan,
            // ✅ Mark as AI plan
            planType: 'ai_generated'
        };
        
        console.log('💾 [API CALL] Saving AI plan with FULL plan data to /api/planner/v2/ai/save');
        console.log('📦 Payload includes:', {
            destination: savePayload.destination,
            durationDays: savePayload.durationDays,
            fullPlanDataSize: fullPlan ? Object.keys(fullPlan).length + ' fields' : 'null',
            planType: savePayload.planType
        });
        
        const response = await fetch('/api/planner/v2/ai/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(savePayload)
        });
        
        console.log('📡 [RESPONSE] Status: ' + response.status);
        
        if (!response.ok) {
            let errorMsg = `HTTP ${response.status}`;
            try {
                const error = await response.json();
                errorMsg = error.message || errorMsg;
            } catch (e) {}
            throw new Error(errorMsg);
        }
        
        const result = await response.json();
        const savedPlan = result.data;
        
        console.log('✅ [SAVED] Plan saved with ID: ' + savedPlan.id);
        
        // Clear localStorage
        localStorage.removeItem('previewPlan');
        localStorage.removeItem('formData');
        
        // Show success message
        showMessage('✅ Plan saved successfully! Redirecting to My Trips...', 'success');
        
        // Redirect to saved plan with plan type
        setTimeout(() => {
            window.location.href = `/smart-planner/plan-view-v2.html?planId=${savedPlan.id}&planType=ai_generated`;
        }, 1500);
        
    } catch (error) {
        console.error('Error saving plan:', error);
        showMessage('❌ Error saving plan: ' + error.message, 'error');
        saveBtn.disabled = false;
        saveBtn.textContent = originalText;
    }
}

/**
/**
 * Display all plan details (common for both preview and saved modes)
 */
function displayPlanDetails(plan) {
    // Update header information
    document.getElementById('destinationName').textContent = `🌍 ${plan.destination}`;
    document.getElementById('planDuration').textContent = `${plan.durationDays} Days`;
    document.getElementById('planBudgetTier').textContent = formatBudgetTier(plan.budgetTier);
    document.getElementById('planTravelStyle').textContent = formatTravelStyle(plan.travelStyle);
    
    // Display daily timeline
    displayDailyTimeline(plan);
    
    // Display budget breakdown
    displayBudgetBreakdown(plan);
    
    // Display insights
    displayInsights(plan);
    
    // Display hotels
    displayHotels(plan);
    
    // Update total cost
    if (plan.budgetBreakdown) {
        document.getElementById('totalCost').textContent = `৳ ${formatNumber(plan.budgetBreakdown.total)}`;
    }
}

/**
 * Display daily timeline with activities
 */
function displayDailyTimeline(plan) {
    const container = document.getElementById('dailyTimelineContainer');
    container.innerHTML = '';
    
    if (!plan.dailyItineraries || plan.dailyItineraries.length === 0) {
        container.innerHTML = '<p>No itinerary data available.</p>';
        return;
    }
    
    plan.dailyItineraries.forEach((day, index) => {
        const dayElement = document.createElement('div');
        dayElement.className = 'timeline-item';
        
        // Day header
        const dateElement = document.createElement('div');
        dateElement.className = 'timeline-date';
        dateElement.textContent = `Day ${day.dayNumber}`;
        
        // Day content
        const contentElement = document.createElement('div');
        contentElement.className = 'timeline-content';
        
        // Day theme
        const themeElement = document.createElement('div');
        themeElement.className = 'day-theme';
        themeElement.textContent = day.theme || 'Exploration Day';
        contentElement.appendChild(themeElement);
        
        // Activities list
        const activitiesList = document.createElement('ul');
        activitiesList.className = 'activity-list';
        
        if (day.activities && day.activities.length > 0) {
            day.activities.forEach(activity => {
                const activityLi = document.createElement('li');
                activityLi.className = 'activity-item';
                
                // Time
                const timeDiv = document.createElement('div');
                timeDiv.className = 'activity-time';
                timeDiv.textContent = formatTime(activity.startTime) + ' - ' + formatTime(activity.endTime);
                
                // Name
                const nameDiv = document.createElement('div');
                nameDiv.className = 'activity-name';
                nameDiv.textContent = activity.activityName || 'Activity';
                
                // Type badge
                const typeDiv = document.createElement('div');
                typeDiv.className = 'activity-type';
                typeDiv.textContent = activity.type || 'Misc';
                
                // Cost
                const costDiv = document.createElement('div');
                costDiv.className = 'activity-cost';
                costDiv.textContent = activity.costBdt ? `৳ ${activity.costBdt}` : '';
                
                activityLi.appendChild(timeDiv);
                activityLi.appendChild(nameDiv);
                activityLi.appendChild(typeDiv);
                if (activity.costBdt) {
                    activityLi.appendChild(costDiv);
                }
                
                activitiesList.appendChild(activityLi);
            });
        }
        
        contentElement.appendChild(activitiesList);
        
        // Day summary
        const summaryDiv = document.createElement('div');
        summaryDiv.className = 'day-summary';
        
        const weatherSpan = document.createElement('span');
        weatherSpan.textContent = `🌤️ ${day.weatherForecast || 'Sunny'} | 🏨 ${day.accommodation || 'Hotel'}`;
        
        const costSpan = document.createElement('span');
        costSpan.textContent = `💰 ৳ ${formatNumber(day.totalCostBdt || 0)}`;
        
        summaryDiv.appendChild(weatherSpan);
        summaryDiv.appendChild(costSpan);
        
        contentElement.appendChild(summaryDiv);
        
        dayElement.appendChild(dateElement);
        dayElement.appendChild(contentElement);
        container.appendChild(dayElement);
    });
}

/**
 * Display budget breakdown
 */
function displayBudgetBreakdown(plan) {
    const breakdown = plan.budgetBreakdown;
    
    if (!breakdown) {
        return;
    }
    
    console.log('Budget Breakdown:', breakdown); // Debug log
    
    document.getElementById('budgetAccommodation').textContent = `৳ ${formatNumber(breakdown.accommodation || 0)}`;
    document.getElementById('budgetFood').textContent = `৳ ${formatNumber(breakdown.food || 0)}`;
    document.getElementById('budgetAttractions').textContent = `৳ ${formatNumber(breakdown.attractions || 0)}`;
    document.getElementById('budgetTransport').textContent = `৳ ${formatNumber(breakdown.transport || 0)}`;
    document.getElementById('budgetMisc').textContent = `৳ ${formatNumber(breakdown.miscellaneous || 0)}`;
    document.getElementById('budgetTotal').textContent = `৳ ${formatNumber(breakdown.total || 0)}`;
}

/**
 * Display travel insights
 */
function displayInsights(plan) {
    const insights = plan.insights;
    
    if (!insights) {
        return;
    }
    
    const listElement = document.getElementById('insightsList');
    listElement.innerHTML = '';
    
    if (insights.highlights && insights.highlights.length > 0) {
        insights.highlights.forEach(highlight => {
            const li = document.createElement('li');
            li.textContent = highlight;
            listElement.appendChild(li);
        });
    }
    
    // Additional info
    if (insights.visaInformation) {
        const visaLi = document.getElementById('visaInfo');
        if (visaLi) {
            visaLi.textContent = insights.visaInformation;
        }
    }
}

/**
 * Display recommended hotels
 */
function displayHotels(plan) {
    const hotelsList = document.getElementById('hotelsList');
    hotelsList.innerHTML = '';
    
    if (!plan.selectedHotels || plan.selectedHotels.length === 0) {
        hotelsList.innerHTML = '<li>No hotels available</li>';
        return;
    }
    
    plan.selectedHotels.slice(0, 5).forEach(hotel => {
        const li = document.createElement('li');
        const rating = hotel.averageRating ? `⭐ ${hotel.averageRating}` : '';
        const price = hotel.midrangePriceBdt ? `৳ ${formatNumber(hotel.midrangePriceBdt)}/night` : '';
        li.innerHTML = `<strong>${hotel.name}</strong> ${rating} ${price}`;
        hotelsList.appendChild(li);
    });
}

// Utility functions

function formatNumber(num) {
    if (!num) return '0';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function formatTime(timeString) {
    if (!timeString) return 'N/A';
    if (typeof timeString === 'string') {
        return timeString.substring(0, 5); // HH:MM format
    }
    return 'N/A';
}

function formatBudgetTier(tier) {
    const map = {
        'economy': '💰 Budget',
        'mid_range': '💰 Mid-Range',
        'midrange': '💰 Mid-Range',
        'luxury': '💎 Luxury'
    };
    return map[tier] || 'Budget';
}

function formatTravelStyle(style) {
    const map = {
        'adventure': '🏔️ Adventure',
        'cultural': '🏛️ Cultural',
        'relaxation': '🏖️ Relaxation',
        'family': '👨‍👩‍👧‍👦 Family',
        'solo': '🚶 Solo'
    };
    return map[style] || 'Travel';
}

function getAccessToken() {
    return localStorage.getItem('token') || '';
}

function showError(message) {
    const container = document.querySelector('.plan-container');
    if (container) {
        container.innerHTML = `<div style="color: red; text-align: center; padding: 40px;"><h2>${message}</h2><a href="/">← Back to Home</a></div>`;
    }
}

function showMessage(message, type) {
    // Create message container if it doesn't exist
    let messageContainer = document.getElementById('messageContainer');
    if (!messageContainer) {
        messageContainer = document.createElement('div');
        messageContainer.id = 'messageContainer';
        document.body.insertBefore(messageContainer, document.body.firstChild);
    }
    
    const bgColor = type === 'error' ? '#ffcdd2' : '#c8e6c9';
    const textColor = type === 'error' ? '#c62828' : '#2e7d32';
    const borderColor = type === 'error' ? '#ef5350' : '#43a047';
    
    const div = document.createElement('div');
    div.style.cssText = `
        padding: 15px 20px;
        margin: 10px;
        background: ${bgColor};
        color: ${textColor};
        border-left: 4px solid ${borderColor};
        border-radius: 4px;
        font-size: 16px;
    `;
    div.textContent = message;
    messageContainer.appendChild(div);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        div.remove();
    }, 5000);
}
