// Sidebar functionality for maintaining scrollability
document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('sidebar');

    if (sidebar) {
        sidebar.addEventListener('show.bs.offcanvas', function () {
            document.body.classList.add('offcanvas-open');
        });

        sidebar.addEventListener('hide.bs.offcanvas', function () {
            document.body.classList.remove('offcanvas-open');
        });
    }

    initializeDestinationCarousel();
});

function initializeDestinationCarousel() {
    const carouselElement = document.getElementById('destinationHeroCarousel');
    if (!carouselElement || typeof bootstrap === 'undefined') return;

    bootstrap.Carousel.getOrCreateInstance(carouselElement, {
        interval: 5200,
        ride: 'carousel',
        pause: false,
        touch: true,
        wrap: true
    });

    const heroSearchContainer = document.querySelector('.search-container');
    const highlightsSection = document.getElementById('destinationHighlightsSection');
    const highlightsTitle = document.getElementById('destinationHighlightsTitle');
    const highlightsSubtitle = document.getElementById('destinationHighlightsSubtitle');
    const highlightsGrid = document.getElementById('destinationPlacesGrid');
    const viewAllWrap = document.getElementById('destinationViewAllWrap');
    const viewAllLink = document.getElementById('destinationViewAllLink');

    const localHeroByDestination = {
        'Dhaka': 'Assets/destinations/dhaka-hero.jpg',
        'Tangail': 'Assets/destinations/tangail-hero.jpg',
        "Cox's Bazar": 'Assets/destinations/coxs-bazar-hero.jpg',
        'Sylhet': 'Assets/destinations/sylhet-hero.jpg',
        'Rangamati': 'Assets/destinations/rangamati-hero.jpg',
        'Bandarban': 'Assets/destinations/bandarban-hero.jpg'
    };

    const destinationPlacesMap = {
        'Dhaka': [
            {
                name: 'Lalbagh Fort',
                location: 'Old Dhaka',
                image: 'Assets/places/lalbagh-fort.jpg',
                category: 'Heritage',
                description: 'A timeless Mughal landmark that gives Dhaka its old-soul grandeur and an instantly recognizable historic atmosphere.'
            },
            {
                name: 'Ahsan Manzil',
                location: 'Kumartoli, Old Dhaka',
                image: 'Assets/places/ahsan-manzil.jpg',
                category: 'Museum',
                description: 'The Pink Palace by the Buriganga remains one of Dhaka’s most iconic and photogenic heritage spots.'
            },
            {
                name: 'National Parliament House',
                location: 'Sher-e-Bangla Nagar',
                image: 'Assets/places/parliament-house.jpg',
                category: 'Architecture',
                description: 'A bold architectural masterpiece that gives the capital a modern, design-forward identity.'
            }
        ],
        'Tangail': [
            {
                name: 'Atia Mosque',
                location: 'Delduar, Tangail',
                image: 'Assets/places/atia-mosque.jpg',
                category: 'Heritage',
                description: 'One of Tangail’s standout heritage sites — historic, elegant, and deeply rooted in Bengal’s architectural story.'
            },
            {
                name: 'Madhupur National Park',
                location: 'Madhupur, Tangail',
                image: 'Assets/places/madhupur-national-park.jpg',
                category: 'Wildlife',
                description: 'A greener, quieter escape with forest landscapes that feel far from the city rush.'
            },
            {
                name: 'Jamuna Bridge Corridor',
                location: 'Tangail Region',
                image: 'Assets/places/jamuna-bridge-tangail.jpg',
                category: 'Nature',
                description: 'The mighty river corridor gives Tangail a bigger landscape feel, connecting open sky, water, and movement.'
            }
        ],
        "Cox's Bazar": [
            {
                name: "Cox's Bazar Sea Beach",
                location: "Cox's Bazar Town",
                image: 'Assets/places/coxs-bazar-sea-beach.jpg',
                category: 'Beach',
                description: 'The famous long sea beach remains the headline act — dramatic horizon, soft sand, and sunset energy.'
            },
            {
                name: 'Inani Beach',
                location: "27 km from Cox's Bazar",
                image: 'Assets/places/inani-beach.jpg',
                category: 'Beach',
                description: 'Rocky textures, cleaner blue tones, and a calmer vibe make Inani one of the coast’s prettiest stops.'
            },
            {
                name: 'Himchari National Park',
                location: "12 km from Cox's Bazar",
                image: 'Assets/places/himchari-national-park.jpg',
                category: 'Nature',
                description: 'A scenic break from the beach with elevated views, greenery, and a more adventurous coastal mood.'
            }
        ],
        'Sylhet': [
            {
                name: 'Ratargul Swamp Forest',
                location: 'Sylhet',
                image: 'Assets/places/ratargul-swamp-forest.jpg',
                category: 'Nature',
                description: 'A surreal watery forest landscape that makes Sylhet feel lush, mysterious, and completely different.'
            },
            {
                name: 'Jaflong',
                location: 'Goainghat, Sylhet',
                image: 'Assets/places/jaflong.jpg',
                category: 'Scenic',
                description: 'River, hills, stone beds, and distant border landscapes combine into one of Sylhet’s classic views.'
            },
            {
                name: 'Lalakhal',
                location: 'Jaintiapur, Sylhet',
                image: 'Assets/places/lalakhal.png',
                category: 'River',
                description: 'Soft blue-green water and a peaceful river atmosphere make Lalakhal feel instantly calming.'
            }
        ],
        'Rangamati': [
            {
                name: 'Kaptai Lake',
                location: 'Rangamati Sadar',
                image: 'Assets/places/kaptai-lake.jpg',
                category: 'Nature',
                description: 'The lake is the visual soul of Rangamati — open water, hill contours, and a quiet panoramic feel.'
            },
            {
                name: 'Hanging Bridge',
                location: 'Rangamati Town',
                image: 'Assets/places/rangamati-hanging-bridge.jpg',
                category: 'Landmark',
                description: 'A familiar local landmark that gives visitors an easy first taste of Rangamati’s hill-lake setting.'
            },
            {
                name: 'Shuvolong Waterfall',
                location: 'Rangamati Hill District',
                image: 'Assets/places/shuvolong-waterfall.jpg',
                category: 'Scenic',
                description: 'One of Rangamati’s most memorable natural stops, bringing together water, cliffs, and a deeper hill-country feel.'
            }
        ],
        'Bandarban': [
            {
                name: 'Nilgiri',
                location: 'Bandarban',
                image: 'Assets/places/nilgiri-bandarban.jpg',
                category: 'Hilltop',
                description: 'Bandarban at its most cinematic — high hills, clouds, and a strong sense of elevation and adventure.'
            },
            {
                name: 'Buddha Dhatu Jadi',
                location: 'Bandarban Town',
                image: 'Assets/places/buddha-dhatu-jadi.jpg',
                category: 'Religious',
                description: 'A serene spiritual landmark that adds quiet beauty and cultural texture to the Bandarban experience.'
            },
            {
                name: 'Nafakhum Waterfall',
                location: 'Bandarban Region',
                image: 'Assets/places/nafakhum-waterfall.jpg',
                category: 'Adventure',
                description: 'A stronger, wilder side of Bangladesh — trekking routes, dramatic landscapes, and serious explorer energy.'
            }
        ]
    };

    const categoryClassMap = {
        Heritage: 'heritage',
        Museum: 'heritage',
        Architecture: 'heritage',
        Beach: 'beach',
        Nature: 'nature',
        River: 'nature',
        Scenic: 'nature',
        Wildlife: 'wildlife',
        Religious: 'heritage',
        Landmark: 'heritage',
        Hilltop: 'nature',
        Adventure: 'wildlife'
    };

    window.exploreDestination = async function(destination) {
        if (!destination || !highlightsSection || !highlightsGrid) return;

        highlightsSection.classList.remove('d-none');
        highlightsTitle.textContent = `Beautiful places in ${destination}`;
        highlightsSubtitle.textContent = `Discover stunning spots, scenic locations, and must-visit attractions in ${destination}.`;
        highlightsGrid.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="text-muted mt-3 mb-0">Loading featured places for ${escapeHtml(destination)}...</p>
            </div>`;
        viewAllWrap.classList.add('d-none');

        try {
            const response = await fetch(`/api/places/nearby?location=${encodeURIComponent(destination)}&sortBy=rating`);
            if (!response.ok) throw new Error('Failed to load destination places');

            const data = await response.json();
            const apiPlaces = Array.isArray(data.places) ? data.places.slice(0, 6) : [];
            const normalizedPlaces = apiPlaces.map((place) => ({
                ...place,
                imageUrl: (window.getLocalPlaceImage ? window.getLocalPlaceImage(place.name, place.imageUrl) : (place.imageUrl || localHeroByDestination[destination] || 'Assets/green scenary.jpg'))
            }));
            const places = normalizedPlaces.length > 0 ? normalizedPlaces : (destinationPlacesMap[destination] || []);

            renderDestinationPlaces(destination, places);
        } catch (error) {
            const fallbackPlaces = destinationPlacesMap[destination] || [];
            renderDestinationPlaces(destination, fallbackPlaces);
        }

        setTimeout(() => {
            highlightsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 160);
    };

    function renderDestinationPlaces(destination, places) {
        if (!places || places.length === 0) {
            highlightsGrid.innerHTML = `
                <div class="col-12 text-center py-5">
                    <div class="destination-empty-state">
                        <i class="bi bi-geo-alt"></i>
                        <h4 class="mt-3">No highlighted places found</h4>
                        <p class="text-muted mb-0">Try another destination or explore all places from the nearby places page.</p>
                    </div>
                </div>`;
            viewAllLink.href = `places.html?location=${encodeURIComponent(destination)}`;
            viewAllLink.textContent = `Browse all places in ${destination}`;
            viewAllWrap.classList.remove('d-none');
            return;
        }

        highlightsGrid.innerHTML = places.map((place) => {
            const imageUrl = window.getLocalPlaceImage
                ? window.getLocalPlaceImage(place.name, place.imageUrl || place.image || localHeroByDestination[destination] || 'Assets/green scenary.jpg')
                : (place.imageUrl || place.image || localHeroByDestination[destination] || 'Assets/green scenary.jpg');
            const title = place.name || 'Beautiful Place';
            const location = place.distanceNote || place.location || place.upazila || destination;
            const description = place.description || `Discover one of the most beautiful attractions in ${destination}.`;
            const category = place.category || 'Featured';
            const rating = typeof place.rating === 'number' ? place.rating.toFixed(1) : null;
            const categoryClass = categoryClassMap[category] || 'default';

            return `
                <div class="col-md-6 col-xl-4">
                    <article class="destination-place-card h-100">
                        <div class="destination-place-media">
                            <img src="${escapeHtml(imageUrl)}" alt="${escapeHtml(title)}" loading="lazy"
                                 onerror="this.src='Assets/green scenary.jpg'">
                            <div class="destination-place-overlay"></div>
                            <span class="destination-place-badge ${categoryClass}">${escapeHtml(category)}</span>
                        </div>
                        <div class="destination-place-body">
                            <div class="destination-place-meta">
                                <span class="destination-place-location"><i class="bi bi-geo-alt-fill"></i> ${escapeHtml(location)}</span>
                                ${rating ? `<span class="destination-place-rating"><i class="bi bi-star-fill"></i> ${rating}</span>` : ''}
                            </div>
                            <h3>${escapeHtml(title)}</h3>
                            <p>${escapeHtml(description)}</p>
                            <div class="destination-place-footer">
                                <span class="destination-chip"><i class="bi bi-compass"></i> Popular stop</span>
                                <a class="destination-place-link" href="places.html?location=${encodeURIComponent(destination)}">See more <i class="bi bi-arrow-up-right"></i></a>
                            </div>
                        </div>
                    </article>
                </div>`;
        }).join('');

        viewAllLink.href = `places.html?location=${encodeURIComponent(destination)}`;
        viewAllLink.textContent = `View all places in ${destination}`;
        viewAllWrap.classList.remove('d-none');
    }

    carouselElement.addEventListener('slid.bs.carousel', function (event) {
        const activeSlide = event.relatedTarget;
        const destination = activeSlide?.dataset?.destination;
        const title = activeSlide?.dataset?.title;
        const subtitle = activeSlide?.dataset?.subtitle;
        const heroEyebrow = document.getElementById('heroActiveDestination');
        const heroTitle = document.getElementById('heroActiveTitle');
        const heroSubtitle = document.getElementById('heroActiveSubtitle');

        if (destination && heroEyebrow) heroEyebrow.textContent = destination;
        if (title && heroTitle) heroTitle.textContent = title;
        if (subtitle && heroSubtitle) heroSubtitle.textContent = subtitle;
    });
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}
