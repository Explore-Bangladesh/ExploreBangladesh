// package.js

const packageContainer = document.getElementById("packageContainer");
const budgetRange = document.getElementById("budgetRange");
const budgetValue = document.getElementById("budgetValue");
const searchInput = document.getElementById("searchDestination");

// Sample packages data
const packagesData = [
    {
        name: "Cox's Bazar Budget Trip",
        price: 15999,
        days: "3 Days / 2 Nights",
        description: "Relax on the world's longest sea beach with luxury hotel stay.",
        features: ["Hotel", "Transport", "Meals"],
        image: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
        badge: "Popular"
    },
    {
        name: "Sundarbans Adventure",
        price: 12499,
        days: "2 Days / 1 Night",
        description: "Explore the largest mangrove forest and spot Royal Bengal Tigers.",
        features: ["Boat Safari", "Guide", "Meals"],
        image: "https://images.unsplash.com/photo-1589308078059-be1415eab4c3?w=600",
        badge: "Trending"
    },
    {
        name: "Sylhet Tea Garden Escape",
        price: 18999,
        days: "4 Days / 3 Nights",
        description: "Enjoy scenic tea gardens, waterfalls, and beautiful hills.",
        features: ["Resort", "Sightseeing", "Breakfast"],
        image: "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=600",
        badge: "New"
    },
    {
        name: "Sajek Valley Cloud Tour",
        price: 14499,
        days: "3 Days / 2 Nights",
        description: "Witness clouds touching mountains in the beautiful Sajek Valley.",
        features: ["Resort", "Jeep Ride", "Meals"],
        image: "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=600",
        badge: ""
    },
    {
        name: "Bandarban Hill Track",
        price: 19499,
        days: "4 Days / 3 Nights",
        description: "Discover hills, waterfalls and tribal culture.",
        features: ["Trekking", "Resort", "Guide"],
        image: "https://images.unsplash.com/photo-1526779259212-939e64788e3c?w=600",
        badge: ""
    },
    {
        name: "Saint Martin Island Tour",
        price: 17999,
        days: "3 Days / 2 Nights",
        description: "Crystal clear water and coral island beauty.",
        features: ["Resort", "Ship Ride", "Meals"],
        image: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
        badge: ""
    },
    {
        name: "Rangamati Lake Retreat",
        price: 10999,
        days: "2 Days / 1 Night",
        description: "Boat ride on Kaptai Lake and hill scenery.",
        features: ["Boat", "Resort", "Meals"],
        image: "https://images.unsplash.com/photo-1518684079-3c830dcef090?w=600",
        badge: ""
    },
    {
        name: "Kuakata Sea Beach Trip",
        price: 13499,
        days: "3 Days / 2 Nights",
        description: "Watch sunrise and sunset from the same beach.",
        features: ["Hotel", "Sightseeing", "Meals"],
        image: "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600",
        badge: ""
    },
    {
        name: "Srimangal Nature Tour",
        price: 11999,
        days: "2 Days / 1 Night",
        description: "Visit tea estates and taste the famous seven-layer tea.",
        features: ["Resort", "Tour Guide", "Breakfast"],
        image: "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=600",
        badge: ""
    }
];

// Function to render packages
function renderPackages() {
    packageContainer.innerHTML = "";
    packagesData.forEach(pkg => {
        const col = document.createElement("div");
        col.className = "col-lg-4 col-md-6";
        col.innerHTML = `
            <div class="package-card">
                <div class="package-image">
                    <img src="${pkg.image}" alt="${pkg.name}" class="img-fluid">
                    ${pkg.badge ? `<div class="package-badge">${pkg.badge}</div>` : ""}
                    <div class="package-overlay">
                        <button onclick="bookPackage('${pkg.name}')">View Details</button>
                    </div>
                </div>
                <div class="package-body">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="badge bg-primary">${pkg.days}</span>
                        <span class="package-price">৳${pkg.price.toLocaleString()}</span>
                    </div>
                    <h5 class="package-title">${pkg.name}</h5>
                    <p class="package-description">${pkg.description}</p>
                    <div class="package-features mb-2">
                    ${pkg.features.map(f => `<span><i class="bi bi-check-circle-fill"></i> ${f}</span>`).join("")}
                   </div>
                   <button class="btn btn-primary w-100 mt-2" onclick="bookPackage('${pkg.name}')">Book Now</button>
                </div>
            </div>
        `;
        packageContainer.appendChild(col);
    });
}

// Initial render
renderPackages();

// Filter packages by budget and search
function filterPackages() {
    const maxBudget = parseInt(budgetRange.value) || Infinity;
    const searchText = searchInput.value.toLowerCase();

    packagesData.forEach((pkg, i) => {
        const col = packageContainer.children[i];
        const matchesBudget = pkg.price <= maxBudget;
        const matchesSearch = pkg.name.toLowerCase().includes(searchText);
        col.style.display = (matchesBudget && matchesSearch) ? "" : "none";
    });
}

// Event listeners
budgetRange.addEventListener("input", () => {
    budgetValue.textContent = `৳${parseInt(budgetRange.value).toLocaleString()}`;
    filterPackages();
});
searchInput.addEventListener("input", filterPackages);
document.querySelector(".search-bar button").addEventListener("click", filterPackages);

// Booking modal
let loggedIn = false;
function bookPackage(packageName){
    if(!loggedIn) {
        alert("Please login first. Demo login activated.");
        loggedIn = true;
    }
    document.getElementById("packageTitle").textContent = packageName;
    document.getElementById("bookingModal").style.display = "flex";
}
function submitBooking(event) {
    event.preventDefault();
    alert("Booking Confirmed! Thank you.");
    document.getElementById("bookingModal").style.display = "none";
}
window.onclick = function(event){
    const modal = document.getElementById("bookingModal");
    if(event.target === modal){
        modal.style.display = "none";
    }
};
// Initialize Leaflet Map
const map = L.map('map').setView([23.6850, 90.3563], 7);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// Destination markers
const destinations = [
    {name:"Cox's Bazar", coords:[21.4272,92.0058]},
    {name:"Sundarbans", coords:[21.9497,89.1833]},
    {name:"Sylhet", coords:[24.8949,91.8687]},
    {name:"Sajek Valley", coords:[23.3818,92.2932]},
    {name:"Bandarban", coords:[22.1953,92.2184]},
    {name:"Saint Martin", coords:[20.6230,92.3220]},
    {name:"Rangamati", coords:[22.7324,92.2985]},
    {name:"Kuakata", coords:[21.8167,90.1194]},
    {name:"Srimangal", coords:[24.3083,91.7333]}
];

destinations.forEach(d => {
    L.marker(d.coords)
        .addTo(map)
        .bindPopup(`<b>${d.name}</b>`);
});