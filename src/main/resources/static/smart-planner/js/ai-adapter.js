/**
 * AI Engine Response Adapter
 * Transforms Google Gemini AI response format to UI-compatible format
 * 
 * This adapter bridges the gap between the pure AI engine JSON response
 * and the front-end UI expectations, keeping code separation clean.
 */

/**
 * Transform AI Engine response to UI-compatible format
 * @param {Object} aiResponse - Raw response from Google Gemini AI
 * @returns {Object} - Transformed response ready for UI display
 */
function adaptAIResponse(aiResponse) {
    console.log('🔄 [AI ADAPTER] Transforming AI response to UI format...');
    
    if (!aiResponse) {
        console.error('❌ [AI ADAPTER] Empty AI response');
        return null;
    }

    try {
        // Transform the AI response to match UI expectations
        const adaptedPlan = {
            // Basic info (unchanged)
            id: aiResponse.planId || null,
            userId: aiResponse.userId || null,
            destination: aiResponse.destination || 'Unknown',
            durationDays: aiResponse.durationDays || 1,
            budgetTier: aiResponse.budgetTier || 'midrange',
            travelStyle: aiResponse.travelStyle || 'cultural',
            startDate: aiResponse.startDate || new Date().toISOString().split('T')[0],
            endDate: aiResponse.endDate || new Date().toISOString().split('T')[0],
            isSaved: aiResponse.isSaved || false,
            planStatus: aiResponse.planStatus || 'ai_generated',
            estimatedBudget: aiResponse.estimatedBudget || 0,

            // AI-specific insights (transform to structured format)
            insights: {
                highlights: [
                    aiResponse.aiInsights || 'AI-generated personalized itinerary',
                    `Best time to visit: ${aiResponse.bestTimeToVisit || 'Year-round'}`,
                    `Local cuisine: ${aiResponse.localCuisine || 'Bangladeshi cuisine'}`,
                ],
                visaInformation: 'Check visa requirements based on your nationality',
                weatherAdvisories: aiResponse.weatherInfo || 'Check local weather forecast',
                culturalTips: aiResponse.localTravelTips || 'Respect local customs and traditions'
            },

            // Budget breakdown (calculated from daily costs)
            budgetBreakdown: calculateBudgetBreakdown(
                aiResponse.dailyItineraries || [],
                aiResponse.estimatedBudget || 0
            ),

            // Daily itineraries (with transformed field names)
            dailyItineraries: transformDailyItineraries(aiResponse.dailyItineraries || []),

            // Hotels (from AI recommendations or create defaults)
            selectedHotels: buildHotelsFromAI(aiResponse.accommodations || '', aiResponse.destination),

            // Packing and safety
            packingTips: aiResponse.packingTips || [],
            safetyAdvisories: aiResponse.safetyAdvisories || 'Follow local safety guidelines'
        };

        console.log('✅ [AI ADAPTER] Transformation complete');
        console.log('📋 Adapted plan structure:', {
            destination: adaptedPlan.destination,
            days: adaptedPlan.durationDays,
            dailyCount: adaptedPlan.dailyItineraries.length,
            activitiesCount: adaptedPlan.dailyItineraries.reduce((sum, day) => sum + (day.activities?.length || 0), 0)
        });

        return adaptedPlan;

    } catch (error) {
        console.error('❌ [AI ADAPTER] Error transforming response:', error);
        throw new Error('Failed to adapt AI response: ' + error.message);
    }
}

/**
 * Transform daily itineraries from AI format to UI format
 */
function transformDailyItineraries(aiDailyItineraries) {
    return aiDailyItineraries.map((day, index) => ({
        dayNumber: day.dayNumber || (index + 1),
        date: day.date || new Date().toISOString().split('T')[0],
        
        // AI uses "dayTitle", UI expects "theme"
        theme: day.dayTitle || day.theme || `Day ${day.dayNumber} Exploration`,
        
        // Additional day info
        summary: day.summary || `Explore ${day.dayTitle || 'the destination'}`,
        mealPlan: day.mealPlan || 'Breakfast → Lunch → Dinner',
        travelTips: day.travelTips || 'Use local transport for authentic experience',
        weatherForecast: day.weatherExpectation || 'Sunny conditions',
        accommodation: 'Hotel',

        // Budget
        totalCostBdt: day.estimatedDailyCost || calculateDayTotal(day.activities || []),

        // Packing reminders from AI
        packingReminders: day.packingReminders || [
            'Comfortable shoes',
            'Water bottle',
            'Sunscreen'
        ],

        // Activities with transformed field names
        activities: transformActivities(day.activities || [])
    }));
}

/**
 * Transform activities from AI format to UI format
 */
