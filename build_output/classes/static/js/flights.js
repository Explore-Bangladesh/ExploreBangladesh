// Flights Search JavaScript
const API_BASE_URL = 'http://localhost:8080/api/flights';

let tripType = 'oneWay';

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    // Set default departure date (tomorrow)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    document.getElementById('departureDate').value = tomorrow.toISOString().split('T')[0];

    // Set default return date (3 days from now)
    const returnDate = new Date();
    returnDate.setDate(returnDate.getDate() + 4);
    document.getElementById('returnDate').value = returnDate.toISOString().split('T')[0];

    // Set min date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('departureDate').min = today;
    document.getElementById('returnDate').min = today;

    // Check for URL parameters (from homepage redirect)
    const urlParams = new URLSearchParams(window.location.search);
    const origin = urlParams.get('origin');
    const destination = urlParams.get('destination');
    const date = urlParams.get('date');
    const travelers = urlParams.get('travelers');

    if (origin && destination) {
        // Set form values from URL params
        document.getElementById('origin').value = origin;
        document.getElementById('destination').value = destination;

        if (date) {
            document.getElementById('departureDate').value = date;
        }
        if (travelers) {
            document.getElementById('passengers').value = travelers;
        }

        // Auto-trigger search after a short delay
        setTimeout(() => {
            searchFlights();
        }, 500);
    }
});

// Form submit handler
document.getElementById('flightSearchForm').addEventListener('submit', function (e) {
    e.preventDefault();
    searchFlights();
});

// Set trip type (one-way or round-trip)
function setTripType(type) {
    tripType = type;

    document.getElementById('oneWayBtn').classList.toggle('active', type === 'oneWay');
    document.getElementById('roundTripBtn').classList.toggle('active', type === 'roundTrip');
    document.getElementById('returnSection').classList.toggle('active', type === 'roundTrip');
}

// Swap origin and destination airports
function swapAirports() {
    const origin = document.getElementById('origin');
    const destination = document.getElementById('destination');

    const temp = origin.value;
    origin.value = destination.value;
    destination.value = temp;
}

// Show loading spinner
function showLoading() {
    document.getElementById('loadingSpinner').classList.add('active');
    document.getElementById('noResults').classList.remove('active');
    document.getElementById('flightsList').innerHTML = '';
    document.getElementById('resultsHeader').style.display = 'none';
}

// Show error message
function showError(message) {
    document.getElementById('loadingSpinner').classList.remove('active');
    document.getElementById('noResults').classList.add('active');
    document.getElementById('noResults').querySelector('p').textContent = message;
}

// Search flights
async function searchFlights() {
    const origin = document.getElementById('origin').value;
    const destination = document.getElementById('destination').value;
    const departureDate = document.getElementById('departureDate').value;
    const returnDate = tripType === 'roundTrip' ? document.getElementById('returnDate').value : null;
    const passengers = parseInt(document.getElementById('passengers').value);

    if (!origin || !destination || !departureDate) {
        alert('Please fill in all required fields');
        return;
    }

    if (origin === destination) {
        alert('Origin and destination cannot be the same');
        return;
    }

    showLoading();

    try {
        const searchRequest = {
            origin: origin,
            destination: destination,
            departureDate: departureDate,
            returnDate: returnDate,
            adults: passengers
        };

        console.log('Flight search request:', searchRequest);

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
        console.log('Flight search response:', data);

        displayResults(data, origin, destination);

    } catch (error) {
        console.error('Error searching flights:', error);
        showError('Failed to search flights. Please try again.');
    }
}

// Display flight results
function displayResults(data, origin, destination) {
    document.getElementById('loadingSpinner').classList.remove('active');

    const flights = data.flights || [];

    if (flights.length === 0) {
        document.getElementById('noResults').classList.add('active');
        return;
    }

    document.getElementById('noResults').classList.remove('active');
    document.getElementById('resultsHeader').style.display = 'block';
    document.getElementById('resultsCount').textContent = flights.length;
    document.getElementById('searchOrigin').textContent = origin;
    document.getElementById('searchDest').textContent = destination;

    const flightsList = document.getElementById('flightsList');
    flightsList.innerHTML = '';

    flights.forEach(flight => {
        const flightCard = createFlightCard(flight);
        flightsList.innerHTML += flightCard;
    });
}

