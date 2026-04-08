// Guides Search JavaScript (same pattern as flights.js / cars.js)
const API_BASE_URL = "http://localhost:8080/api/guides";

let hasSearched = false;

document.addEventListener("DOMContentLoaded", function () {
  showInitial();

  // Optional: read URL params for auto-search
  // Example: guides.html?city=Dhaka&language=English
  const urlParams = new URLSearchParams(window.location.search);
  const city = urlParams.get("city");
  const language = urlParams.get("language");

  if (city) document.getElementById("cityInput").value = city;
  if (language) document.getElementById("languageSelect").value = language;

  if (city || language) {
    setTimeout(() => searchGuides(), 300);
  }
});

const guideSearchForm = document.getElementById("guideSearchForm");
if (guideSearchForm) {
  guideSearchForm.addEventListener("submit", function (e) {
    e.preventDefault();
    searchGuides();
  });
}

function showInitial() {
  document.getElementById("initialState").classList.add("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.remove("active");
  document.getElementById("guidesList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function showLoading() {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.add("active");
  document.getElementById("noResults").classList.remove("active");
  document.getElementById("guidesList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function showNoResults(message) {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.add("active");
  document.getElementById("noResults").querySelector("p").textContent = message;
  document.getElementById("guidesList").innerHTML = "";
  document.getElementById("resultsHeader").style.display = "none";
}

function hideStates() {
  document.getElementById("initialState").classList.remove("active");
  document.getElementById("loadingSpinner").classList.remove("active");
  document.getElementById("noResults").classList.remove("active");
}

// Search guides
async function searchGuides() {
  const city = document.getElementById("cityInput").value.trim();
  const language = document.getElementById("languageSelect").value;

  if (!city && !language) {
    alert("Please enter a city or select a language");
    return;
  }

  hasSearched = true;
  showLoading();

  try {
    // Backend options:
    // 1) returns []  (simple)
    // 2) returns { guides: [] } (flights-like)
    // We'll support both.
    const params = new URLSearchParams();
    if (city) params.set("city", city);
    if (language) params.set("language", language);

    const response = await fetch(`${API_BASE_URL}?${params.toString()}`);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    displayResults(data, { city, language });

  } catch (error) {
    console.error("Error searching guides:", error);
    showNoResults("Failed to search guides. Please try again.");
  }
}

// Display guide results
function displayResults(data, meta) {
  const guides = Array.isArray(data) ? data : (data && data.guides) ? data.guides : [];
  document.getElementById("loadingSpinner").classList.remove("active");

  if (!guides || guides.length === 0) {
    if (hasSearched) {
      showNoResults("No guides found for your search.");
    } else {
      showInitial();
    }
    return;
  }

  hideStates();

  document.getElementById("resultsHeader").style.display = "block";
  document.getElementById("resultsCount").textContent = guides.length;
  document.getElementById("searchCity").textContent = meta?.city || "-";
  document.getElementById("searchLanguage").textContent = meta?.language || "Any";

  const list = document.getElementById("guidesList");
  list.innerHTML = "";

  guides.forEach((g) => {
    list.innerHTML += createGuideCard(g);
  });
}

// Create guide card HTML
function createGuideCard(guide) {
  const name = guide.name || "Guide";
  const city = guide.city || "-";
  const years = guide.experienceYears ?? guide.experience ?? "-";

  // language can be string or array
  const langs = Array.isArray(guide.languages)
    ? guide.languages.join(", ")
    : (guide.languages || guide.language || "-");

  const rating = Number(guide.rating ?? 0);
  const ratingText = rating > 0 ? `${rating.toFixed(1)}` : "N/A";
  const stars = rating > 0 ? renderStars(rating) : "";

  const imageUrl =
    guide.imageUrl ||
    "https://source.unsplash.com/120x120/?portrait,person";

  return `
    <div class="col-md-4">
      <div class="card shadow-sm h-100 text-center p-3">
        <img src="${imageUrl}" class="rounded-circle mx-auto mb-3 guide-avatar" alt="${escapeHtml(name)}" />
        <h5 class="fw-bold">${escapeHtml(name)}</h5>
        <p class="text-muted mb-1">${escapeHtml(city)}</p>
        <p class="text-warning mb-2">${stars} <span class="text-muted">(${escapeHtml(ratingText)})</span></p>
        <p class="small text-muted mb-3">
          ${escapeHtml(String(years))} Years Experience <br />
          Languages: ${escapeHtml(String(langs))}
        </p>
        <button class="btn btn-primary mt-auto" onclick="hireGuide('${guide.id ?? ""}', '${encodeURIComponent(name)}')">
          Hire Guide
        </button>
      </div>
    </div>
  `;
}

// Hire guide -> open Google search (same idea as select flight/car)
function hireGuide(guideId, encodedName) {
  const city = document.getElementById("cityInput").value.trim();
  const language = document.getElementById("languageSelect").value;
  const name = decodeURIComponent(encodedName || "");

  const searchQuery = `hire travel guide ${name} ${city} ${language}`.trim();
  const googleSearchUrl = `https://www.google.com/search?q=${encodeURIComponent(searchQuery)}`;

  window.open(googleSearchUrl, "_blank");
}

function renderStars(rating) {
  // 0..5
  const full = Math.max(0, Math.min(5, Math.floor(rating)));
  const half = rating - full >= 0.5 && full < 5 ? 1 : 0;
  const empty = 5 - full - half;

  return "★".repeat(full) + (half ? "½" : "") + "☆".repeat(empty);
}

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}