function transformActivities(aiActivities) {
    return aiActivities.map((activity, index) => ({
        // Time (keep as is)
        startTime: activity.startTime || '09:00',
        endTime: activity.endTime || '10:00',

        // AI uses "estimatedCost", UI expects "costBdt"
        costBdt: activity.estimatedCost || activity.cost || 0,

        // Names and descriptions
        activityName: activity.activityName || activity.name || 'Activity',
        type: activity.activityType || activity.type || 'activity',
        description: activity.description || 'Enjoy this activity',

        // Additional details from AI
        location: activity.location || 'Local Area',
        duration: activity.duration || calculateDuration(activity.startTime, activity.endTime),
        recommendations: activity.recommendations || 'Enjoy and take photos',
        bookingInfo: activity.bookingInfo || 'No advance booking required',
        travelTime: activity.travelTime || '0 minutes',
        dressCode: activity.dressCode || 'Casual',

        // Order for timeline display
        visitationOrder: index + 1
    }));
}

/**
 * Calculate budget breakdown from daily itineraries
 */
function calculateBudgetBreakdown(dailyItineraries, totalEstimatedBudget) {
    let accommodation = 0;
    let food = 0;
    let attractions = 0;
    let transport = 0;
    let miscellaneous = 0;

    // Parse activities from all days to categorize costs
    dailyItineraries.forEach(day => {
        if (day.activities && Array.isArray(day.activities)) {
            day.activities.forEach(activity => {
                const cost = activity.estimatedCost || activity.cost || 0;
                const type = (activity.activityType || activity.type || '').toLowerCase();

                if (type === 'meal') {
                    food += cost;
                } else if (type === 'attraction') {
                    attractions += cost;
                } else if (type === 'travel') {
                    transport += cost;
                } else if (type === 'accommodation') {
                    accommodation += cost;
                } else {
                    miscellaneous += cost;
                }
            });
        }
    });

    // If accommodation is 0, allocate proportional amount from total
    if (accommodation === 0 && totalEstimatedBudget > 0) {
        accommodation = Math.round(totalEstimatedBudget * 0.4); // 40% for accommodation
    }

    // Calculate total
    const total = food + attractions + transport + accommodation + miscellaneous;
    const finalTotal = total > 0 ? total : totalEstimatedBudget;

    return {
        accommodation: Math.round(accommodation),
        food: Math.round(food),
        attractions: Math.round(attractions),
        transport: Math.round(transport),
        miscellaneous: Math.round(miscellaneous),
        total: Math.round(finalTotal)
    };
}

/**
 * Build hotels list from AI recommendations
 */
function buildHotelsFromAI(accommodationText, destination) {
    const hotels = [];

    // If AI provided accommodation recommendations, parse them
    if (accommodationText && accommodationText.length > 0) {
        // Create a default hotel entry based on destination
        hotels.push({
            id: 1,
            name: `Recommended ${destination} Hotel`,
            city: destination,
            rating: 4.5,
            averageRating: 4.5,
            midrangePriceBdt: 5000,
            description: accommodationText
        });
    } else {
        // Create default hotels if AI didn't provide specific recommendations
        hotels.push({
            id: 1,
            name: `${destination} City Hotel`,
            city: destination,
            rating: 4.3,
            averageRating: 4.3,
            midrangePriceBdt: 5000,
            description: 'Comfortable accommodation in the city center'
        });
    }

    return hotels;
}

/**
 * Calculate total cost for a day from its activities
 */
function calculateDayTotal(activities) {
    return activities.reduce((total, activity) => {
        return total + (activity.estimatedCost || activity.cost || 0);
    }, 0);
}

/**
 * Calculate duration between two times
 */
function calculateDuration(startTime, endTime) {
    if (!startTime || !endTime) return 'N/A';

    try {
        const [startH, startM] = startTime.split(':').map(Number);
        const [endH, endM] = endTime.split(':').map(Number);

        const startMinutes = startH * 60 + startM;
        const endMinutes = endH * 60 + endM;
        const diffMinutes = endMinutes - startMinutes;

        if (diffMinutes <= 0) return '1 hour';

        const hours = Math.floor(diffMinutes / 60);
        const minutes = diffMinutes % 60;

        if (hours === 0) return `${minutes} minutes`;
        if (minutes === 0) return `${hours} hour${hours > 1 ? 's' : ''}`;
        return `${hours}h ${minutes}m`;
    } catch (error) {
        return 'N/A';
    }
}

/**
 * Detect if a plan is from AI engine
 */
function isAIPlan(plan) {
    return plan && (
        plan.planStatus === 'ai_generated' ||
        plan.aiInsights ||
        plan.weatherInfo ||
        (plan.dailyItineraries && plan.dailyItineraries[0] && plan.dailyItineraries[0].dayTitle)
    );
}

console.log('✅ AI Adapter loaded successfully');
