// Cars Search JavaScript (pattern aligned with flights.js)
const API_BASE_URL = "http://localhost:8080/api/cars";
const FALLBACK_LOCATIONS = [
  "Dhaka",
  "Cox's Bazar",
  "Chittagong",
  "Sylhet",
  "Rangamati",
  "Khulna",
  "Rajshahi",
  "Bandarban",
  "Tangail",
  "Mymensingh",
  "Barishal",
  "Rangpur"
];

let hasSearched = false;

// Initialize on page load
document.addEventListener("DOMContentLoaded", function () {
  loadPickupLocationSuggestions();

  // Set default pick-up date (tomorrow)
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  document.getElementById("pickupDate").value = tomorrow.toISOString().split("T")[0];

  // Set default drop-off date (3 days after pick-up => today + 4)
  const dropoff = new Date();
  dropoff.setDate(dropoff.getDate() + 4);
  document.getElementById("dropoffDate").value = dropoff.toISOString().split("T")[0];

  // Set min date to today
  const today = new Date().toISOString().split("T")[0];
  document.getElementById("pickupDate").min = today;
  document.getElementById("dropoffDate").min = today;

  // Ensure drop-off can't be before pick-up
  document.getElementById("pickupDate").addEventListener("change", () => {
    const pickupVal = document.getElementById("pickupDate").value;
    const dropoffInput = document.getElementById("dropoffDate");

    dropoffInput.min = pickupVal || today;

    if (dropoffInput.value && pickupVal && dropoffInput.value < pickupVal) {
      dropoffInput.value = pickupVal;
    }
  });

  // Show initial state on load (so container doesn't look empty)
  showInitial();

  // Check URL parameters (optional)
  // Example: cars.html?location=Dhaka&pickupDate=2026-03-10&dropoffDate=2026-03-12
  const urlParams = new URLSearchParams(window.location.search);
  const location = urlParams.get("location");
  const pickupDate = urlParams.get("pickupDate");
  const dropoffDate = urlParams.get("dropoffDate");

  if (location) document.getElementById("pickupLocation").value = location;
  if (pickupDate) document.getElementById("pickupDate").value = pickupDate;
  if (dropoffDate) document.getElementById("dropoffDate").value = dropoffDate;

  // Auto-search if location exists
  if (location) {
    setTimeout(() => searchCars(), 300);
  }
});

async function loadPickupLocationSuggestions() {
  const datalist = document.getElementById("pickupLocationSuggestions");
  if (!datalist) return;

  let locations = [];
  try {
    const response = await fetch(`${API_BASE_URL}/cities`);
    if (response.ok) {
      const apiCities = await response.json();
      if (Array.isArray(apiCities)) {
        locations = apiCities.filter(Boolean);
      }
    }
  } catch (error) {
    console.warn("Could not load car city suggestions from API.", error);
  }

  if (locations.length === 0) {
    locations = FALLBACK_LOCATIONS;
  }

  const uniqueSorted = [...new Set(locations.map((item) => String(item).trim()).filter(Boolean))]
    .sort((a, b) => a.localeCompare(b));

  datalist.innerHTML = "";
  uniqueSorted.forEach((city) => {
    const option = document.createElement("option");
    option.value = city;
    datalist.appendChild(option);
  });
}

// Form submit handler
document.getElementById("carSearchForm").addEventListener("submit", function (e) {
  e.preventDefault();
  searchCars();
});

function showInitial() {
  document.getElementById("initialState").classList.add("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.remove("active");
  document.getElementById("carsList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function showLoading() {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.add("active");
  document.getElementById("noResults").classList.remove("active");
  document.getElementById("carsList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function showNoResults(message) {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.add("active");
  document.getElementById("noResults").querySelector("p").textContent = message;
  document.getElementById("carsList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function hideStates() {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.remove("active");
}

// Search cars
async function searchCars() {
  const location = document.getElementById("pickupLocation").value.trim();
  const pickupDate = document.getElementById("pickupDate").value;
  const dropoffDate = document.getElementById("dropoffDate").value;

  if (!location) {
    alert("Please enter a pick-up location");
    return;
  }

  if (!pickupDate || !dropoffDate) {
    alert("Please select pick-up and drop-off dates");
    return;
  }

  if (dropoffDate < pickupDate) {
    alert("Drop-off date cannot be earlier than pick-up date");
    return;
  }

  hasSearched = true;
  showLoading();

  try {
    // Compatible with your existing backend style:
    // GET /api/cars?location=Dhaka
    // We also include dates (backend can ignore if not needed)
    const params = new URLSearchParams({
      location,
      pickupDate,
      dropoffDate
    });

    const response = await fetch(`${API_BASE_URL}?${params.toString()}`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    displayResults(data, { location, pickupDate, dropoffDate });

  } catch (error) {
    console.error("Error searching cars:", error);
    showNoResults("Failed to search cars. Please try again.");
  }
}

// Display results
function displayResults(data, searchMeta) {
  const cars = Array.isArray(data) ? data : (data && data.cars) ? data.cars : [];
  document.getElementById("loadingSpinner").classList.remove("active");

  if (!cars || cars.length === 0) {
    // Only show "no cars" after user searched
    if (hasSearched) {
      showNoResults("No cars found for your search.");
    } else {
      showInitial();
    }
    return;
  }

  hideStates();

  document.getElementById("resultsHeader").style.display = "block";
  document.getElementById("resultsCount").textContent = cars.length;
  document.getElementById("searchLocation").textContent = searchMeta?.location || "-";
  document.getElementById("searchPickupDate").textContent = searchMeta?.pickupDate || "-";
  document.getElementById("searchDropoffDate").textContent = searchMeta?.dropoffDate || "-";

  const carsList = document.getElementById("carsList");
  carsList.innerHTML = "";

  cars.forEach((car) => {
    carsList.innerHTML += createCarCard(car);
  });
}

// Create car card HTML
function createCarCard(car) {
  const imageUrl = car.imageUrl || "https://via.placeholder.com/600x400";
  const name = car.name || "Car";
  const type = car.type || "Standard";
  const seats = car.seats ?? "-";
  const price = car.price ?? 0;

  const priceFormatted = Number(price).toLocaleString("en-BD");

  return `
    <div class="col-md-4">
      <div class="card car-card h-100 shadow-sm">
        <img src="${imageUrl}" class="card-img-top" alt="${escapeHtml(name)}">
        <div class="card-body d-flex flex-column">
          <h5 class="card-title">${escapeHtml(name)}</h5>
          <p class="card-text mb-1">${escapeHtml(type)} | ${escapeHtml(String(seats))} seats</p>
          <p class="text-primary fw-bold mb-3">৳${priceFormatted} / day</p>
          <button class="btn btn-primary mt-auto w-100"
            style="background:#1a73e8;border-color:#1a73e8;"
            onclick="selectCar('${car.id ?? ""}', '${encodeURIComponent(name)}')">
            Select
          </button>
        </div>
      </div>
    </div>
  `;
}

// Select car -> open Google search
function selectCar(carId, encodedCarName) {
  const location = document.getElementById("pickupLocation").value.trim();
  const pickupDate = document.getElementById("pickupDate").value;
  const dropoffDate = document.getElementById("dropoffDate").value;

  const carName = decodeURIComponent(encodedCarName || "");
  const searchQuery = `rent car ${carName} in ${location} ${pickupDate} to ${dropoffDate}`;
  const googleSearchUrl = `https://www.google.com/search?q=${encodeURIComponent(searchQuery)}`;

  window.open(googleSearchUrl, "_blank");
}

// Helper to prevent HTML injection
function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}