/**
 * Smart Planner Form - JavaScript Module
 * Handles form submission and plan generation
 */

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('smartPlanForm');
    const generateBtn = document.getElementById('generateBtn');
    const formMessage = document.getElementById('formMessage');
    const loadingState = document.getElementById('loadingState');
    const successState = document.getElementById('successState');

    // Initialize navbar authentication
    updateNavbar();

    // Form submission
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        // Prevent duplicate submissions by disabling button
        generateBtn.disabled = true;
        const originalButtonText = generateBtn.innerHTML;
        generateBtn.innerHTML = '⏳ Generating...';

        // DEVELOPMENT MODE: Skip authentication check
        // In production, uncomment the check below:
        // if (!isLoggedIn()) {
        //     showMessage('❌ Please log in to create a plan', 'error');
        //     setTimeout(() => window.location.href = '/login.html', 1500);
        //     return;
        // }

        // Show loading state
        showLoading(true);
        formMessage.style.display = 'none';

        // Get form data
        const formData = new FormData(form);
        const planningEngine = formData.get('planningEngine') || 'ai';
        
        const request = {
            destination: formData.get('destination'),
            durationDays: formData.get('durationDays') ? parseInt(formData.get('durationDays')) : null,
            budgetTier: formData.get('budgetTier'),
            travelStyle: formData.get('travelStyle'),
            startDate: formData.get('startDate')
        };

        // Validate required fields
        if (!request.destination || request.destination.trim() === '') {
            showMessage('❌ Please enter a destination', 'error');
            showLoading(false);
            generateBtn.disabled = false;
            generateBtn.innerHTML = originalButtonText;
            return;
        }
        
        if (!request.durationDays || request.durationDays < 1) {
            showMessage('❌ Please enter duration (minimum 1 day)', 'error');
            showLoading(false);
            generateBtn.disabled = false;
            generateBtn.innerHTML = originalButtonText;
            return;
        }
        
        console.log('📤 Submitting plan request:', request);
        console.log('🤖 Planning Engine:', planningEngine);

        try {
            // Determine which endpoint and loading message to use
            let endpoint, loadingMessage, loadingSubtitle;
            
            if (planningEngine === 'ai') {
                endpoint = '/api/planner/ai/generate';
                loadingMessage = '🤖 AI Engine is generating your perfect itinerary...';
                loadingSubtitle = 'Google Gemini AI is analyzing destinations, attractions, weather, and creating a realistic hour-by-hour schedule.';
            } else {
                endpoint = '/api/planner/v2/generate';
                loadingMessage = '✏️ Building your custom itinerary...';
                loadingSubtitle = 'Our smart algorithm is analyzing destinations and attractions for you.';
            }
            
            // Update loading message
            document.getElementById('loadingTitle').textContent = loadingMessage;
            document.getElementById('loadingSubtitle').textContent = loadingSubtitle;
            
            console.log(`🚀 Calling ${endpoint}...`);
            
            // Build headers - include auth token only if available
            const headers = {
                'Content-Type': 'application/json'
            };
            const token = getAccessToken();
            if (token) {
                headers['Authorization'] = 'Bearer ' + token;
            }
            
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(request)
            });

            console.log(`📡 Response status: ${response.status}`);

            if (!response.ok) {
                let errorMsg = `HTTP ${response.status}: `;
                try {
                    const errorData = await response.json();
                    errorMsg += errorData.message || JSON.stringify(errorData);
                } catch (e) {
                    errorMsg += response.statusText;
                }
                throw new Error(errorMsg);
            }

            const result = await response.json();
            console.log('✅ Full API Response:', result);
            
            // Extract plan from API response wrapper
            const plan = result.data;
            if (!plan) {
                console.error('❌ Missing plan data in response:', result);
                throw new Error('Invalid plan response: Missing plan data');
            }

            console.log('🎉 Plan generated (preview - NOT YET SAVED)');
            console.log('📍 Plan details:', plan);

            // Store the generated plan preview in localStorage for display
            localStorage.setItem('previewPlan', JSON.stringify(plan));
            localStorage.setItem('formData', JSON.stringify(request));
            localStorage.setItem('planningEngine', planningEngine);

            // Show success
            form.style.display = 'none';
            loadingState.style.display = 'none';
            successState.style.display = 'block';

            // Redirect to plan view (without planId - indicates preview mode)
            console.log('🔄 Redirecting to plan preview...');
            setTimeout(() => {
                window.location.href = `/smart-planner/plan-view-v2.html?preview=true&engine=${planningEngine}`;
            }, 2000);

        } catch (error) {
            console.error('❌ Error generating plan:', error);
            showMessage('❌ Error: ' + error.message, 'error');
            showLoading(false);
            // Re-enable button on error
            generateBtn.disabled = false;
            generateBtn.innerHTML = originalButtonText;
        }
    });

    function showLoading(show) {
        if (show) {
            form.style.display = 'none';
            loadingState.style.display = 'block';
        } else {
            form.style.display = 'block';
            loadingState.style.display = 'none';
        }
    }

    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = 'form-message ' + (type === 'error' ? 'error' : 'success');
        formMessage.style.display = 'block';
    }

    function isLoggedIn() {
        const token = getAccessToken();
        return token && token.length > 0;
    }

    function getAccessToken() {
        return localStorage.getItem('token') || '';
    }
});
