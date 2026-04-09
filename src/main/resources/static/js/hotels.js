// Hotels Page JavaScript
const API_BASE_URL = 'http://localhost:8080/api/hotels';

// Set minimum date to today
document.addEventListener('DOMContentLoaded', function () {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('checkInDate').setAttribute('min', today);
    document.getElementById('checkOutDate').setAttribute('min', today);

    // Set default dates if not provided
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dayAfter = new Date();
    dayAfter.setDate(dayAfter.getDate() + 2);

    // Check for URL parameters (from Homepage redirect)
    const urlParams = new URLSearchParams(window.location.search);
    const destParam = urlParams.get('destination');
    const checkInParam = urlParams.get('checkIn');
    const checkOutParam = urlParams.get('checkOut');
    const guestsParam = urlParams.get('guests');

    if (destParam) {
        document.getElementById('destination').value = destParam;
        document.getElementById('checkInDate').value = checkInParam || tomorrow.toISOString().split('T')[0];
        document.getElementById('checkOutDate').value = checkOutParam || dayAfter.toISOString().split('T')[0];
        if (guestsParam) document.getElementById('adults').value = guestsParam;

        // Auto-trigger search
        searchHotels();
    } else {
        // Default behavior
        document.getElementById('checkInDate').value = tomorrow.toISOString().split('T')[0];
        document.getElementById('checkOutDate').value = dayAfter.toISOString().split('T')[0];
    }
});

// Handle search form submission
document.getElementById('hotelSearchForm').addEventListener('submit', function (e) {
    e.preventDefault();
    searchHotels();
});