// Create flight card HTML
function createFlightCard(flight) {
    const outbound = flight.itineraries[0];
    const firstSegment = outbound.segments[0];
    const lastSegment = outbound.segments[outbound.segments.length - 1];

    const stopsText = flight.numberOfStops === 0 ? 'Nonstop' :
        `${flight.numberOfStops} stop${flight.numberOfStops > 1 ? 's' : ''}`;
    const stopsClass = flight.numberOfStops === 0 ? 'nonstop' : 'stops';

    // Format price with commas
    const priceFormatted = parseFloat(flight.price).toLocaleString('en-BD');

    // Check if round trip (has return itinerary)
    const hasReturn = flight.itineraries.length > 1;
    let returnHtml = '';

    if (hasReturn) {
        const returnFlight = flight.itineraries[1];
        const returnFirst = returnFlight.segments[0];
        const returnLast = returnFlight.segments[returnFlight.segments.length - 1];
        const returnStops = returnFlight.segments.length - 1;
        const returnStopsText = returnStops === 0 ? 'Nonstop' : `${returnStops} stop${returnStops > 1 ? 's' : ''}`;
        const returnStopsClass = returnStops === 0 ? 'nonstop' : 'stops';

        returnHtml = `
            <hr class="my-3">
            <div class="row align-items-center">
                <div class="col-md-2">
                    <div class="airline-info">
                        <div class="airline-logo">${returnFirst.carrierCode}</div>
                        <div>
                            <div class="fw-semibold">${returnFirst.carrierName || returnFirst.carrierCode}</div>
                            <small class="text-muted">${returnFirst.flightNumber}</small>
                        </div>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="flight-route">
                        <div class="flight-time">
                            <div class="time">${returnFirst.departureTime}</div>
                            <div class="airport">${returnFirst.departureAirport}</div>
                        </div>
                        <div class="flight-duration">
                            <div class="duration-text">${returnFlight.duration}</div>
                            <div class="flight-line"></div>
                            <span class="stops-badge ${returnStopsClass}">${returnStopsText}</span>
                        </div>
                        <div class="flight-time">
                            <div class="time">${returnLast.arrivalTime}</div>
                            <div class="airport">${returnLast.arrivalAirport}</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="text-muted small">Return</div>
                </div>
            </div>
        `;
    }

    return `
        <div class="flight-card p-4">
            <div class="row align-items-center">
                <!-- Airline Info -->
                <div class="col-md-2">
                    <div class="airline-info">
                        <div class="airline-logo">${firstSegment.carrierCode}</div>
                        <div>
                            <div class="fw-semibold">${flight.airlineName || flight.airline}</div>
                            <small class="text-muted">${firstSegment.flightNumber}</small>
                        </div>
                    </div>
                </div>
                
                <!-- Flight Route -->
                <div class="col-md-6">
                    <div class="flight-route">
                        <div class="flight-time">
                            <div class="time">${firstSegment.departureTime}</div>
                            <div class="airport">${firstSegment.departureAirport}</div>
                        </div>
                        <div class="flight-duration">
                            <div class="duration-text">${outbound.duration}</div>
                            <div class="flight-line"></div>
                            <span class="stops-badge ${stopsClass}">${stopsText}</span>
                        </div>
                        <div class="flight-time">
                            <div class="time">${lastSegment.arrivalTime}</div>
                            <div class="airport">${lastSegment.arrivalAirport}</div>
                        </div>
                    </div>
                </div>
                
                <!-- Price -->
                <div class="col-md-4">
                    <div class="price-section">
                        <div class="price-amount">৳${priceFormatted}</div>
                        <div class="price-currency">${hasReturn ? 'round trip' : 'one way'}</div>
                        <button class="btn btn-primary mt-2" style="background: #1a73e8; border-color: #1a73e8;" onclick="selectFlight('${flight.id}')">
                            Select
                        </button>
                    </div>
                </div>
            </div>
            ${returnHtml}
        </div>
    `;
}

// Select flight - perform Google search for booking
function selectFlight(flightId) {
    const origin = document.getElementById('origin').value;
    const destination = document.getElementById('destination').value;
    const departureDate = document.getElementById('departureDate').value;

    // Get airport names for better search
    const originSelect = document.getElementById('origin');
    const destSelect = document.getElementById('destination');
    const originName = originSelect.options[originSelect.selectedIndex].text;
    const destName = destSelect.options[destSelect.selectedIndex].text;

    const searchQuery = `book flight ${originName} to ${destName} ${departureDate}`;
    const googleSearchUrl = `https://www.google.com/search?q=${encodeURIComponent(searchQuery)}`;

    window.open(googleSearchUrl, '_blank');
}
