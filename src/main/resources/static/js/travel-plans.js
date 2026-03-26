// travel-plans.js — Budget Tier Selection + Plan Grid

const API_BASE = '/api/plans';
let allPlans = [];
let activeTier = null;

document.addEventListener('DOMContentLoaded', async () => {
    await loadPlans();
    setupTierCards();
});

async function loadPlans() {
    try {
        const res = await fetch(API_BASE);
        allPlans = await res.json();
        renderPlans(allPlans);
    } catch (e) {
        console.error('Failed to load plans:', e);
        document.getElementById('planGrid').innerHTML = '<p class="text-center text-danger">Failed to load travel plans.</p>';
    }
}

function setupTierCards() {
    document.querySelectorAll('.tier-card').forEach(card => {
        card.addEventListener('click', () => {
            const tier = card.dataset.tier;
            document.querySelectorAll('.tier-card').forEach(c => c.classList.remove('active'));

            if (activeTier === tier) {
                activeTier = null;
                renderPlans(allPlans);
            } else {
                activeTier = tier;
                card.classList.add('active');
                renderPlans(allPlans.filter(p => p.budgetTier === tier));
            }
        });
    });
}

function renderPlans(plans) {
    const grid = document.getElementById('planGrid');
    if (plans.length === 0) {
        grid.innerHTML = '<p class="text-center text-muted mt-4">No plans found for this tier.</p>';
        return;
    }

    grid.innerHTML = plans.map(p => `
        <div class="col-lg-4 col-md-6 mb-4">
            <div class="plan-card" onclick="location.href='itinerary.html?id=${p.id}'">
                <div class="plan-image-wrap">
                    <img src="${p.destinationImage}" alt="${p.destination}" loading="lazy">
                    <span class="tier-badge tier-${p.budgetTier.toLowerCase()}">${p.budgetTier}</span>
                    <span class="duration-badge">${p.durationDays}D / ${p.durationNights}N</span>
                </div>
                <div class="plan-body">
                    <h5 class="plan-title">${p.destination}</h5>
                    <p class="plan-division"><i class="bi bi-geo-alt-fill"></i> ${p.division} Division</p>
                    <p class="plan-desc">${p.description.substring(0, 100)}...</p>
                    <div class="plan-highlights">
                        ${(p.highlights || []).slice(0, 3).map(h => `<span class="highlight-tag">${h}</span>`).join('')}
                    </div>
                    <div class="plan-footer">
                        <span class="plan-price">৳${p.totalCost.toLocaleString()}</span>
                        <span class="plan-group"><i class="bi bi-people-fill"></i> ${p.groupSize}</span>
                    </div>
                    <button class="btn-view-itinerary" onclick="event.stopPropagation(); location.href='itinerary.html?id=${p.id}'">
                        View Itinerary <i class="bi bi-arrow-right"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}