// Search hotels function
async function searchHotels() {
    const destination = document.getElementById('destination').value.trim();
    const checkInDate = document.getElementById('checkInDate').value;
    const checkOutDate = document.getElementById('checkOutDate').value;
    const adults = parseInt(document.getElementById('adults').value);

    if (!destination || !checkInDate || !checkOutDate) {
        alert('Please fill in all required fields');
        return;
    }

    // Validate dates
    if (new Date(checkInDate) >= new Date(checkOutDate)) {
        alert('Check-out date must be after check-in date');
        return;
    }

    // Show loading spinner
    showLoading();

    try {
        const searchRequest = {
            destination: destination,
            checkInDate: checkInDate,
            checkOutDate: checkOutDate,
            adults: adults
        };

        console.log('Search request:', searchRequest);

        const response = await fetch(`${API_BASE_URL}/search`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(searchRequest)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Search response:', data);

        displayResults(data, destination);

    } catch (error) {
        console.error('Error searching hotels:', error);
        showError('Failed to search hotels. Please try again.');
    }
}

// Display search results
function displayResults(data, searchedDestination) {
    const hotelsGrid = document.getElementById('hotelsGrid');
    const resultsHeader = document.getElementById('resultsHeader');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const noResults = document.getElementById('noResults');
    const mapPreviewCard = document.getElementById('mapPreviewCard');

    // Hide loading spinner
    loadingSpinner.classList.remove('active');

    // Parse Geoapify response
    const features = data.features || [];

    if (!features || features.length === 0) {
        noResults.classList.add('active');
        noResults.querySelector('h4').textContent = 'No hotels found';
        noResults.querySelector('p').textContent = 'Try adjusting your search criteria';
        resultsHeader.style.display = 'none';
        mapPreviewCard.style.display = 'none';
        hotelsGrid.innerHTML = '';
        const existingNav = document.getElementById('paginationNav');
        if (existingNav) existingNav.remove();
        return;
    }

    // Show results header
    noResults.classList.remove('active');
    resultsHeader.style.display = 'block';
    document.getElementById('resultsCount').textContent = features.length;
    document.getElementById('searchedDest').textContent = searchedDestination;

    // Store destination globally for use in view details
    window.currentSearchDestination = searchedDestination;

    // Clear previous results
    hotelsGrid.innerHTML = '';

    // Store all features globally for pagination
    window.allHotels = features;
    window.currentPage = 1;
    window.itemsPerPage = 15;

    // Show the map preview card and update the static map image
    mapPreviewCard.style.display = 'block';

    // Update static map centered on first hotel
    if (features.length > 0 && features[0].geometry) {
        const lon = features[0].geometry.coordinates[0];
        const lat = features[0].geometry.coordinates[1];
        const mapPreviewImg = document.getElementById('mapPreviewImg');
        mapPreviewImg.src = `https://api.geoapify.com/v1/staticmap?style=osm-bright&width=300&height=200&center=lonlat:${lon},${lat}&zoom=12&apiKey=2a42881f098048d7ae41b52f7c540f33`;
    }

    setupPaginationControls();
    renderPage(1);
}

function renderPage(page) {
    const hotelsGrid = document.getElementById('hotelsGrid');
    hotelsGrid.innerHTML = '';

    const start = (page - 1) * window.itemsPerPage;
    const end = start + window.itemsPerPage;
    const pageItems = window.allHotels.slice(start, end);

    pageItems.forEach((feature, index) => {
        // Calculate original index to ensure consistent images
        const originalIndex = start + index;
        const hotelCard = createHotelCardFromGeoapify(feature, originalIndex);
        hotelsGrid.appendChild(hotelCard);
    });

    // Update results count text
    const total = window.allHotels.length;
    const showingStart = start + 1;
    const showingEnd = Math.min(end, total);
    document.getElementById('resultsCount').textContent = `${showingStart}-${showingEnd} of ${total}`;

    updatePaginationButtons();
}

function setupPaginationControls() {
    // Remove existing pagination if any
    const existingNav = document.getElementById('paginationNav');
    if (existingNav) existingNav.remove();

    const totalPages = Math.ceil(window.allHotels.length / window.itemsPerPage);
    const prevDisabledClass = window.currentPage === 1 ? 'disabled' : '';
    const nextDisabledClass = window.currentPage === totalPages ? 'disabled' : '';

    const paginationHtml = `
        <nav id="paginationNav" class="mt-4" aria-label="Hotel pagination">
            <ul class="pagination justify-content-center">
                <li class="page-item ${prevDisabledClass}" id="prevBtn">
                    <button class="page-link" onclick="changePage(-1)">Previous</button>
                </li>
                <li class="page-item disabled">
                    <span class="page-link" id="pageIndicator">Page ${window.currentPage} of ${totalPages}</span>
                </li>
                <li class="page-item ${nextDisabledClass}" id="nextBtn">
                    <button class="page-link" onclick="changePage(1)">Next</button>
                </li>
            </ul>
        </nav>
    `;

    document.getElementById('hotelsGrid').parentElement.insertAdjacentHTML('beforeend', paginationHtml);
}

function changePage(delta) {
    const totalPages = Math.ceil(window.allHotels.length / window.itemsPerPage);
    const newPage = window.currentPage + delta;

    if (newPage >= 1 && newPage <= totalPages) {
        window.currentPage = newPage;
        renderPage(newPage);
        // Scroll to top of results
        document.getElementById('resultsHeader').scrollIntoView({ behavior: 'smooth' });
    }
}

function updatePaginationButtons() {
    const totalPages = Math.ceil(window.allHotels.length / window.itemsPerPage);

    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const pageIndicator = document.getElementById('pageIndicator');

    if (prevBtn) prevBtn.classList.toggle('disabled', window.currentPage === 1);
    if (nextBtn) nextBtn.classList.toggle('disabled', window.currentPage === totalPages);
    if (pageIndicator) pageIndicator.textContent = `Page ${window.currentPage} of ${totalPages}`;
}

// Create hotel card HTML
function createHotelCard(hotel) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4';

    const stars = '★'.repeat(Math.floor(hotel.starRating)) + '☆'.repeat(5 - Math.floor(hotel.starRating));

    col.innerHTML = `
        <div class="card hotel-card">
            <img src="${hotel.imageUrl || 'https://via.placeholder.com/400x200?text=Hotel+Image'}" 
                 class="card-img-top hotel-image" 
                 alt="${hotel.name}"
                 onerror="this.src='https://via.placeholder.com/400x200?text=Hotel+Image'">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <h5 class="card-title mb-0">${hotel.name}</h5>
                    ${hotel.guestRating > 0 ? `
                        <span class="guest-rating-badge">${hotel.guestRating.toFixed(1)}</span>
                    ` : ''}
                </div>
                
                <div class="star-rating mb-2" title="${hotel.starRating} stars">
                    ${stars}
                </div>
                
                <p class="card-text text-muted small mb-2">
                    <i class="bi bi-geo-alt"></i> ${hotel.address || 'Address unavailable'}
                </p>
                
                ${hotel.reviewCount > 0 ? `
                    <p class="card-text small text-muted mb-2">
                        <i class="bi bi-chat-left-text"></i> ${hotel.reviewCount} reviews
                    </p>
                ` : ''}
                
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <div class="price-tag">
                        ${hotel.price.displayPrice || `৳${hotel.price.amount.toFixed(0)}`}
                    </div>
                    <button class="btn btn-sm btn-outline-primary" onclick="viewHotelDetails('${hotel.id}')">
                        View Details
                    </button>
                </div>
            </div>
        </div>
    `;

    return col;
}

