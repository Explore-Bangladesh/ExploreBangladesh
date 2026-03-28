// itinerary.js — Detailed day-by-day itinerary view

const API_BASE = '/api/plans';
let planData = null;

function setFallbackBackLinkVisible(visible) {
    const fallback = document.getElementById('fallbackBackLink');
    if (!fallback) return;
    fallback.style.display = visible ? 'block' : 'none';
}

document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get('id');
    if (!id) {
        setFallbackBackLinkVisible(true);
        document.getElementById('itineraryContent').innerHTML = '<p class="text-center text-danger mt-5">No plan ID specified.</p>';
        return;
    }
    setFallbackBackLinkVisible(false);
    await loadPlan(id);
});

async function loadPlan(id) {
    try {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) throw new Error('Plan not found');
        planData = await res.json();
        setFallbackBackLinkVisible(false);
        renderHeader();
        renderCostBreakdown();
        renderDayTabs();
        if (planData.days && planData.days.length > 0) {
            showDay(1);
        }
    } catch (e) {
        console.error('Failed to load plan:', e);
        setFallbackBackLinkVisible(true);
        document.getElementById('itineraryContent').innerHTML = '<p class="text-center text-danger mt-5">Failed to load travel plan.</p>';
    }
}

function renderHeader() {
    const h = document.getElementById('planHeader');
    h.innerHTML = `
        <div class="plan-hero" style="background-image: url('${planData.destinationImage}')">
            <a href="travel-plans.html" class="hero-back-btn"><i class="bi bi-arrow-left"></i> Back to All Packages</a>
            <div class="plan-hero-overlay">
                <span class="tier-badge tier-${planData.budgetTier.toLowerCase()}">${planData.budgetTier}</span>
                <h1>${planData.destination}</h1>
                <div class="plan-meta">
                    <span><i class="bi bi-calendar3"></i> ${planData.durationDays} Days / ${planData.durationNights} Nights</span>
                    <span><i class="bi bi-geo-alt-fill"></i> ${planData.division} Division</span>
                    <span><i class="bi bi-people-fill"></i> ${planData.groupSize}</span>
                    <span><i class="bi bi-sun"></i> Best: ${planData.bestTimeToVisit}</span>
                </div>
                <div class="plan-hero-price">৳${planData.totalCost.toLocaleString()}<small>/person</small></div>
            </div>
        </div>
        <div class="plan-description-box">
            <p>${planData.description}</p>
            <div class="plan-highlights-row">
                ${(planData.highlights || []).map(h => `<span class="highlight-pill"><i class="bi bi-check-circle-fill"></i> ${h}</span>`).join('')}
            </div>
        </div>
    `;
}

function renderCostBreakdown() {
    const container = document.getElementById('costBreakdown');
    if (!planData.costBreakdown || planData.costBreakdown.length === 0) {
        container.style.display = 'none';
        return;
    }

    const colors = { TRANSPORT: '#4fc3f7', ACCOMMODATION: '#7c4dff', FOOD: '#ff7043', ACTIVITIES: '#66bb6a', MISCELLANEOUS: '#bdbdbd' };
    const icons = { TRANSPORT: 'bi-bus-front', ACCOMMODATION: 'bi-house-door', FOOD: 'bi-cup-hot', ACTIVITIES: 'bi-binoculars', MISCELLANEOUS: 'bi-three-dots' };
    const total = planData.costBreakdown.reduce((s, c) => s + c.amount, 0);

    container.innerHTML = `
        <h4><i class="bi bi-pie-chart-fill"></i> Cost Breakdown</h4>
        <div class="cost-bar">
            ${planData.costBreakdown.map(c => `<div class="cost-segment" style="width:${(c.amount/total*100).toFixed(1)}%;background:${colors[c.category]||'#999'}" title="${c.category}: ৳${c.amount.toLocaleString()}"></div>`).join('')}
        </div>
        <div class="cost-legend">
            ${planData.costBreakdown.map(c => `
                <div class="cost-item">
                    <i class="bi ${icons[c.category]||'bi-circle'}" style="color:${colors[c.category]||'#999'}"></i>
                    <span class="cost-cat">${c.category.charAt(0) + c.category.slice(1).toLowerCase()}</span>
                    <span class="cost-amt">৳${c.amount.toLocaleString()}</span>
                    <span class="cost-desc">${c.description || ''}</span>
                </div>
            `).join('')}
        </div>
    `;
}

function renderDayTabs() {
    const tabs = document.getElementById('dayTabs');
    if (!planData.days || planData.days.length === 0) {
        tabs.innerHTML = '<p class="text-muted">No itinerary details available.</p>';
        return;
    }

    tabs.innerHTML = planData.days.map(d => `
        <button class="day-tab" data-day="${d.dayNumber}" onclick="showDay(${d.dayNumber})">
            Day ${d.dayNumber}
        </button>
    `).join('');
}

function showDay(dayNum) {
    // Update tab active state
    document.querySelectorAll('.day-tab').forEach(t => t.classList.remove('active'));
    const activeTab = document.querySelector(`.day-tab[data-day="${dayNum}"]`);
    if (activeTab) activeTab.classList.add('active');

    const day = planData.days.find(d => d.dayNumber === dayNum);
    if (!day) return;

    const timeline = document.getElementById('dayTimeline');
    const typeIcons = { TRAVEL: 'bi-bus-front', FOOD: 'bi-cup-hot', SIGHTSEEING: 'bi-binoculars', REST: 'bi-moon-stars', ACTIVITY: 'bi-lightning' };
    const typeColors = { TRAVEL: '#4fc3f7', FOOD: '#ff7043', SIGHTSEEING: '#66bb6a', REST: '#ab47bc', ACTIVITY: '#ffa726' };

    timeline.innerHTML = `
        <div class="day-header">
            <h3><i class="bi bi-calendar-day"></i> Day ${day.dayNumber}: ${day.title}</h3>
            <p class="day-summary">${day.summary || ''}</p>
        </div>
        <div class="timeline">
            ${(day.activities || []).map(a => `
                <div class="timeline-item">
                    <div class="timeline-time">
                        <span class="time-start">${a.startTime || ''}</span>
                        <span class="time-sep">–</span>
                        <span class="time-end">${a.endTime || ''}</span>
                    </div>
                    <div class="timeline-dot" style="background:${typeColors[a.activityType]||'#999'}">
                        <i class="bi ${typeIcons[a.activityType]||'bi-circle'}"></i>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-card">
                            <div class="tc-header">
                                <h5>${a.title}</h5>
                                ${a.estimatedCost > 0 ? `<span class="tc-cost">৳${a.estimatedCost.toLocaleString()}</span>` : ''}
                            </div>
                            <p class="tc-desc">${a.description || ''}</p>
                            ${a.location ? `<div class="tc-location"><i class="bi bi-geo-alt"></i> ${a.location}</div>` : ''}
                            ${a.tips ? `<div class="tc-tips"><i class="bi bi-lightbulb"></i> <strong>Tip:</strong> ${a.tips}</div>` : ''}
                        </div>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
}
