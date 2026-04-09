// travel-plans.js — Budget Tier Selection + Plan Grid

const API_BASE = '/api/plans';
let allPlans = [];
let activeTier = null;
let filteredPlans = [];
let currentPage = 1;

const ROWS_PER_PAGE = 4;

document.addEventListener('DOMContentLoaded', async () => {
    await loadPlans();
    setupTierCards();

    window.addEventListener('resize', () => {
        renderPlansPage();
    });
});

async function loadPlans() {
    try {
        const res = await fetch(API_BASE);
        allPlans = await res.json();
        filteredPlans = allPlans;
        currentPage = 1;
        renderPlansPage();
    } catch (e) {
        console.error('Failed to load plans:', e);
        document.getElementById('planGrid').innerHTML = '<p class="text-center text-danger">Failed to load travel plans.</p>';
        renderPagination(0);
    }
}

function setupTierCards() {
    document.querySelectorAll('.tier-card').forEach(card => {
        card.addEventListener('click', () => {
            const tier = card.dataset.tier;
            document.querySelectorAll('.tier-card').forEach(c => c.classList.remove('active'));

            if (activeTier === tier) {
                activeTier = null;
                filteredPlans = allPlans;
            } else {
                activeTier = tier;
                card.classList.add('active');
                filteredPlans = allPlans.filter(p => p.budgetTier === tier);
            }

            currentPage = 1;
            renderPlansPage();
        });
    });
}

function getCardsPerRow() {
    if (window.innerWidth >= 992) return 3; // col-lg-4
    if (window.innerWidth >= 768) return 2; // col-md-6
    return 1;
}

function getItemsPerPage() {
    return ROWS_PER_PAGE * getCardsPerRow();
}

function renderPlansPage() {
    const plans = filteredPlans || [];
    const grid = document.getElementById('planGrid');

    if (plans.length === 0) {
        grid.innerHTML = '<p class="text-center text-muted mt-4">No plans found for this tier.</p>';
        renderPagination(0);
        return;
    }

    const itemsPerPage = getItemsPerPage();
    const totalPages = Math.max(1, Math.ceil(plans.length / itemsPerPage));

    if (currentPage > totalPages) {
        currentPage = totalPages;
    }

    const start = (currentPage - 1) * itemsPerPage;
    const pageItems = plans.slice(start, start + itemsPerPage);

    grid.innerHTML = pageItems.map(p => `
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

    renderPagination(totalPages);
}

function renderPagination(totalPages) {
    const grid = document.getElementById('planGrid');
    if (!grid) return;

    let nav = document.getElementById('planPaginationNav');
    if (!nav) {
        const paginationHtml = `
            <nav id="planPaginationNav" class="mt-3" aria-label="Package pagination">
                <ul class="pagination justify-content-center mb-0">
                    <li class="page-item" id="planPrevItem">
                        <button class="page-link" id="planPrevBtn" type="button">Previous</button>
                    </li>
                    <li class="page-item disabled">
                        <span class="page-link" id="planPageIndicator">Page 1 of 1</span>
                    </li>
                    <li class="page-item" id="planNextItem">
                        <button class="page-link" id="planNextBtn" type="button">Next</button>
                    </li>
                </ul>
            </nav>
        `;
        grid.parentElement.insertAdjacentHTML('beforeend', paginationHtml);
        nav = document.getElementById('planPaginationNav');

        document.getElementById('planPrevBtn').addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderPlansPage();
            }
        });

        document.getElementById('planNextBtn').addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                renderPlansPage();
            }
        });
    }

    if (totalPages <= 1) {
        nav.classList.add('d-none');
        return;
    }

    nav.classList.remove('d-none');

    const prevItem = document.getElementById('planPrevItem');
    const nextItem = document.getElementById('planNextItem');
    const indicator = document.getElementById('planPageIndicator');

    if (prevItem) prevItem.classList.toggle('disabled', currentPage === 1);
    if (nextItem) nextItem.classList.toggle('disabled', currentPage === totalPages);
    if (indicator) indicator.textContent = `Page ${currentPage} of ${totalPages}`;
}