// View hotel details - always use Google search for reliability
// (Many hotel websites from API are outdated/dead)
function viewHotelDetails(hotelName) {
    // Get the current search destination for disambiguation
    const destination = window.currentSearchDestination || 'Bangladesh';

    // Build search query with hotel name + destination for accuracy
    const searchQuery = encodeURIComponent(`${hotelName} hotel ${destination} Bangladesh`);
    const googleSearchUrl = `https://www.google.com/search?q=${searchQuery}`;

    window.open(googleSearchUrl, '_blank');
}

// Create hotel card HTML from Geoapify data
function createHotelCardFromGeoapify(feature, index) {
    const col = document.createElement('div');
    col.className = 'col-md-6 col-lg-4';

    const props = feature.properties || {};
    const name = props.name || props.address_line1 || `Hotel ${index + 1}`;
    const address = props.address_line2 || props.city || 'Bangladesh';

    // Generate a random price for demonstration (since Geoapify doesn't provide prices)
    const basePrice = Math.floor(Math.random() * 8000) + 2000;
    const displayPrice = `৳${basePrice.toLocaleString('en-BD')} per night`;

    // Random star rating
    const starRating = Math.floor(Math.random() * 3) + 3; // 3-5 stars
    const stars = '★'.repeat(starRating) + '☆'.repeat(5 - starRating);

    // Random guest rating
    const guestRating = (Math.random() * 2 + 3).toFixed(1); // 3.0-5.0

    // Curated list of high-quality hotel images from Unsplash
    const hotelImages = [
        'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1560200353-ce0a76b1d438?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1625244724120-1fd1d34d00f6?auto=format&fit=crop&w=400&h=200&q=80',
        'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=400&h=200&q=80'
    ];

    // Pick an image based on the index (deterministic)
    const imageUrl = hotelImages[index % hotelImages.length];

    col.innerHTML = `
        <div class="card hotel-card">
            <img src="${imageUrl}"
                 class="card-img-top hotel-image"
                 alt="${name}">
                <div class="card-body">
                    <div class="mb-2">
                        <h5 class="card-title mb-0">${name}</h5>
                    </div>

                    <p class="card-text text-muted small mb-2">
                        <i class="bi bi-geo-alt"></i> ${address}
                    </p>
                    <div class="mt-3 w-100">
                        <button class="btn btn-sm btn-outline-primary w-100" onclick="viewHotelDetails('${name.replace(/'/g, "\\'").replace(/"/g, '&quot;')}')">
                            View Details
                        </button>
                    </div>
                </div>
            </div>
    `;

    return col;
}

// Apply filters
function applyFilters() {
    searchHotels();
}

// Show loading spinner
function showLoading() {
    document.getElementById('loadingSpinner').classList.add('active');
    document.getElementById('noResults').classList.remove('active');
    document.getElementById('resultsHeader').style.display = 'none';
    document.getElementById('mapPreviewCard').style.display = 'none';
    document.getElementById('hotelsGrid').innerHTML = '';
    const existingNav = document.getElementById('paginationNav');
    if (existingNav) existingNav.remove();
}

// Show error message
function showError(message) {
    document.getElementById('loadingSpinner').classList.remove('active');
    document.getElementById('noResults').classList.add('active');
    document.getElementById('resultsHeader').style.display = 'none';
    document.getElementById('mapPreviewCard').style.display = 'none';
    document.getElementById('hotelsGrid').innerHTML = '';
    document.getElementById('noResults').querySelector('h4').textContent = 'Error';
    document.getElementById('noResults').querySelector('p').textContent = message;
    const existingNav = document.getElementById('paginationNav');
    if (existingNav) existingNav.remove();
}

// Update check-out min date when check-in changes
document.getElementById('checkInDate').addEventListener('change', function () {
    const checkInDate = new Date(this.value);
    const minCheckOut = new Date(checkInDate);
    minCheckOut.setDate(minCheckOut.getDate() + 1);
    document.getElementById('checkOutDate').setAttribute('min', minCheckOut.toISOString().split('T')[0]);
});

// ============== MAP FUNCTIONALITY ==============

// Global map instance
let hotelMap = null;

// Show map with hotel markers
function showMap() {
    if (!window.allHotels || window.allHotels.length === 0) {
        alert('No hotels to display on map. Please search first.');
        return;
    }

    // Update modal title with destination
    document.getElementById('mapDestination').textContent = window.currentSearchDestination || 'Bangladesh';

    // Show the modal
    const mapModal = new bootstrap.Modal(document.getElementById('mapModal'));
    mapModal.show();

    // Need to wait for modal to be visible before initializing map
    document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
        initializeMap();
    }, { once: true });
}

// Initialize Leaflet map
function initializeMap() {
    const mapContainer = document.getElementById('mapContainer');

    // Clear existing map if any
    if (hotelMap) {
        hotelMap.remove();
        hotelMap = null;
    }

    // Calculate center from first hotel or use default Bangladesh center
    let centerLat = 23.8103;
    let centerLon = 90.4125;

    if (window.allHotels.length > 0 && window.allHotels[0].geometry) {
        const firstHotel = window.allHotels[0].geometry.coordinates;
        centerLon = firstHotel[0];
        centerLat = firstHotel[1];
    }

    // Create map
    hotelMap = L.map('mapContainer').setView([centerLat, centerLon], 13);

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(hotelMap);

    // Create bounds to fit all markers
    const bounds = L.latLngBounds();

    // Add markers for each hotel
    window.allHotels.forEach((hotel, index) => {
        if (hotel.geometry && hotel.geometry.coordinates) {
            const lon = hotel.geometry.coordinates[0];
            const lat = hotel.geometry.coordinates[1];
            const props = hotel.properties || {};
            const name = props.name || props.address_line1 || `Hotel ${index + 1}`;
            const address = props.address_line2 || props.city || '';

            // Create custom icon
            const hotelIcon = L.divIcon({
                className: 'hotel-marker',
                html: `<div style="
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 8px 12px;
                    border-radius: 20px;
                    font-weight: bold;
                    font-size: 12px;
                    white-space: nowrap;
                    box-shadow: 0 3px 10px rgba(0,0,0,0.3);
                    border: 2px solid white;
                ">🏨 ${index + 1}</div>`,
                iconSize: [40, 40],
                iconAnchor: [20, 40]
            });

            // Add marker
            const marker = L.marker([lat, lon], { icon: hotelIcon }).addTo(hotelMap);

            // Add popup
            marker.bindPopup(`
                <div style="min-width: 200px;">
                    <h6 style="margin-bottom: 5px; color: #667eea;">${name}</h6>
                    <p style="margin: 0; font-size: 12px; color: #666;">
                        <i class="bi bi-geo-alt"></i> ${address}
                    </p>
                    <button onclick="viewHotelDetails('${name.replace(/'/g, "\\'")}')" 
                            class="btn btn-sm btn-primary mt-2 w-100">
                        View Details
                    </button>
                </div>
            `);

            bounds.extend([lat, lon]);
        }
    });

    // Fit map to show all markers
    if (bounds.isValid()) {
        hotelMap.fitBounds(bounds, { padding: [50, 50] });
    }

    // Fix for map tiles not loading properly in modal
    setTimeout(() => {
        hotelMap.invalidateSize();
    }, 100);
}
