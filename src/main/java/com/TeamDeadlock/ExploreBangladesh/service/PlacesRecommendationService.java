package com.TeamDeadlock.ExploreBangladesh.service;

import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse;
import com.TeamDeadlock.ExploreBangladesh.dto.PlaceRecommendationResponse.Place;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlacesRecommendationService {

    private static final Map<String, List<Place>> LOCATION_PLACES = new HashMap<>();
    
    // Default messages for unavailable services  
    private static final String NO_HOTELS_MSG = "No dedicated hotels at this location. Check nearby towns or use our Hotels page to search.";
    private static final String NO_CARS_MSG = "Limited car rental services. Book through our Cars page or arrange transport from major cities.";
    private static final String NO_GUIDES_MSG = "Local guides available upon request. Contact tourism office or book through our Travel Guide page.";

    // Helper to generate "How to Go" instructions based on location
    private static String generateHowToGo(String distanceNote, String cityName) {
        if (distanceNote == null) distanceNote = "";
        distanceNote = distanceNote.toLowerCase();
        
        // Parse distance if mentioned
        if (distanceNote.contains("km from")) {
            String city = "the city";
            if (distanceNote.contains("dhaka")) city = "Dhaka";
            else if (distanceNote.contains("chittagong")) city = "Chittagong";
            else if (distanceNote.contains("sylhet")) city = "Sylhet";
            else if (distanceNote.contains("cox")) city = "Cox's Bazar";
            else if (distanceNote.contains("rajshahi")) city = "Rajshahi";
            else if (distanceNote.contains("khulna")) city = "Khulna";
            else if (distanceNote.contains("barishal")) city = "Barishal";
            else if (distanceNote.contains("rangpur")) city = "Rangpur";
            else if (distanceNote.contains("gazipur")) city = "Gazipur";
            else if (distanceNote.contains("narayanganj")) city = "Narayanganj";
            
            return String.format("From %s: Take local bus/CNG (৳50-150, 1-2 hrs). Private car/taxi recommended (৳800-2000). " +
                    "Uber/Pathao available in major cities. Check Google Maps for detailed route.", city);
        } else if (distanceNote.contains("city center") || distanceNote.contains("town")) {
            return "Located in city center. Accessible by rickshaw/CNG (৳30-80, 10-20 min). " +
                    "Walking distance from main areas. Uber/Pathao available. Local buses frequent (৳15-30).";
        } else if (distanceNote.contains("old dhaka") || distanceNote.contains("old")) {
            return "In Old Dhaka area: Best reached by rickshaw (৳80-150, 30-40 min from center). " +
                    "CNG/auto-rickshaw to nearby point then walk. Uber/Pathao to main roads. Consider guided tour.";
        } else {
            return "Accessible by local transport (bus/CNG/auto-rickshaw: ৳50-150). " +
                    "Private car rental available (৳1500-3000/day). Check local transport apps or Google Maps for best route.";
        }
    }

    // Helper to build a Place object (basic version for backwards compatibility)
    private static Place makePlace(String id, String name, String category,
                                   String description, String imageUrl,
                                   double rating, String distanceNote,
                                   String... highlights) {
        Place p = new Place();
        p.setId(id);
        p.setName(name);
        p.setCategory(category);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setRating(rating);
        p.setDistanceNote(distanceNote);
        p.setHighlights(Arrays.asList(highlights));
        // Set intelligent default values for new fields
        p.setHowToGo(generateHowToGo(distanceNote, name));
        p.setNearbyHotels(NO_HOTELS_MSG);
        p.setNearbyCarRentals(NO_CARS_MSG);
        p.setAvailableGuides(NO_GUIDES_MSG);
        return p;
    }

    // Enhanced helper with additional metadata
    private static Place makePlaceEnhanced(String id, String name, String category,
                                          String description, String imageUrl,
                                          double rating, String distanceNote,
                                          double latitude, double longitude,
                                          String entranceFee, String bestTimeToVisit,
                                          String openingHours, int estimatedDuration,
                                          String accessibility,
                                          String... highlights) {
        Place p = new Place();
        p.setId(id);
        p.setName(name);
        p.setCategory(category);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setRating(rating);
        p.setDistanceNote(distanceNote);
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        p.setEntranceFee(entranceFee);
        p.setBestTimeToVisit(bestTimeToVisit);
        p.setOpeningHours(openingHours);
        p.setEstimatedDuration(estimatedDuration);
        p.setAccessibility(accessibility);
        p.setHighlights(Arrays.asList(highlights));
        // Set intelligent default values for new fields
        p.setHowToGo(generateHowToGo(distanceNote, name));
        p.setNearbyHotels(NO_HOTELS_MSG);
        p.setNearbyCarRentals(NO_CARS_MSG);
        p.setAvailableGuides(NO_GUIDES_MSG);
        return p;
    }
    
    // Fully comprehensive helper with all travel details
    private static Place makePlaceComplete(String id, String name, String category,
                                          String description, String imageUrl,
                                          double rating, String distanceNote,
                                          double latitude, double longitude,
                                          String entranceFee, String bestTimeToVisit,
                                          String openingHours, int estimatedDuration,
                                          String accessibility,
                                          String upazila, String howToGo,
                                          String hotels, String carRentals, String guides,
                                          String... highlights) {
        Place p = new Place();
        p.setId(id);
        p.setName(name);
        p.setCategory(category);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setRating(rating);
        p.setDistanceNote(distanceNote);
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        p.setEntranceFee(entranceFee);
        p.setBestTimeToVisit(bestTimeToVisit);
        p.setOpeningHours(openingHours);
        p.setEstimatedDuration(estimatedDuration);
        p.setAccessibility(accessibility);
        p.setUpazila(upazila);
        p.setHowToGo(howToGo);
        p.setNearbyHotels(hotels);
        p.setNearbyCarRentals(carRentals);
        p.setAvailableGuides(guides);
        p.setHighlights(Arrays.asList(highlights));
        return p;
    }

    static {

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║  DHAKA DIVISION                                                  ║
        // ╚══════════════════════════════════════════════════════════════════╝

        // ── Dhaka ─────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("dhaka", Arrays.asList(
            makePlaceComplete("dhk-1", "Lalbagh Fort", "Heritage",
                "A 17th-century Mughal fort on the Buriganga River featuring the tomb of Pari Bibi and the Diwan-i-Aam — a must-visit landmark of Old Dhaka.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600", 4.8, "Old Dhaka",
                23.7190, 90.3869, "৳20 (local), ৳200 (foreign)", "Oct-Mar (Cool Season)", 
                "9:00 AM - 5:00 PM (Closed Sunday)", 120, "Wheelchair accessible main areas",
                "Lalbagh", // Upazila
                "From Gulistan: Take a rickshaw/CNG (15 min, ৳50-80). From Motijheel: Bus/rickshaw to Chawk Bazaar (20 min). Located near Azimpur area, easily accessible via Old Dhaka routes.",
                "Hotels near Lalbagh: Hotel Al-Razzaque International (₪), Lakeshore Hotel Dhaka (₪₪), or stay in Gulistan area (10 min away). Budget guesthouses available in Azimpur.",
                "Car rentals available through Pathao Prime, Obhai, or local agencies near Gulistan (₹500-1000/day). Airport pickup also available.",
                "Licensed guides available at fort entrance (₹300-500 for 1-2 hrs). Book heritage walks through 'Dhaka Heritage Tours' or 'Guide Tours BD'.",
                "History", "Photography", "Architecture"),
            makePlaceComplete("dhk-2", "Ahsan Manzil (Pink Palace)", "Heritage",
                "The iconic Pink Palace on the Buriganga riverfront, home to the Nawabs of Dhaka and now a national museum packed with history.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.8, "Old Dhaka",
                23.7088, 90.4053, "৳30 (local), ৳100 (foreign)", "Oct-Mar", 
                "10:30 AM - 5:30 PM (Closed Thursday)", 90, "Limited accessibility",
                "Kotwali", // Upazila
                "From Sadarghat: 10 min walk along the river. From Gulistan: Rickshaw/CNG to Islampur (20 min, ৳60-100). Boat access also available from Sadarghat.",
                "Stay in Old Dhaka: Hotel Star (₪), Hotel Orbit (₪₪) in Sadarghat area. More options in Motijheel (15 min away).",
                "Local car services: Uber/Pathao to Old Dhaka. Private cars can be hired from Gulistan area (₹800-1500/day).",
                "Museum guides available inside (₹200-400). Old Dhaka walking tour guides at Sadarghat (₹500-800 for combined heritage tour).",
                "Museum", "History", "Architecture"),
            makePlaceComplete("dhk-3", "Star Mosque (Tara Masjid)", "Religious",
                "An exquisitely decorated mosque adorned with Chinese porcelain tiles forming star patterns — a unique gem of Old Dhaka.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.7, "Old Dhaka",
                23.7170, 90.3973, "Free", "Year-round", 
                "Prayer times (respectful visits outside prayers)", 30, "Ground floor accessible",
                "Kotwali", // Upazila
                "Located in Armanitola, Old Dhaka. From Sadarghat: 15 min rickshaw. From Gulistan: CNG to Armanitola (15 min, ৳50-70). Walk through bustling old bazaars.",
                "Limited hotels in immediate area. Stay in Sadarghat/Gulistan area (15-20 min). Guesthouse options near Dhaka University.",
                NO_CARS_MSG,
                "Local community guides available near mosque entrance (optional donation). Heritage tour guides include this in Old Dhaka circuit.",
                "Architecture", "Religious", "Photography"),
            makePlaceComplete("dhk-4", "National Parliament House", "Heritage",
                "Louis Kahn's masterpiece of 20th-century architecture — arguably the most important building in Bangladesh.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.9, "Sher-e-Bangla Nagar",
                23.7625, 90.3776, "Free (advance booking required)", "Oct-Apr", 
                "By appointment only", 60, "Fully accessible",
                "Sher-e-Bangla Nagar", // Upazila
                "From Farmgate: Bus #6, #48 or rickshaw (15 min). From Mirpur: Bus to Manik Mia Avenue (20 min). Metro: Upcoming station nearby. Uber/Pathao recommended.",
                "Hotels in Sher-e-Bangla Nagar: The Westin Dhaka (₪₪₪₪), Le Meridien (₪₪₪₪). Mid-range in Farmgate/Mohakhali (10 min away).",
                "All major car rental agencies available: Europcar, Sakura Rent-a-Car, local agencies near Farmgate (₹1000-2000/day).",
                "Prior booking required for guided tours through Parliament Secretariat. Architecture students often offer tours (₹500-1000).",
                "Architecture", "National Landmark", "Photography"),
            makePlaceComplete("dhk-5", "National Museum of Bangladesh", "Heritage",
                "The largest museum in Bangladesh with over 83,000 artifacts spanning art, history, natural history and ethnography.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.6, "Shahbagh",
                23.7378, 90.3950, "৳20 (local), ৳100 (foreign)", "Oct-Mar",
                "10:30 AM - 5:30 PM (Closed Sun, Mon)", 120, "Wheelchair accessible",
                "Shahbagh", // Upazila
                "Located at Shahbagh intersection. From Dhaka University: Walking distance. From Gulistan: Bus #3, #8 or rickshaw (15 min). Metro station planned.",
                "Shahbagh hotels: Hotel Sarina (₪₪), Pan Pacific Sonargaon (₪₪₪). Many guesthouses near Dhaka University (5-10 min walk).",
                "Car rentals: Tiger Rent-a-car in Kawran Bazar (15 min). Pathao/Uber readily available. Airport transfers bookable.",
                "Museum audio guides (₹100). Professional guides at entrance (₹400-600). Student guides from Dhaka University available.",
                "Museum", "Education", "Art"),
            makePlaceComplete("dhk-6", "Shaheed Minar", "Heritage",
                "Bangladesh's national language movement monument — a symbol of cultural pride and the struggle for the Bangla language.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.9, "Dhaka University area",
                23.7356, 90.3984, "Free", "Year-round",
                "24/7 (best visited morning or evening)", 30, "Fully accessible",
                "Shahbagh", // Upazila
                "Inside Dhaka Medical College campus. From Shahbagh: 5 min walk. Accessible via Dhaka University area. Any Shahbagh-bound transport.",
                "Hotels in Shahbagh/Dhaka University area. Many budget options in nearby Old Dhaka and Nilkhet area.",
                NO_CARS_MSG + " Easily accessible on foot from central Dhaka.",
                "Student guides from DU History Dept available (₹200-400). Local volunteers during February (Language Movement month).",
                "History", "National Symbol", "Monument"),
            makePlaceComplete("dhk-7", "Liberation War Museum", "Heritage",
                "A powerful museum documenting the 1971 Liberation War through artifacts, photographs and testimonies.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.8, "Segunbagicha",
                23.7390, 90.3977, "₹20 (locals) / ₹50 (foreigners)", "Oct-Mar",
                "10:00 AM - 5:00 PM (Sun-Thu, Closed Fri-Sat)", 120, "Fully accessible",
                "Ramna", // Upazila
                "From Dhaka center: Take bus/taxi to Segunbagicha (15 min, ৳30-50). Uber/Pathao available (৳150-250). Walking distance from Ramna Park.",
                "Nearby hotels: Hotel 71 (₪₪₪), Dhaka Regency (₪₪₪₪), Hotel Pacific Inn (₪₪). Many options in nearby Shahbagh area.",
                "Car rental: Uber/Pathao/CNG auto-rickshaw easily available. Dhaka Rent-A-Car (01711-XXXXXX) for full-day rental (৳2500-4000).",
                "Professional tour guides available at entrance (৳400-600 for 1.5 hrs). Audio guide available (৳100). Book group tours: Bangladesh Tourism Board.",
                "Museum", "History", "Education"),
            makePlaceComplete("dhk-8", "Dhakeshwari National Temple", "Religious",
                "The national Hindu temple of Bangladesh, dedicated to Goddess Dhakeshwari — the deity from whom Dhaka takes its name.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.6, "Old Dhaka",
                23.7214, 90.3839, "Free (donations welcome)", "Year-round",
                "6:00 AM - 12:00 PM, 4:00 PM - 9:00 PM (Daily)", 60, "Remove shoes before entering",
                "Lalbagh", // Upazila
                "From Dhaka center: Rickshaw/CNG to Dhakeshwari (15 min, ৳50-100). Bus from Gulistan to Lalbagh area (৳20-30). Uber/Pathao (৳150-250).",
                "Nearby: Hotel Abakash (₪₪), Lalbagh area guesthouses. Central Dhaka hotels 20 min away: Pan Pacific Sonargaon (₪₪₪₪₪).",
                "CNG/auto widely available (৳50-80 local). Car rental: Dhaka City Tours (01711-XXXXXX) - ৳3000/day. Rickshaw from Lalbagh Fort (৳30-50).",
                "Temple priests provide history (donations). Hindu heritage tours: Heritage Dhaka (01798-XXXXXX) - ৳700/person. Photography respectfully allowed.",
                "Religious", "History", "Cultural"),
            makePlaceComplete("dhk-9", "Baldha Garden", "Nature",
                "One of the oldest botanical gardens in the subcontinent (1909), housing rare plant species from around the world.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.4, "Wari, Old Dhaka",
                23.7200, 90.4100, "₹10 entry", "Oct-Mar (cooler months)",
                "9:00 AM - 5:00 PM (Daily)", 90, "Limited accessibility",
                "Wari", // Upazila
                "From Dhaka center: Auto-rickshaw/CNG to Wari (20 min, ৳80-120). Bus from Gulistan to Wari (৳20-30). Uber/Pathao (৳200-300).",
                "Nearby: Hotel Golden Deer (₪₪), Hotel Ruposhi Bangla (₪₪₪₪). More options in Motijheel area (2 km away).",
                "Local CNG available everywhere. Reserve car: Green Bangla Tours (01798-XXXXXX) - ৳3000/day. Rickshaw for local travel (৳50-100).",
                "Local botanical guides available at entrance (৳300-500/90 min). Nature photography tours: Nature Explorers BD (01711-XXXXXX).",
                "Botanical Garden", "Nature", "Photography"),
            makePlaceComplete("dhk-10", "Saat Gombuj Mosque", "Religious",
                "A 17th-century Mughal mosque with seven domes built by Shaista Khan — a serene oasis in the city.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.5, "Mohammadpur",
                23.7650, 90.3635, "Free", "Year-round",
                "Open for prayers - Respectful visits outside prayer times", 45, "Remove shoes, dress modestly",
                "Mohammadpur", // Upazila
                "From Dhaka center: Bus to Mohammadpur (30 min, ৳25-40). CNG from Dhanmondi/Farmgate (৳80-120). Uber/Pathao (৳200-300).",
                "Nearby: Hotel Ornate (₪₪₪), Mohammadpur residential hotels. Better options in Dhanmondi (2 km): Le Méridien (₪₪₪₪₪).",
                "CNG abundant (৳60-100 to Dhanmondi). Bus to city center (৳25-40). Rent car: Mohammadpur Car Rental (01552-XXXXXX) - ৳2500/day.",
                "Mosque caretakers provide basic info. Islamic heritage tours: Old Dhaka Heritage (01711-XXXXXX). Photography from outside only.",
                "History", "Religious", "Architecture"),
            makePlaceComplete("dhk-11", "Curzon Hall", "Heritage",
                "A grand colonial-era building (1904) with Indo-Saracenic architecture, now part of Dhaka University.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.5, "Dhaka University campus",
                23.7276, 90.3947, "Free (respectful campus visit)", "Oct-Mar",
                "Campus open daily - Building exterior viewable", 45, "University campus",
                "Shahbagh", // Upazila
                "From Dhaka center: Walk from Shahbagh (10 min). Rickshaw from Nilkhet/TSC (৳20-30). Bus to Shahbagh (৳15-25). Uber/Pathao (৳100-180).",
                "Nearby: Hotel 71 (₪₪₪), Star Hotel (₪₪). University area: Dhaka Regency (₪₪₪₪). Many budget hostels for students.",
                "Rickshaw best for campus tour (৳50-100/hr). CNGs to Shahbagh gate (৳80). Walking from Ramna Park. Uber/Pathao available.",
                "University students often give informal tours. DU Heritage Club tours (01711-XXXXXX) - ৳500/group. Photography allowed from outside.",
                "Architecture", "Heritage", "Photography"),
            makePlaceComplete("dhk-12", "Rose Garden Palace", "Heritage",
                "A 1930s art-deco mansion famous as the birthplace of the Awami League — a stunning heritage building.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600", 4.4, "Tikatuli, Old Dhaka",
                23.7185, 90.4070, "₹50 entry", "Nov-Feb",
                "10:00 AM - 5:00 PM (Closed Mon)", 60, "Partially accessible",
                "Kotwali", // Upazila
                "From Dhaka center: Rickshaw/CNG from Gulistan to Tikatuli (15 min, ৳50-80). Bus to K.M. Das Lane stop (৳20). Uber/Pathao (৳180-250).",
                "Nearby: Hotel Star (₪₪), Abakash Hotel & Resort (₪₪₪). Historic Old Dhaka guesthouses: Heritage Guest House (₪₪).",
                "CNG/auto-rickshaw widely available (৳40-60 local trips). Car rental: Dhaka Heritage Tours (01552-XXXXXX) - half day ৳2000.",
                "Heritage guides at entrance (৳350-500). Historical walking tours including this palace: Old Dhaka Walks (01711-XXXXXX) - ৳800/person.",
                "Heritage", "History", "Architecture"),
            makePlaceComplete("dhk-13", "Buriganga River Cruise", "Nature",
                "Sail on the ancient Buriganga River around Old Dhaka — witness the city's historic waterfront and bustling port at dusk.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.5, "Sadarghat, Old Dhaka",
                23.7104, 90.4074, "Boat charter ৳500-2000 (1-2 hrs)", "Oct-Mar (sunset 5:30 PM)",
                "Best time: 4:00 PM - 7:00 PM", 120, "Boat access",
                "Kotwali", // Upazila
                "From Dhaka: Rickshaw/CNG to Sadarghat Launch Terminal (30 min from center, ৳80-150). Bus from Gulistan (৳15-25). Walk from Lalbagh Fort (20 min).",
                "Old Dhaka: Hotel Star (₪₪), River View Guest House (₪). Better hotels in Motijheel (3 km): Hotel Purbani (₪₪₪).",
                "Boats hired at Sadarghat ghat (৳500-2000). CNG to ghat (৳80-150). Combined city tour: Old Dhaka Tours (01798-XXXXXX) - ৳3500 with transport.",
                "Boat operators provide commentary (৳100-200 tip). Sunset photography tours: Dhaka River Tours (01711-XXXXXX) - ৳1200/person including boat.",
                "River Cruise", "Photography", "Sunset"),
            makePlaceComplete("dhk-14", "Jatiyo Smriti Soudho (National Martyrs' Memorial)", "Heritage",
                "A majestic 45-metre tall memorial in Savar dedicated to the martyrs of the 1971 Liberation War.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.9, "Savar, 35 km from Dhaka",
                23.9109, 90.2741, "Free", "Oct-Mar", 
                "6:00 AM - 6:00 PM (Daily)", 90, "Fully accessible",
                "Savar", // Upazila
                "From Dhaka: Bus from Gabtoli terminal to Savar (45 min, ৳50-80). Or hire private car/CNG from Dhaka (1 hr, ৳500-800). Uber/Pathao available.",
                "Hotels in Savar: Hotel Star International (₪₪), Parjatan Motel. More options in Dhaka city (35 km, 1 hr drive).",
                "Car rentals: Book from Dhaka agencies for day trip (₹1500-2500). Local CNGs available at Savar bus stand.",
                "Guides available at memorial entrance (₹300-500). Group tours bookable from Dhaka travel agencies.",
                "Monument", "History", "Architecture"),
            makePlaceComplete("dhk-15", "Sonargaon Panam City", "Heritage",
                "A ghost-town of 52 19th-century merchant mansions — once the capital of Bengal, surreal and hauntingly beautiful.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600", 4.8, "29 km from Dhaka",
                23.6476, 90.6112, "₹100 (includes Folk Museum)", "Nov-Feb",
                "9:00 AM - 5:00 PM (Daily)", 150, "Partially accessible (ruins)",
                "Sonargaon", // Upazila (Narayanganj district)
                "From Dhaka: Bus from Gulistan to Mograpara (1.5 hrs, ৳60-80). Direct bus to Sonargaon (৳80). Private car/taxi (1 hr, ৳800-1200). Train to Narayanganj then local transport.",
                "Sonargaon Resort (₪₪₪) on-site. Hotel Golden Tower Narayanganj (₪₪). Day trip recommended - return to Dhaka accommodations.",
                "Rent car with driver from Dhaka (৳2500-3500/day). Local auto from Mograpara stand (৳150-200). Bicycle rental at Folk Museum (৳100/hr).",
                "Certified guides at entrance (৳500-700 for 2 hrs). Photography tours: Bengal Heritage Tours (01711-XXXXXX) - ৳1200/person including transport from Dhaka.",
                "Photography", "History", "Architecture"),
            makePlaceComplete("dhk-16", "Dhaka Zoo & Botanical Garden", "Wildlife",
                "Home to over 2,000 animals and a large botanical collection — one of the most popular family destinations in Dhaka.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.3, "Mirpur, Dhaka",
                23.8103, 90.3660, "₹50 adults / ₹20 children", "Nov-Feb (cooler)",
                "9:00 AM - 6:00 PM (Daily)", 180, "Family-friendly, wheelchair accessible",
                "Mirpur", // Upazila
                "From Dhaka center: Bus to Mirpur-10 (45 min, ৳30-50). Direct bus from Gabtoli/Mohakhali. Uber/Pathao to zoo gate (৳250-400). CNG from Mirpur-10 (৳80).",
                "Nearby: Hotel Athena (₪₪), Mirpur Garden Hotel (₪₪). Family options: Adventure Suites Mirpur (₪₪₪). Central Dhaka hotels 30 min away.",
                "Parking available (৳50). Uber/Pathao for return. Local CNG to Mirpur shopping areas (৳50-80). Rent car: Dhaka Easy Rental (01912-XXXXXX) - ৳3000/day.",
                "No formal guides but zoo staff help visitors. Educational tours for kids: Dhaka Zoo Education Dept (02-9003571). Photography allowed everywhere.",
                "Wildlife", "Family Fun", "Nature"),
            makePlaceComplete("dhk-17", "Shankhari Bazaar (Conch Shell Alley)", "Cultural",
                "A 400-year-old Hindu artisan street in Old Dhaka famous for intricate conch shell carvings and traditional crafts.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.4, "Old Dhaka",
                23.7125, 90.4089, "Free (shopping optional)", "Year-round",
                "9:00 AM - 7:00 PM (Daily)", 90, "Narrow lanes, walking tour",
                "Kotwali", // Upazila
                "From Dhaka: Rickshaw to Shankhari Bazaar via Lalbagh (30 min from center, ৳80-120). Walk from Buriganga ghat (15 min). CNG to nearby area (৳100-150).",
                "Old Dhaka guesthouses: Heritage Guest House (₪₪), Hotel Star (₪₪). Central Dhaka hotels 30 min away.",
                "Access by rickshaw/walking only (lanes too narrow for cars). Hire walking guide with transport: Old Dhaka Walks (01711-XXXXXX) - ৳800/person.",
                "Local artisan guides (৳300-500 for 1.5 hrs). Craft workshops available. Heritage walking tours: Dhaka Heritage Tours (01798-XXXXXX).",
                "Craft", "Culture", "Photography"),
            makePlaceComplete("dhk-18", "Karwan Bazaar Fish Market", "Cultural",
                "Dhaka's largest wholesale market — an overwhelming sensory experience exploding with colours and activity at dawn.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", 4.2, "Karwan Bazaar",
                23.7500, 90.3910, "Free", "Year-round (Best: Early morning)",
                "5:00 AM - 9:00 AM (Peak activity)", 60, "Wet market, wear appropriate shoes",
                "Tejgaon", // Upazila
                "From Dhaka center: CNG to Karwan Bazaar (15 min, ৳60-100). Bus from Shahbagh/Farmgate (৳15-25). Uber/Pathao (৳150-250). Walk from Karwan Bazar metro station.",
                "Nearby: Hotel Rivera (₪₪), Capital Hotel (₪₪). Close to business district: Pan Pacific Sonargaon (₪₪₪₪₪) 2 km away.",
                "Best arrive by taxi/Uber early morning (৳150-250). CNG from Farmgate (৳60-100). Return transport difficult during rush hour.",
                "Street photography guides: Dhaka Urban Photo Tours (01711-XXXXXX) - ৳1000/person. Go with local guide recommended for navigation.",
                "Market", "Culture", "Photography"),
            makePlaceComplete("dhk-19", "Gulshan Lake & Avenue", "Nature",
                "A peaceful lake surrounded by Dhaka's diplomatic zone — great for evening walks and people-watching.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600", 4.2, "Gulshan",
                23.7925, 90.4078, "Free", "Year-round (Best: Evening)",
                "Open 24/7 - Best time 5:00 PM - 8:00 PM", 90, "Paved walkways",
                "Gulshan", // Upazila
                "From Dhaka center: Bus to Gulshan (30 min, ৳30-50). CNG from Mohakhali/Banani (৳100-150). Uber/Pathao (৳200-350). Walk from Gulshan metro station.",
                "Gulshan area: The Westin Dhaka (₪₪₪₪₪), Hotel Sarina (₪₪₪₪), bnaani Park Residences (₪₪₪). Many upscale restaurants nearby.",
                "Uber/Pathao readily available (৳150-300 to center). CNG from Gulshan Circle (৳100-200). Walking distance to Gulshan-1 shops/restaurants.",
                "No formal guides. Walking clubs meet here. Nearby cafes: North End Coffee (great views). Bird watching early morning.",
                "Lake", "Walking", "Relaxation"),
            makePlaceComplete("dhk-20", "Nizam-ud-Daula Mosque (Chawk Masjid)", "Religious",
                "A 17th-century mosque in the heart of Old Dhaka's spice market — one of the most atmospheric prayer spaces in the city.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.3, "Chawkbazaar, Old Dhaka",
                23.7156, 90.4156, "Free", "Year-round",
                "Open for prayers - Visit outside prayer times", 30, "Remove shoes, modest dress",
                "Kotwali", // Upazila
                "From Dhaka: Rickshaw through Old Dhaka to Chawkbazaar (40 min from center, ৳100-150). CNG to nearby area, then walk (৳120-180). Best with guide.",
                "Old Dhaka: Hotel Star (₪₪), Abakash Hotel (₪₪₪). Spice market area guesthouses. Better hotels in Motijheel (4 km).",
                "Rickshaw through narrow lanes (৳100-150). Walking tour recommended. Old Dhaka heritage tour with transport: ৳3000-4000/group.",
                "Mosque imam provides brief history. Combined old Dhaka walking tour: Old Dhaka Heritage (01711-XXXXXX) - ৳700/person 3-4 stops.",
                "Religious", "Heritage", "Architecture"),
            makePlaceComplete("dhk-21", "Ramna Park", "Nature",
                "Dhaka's oldest and largest urban park — beloved for Pahela Boishakh celebrations and peaceful morning walks.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.4, "Shahbagh, Dhaka",
                23.7383, 90.3988, "Free", "Year-round (Special: Apr 14 - Pahela Boishakh)",
                "5:00 AM - 9:00 PM (Daily)", 90, "Paved paths, benches",
                "Ramna", // Upazila
                "From Dhaka center: Walk from Shahbagh/Press Club (5 min). Rickshaw from TSC/Dhanmondi (৳30-50). Bus to Shahbagh (৳15-25). Uber/Pathao (৳100-200).",
                "Adjacent: Hotel 71 (₪₪₪), Dhaka Regency (₪₪₪₪). Walking distance: Pan Pacific Sonargaon Hotel (₪₪₪₪₪). Many nearby hotels in Shahbagh.",
                "Uber/Pathao widely available (৳100-250 to destinations). Rickshaw from park gates (৳30-80). Walking distance to Parliament, museums.",
                "Morning walking groups welcome visitors. Cultural programs during Pahela Boishakh. Bird watching clubs: Dhaka Bird Club. No formal guides needed.",
                "Park", "Walking", "Culture")
        ));

        // ── Gazipur ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("gazipur", Arrays.asList(
            makePlaceComplete("gzp-1", "Bangabandhu Safari Park", "Wildlife",
                "Bangladesh's only official safari park with open-range enclosures for tigers, lions, zebras and giraffes.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.6, "40 km from Dhaka",
                24.0914, 90.3914, "₹300 adults / ₹150 children (Safari Bus +₹100)", "Nov-Mar",
                "9:00 AM - 5:00 PM (Closed Mon)", 210, "Family-friendly",
                "Gazipur Sadar", // Upazila
                "From Gazipur town: Bus to Safari Park (45 min, ৳50-70). From Dhaka: Bus from Mohakhali to Safari Park stop (1.5 hrs, ৳80-100). Private car from Dhaka via Joydevpur Road (1 hr, ৳1000-1500).",
                "On-site: Safari Park Resort (₪₪). Nearby Gazipur: Hotel Al-Rafi (₪₪), Hotel Star International (₪₪₪). Day trip from Dhaka recommended.",
                "Rent car with driver from Dhaka (৳3500-4500/day). Gazipur local cars available (৳2000-3000/day). Safari bus included in ticket.",
                "Wildlife guides on safari bus (included). Photography tours: Wild Bangladesh (01711-XXXXXX) - ৳1500/group. Educational programs for schools available.",
                "Safari", "Wildlife", "Family Fun"),
            makePlaceComplete("gzp-2", "Bhawal National Park", "Wildlife",
                "A large forested park near Dhaka offering a refreshing escape — great for picnics and nature walks.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.4, "40 km from Dhaka",
                24.1897, 90.4283, "₹50 adults / ₹20 children", "Oct-Mar",
                "6:00 AM - 6:00 PM (Daily)", 180, "Walking trails, picnic spots",
                "Gazipur Sadar", // Upazila
                "From Gazipur town: Auto-rickshaw to park gate (30 min, ৳100-150). From Dhaka: Bus from Mohakhali to Rajendrapur, then local transport (1.5 hrs total, ৳100). Private car via Gazipur Road (1 hr, ৳800-1200).",
                "Near park: Rajendrapur Cottages (₪₪). Nearby: Hotel Grand Sultan (₪₪), Bhawal Resort (₪₪₪). Picnic pavilions available inside park.",
                "Car rental from Gazipur: ৳2500-3500/day. Auto-rickshaw for local movement (৳200-400 park tour). Bicycle rental at entrance (৳150/3 hrs).",
                "Forest guides at entrance (৳400-600/2 hrs). Bird watching tours: Bangladesh Bird Club (01552-XXXXXX). Nature photography groups welcome.",
                "Nature Walk", "Picnic", "Biodiversity"),
            makePlaceComplete("gzp-3", "Nuhash Palli", "Cultural",
                "The private estate of legendary author Humayun Ahmed — a literary-inspired garden paradise with sculptures and film sets.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.7, "40 km from Dhaka",
                24.0103, 90.4800, "₹100 entry (support maintenance)", "Oct-Apr",
                "9:00 AM - 5:00 PM (Fri-Wed, Limited visitors)", 120, "Beautiful grounds",
                "Gazipur Sadar", // Upazila  
                "From Gazipur town: Private rickshaw/auto to Pirulia (40 min, ৳200-300). From Dhaka: Bus to Gazipur then auto to Nuhash Palli (2 hrs total, ৳150). Private car recommended (1.5 hrs from Dhaka, ৳1200-1800).",
                "On-site: Guesthouse (advance booking required). Nearby: Gazipur city hotels. Literary fans often do day trip from Dhaka.",
                "Best by private car from Dhaka (৳3000-4000 round trip with waiting). Local auto from Gazipur (৳800-1200 round trip). No public transport to estate.",
                "Literary guides often available (৳500). Humayun Ahmed fan club tours: HAR Foundation (01711-XXXXXX). Advance permission recommended for groups.",
                "Literature", "Garden", "Cultural Heritage"),
            makePlaceComplete("gzp-4", "Bali Para Eco Park", "Nature",
                "A nature park beside the Turag River, popular for boating and enjoying riverside greenery.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600", 4.2, "25 km from Dhaka",
                23.9500, 90.3600, "₹30 entry", "Oct-Apr",
                "8:00 AM - 6:00 PM (Daily)", 150, "Boat facilities, picnic zones",
                "Tongi", "From Gazipur: Bus to Tongi then auto (40 min, ৳60-100). From Dhaka: Bus to Tongi (1 hr, ৳50-80), then auto (৳50-80). Private car (45 min, ৳700-1000).",
                "Basic facilities on-site. Tongi hotels: Tongi Paradise Hotel (₪₪). Better to stay in Dhaka (25 km) or Gazipur for more options.",
                "Auto from Tongi (৳60-100). Car rental from Dhaka: ৳2500-3500/day. Boat rentals at park (৳200-400/hr). CNGs available.",
                "Park staff provide basic info. Boat operators give river tours. Photography allowed. Group picnic organizers: ৳500 setup fee.",
                "Picnic", "Boating", "Nature"),
            makePlaceComplete("gzp-5", "Purbail Zamindarbari", "Heritage",
                "An 18th-century zamindari mansion with ornate terracotta work set beside a tranquil pond.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.1, "Gazipur",
                24.0300, 90.4200, "Free (donations)", "Oct-Mar",
                "9:00 AM - 5:00 PM", 60, "Historical ruins",
                "Gazipur Sadar", "From Gazipur town: Auto-rickshaw to Purbail (30 min, ৳150-200). Local bus (৳30-50). From Dhaka: Bus to Gazipur (1.5 hrs, ৳80-100), then local auto.",
                "Basic guesthouses in Gazipur town (৳800-1500). Better hotels: Star International (₪₪₪). Day trip from Dhaka/Gazipur recommended.",
                "Auto from Gazipur (৳150-250). Private car tour of heritage sites: ৳2000-3000. CNGs available at Gazipur stand.",
                "Local caretakers share history (৳200-300). Heritage photography tours: Gazipur Heritage Society (01798-XXXXXX). Combine with other zamindar baris.",
                "Heritage", "Photography", "History"),
            makePlaceComplete("gzp-6", "Tongi Bridge & River", "Nature",
                "A scenic bridge over the Turag River in Tongi — witness spectacular river sunsets and boat activity.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.0, "Tongi, Gazipur",
                23.8989, 90.4097, "Free", "Year-round (Best: Sunset)",
                "Open 24/7 - Sunset 5:00 PM - 6:30 PM", 45, "Bridge walkway",
                "Tongi", "From Gazipur: Bus to Tongi Bridge (30 min, ৳30-50). From Dhaka: Train to Tongi Station (45 min, ৳30-60), walk 10 min. Bus from Mohakhali (1 hr, ৳40-60).",
                "Tongi hotels: Hotel Paradise (₪₪), Tongi Rest House (₪). Better options in Gazipur or Dhaka. Often combined with other visits.",
                "Local transport: Auto/CNG (৳50-100 within Tongi). Train to Dhaka frequent (৳30-60). Walkable from Tongi Station. Bus connections good.",
                "Local photographers at sunset spot. River boat tours available (৳200-300). Street food vendors explain local culture.",
                "River View", "Photography", "Sunset"),
            makePlaceComplete("gzp-7", "Bir Shreshtha Matiur Rahman Park", "Heritage",
                "A park dedicated to the national hero Bir Shreshtha Matiur Rahman, with a memorial and museum.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.2, "Gazipur city",
                24.0000, 90.4000, "Free", "Year-round",
                "8:00 AM - 6:00 PM (Daily)", 60, "Memorial park",
                "Gazipur Sadar", "From Gazipur center: Walk/rickshaw (10 min, ৳20-40). From Dhaka: Bus to Gazipur city (1.5 hrs, ৳80-100), then rickshaw to park.",
                "Gazipur city hotels: Hotel Star International (₪₪₪), Hotel Al-Rafi (₪₪). Walking distance to city center restaurants.",
                "Rickshaw from Gazipur bus stand (৳30-50). Walking from city center (15 min). Auto to other attractions (৳100-150).",
                "Museum curator provides Liberation War history. Educational groups welcome. Photography allowed. Respectful visits encouraged.",
                "Memorial", "History", "Park"),
            makePlaceComplete("gzp-8", "Kaliakair Eco Resort Area", "Nature",
                "A scenic area in western Gazipur with resorts, lakes, and forested hills ideal for weekend getaways.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", 4.3, "Kaliakair, Gazipur",
                24.0800, 90.2200, "Resort entry varies", "Oct-Mar",
                "Resorts open 24/7", 240, "Resort facilities",
                "Kaliakair", "From Gazipur: Bus to Kaliakair (1 hr, ৳50-80). From Dhaka: Bus via Savar to Kaliakair (2 hrs, ৳100-150). Private car recommended (1.5 hrs, ৳1500-2500).",
                "Multiple resorts: Green Valley Resort (₪₪₪), Lake View Resort (₪₪), Kaliakair Eco Cottage (₪₪). Weekend packages available.",
                "Rent car from Dhaka with driver (৳3500-5000/day). Resort pick-up service available (advance booking). No reliable public transport to resorts.",
                "Resort staff organize nature walks. Fishing guides available at lakes. Weekend barbecue packages: ৳2000-3000/group.",
                "Resort", "Nature", "Walking"),
            makePlaceComplete("gzp-9", "Bashudev Temple", "Religious",
                "An ancient Vaishnava temple in Gazipur with centuries of religious history and vibrant festivals.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.1, "Gazipur",
                24.0250, 90.4100, "Free (donations welcome)", "Year-round (Festival: Feb-Mar)",
                "6:00 AM - 8:00 PM (Daily)", 45, "Remove shoes",
                "Gazipur Sadar", "From Gazipur town: Rickshaw to Bashudev (20 min, ৳40-60). From Dhaka: Bus to Gazipur (1.5 hrs, ৳80-100), then local transport (৳50-80).",
                "Gazipur hotels: Hotel Star International (₪₪₪), basic lodges near temple (৳600-1000). Day trip from Dhaka feasible.",
                "Rickshaw/CNG from Gazipur center (৳50-100). Auto-rickshaw available. Private car parking at temple (৳20).",
                "Temple priests share history (donations). Hindu cultural tours: Gazipur Heritage (01711-XXXXXX). Festival times crowded but colorful.",
                "Religious", "Heritage", "Cultural"),
            makePlaceComplete("gzp-10", "Gazipur Birsreshtha Memorial Museum", "Heritage",
                "A museum honoring the seven Bir Shreshtha national heroes of the 1971 Liberation War.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.3, "Gazipur city",
                24.0050, 90.4050, "₹20 entry", "Oct-Mar",
                "9:00 AM - 5:00 PM (Sun-Thu, Closed Fri)", 90, "Museum facility",
                "Gazipur Sadar", "From Gazipur center: Walk (15 min) or rickshaw (৳20-40). From Dhaka: Bus to Gazipur city (1.5 hrs, ৳80-100), then rickshaw/walk.",
                "Nearby Gazipur hotels: Hotel Star International (₪₪₪), Hotel Al-Rafi (₪₪). Walking distance to restaurants and market.",
                "Rickshaw from Gazipur bus station (৳30-50). Walking distance from city center. Parking available (৳20).",
                "Museum guides provide Liberation War details (৳200-300). Educational tours for students available (free). Photography inside restricted.",
                "Museum", "History", "Education"),
            makePlaceComplete("gzp-11", "Rajendrapur Cantonment Forest", "Nature",
                "A serene cantonment forest ideal for morning cycling and nature walks away from city crowds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.2, "Rajendrapur, Gazipur",
                24.1200, 90.4500, "Permission required (cantonment area)", "Nov-Feb",
                "Early morning 6:00 AM - 10:00 AM best", 120, "Forest trails",
                "Gazipur Sadar", "From Gazipur: Bus to Rajendrapur (30 min, ৳40-60). From Dhaka: Bus via Gazipur to Rajendrapur (1.5 hrs, ৳100-120). Entry permission needed at gate.",
                "Rajendrapur Cantonment guesthouses (official visitors only). Nearby: Bhawal Resort (₪₪₪). Gazipur hotels 10 km away.",
                "Local bus/auto from Gazipur (৳60-100). Bicycle rental in Rajendrapur (৳100-150/day). Private car with early start recommended.",
                "Cantonment staff supervise visits. Cycling groups active on weekends. Permission from cantonment authority required (01XXX-XXXXXX).",
                "Cycling", "Nature Walk", "Peaceful"),
            makePlaceComplete("gzp-12", "Dream Holiday Park", "Entertainment",
                "A large amusement and holiday park packed with rides, cable cars, and entertainment for all ages.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", 4.1, "Narsingdi Road, Gazipur",
                24.0400, 90.4400, "₹500 adults / ₹300 children (includes rides)", "Oct-Apr",
                "10:00 AM - 7:00 PM (Fri-Wed, extended weekends)", 240, "Family park",
                "Gazipur Sadar", "From Gazipur: Bus on Narsingdi Road (25 min, ৳30-50). From Dhaka: Bus to Gazipur then local bus (2 hrs total, ৳100-150). Private car (1.5 hrs, ৳1200-1800).",
                "On-site: Dream Hotel (₪₪). Nearby Gazipur: Hotel Star International (₪₪₪). Family packages available with accommodation.",
                "Car rental with driver from Dhaka (৳3500-4500/day). Park shuttle from Gazipur city (৳100). Parking available (৳50).",
                "Park staff assist visitors. Ride operators provide safety briefings. Group packages for schools/offices: ৳8000-15000 (20-50 people).",
                "Amusement Park", "Family Fun", "Rides"),
            makePlace("gzp-13", "Shyampur Cloth Market (Village)", "Cultural",
                "A traditional weaving village near Gazipur where artisans produce handloom fabrics using centuries-old techniques.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.0, "Gazipur", "Craft", "Culture", "Shopping"),
            makePlace("gzp-14", "Pubail and Kaliganj Heritage Trail", "Heritage",
                "A trail through colonial-era buildings, old mosques and zamindarbaris scattered across Kaliganj upazila.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.1, "Kaliganj, Gazipur", "Heritage", "Walking Tour", "History"),
            makePlace("gzp-15", "Turag River Sunset Cruise", "Nature",
                "A boat cruise on the Turag River catching the golden sunset — a local favourite during cooler months.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.3, "Tongi Ghat", "Boat Ride", "Sunset", "Photography"),
            makePlace("gzp-16", "Joydebpur Rajbari", "Heritage",
                "The palace of the Zamindars of Joydebpur — a beautiful colonial-era structure with historical significance.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.2, "Joydebpur, Gazipur", "Heritage", "History", "Architecture"),
            makePlace("gzp-17", "Gazipur City Corporation Park", "Nature",
                "A well-maintained urban park with walking paths, fountains and green spaces for relaxation.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600", 4.0, "Gazipur city center", "Park", "Walking", "Relaxation"),
            makePlace("gzp-18", "Bangabandhu Hi-Tech City", "Cultural",
                "Bangladesh's first hi-tech city — a fascinating glimpse into the country's technology ambitions.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.1, "Kaliakair, Gazipur", "Technology", "Modern", "Education"),
            makePlace("gzp-19", "Shalnar Haor Wetland", "Wildlife",
                "A scenic seasonal wetland attracting migratory birds in winter — great for birdwatching and photography.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.2, "Gazipur", "Birdwatching", "Wetland", "Photography"),
            makePlace("gzp-20", "Gazipur Fruit Garden Belt", "Nature",
                "Sprawling orchards of jackfruit, litchi, mango and guava lining the roads — a refreshing agricultural landscape.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600", 4.1, "Gazipur", "Orchard", "Nature", "Relaxation")
        ));

        // ── Narayanganj ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("narayanganj", Arrays.asList(
            makePlace("nrj-1", "Panam City (Sonargaon)", "Heritage",
                "A ghost-town of 52 19th-century merchant mansions, once the capital of Bengal — surreal and hauntingly beautiful.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600", 4.8, "29 km from Dhaka", "Photography", "History", "Architecture"),
            makePlace("nrj-2", "Bangladesh Folk Art Museum", "Heritage",
                "A rich museum inside Sonargaon showcasing traditional folk art, crafts, and cultural heritage of Bangladesh.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.6, "Sonargaon", "Folk Art", "Museum", "Culture"),
            makePlace("nrj-3", "Idrakpur Fort", "Heritage",
                "A rare 17th-century Mughal water-fort built by Mir Jumla to defend against Arakanese pirates.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.5, "Munshiganj, 35 km from Dhaka", "History", "Fort", "Architecture"),
            makePlace("nrj-4", "Meghna River Cruise", "Nature",
                "Take a boat on the vast Meghna River to explore scenic char islands and experience life on the river.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.4, "Narayanganj city", "Boat Ride", "River", "Nature"),
            makePlace("nrj-5", "Sonargaon Old Capital Area", "Heritage",
                "Explore the medieval capital of Bengal including the tomb of Ghiyasuddin Azam Shah and old ruins.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.5, "Sonargaon", "History", "Archaeology", "Heritage"),
            makePlace("nrj-6", "Hajiganj Fort (Munshiganj)", "Heritage",
                "A 16th-century riverside fortification built to protect the Mughal capital from pirates.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.3, "Munshiganj", "Fort", "History", "River View"),
            makePlace("nrj-7", "Shyamla River & Char Island", "Nature",
                "A scenic boat ride to seasonal char islands where locals fish and live a traditional riverside life.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", 4.2, "Narayanganj", "Boat Ride", "Char Island", "Nature"),
            makePlace("nrj-8", "Narayanganj River Port", "Cultural",
                "One of the busiest inland river ports in Bangladesh — colourful launches and country boats cover the river.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.1, "Narayanganj city", "River Port", "Photography", "Culture"),
            makePlace("nrj-9", "Baror Vita Archaeological Mound", "Heritage",
                "An ancient archaeological mound in Sonargaon area with traces of early medieval settlement.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.0, "Sonargaon", "Archaeology", "History", "Heritage"),
            makePlace("nrj-10", "Mograpara Dargah", "Religious",
                "An important Sufi dargah and religious site in Narayanganj attracting pilgrims from across the region.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.2, "Narayanganj", "Religious", "Pilgrimage", "Sufi"),
            makePlace("nrj-11", "Narayanganj Museum", "Heritage",
                "District museum showcasing local history, the jute trade era, and Narayanganj's industrial heritage.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.1, "Narayanganj city", "Museum", "History", "Education"),
            makePlace("nrj-12", "Fatullah Jute Mills Heritage Area", "Cultural",
                "Explore the historic jute mill area that made Narayanganj 'the Dundee of the East' in colonial times.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.0, "Fatullah", "Industrial Heritage", "History", "Culture"),
            makePlace("nrj-13", "Goaldi Mosque", "Religious",
                "A magnificent 16th-century Sultanate-era mosque with intricate terracotta ornamentation.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.5, "Sonargaon", "History", "Architecture", "Religion"),
            makePlace("nrj-14", "Tin Shohid Ghat", "Heritage",
                "A historic riverside ghat with memorials to three local martyrs of the 1971 Liberation War.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600", 4.1, "Narayanganj city", "Memorial", "History", "Religion"),
            makePlace("nrj-15", "Sadar Hospital Heritage Building", "Heritage",
                "A fine example of colonial-era hospital architecture in the heart of Narayanganj.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600", 4.0, "Narayanganj city", "Heritage", "Architecture", "History"),
            makePlace("nrj-16", "Shitalakshya River Cruise", "Nature",
                "A boat ride on the scenic Shitalakshya River passing old mills, mosques, and fishing villages.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600", 4.3, "Narayanganj city", "River Cruise", "Photography", "Sunset"),
            makePlace("nrj-17", "Khanpur Dighi", "Nature",
                "A large historic lake with a park in Narayanganj, popular for morning walks and picnics.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600", 4.1, "Narayanganj", "Lake", "Park", "Picnic"),
            makePlace("nrj-18", "Bangabandhu Textile Mills", "Cultural",
                "A historic textile town known for its massive garment industry — one of the world's leading garment hubs.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600", 4.0, "Narayanganj", "Industry", "Culture", "Heritage"),
            makePlace("nrj-19", "Munshiganj Rameswar Temple", "Religious",
                "A beautiful 19th-century Hindu temple with a peaceful riverside location in Munshiganj.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600", 4.2, "Munshiganj", "Temple", "Religious", "Heritage"),
            makePlace("nrj-20", "Bilobpur Char", "Nature",
                "A scenic river char in the Meghna accessible by boat — a beautiful retreat from the city.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", 4.2, "Narayanganj", "Char Island", "Nature", "Photography")
        ));

        // ── Tangail ────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("tangail", Arrays.asList(
            makePlace("tng-1", "Atia Mosque", "Heritage",
                "A magnificent 16th-century Mughal mosque with intricate terracotta ornamentation — one of the finest pre-Mughal architectural gems in Bangladesh.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "12 km from Tangail town", "Photography", "History", "Architecture"),
            makePlace("tng-2", "Pakutia Zamindar Bari", "Heritage",
                "A grand 19th-century zamindari mansion surrounded by lush greenery and peaceful ponds — perfect for history lovers.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "18 km from Tangail town", "Photography", "History", "Peaceful"),
            makePlace("tng-3", "Dhanbari Nawab Palace", "Heritage",
                "The stately palace of the Nawabs of Dhanbari, featuring impressive colonial architecture and a serene surrounding garden.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.3, "35 km from Tangail town", "History", "Architecture", "Garden"),
            makePlace("tng-4", "Jamuna Resort & Riverfront", "Nature",
                "A beautiful resort on the banks of the mighty Jamuna River offering stunning river views, boat rides and fresh air.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.6, "40 km from Tangail town", "River View", "Boat Ride", "Resort"),
            makePlace("tng-5", "Madhupur National Park", "Wildlife",
                "A protected sal forest and national park home to diverse wildlife, tribal Garo communities, and beautiful nature trails.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "45 km from Tangail town", "Trekking", "Wildlife", "Tribal Culture"),
            makePlace("tng-6", "Nathun Para Dighi (Peerless Lake)", "Nature",
                "A large historic lake in Tangail town surrounded by trees, popular for morning walks and relaxation.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.1, "In Tangail town", "Walking", "Relaxation", "Photography"),
            makePlace("tng-7", "Porabari Zamindarbari", "Heritage",
                "An ornate 19th-century zamindar mansion with terracotta-work and lotus-shaped ponds — off the beaten track.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.2, "10 km from Tangail town", "History", "Photography", "Architecture"),
            makePlace("tng-8", "Nagarpur Zamindar Bari", "Heritage",
                "A hauntingly beautiful ruined palace complex in Nagarpur with courtyards, ponds and riverside views.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "27 km from Tangail town", "History", "Photography", "Ruins"),
            makePlace("tng-9", "Bhuapur Jamuna Riverfront", "Nature",
                "A scenic riverfront on the Jamuna ideal for sunset watching, fishing, and experiencing rural Bangladesh life.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "42 km from Tangail town", "Sunset", "Photography", "Fishing"),
            makePlace("tng-10", "Karatia Zamindar Bari", "Heritage",
                "A 200-year-old zamindar estate with classical architecture, surrounded by old trees and a tranquil atmosphere.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.2, "22 km from Tangail town", "History", "Photography", "Architecture")
        ));

        // ── Dhaka ────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("dhaka", Arrays.asList(
            makePlace("dhk-1", "Lalbagh Fort", "Heritage",
                "A 17th-century Mughal fort on the Buriganga River, featuring the tomb of Pari Bibi and the Diwan-i-Aam — a must-visit of Old Dhaka.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.8, "Old Dhaka", "History", "Photography", "Architecture"),
            makePlace("dhk-2", "Ahsan Manzil (Pink Palace)", "Heritage",
                "The iconic Pink Palace on the Buriganga riverfront, home to the Nawabs of Dhaka and now a national museum packed with history.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.8, "Old Dhaka", "Museum", "History", "Architecture"),
            makePlace("dhk-3", "Star Mosque (Tara Masjid)", "Religious",
                "An exquisitely decorated mosque adorned with Chinese porcelain tiles forming star patterns — a unique gem of Old Dhaka.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "Old Dhaka", "Architecture", "Religious", "Photography"),
            makePlace("dhk-4", "National Parliament House", "Heritage",
                "Louis Kahn's masterpiece of 20th-century architecture — arguably the most important building in Bangladesh.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.9, "Sher-e-Bangla Nagar", "Architecture", "National Landmark", "Photography"),
            makePlace("dhk-5", "National Museum of Bangladesh", "Heritage",
                "The largest museum in Bangladesh with over 83,000 artifacts spanning art, history, natural history and ethnography.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.6, "Shahbagh, Dhaka", "Museum", "Education", "Art"),
            makePlace("dhk-6", "Shaheed Minar", "Heritage",
                "Bangladesh's national language movement monument — a symbol of cultural pride and the struggle for the Bangla language.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.9, "Dhaka University area", "History", "National Symbol", "Monument"),
            makePlace("dhk-7", "Liberation War Museum", "Heritage",
                "A powerful museum documenting the 1971 Liberation War through artifacts, photographs and testimonies.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.8, "Segunbagicha, Dhaka", "Museum", "History", "Education"),
            makePlace("dhk-8", "Dhakeshwari National Temple", "Religious",
                "The national Hindu temple of Bangladesh, dedicated to Goddess Dhakeshwari — the deity from whom Dhaka takes its name.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.6, "Old Dhaka", "Religious", "History", "Cultural"),
            makePlace("dhk-9", "Baldha Garden", "Nature",
                "One of the oldest botanical gardens in the subcontinent (1909), housing rare plant species from around the world.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "Wari, Old Dhaka", "Botanical Garden", "Nature", "Photography"),
            makePlace("dhk-10", "Saat Gombuj Mosque", "Religious",
                "A 17th-century Mughal mosque with seven domes built by Shaista Khan — a serene oasis in the city.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.5, "Mohammadpur, Dhaka", "History", "Religious", "Architecture"),
            makePlace("dhk-11", "Curzon Hall", "Heritage",
                "A grand colonial-era building (1904) with Indo-Saracenic architecture, now part of Dhaka University.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Dhaka University campus", "Architecture", "Heritage", "Photography"),
            makePlace("dhk-12", "Rose Garden Palace", "Heritage",
                "A 1930s art-deco mansion in Tikatuli — famous as the birthplace of the Awami League and a stunning heritage building.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.4, "Tikatuli, Old Dhaka", "Heritage", "History", "Architecture")
        ));

        // ── Cox's Bazar ───────────────────────────────────────────────────────
        LOCATION_PLACES.put("cox's bazar", Arrays.asList(
            makePlaceComplete("cxb-1", "Cox's Bazar Sea Beach", "Beach",
                "The world's longest natural sea beach stretching 120 km with golden sands and crashing Bay of Bengal waves.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.9, "Cox's Bazar town",
                21.4272, 92.0058, "Free", "Oct-Mar (Winter Season)",
                "24/7 (Best sunrise/sunset)", 180, "Beach wheelchairs available at some points",
                "Cox's Bazar Sadar", // Upazila
                "From Dhaka: Bus (10-12 hrs, ৳800-1500), Flight (1 hr, ৳3000-6000). From Chittagong: Bus (4 hrs, ৳300-500). Local transport: Rickshaw, CNG.",
                "Beach hotels: Long Beach Hotel (₪₪), Hotel Sea Crown (₪₪₪), Sayeman Beach Resort (₪₪₪₪). Budget: Hotel Praasad, Hotel Sea Palace.",
                "Car rentals widely available: Sakura Rent-a-Car Cox's Bazar, local agencies near Beach Road (₹1500-3000/day). Motorbike rentals (₹500-800/day).",
                "Beach guides at Laboni Point (₹300-600/day). Tour packages through local agencies. Hotel concierge can arrange.",
                "Swimming", "Sunset", "Surfing"),
            makePlaceComplete("cxb-2", "Saint Martin's Island", "Beach",
                "Bangladesh's only coral island — famous for its crystal-clear blue water, coral reefs, coconut palms and seafood.",
                "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=600",
                4.9, "120 km south of Cox's Bazar",
                20.6259, 92.3225, "No entry fee, but boat + accommodation packages required", "Nov-Mar (Sea calm)",
                "Island open sunrise to sunset", 480, "Basic facilities, sandy terrain",
                "Teknaf", // Upazila
                "From Cox's Bazar: Bus to Teknaf (3 hrs, ৳150-250), then ship to island (3 hrs, ৳700-1200 return). Must book overnight package. Day trips during calm season.",
                "Island accommodations: Blue Marine Resort, Prajapati Resort, Beach Resort (basic cottages ₹1500-3000/night including boat). Pre-booking essential.",
                NO_CARS_MSG + " Transport on island by walking or motorcycle taxis (₹100-200).",
                "Island guides included in most packages (₹500-800). Snorkeling guides available. Book through Teknaf agencies.",
                "Snorkeling", "Coral Reef", "Island"),
            makePlaceComplete("cxb-3", "Inani Beach", "Beach",
                "A serene coral-strewn beach 27 km south of Cox's Bazar with crystal-clear water and colorful pebbles.",
                "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=600",
                4.8, "27 km from Cox's Bazar",
                21.2567, 92.0371, "Free", "Oct-Mar",
                "24/7 (Best time morning/evening)", 120, "Beach access via sandy paths",
                "Ukhia", // Upazila
                "From Cox's Bazar: Marine Drive bus/CNG (45 min, ৳100-200). Private car (₹800-1200). Enjoy scenic Marine Drive route.",
                "Limited beachfront: Seagull Hotel Inani (₪₪), some basic guesthouses. Most stay in Cox's Bazar and day trip.",
                "Rent cars in Cox's Bazar for Inani trip (₹1500-2500/day). CNGs for hire (₹600-1000 round trip).",
                "Beach guides at Inani (₹200-400). Snorkeling equipment rentals available. Cox's Bazar tour agencies include Inani.",
                "Snorkeling", "Photography", "Coral"),
            makePlaceComplete("cxb-4", "Himchari National Park", "Wildlife",
                "A forested hill area with a stunning waterfall right beside the sea, home to diverse wildlife.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "12 km from Cox's Bazar",
                21.3497, 91.9744, "₹50 entry + ₹20 vehicle parking", "Jun-Sep (rainy season for waterfall)",
                "8:00 AM - 6:00 PM (Daily)", 150, "Hiking trails, waterfall area",
                "Cox's Bazar Sadar", // Upazila
                "From Cox's Bazar town: CNG/auto to Himchari (30 min, ৳150-250). Bus to Himchari stop (৳50-80). Private car/bike rental (৳600-1000). Easy Marine Drive route.",
                "Near park: Himchari Resort (₪₪). Most stay in Cox's Bazar town (12 km) - Sea Pearl (₪₪₪₪), Hotel Grand Palace (₪₪₪). Day trip popular.",
                "Rent motorcycle in Cox's Bazar (৳600-800/day). Car rental (৳2000-3000/day). CNGs available for round trip (৳500-800).",
                "Forest guides at park entrance (৳300-500). Wildlife spotting tours: Cox's Bazar Eco Tours (01798-XXXXXX). Waterfall trekking guides (৳200).",
                "Waterfall", "Trekking", "Wildlife"),
            makePlaceComplete("cxb-5", "Marine Drive Road", "Nature",
                "An 80 km scenic coastal road running beside the sea — one of the most beautiful drives in South Asia.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.6, "Along the coast",
                21.3547, 92.0027, "Free (no toll)", "Year-round (Best: Oct-Mar)",
                "Open 24/7 - Best morning/sunset", 180, "Paved road, viewpoints",
                "Cox's Bazar Sadar to Teknaf", // Multiple upazilas
                "From Cox's Bazar: Start at Kolatoli, drive south via Inani to Teknaf (2-3 hrs one way). Rent motorcycle/car from Cox's Bazar. Bus also travels this route (৳150-250 to Teknaf).",
                "Along route: Seagull Hotel Inani (₪₪), basic restaurants at viewpoints. Main hotels in Cox's Bazar or Teknaf towns. Stop for photo breaks.",
                "Motorcycle rental: ৳600-1000/day. Car with driver: ৳2500-4000/day. Private jeep tours: ৳3500-5000 (includes stops). Fuel stations available.",
                "Self-drive popular. Photo stop guides at major viewpoints (৳100-200). Full tour packages: Marine Drive Tours Cox's Bazar (01711-XXXXXX) - ৳1500/person.",
                "Scenic Drive", "Photography", "Cycling"),
            makePlace("cxb-6", "Moheshkhali Island", "Heritage",
                "An island accessible by speedboat featuring the famous Adinath Temple on a hilltop and beautiful mangroves.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "14 km by boat", "Boat Ride", "Temple", "Island"),
            makePlace("cxb-7", "Ramu Buddhist Vihara", "Religious",
                "A beautiful Buddhist heritage site in Ramu with ancient statues, pagodas and a peaceful atmosphere.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.5, "21 km from Cox's Bazar", "Buddhist Temple", "Heritage", "Photography"),
            makePlace("cxb-8", "Dulahazara Safari Park", "Wildlife",
                "A large wildlife safari park home to Bengal tigers, elephants, crocodiles and hundreds of deer.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "50 km from Cox's Bazar", "Safari", "Wildlife", "Family Fun"),
            makePlace("cxb-9", "Teknaf Wildlife Sanctuary", "Wildlife",
                "A pristine wildlife sanctuary at the southernmost tip of Bangladesh, bordering Myanmar.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.6, "84 km from Cox's Bazar", "Trekking", "Wildlife", "Nature"),
            makePlace("cxb-10", "Laboni Beach Point", "Beach",
                "The main beach access point in Cox's Bazar town with beach equipment rentals, shops and the iconic beach walk.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.4, "Cox's Bazar town center", "Beach Walk", "Sunset", "Shopping")
        ));

        // ── Sylhet ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("sylhet", Arrays.asList(
            makePlaceComplete("syl-1", "Ratargul Swamp Forest", "Wildlife",
                "Bangladesh's only freshwater swamp forest — called the Amazon of Bangladesh. Take a boat through the eerie green canopy.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.9, "26 km from Sylhet city",
                25.0350, 92.1050, "₹200 boat rental (2-4 people)", "Jun-Oct (monsoon - water high)",
                "8:00 AM - 5:00 PM (Daily)", 120, "Boat access only",
                "Gowainghat", // Upazila
                "From Sylhet city: Bus/CNG to Ratargul (1 hr, ৳80-150). Private car (45 min, ৳800-1200). Reserve car: Sylhet Tours (01711-XXXXXX). Boats mandatory for forest entry.",
                "Near Ratargul: Ratargul Eco Resort (₪₪). Most tourists stay in Sylhet city: Hotel Rose View (₪₪₪), Grand Palace Hotel (₪₪₪₪).",
                "Car rental from Sylhet: ৳2500-3500/day. CNGs for Ratargul trip (৳600-1000 round trip). Boats at forest (included in entry). No cars inside.",
                "Local boatmen guide through forest (৳200-300 tip). Bird watching guide: Sylhet Eco Tourism (01798-XXXXXX). Photography boats available (৳400 for 2 hrs).",
                "Boat Tour", "Photography", "Wildlife"),
            makePlaceComplete("syl-2", "Jaflong", "Nature",
                "A scenic area at the foothills of the Meghalaya hills with the Piyain River, stone collection, and views into India.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.8, "60 km from Sylhet city",
                25.1725, 92.0089, "Free entry, ₹300-500 boat rental", "Oct-Mar (dry season)",
                "Open 24/7 - Best morning/afternoon", 180, "Rocky terrain, river access",
                "Gowainghat", // Upazila
                "From Sylhet: Direct bus from Sylhet to Jaflong (2 hrs, ৳100-150). Shared jeep/micro (৳150-200). Private car (1.5 hrs, ৳1500-2500). Tours include Ratargul + Jaflong.",
                "Jaflong Resort (₪₪), Hill View Resort (₪₪). Basic guesthouses (৳800-1500/night). Many do day trip from Sylhet. Restaurant at Zero Point.",
                "Rent car from Sylhet for day trip (৳3000-4500 including driver). Shared jeeps from Sylhet (৳150/person). Boat rentals at river (৳300-500/boat).",
                "Local guides at Zero Point (৳300-500). Stone collector tours available. Combined Jaflong-Lalakhal tours: Sylhet Paradise Tours (01552-XXXXXX) - ৳1200/person.",
                "River", "Photography", "Scenic"),
            makePlaceComplete("syl-3", "Lalakhal", "Nature",
                "Famous for its strikingly blue-green river water — boat rides here are an unmissable Sylhet experience.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.8, "35 km from Sylhet city",
                25.1234, 92.2156, "₹500-800 boat rental (holds 4-6 people)", "Nov-Apr (water clearest)",
                "8:00 AM - 6:00 PM", 150, "Boat access, tea gardens",
                "Jaintiapur", // Upazila
                "From Sylhet: Bus to Jaintiapur then local jeep to Lalakhal (1.5 hrs total, ৳120-180). Private car best option (1 hr, ৳1200-1800). Tours combine with Jaflong.",
                "Basic lodges in Jaintiapur (৳600-1200). Better to stay in Sylhet city. Tea garden guesthouses available with advance booking.",
                "Car rental from Sylhet: ৳2500-3500/day. Shared jeep from Jaintiapur bazaar (৳200 per vehicle to ghat). Boats compulsory (৳500-800 for 2 hr ride).",
                "Boatmen provide commentary (৳100-200 tip). Photography tours: Blue Water Sylhet Tours (01711-XXXXXX) - ৳1000/person. Tea garden visits arranged.",
                "Boat Ride", "Photography", "Nature"),
            makePlace("syl-4", "Bisnakandi", "Nature",
                "A breathtaking natural wonder where the Piyan River meets stone quarries and Meghalaya hills rise above.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.7, "33 km from Sylhet city", "Trekking", "Photography", "River"),
            makePlace("syl-5", "Hazrat Shah Jalal Dargah", "Religious",
                "The sacred mausoleum of Hazrat Shah Jalal — one of the most revered Islamic shrines in Bangladesh.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.9, "Sylhet city center", "Religious", "History", "Peaceful"),
            makePlace("syl-6", "Hazrat Shah Paran Dargah", "Religious",
                "The mausoleum of Shah Paran (nephew of Shah Jalal), a highly revered pilgrimage site in Sylhet.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.7, "8 km from Sylhet city", "Religious", "Pilgrimage", "History"),
            makePlace("syl-7", "Khadim Nagar National Park", "Wildlife",
                "A lush protected forest near Sylhet city, ideal for birdwatching and nature walks.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "14 km from Sylhet city", "Birdwatching", "Trekking", "Nature"),
            makePlace("syl-8", "Madhabpur Lake", "Nature",
                "A serene lake surrounded by floating tea gardens — a uniquely beautiful landscape in Moulvibazar.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.7, "75 km from Sylhet city", "Tea Garden", "Lake", "Photography"),
            makePlace("syl-9", "Lawachara National Park", "Wildlife",
                "A tropical rainforest home to the critically endangered Western Hoolock Gibbon and rich biodiversity.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.8, "80 km from Sylhet city", "Trekking", "Wildlife", "Gibbon Spotting"),
            makePlace("syl-10", "Ali Amjad's Clock Tower", "Heritage",
                "A beautiful 19th-century Gothic clock tower beside the Surma River — a beloved symbol of Sylhet city.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.3, "Sylhet city center", "Heritage", "Photography", "Iconic")
        ));

        // ── Chittagong ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("chittagong", Arrays.asList(
            makePlaceComplete("ctg-1", "Patenga Beach", "Beach",
                "A popular sea beach at the mouth of the Karnaphuli River with views of ships and the busy Chittagong port.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.5, "22 km from Chittagong city",
                22.2367, 91.7987, "Free entry", "Oct-Mar (cooler weather)",
                "Open 24/7 - Best: Sunset 5-6 PM", 120, "Sandy beach, accessible",
                "Patenga", // Upazila (part of Chittagong city)
                "From Chittagong city: Bus from New Market/GEC to Patenga (45 min, ৳30-50). CNG auto-rickshaw (৳200-300). Uber/Pathao available (৳250-400). Local buses frequent.",
                "Nearby: Hotel Sea Palace (₪₪), Beach View Hotel (₪₪). Central Chittagong hotels: Hotel Agrabad (₪₪₪), Peninsula Hotel (₪₪₪₪).",
                "CNGs abundant for return (৳200-300). Rent car from Chittagong: ৳2500-3500/day. Local buses frequent. Uber/Pathao operational.",
                "Beach photographers available (৳50-100). Seafood restaurant staff helpful. No formal guides. Ship watching tours: Chittagong Marine Tours (01798-XXXXXX).",
                "Sunset", "Beach", "Photography"),
            makePlaceComplete("ctg-2", "Foy's Lake", "Nature",
                "A man-made lake in forested hills with an amusement park and cable car — perfect for families.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.4, "8 km from Chittagong city",
                22.3700, 91.8100, "₹300 adults / ₹150 children (includes park)", "Oct-Mar",
                "10:00 AM - 8:00 PM (Daily)", 180, "Family-friendly, cable car",
                "Khulshi", // Upazila (part of Chittagong metropolitan)
                "From Chittagong city center: Bus to Khulshi-Foy's Lake (30 min, ৳20-40). CNG auto-rickshaw (৳150-200). Uber/Pathao (৳180-280). Easy access.",
                "Foy's Lake Concord Resort (₪₪₪₪) on-site. Nearby: Hotel Tower Inn (₪₪), Hotel Millennium (₪₪₪). Central Chittagong 8 km away.",
                "Parking available (৳50). CNGs waiting at exit (৳150-200 to city). Uber/Pathao available. Rent car: Chittagong Rent-A-Car (01711-XXXXXX) - ৳3000/day.",
                "Park staff assist visitors. Paddle boat operators give lake tours (৳200-400). Cable car operators provide commentary. Family activity center on-site.",
                "Boat Ride", "Amusement Park", "Cable Car"),
            makePlace("ctg-3", "Chandranath Hill (Sitakunda)", "Nature",
                "A sacred hilltop with a Hindu temple offering panoramic views of the Bay of Bengal and surrounding hills.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "37 km from Chittagong city", "Trekking", "Temple", "Panoramic View"),
            makePlace("ctg-4", "Commonwealth War Cemetery", "Heritage",
                "A beautifully maintained cemetery for Allied soldiers who died in the Burma Campaign of WWII.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.7, "Chittagong city", "History", "Memorial", "Peaceful"),
            makePlace("ctg-5", "Ethnological Museum", "Heritage",
                "Bangladesh's only ethnological museum showcasing the life and culture of 14 indigenous hill tribes.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.5, "Agrabad, Chittagong", "Museum", "Culture", "Tribal Art"),
            makePlace("ctg-6", "Zia Memorial Museum", "Heritage",
                "A museum inside the Circuit House dedicated to President Ziaur Rahman, with historical artifacts.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.2, "Chittagong city center", "Museum", "History", "Heritage"),
            makePlace("ctg-7", "Batali Hill", "Nature",
                "A hilltop in the city offering a sweeping panoramic view of the Chittagong port and Bay of Bengal.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Chittagong city center", "Panoramic View", "Photography", "Sunset"),
            makePlace("ctg-8", "Guliakhali Sea Beach", "Beach",
                "A quiet, less-visited beach with mangroves growing beside the sea — a hidden gem near Chittagong.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.5, "35 km from Chittagong city", "Beach", "Mangrove", "Peaceful"),
            makePlace("ctg-9", "Karnaphuli River", "Nature",
                "A scenic river cruise from Chittagong port — witness the Karnaphuli Tunnel, shipbreaking yards, and fishing boats.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Chittagong city", "River Cruise", "Photography", "Scenic")
        ));

        // ── Bandarban ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("bandarban", Arrays.asList(
            makePlace("bdb-1", "Nilgiri Hill Resort", "Nature",
                "The highest tourist spot in Bangladesh at 3,000 ft — clouds float beneath your feet and sunrises are phenomenal.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.9, "47 km from Bandarban town", "Sunrise", "Cloud Sea", "Trekking"),
            makePlace("bdb-2", "Golden Temple (Buddha Dhatu Jadi)", "Religious",
                "The largest Buddhist temple complex in Bangladesh on a hilltop, adorned in stunning golden splendor.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.8, "4 km from Bandarban town", "Buddhist Temple", "Photography", "Cultural"),
            makePlace("bdb-3", "Nafakhum Waterfall", "Nature",
                "The largest waterfall in Bangladesh — a multi-day jungle trek to witness this magnificent cascade.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.9, "79 km from Bandarban town", "Waterfall", "Trekking", "Adventure"),
            makePlace("bdb-4", "Boga Lake", "Nature",
                "A mysterious crater lake at 1,246 ft altitude surrounded by dense jungle and tribal Bawm villages.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.8, "68 km from Bandarban town", "Lake", "Trekking", "Camping"),
            makePlace("bdb-5", "Chimbuk Hill", "Nature",
                "Bangladesh's third-highest peak with sweeping views of the Sangu Valley and Murong tribal villages.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "26 km from Bandarban town", "Scenic Drive", "Hill Station", "Tribal Culture"),
            makePlace("bdb-6", "Rijuk Waterfall", "Nature",
                "A spectacular waterfall near Thanchi where water crashes from a significant height through dense forest.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "80 km from Bandarban town", "Waterfall", "Trekking", "Photography"),
            makePlace("bdb-7", "Keokradong Peak", "Nature",
                "One of the highest peaks in Bangladesh offering breathtaking 360° views of the Chittagong Hill Tracts.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.8, "70 km from Bandarban town", "Peak Climbing", "Adventure", "Camping"),
            makePlace("bdb-8", "Meghla Parjatan Complex", "Nature",
                "A scenic park complex with a lake, cable car, hanging bridge and mini zoo — great for families.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.3, "4 km from Bandarban town", "Cable Car", "Hanging Bridge", "Family Fun"),
            makePlace("bdb-9", "Thanchi & Remakri", "Nature",
                "A remote riverside area accessible only by engine boat through the pristine Sangu River — true wilderness.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.7, "90 km from Bandarban town", "River Journey", "Adventure", "Remote")
        ));

        // ── Rangamati ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("rangamati", Arrays.asList(
            makePlace("rmt-1", "Kaptai Lake", "Nature",
                "The largest man-made lake in South Asia surrounded by lush hills — boat rides offer breathtaking scenery.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.9, "Rangamati town", "Boat Ride", "Fishing", "Photography"),
            makePlace("rmt-2", "Hanging Bridge of Rangamati", "Heritage",
                "A famous 335-meter suspension bridge over the Kaptai Lake — an iconic symbol of Rangamati.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.7, "Rangamati town", "Photography", "Scenic", "Iconic"),
            makePlace("rmt-3", "Shuvalang Waterfall", "Nature",
                "A stunning waterfall accessible by boat across Kaptai Lake, surrounded by dense forest.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.8, "25 km by boat from Rangamati", "Waterfall", "Boat Ride", "Swimming"),
            makePlace("rmt-4", "Sajek Valley", "Nature",
                "The 'Queen of Hills' — a breathtaking hilltop area at 1,800 ft with clouds rolling through valleys.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.9, "67 km from Rangamati town", "Cloud Sea", "Sunrise", "Trekking"),
            makePlace("rmt-5", "Rajban Vihara", "Religious",
                "A ornate Buddhist monastery and pilgrimage site on the bank of Kaptai Lake, home to revered monks.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.6, "5 km from Rangamati town", "Buddhist Temple", "Peaceful", "Cultural"),
            makePlace("rmt-6", "Chakma Royal Palace", "Heritage",
                "The residence of the Chakma Raja (tribal king), set beside the Kaptai Lake with beautiful gardens.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "Rangamati town", "Heritage", "Cultural", "Photography"),
            makePlace("rmt-7", "DC Hill", "Nature",
                "A hilltop park offering a panoramic view of the Kaptai Lake and surrounding landscape.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Rangamati town", "Panoramic View", "Gardens", "Relaxation")
        ));

        // ── Khulna ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("khulna", Arrays.asList(
            makePlace("khl-1", "Sundarbans Mangrove Forest", "Wildlife",
                "The world's largest mangrove forest and UNESCO World Heritage Site — home to the Royal Bengal Tiger.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                5.0, "87 km from Khulna city", "Tiger Safari", "Boat Tour", "Wildlife"),
            makePlace("khl-2", "Sixty Dome Mosque (Bagerhat)", "Heritage",
                "A 15th-century UNESCO World Heritage Site mosque with 60 stone pillars — the most impressive medieval mosque in Bangladesh.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.9, "50 km from Khulna city", "Architecture", "History", "UNESCO Site"),
            makePlace("khl-3", "Khan Jahan Ali Dargah", "Religious",
                "The mausoleum of the founder of Bagerhat, with sacred crocodiles living beside the holy pond.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.6, "52 km from Khulna city", "Religious", "History", "Sacred Crocodiles"),
            makePlace("khl-4", "Mongla Port & Pashur River", "Nature",
                "Watch large cargo ships navigate the Pashur River beside dense mangroves — a stunning juxtaposition.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "48 km from Khulna city", "River View", "Photography", "Industrial Heritage"),
            makePlace("khl-5", "Rupsha River Bridge & Bazar", "Nature",
                "A riverside walk and market along the scenic Rupsha River in the heart of Khulna city.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.2, "Khulna city center", "River Walk", "Market", "Photography"),
            makePlace("khl-6", "Roth Khola (Sundarban Launch)", "Wildlife",
                "A government-run launch tour through the Sundarbans waterways — spot deer, crocodiles, and birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "Khulna city", "Boat Tour", "Wildlife", "Mangrove"),
            makePlace("khl-7", "Nine-Dome Mosque (Nau Gombuj Masjid)", "Heritage",
                "A beautifully preserved 15th-century mosque in Bagerhat with nine domes and terracotta work.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.6, "50 km from Khulna city", "Architecture", "History", "Terracotta")
        ));

        // ── Rajshahi ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("rajshahi", Arrays.asList(
            makePlace("rsh-1", "Puthia Temple Complex", "Religious",
                "The largest Hindu temple complex in Bangladesh with multiple 18th-century terracotta temples.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "32 km from Rajshahi city", "Temple", "Terracotta Art", "History"),
            makePlace("rsh-2", "Varendra Research Museum", "Heritage",
                "The oldest museum in Bangladesh (1910) housing a stunning collection of Hindu and Buddhist statues.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.6, "Rajshahi city center", "Museum", "History", "Sculpture"),
            makePlace("rsh-3", "Bagha Mosque", "Heritage",
                "A 16th-century Mughal mosque famous for its intricate terracotta stone ornamentation.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.5, "40 km from Rajshahi city", "Architecture", "History", "Terracotta"),
            makePlace("rsh-4", "Padma River Embankment", "Nature",
                "A beautiful riverside promenade along the mighty Padma — ideal for evening walks and watching the sunset over the river.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Rajshahi city center", "River Walk", "Sunset", "Relaxation"),
            makePlace("rsh-5", "Sona Masjid (Gaur, Chapai Nawabganj)", "Heritage",
                "The largest mosque of the Sultanate period (15th century) with stunning stone carvings — a UNESCO candidate.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.8, "35 km from Rajshahi city", "History", "Architecture", "Sultanate Era"),
            makePlace("rsh-6", "Shah Makhdum Dargah", "Religious",
                "The sacred shrine of 13th-century Islamic missionary Shah Makhdum Rupos — a revered pilgrimage site.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.4, "Rajshahi city center", "Religious", "History", "Pilgrimage"),
            makePlace("rsh-7", "Rajshahi University Campus", "Nature",
                "One of the most beautiful university campuses in Bangladesh — famous for its tree-lined roads and peaceful atmosphere.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "5 km from Rajshahi city", "Nature Walk", "Photography", "Education"),
            makePlace("rsh-8", "Dargapara Silk Weaving Village", "Cultural",
                "Watch Rajshahi silk being woven by hand in traditional workshops — the famous Rajshahi Silk is world-renowned.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.3, "Rajshahi city area", "Craft", "Culture", "Shopping")
        ));

        // ── Bogura ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("bogura", Arrays.asList(
            makePlace("bgr-1", "Mahasthangarh", "Heritage",
                "One of the earliest urban archaeological sites in the subcontinent (3rd century BC) — a must-see for history enthusiasts.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.8, "13 km from Bogura town", "Archaeology", "History", "Fort"),
            makePlace("bgr-2", "Govinda Bhita", "Heritage",
                "Ancient ruins of a Hindu temple complex at Mahasthangarh dating back to the Gupta period.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "14 km from Bogura town", "Archaeology", "History", "Ruins"),
            makePlace("bgr-3", "Mahasthan Museum", "Heritage",
                "A museum housing thousands of artifacts unearthed from the Mahasthangarh excavations.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.5, "13 km from Bogura town", "Museum", "History", "Artifacts"),
            makePlace("bgr-4", "Vasu Bihar", "Heritage",
                "A large Buddhist monastery complex near Mahasthangarh with ruins of meditation cells and stupas.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.3, "15 km from Bogura town", "Buddhism", "Archaeology", "Ruins"),
            makePlace("bgr-5", "Sherpur Zamindarbari", "Heritage",
                "The palace of the zamindars of Sherpur — a beautiful heritage building with ornate colonial architecture.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.2, "8 km from Bogura town", "Architecture", "History", "Photography"),
            makePlace("bgr-6", "Karatia Jamider Banglo", "Heritage",
                "A colonial-era bungalow in a lush garden setting, once used by British administrators.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.1, "20 km from Bogura town", "Heritage", "Garden", "Photography")
        ));

        // ── Mymensingh ───────────────────────────────────────────────────────
        LOCATION_PLACES.put("mymensingh", Arrays.asList(
            makePlace("mym-1", "Shashi Lodge", "Heritage",
                "A beautiful 19th-century Greek-revival palace set within a garden, built by the Maharajas of Mymensingh.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.6, "Mymensingh town", "Architecture", "History", "Gardens"),
            makePlace("mym-2", "Alexander Castle", "Heritage",
                "A magnificent castle built by Raja Shashi Kanta Acharya in 1905 — now housing the Teachers Training College.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.5, "Mymensingh town", "History", "Architecture", "Photography"),
            makePlace("mym-3", "BAU Botanical Garden", "Nature",
                "The largest botanical garden in Bangladesh inside Bangladesh Agricultural University.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Mymensingh town", "Nature Walk", "Photography", "Education"),
            makePlace("mym-4", "Brahmaputra River Embankment", "Nature",
                "A lovely riverside walk along the old Brahmaputra — a popular evening stroll with great views and breeze.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.3, "Mymensingh town center", "River Walk", "Relaxation", "Photography"),
            makePlace("mym-5", "Muktagachha Palace", "Heritage",
                "An ornate 19th-century zamindari palace in Muktagachha, famous for its terracotta artwork.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.3, "19 km from Mymensingh town", "History", "Architecture", "Photography"),
            makePlace("mym-6", "Mymensingh Museum", "Heritage",
                "A district museum housing artifacts reflecting the history and culture of the Mymensingh region.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.1, "Mymensingh town", "Museum", "History", "Culture"),
            makePlace("mym-7", "Gauripur Palace", "Heritage",
                "A hauntingly beautiful ruined zamindari palace in Gauripur with grand rooms and overgrown courtyards.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "20 km from Mymensingh town", "Ruins", "Photography", "History")
        ));

        // ── Comilla ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("comilla", Arrays.asList(
            makePlace("com-1", "Shalbon Vihara (Mainamoti)", "Heritage",
                "Impressive 7th-8th century Buddhist monastery ruins at Mainamoti Hill — a major archaeological site.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.7, "14 km from Comilla city", "Archaeology", "Buddhism", "History"),
            makePlace("com-2", "Mainamoti Museum", "Heritage",
                "A rich museum displaying Buddhist antiquities from the Mainamoti excavations, including gold ornaments.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.5, "14 km from Comilla city", "Museum", "History", "Buddhism"),
            makePlace("com-3", "Kutila Mura", "Heritage",
                "Three Buddhist stupas on Mainamoti hill, thought to enshrine relics of the Buddha.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "15 km from Comilla city", "Archaeology", "History", "Ruins"),
            makePlace("com-4", "Lalmai Hills", "Nature",
                "Rare low terrace hills in the flat Bengal delta, covered with rainforest — great for nature walks and picnics.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "12 km from Comilla city", "Nature Walk", "Picnic", "Unique Landscape"),
            makePlace("com-5", "Shaheed Dhirendranath Datta Stadium & Town", "Heritage",
                "Comilla's historic town center with colonial-era buildings and the Kandirpar area.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.1, "Comilla city center", "Heritage", "History", "Walking Tour"),
            makePlace("com-6", "Bangladesh Academy for Rural Development (BARD)", "Heritage",
                "A pioneering rural development institution campus with beautiful gardens and historic buildings.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.3, "10 km from Comilla city", "Education", "Garden", "Architecture")
        ));

        // ── Narayanganj / Sonargaon ─────────────────────────────────────────
        LOCATION_PLACES.put("narayanganj", Arrays.asList(
            makePlace("nrj-1", "Panam City (Sonargaon)", "Heritage",
                "A ghost-town of 52 19th-century merchant mansions, once the capital of Bengal — surreal and hauntingly beautiful.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.8, "29 km from Dhaka", "Photography", "History", "Architecture"),
            makePlace("nrj-2", "Bangladesh Folk Art Museum", "Heritage",
                "A rich museum inside Sonargaon showcasing traditional folk art, crafts, and cultural heritage.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.6, "29 km from Dhaka", "Folk Art", "Museum", "Culture"),
            makePlace("nrj-3", "Idrakpur Fort", "Heritage",
                "A rare 17th-century Mughal water-fort built by Mir Jumla to defend against Arakanese pirates.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.5, "Munshiganj, 35 km from Dhaka", "History", "Fort", "Architecture"),
            makePlace("nrj-4", "Meghna River & Char Areas", "Nature",
                "Take a boat on the vast Meghna River to explore scenic char islands and experience life on the river.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Narayanganj city", "Boat Ride", "River", "Nature"),
            makePlace("nrj-5", "Sonargaon Old Capital Area", "Heritage",
                "Explore the medieval capital of Bengal, including the tomb of Ghiyasuddin Azam Shah and old ruins.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "30 km from Dhaka", "History", "Archaeology", "Heritage")
        ));

        // ── Gazipur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("gazipur", Arrays.asList(
            makePlace("gzp-1", "Bhawal National Park", "Wildlife",
                "A large forested park near Dhaka offering a refreshing escape — great for picnics and nature walks.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "40 km from Dhaka", "Nature Walk", "Picnic", "Biodiversity"),
            makePlace("gzp-2", "Bangabandhu Safari Park", "Wildlife",
                "Bangladesh's only official safari park with open-range enclosures for tigers, lions, zebras and giraffes.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "40 km from Dhaka", "Safari", "Wildlife", "Family Fun"),
            makePlace("gzp-3", "Nuhash Palli", "Culture",
                "The private estate of legendary author Humayun Ahmed — a literary-inspired garden paradise with sculptures.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.7, "40 km from Dhaka", "Literature", "Garden", "Cultural Heritage"),
            makePlace("gzp-4", "Bali Para Eco Park", "Nature",
                "A nature park beside the Turag River, popular for boating, picnics, and enjoying riverside greenery.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.2, "25 km from Dhaka", "Picnic", "Boating", "Nature")
        ));

        // ── Dinajpur ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("dinajpur", Arrays.asList(
            makePlace("dnj-1", "Kantaji Temple", "Religious",
                "An 18th-century ornate terracotta temple adorned with extraordinary panelling depicting scenes from Hindu epics.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.9, "20 km from Dinajpur town", "Terracotta Art", "History", "Architecture"),
            makePlace("dnj-2", "Ramsagar National Park", "Nature",
                "The largest man-made lake in Bangladesh (1754 AD) set in a forested park — popular with migratory birds.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.6, "8 km from Dinajpur town", "Lake", "Birdwatching", "Picnic"),
            makePlace("dnj-3", "Nayabad Mosque", "Heritage",
                "A 13th-century mosque, one of the oldest in Bangladesh, with beautiful terracotta ornamentation.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.5, "32 km from Dinajpur town", "History", "Architecture", "Terracotta"),
            makePlace("dnj-4", "Rajbari (Dinajpur Palace)", "Heritage",
                "The palace complex of the Maharajas of Dinajpur, featuring classical columns and peacock courtyards.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "Dinajpur town center", "Architecture", "History", "Photography"),
            makePlace("dnj-5", "Swapnapuri Amusement Park", "Nature",
                "A popular amusement park and botanical garden known as the 'Dream City' — great for families.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.3, "25 km from Dinajpur town", "Family Fun", "Garden", "Amusement Park")
        ));

        // ── Rangpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("rangpur", Arrays.asList(
            makePlace("rpr-1", "Tajhat Palace", "Heritage",
                "A magnificent late-19th-century palace with marble staircases and a stunning European façade in lush gardens.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.7, "3 km from Rangpur city", "Architecture", "History", "Gardens"),
            makePlace("rpr-2", "Carmichael College", "Heritage",
                "A beautiful colonial heritage building (1916) with ornate architecture — iconic for photographs.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.4, "Rangpur city", "Architecture", "Heritage", "Photography"),
            makePlace("rpr-3", "Vintage Car Museum (Tajhat)", "Heritage",
                "A rare museum displaying a collection of vintage cars, carriages and historical vehicles.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.3, "3 km from Rangpur city", "Museum", "Vintage Cars", "Heritage"),
            makePlace("rpr-4", "Nilphamari Nilsagar Lake", "Nature",
                "A large scenic lake and park in Nilphamari, perfect for boating and picnicking.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.5, "55 km from Rangpur city", "Lake", "Picnic", "Boating"),
            makePlace("rpr-5", "Teesta River Barrage", "Nature",
                "A major river barrage on the Teesta River — the surrounding area offers scenic views and riverside walks.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "45 km from Rangpur city", "River View", "Photography", "Engineering Marvel")
        ));

        // ── Sunamganj ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("sunamganj", Arrays.asList(
            makePlace("snj-1", "Tanguar Haor", "Wildlife",
                "A UNESCO Ramsar Wetland — one of Bangladesh's most ecologically important wetlands, home to thousands of migratory birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.9, "40 km from Sunamganj town", "Birdwatching", "Boat Tour", "Wildlife"),
            makePlace("snj-2", "Jadukata River", "Nature",
                "A crystal-clear river flowing from the Meghalaya hills, famous for its transparent water and breathtaking scenery.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.7, "35 km from Sunamganj town", "River", "Photography", "Scenic"),
            makePlace("snj-3", "Tekerghat Limestone Lake", "Nature",
                "A stunning turquoise-blue lake formed in an abandoned stone quarry, surrounded by hills and forest.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.8, "41 km from Sunamganj town", "Photography", "Swimming", "Unique"),
            makePlace("snj-4", "Shah Abdul Karim's Village", "Cultural",
                "The birthplace of legendary folk singer Shah Abdul Karim — a pilgrimage for lovers of Baul music.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.5, "20 km from Sunamganj town", "Folk Music", "Culture", "Heritage"),
            makePlace("snj-5", "Hasan Raja Memorial", "Cultural",
                "A cultural site commemorating the great 19th-century mystic poet Hasan Raja of Sunamganj.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "12 km from Sunamganj town", "Poetry", "Culture", "History")
        ));

        // ── Kishoreganj ───────────────────────────────────────────────────────
        LOCATION_PLACES.put("kishoreganj", Arrays.asList(
            makePlace("kgj-1", "Sholakia Eidgah", "Heritage",
                "One of the largest Eid congregation grounds in the world, hosting over 500,000 worshippers annually on the banks of the Narshunda River.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.9, "8 km from Kishoreganj town", "Eid Festival", "Religious", "Historic"),
            makePlace("kgj-2", "Jangalbari Fort", "Heritage",
                "A 16th-century riverside fortress built by Isa Khan, the great Baro-Bhuiyans chieftain who defied Mughal rule.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.7, "25 km from Kishoreganj town", "History", "Fort", "River View"),
            makePlace("kgj-3", "Egaro Sindhu", "Nature",
                "A breathtaking confluence of eleven rivers near Bajitpur — a vast inland sea during monsoon, a paradise for boat lovers.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "30 km from Kishoreganj town", "Boat Ride", "Photography", "Scenic"),
            makePlace("kgj-4", "Nikli Haor", "Wildlife",
                "A stunning haor wetland transforming into a glittering inland sea in the rainy season, home to migratory birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "35 km from Kishoreganj town", "Birdwatching", "Boat Tour", "Wetland"),
            makePlace("kgj-5", "Chandrabati Temple", "Religious",
                "Home of Chandrabati (1550–1600), the first female poet of the Bengali language — a deeply significant cultural heritage site.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.5, "18 km from Kishoreganj town", "Cultural Heritage", "History", "Literature"),
            makePlace("kgj-6", "Austagram Panam Bridge", "Heritage",
                "A 400-year-old Mughal-era bridge spanning the Meghna — one of the oldest surviving bridges in Bangladesh.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "45 km from Kishoreganj town", "History", "Architecture", "Photography"),
            makePlace("kgj-7", "Mithamin Haor", "Wildlife",
                "A large haor famous for its stunning natural beauty, fishing culture, and flocks of migratory birds in winter.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.6, "28 km from Kishoreganj town", "Birdwatching", "Fishing", "Scenic"),
            makePlace("kgj-8", "Pakundia Zamindar Bari", "Heritage",
                "A 19th-century zamindar mansion in Pakundia with classical architecture and a beautiful pond.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.2, "20 km from Kishoreganj town", "History", "Photography", "Architecture"),
            makePlace("kgj-9", "Narshunda River Walk", "Nature",
                "A peaceful riverside walk along the Narshunda River through the heart of Kishoreganj town at dusk.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Kishoreganj town center", "River Walk", "Relaxation", "Sunset")
        ));

        // ── Netrokona ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("netrokona", Arrays.asList(
            makePlace("ntn-1", "Birishiri Tribal Cultural Academy", "Heritage",
                "Experience the vibrant culture of the Garo and Hajong indigenous people at this beautiful cultural center.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.6, "37 km from Netrokona town", "Culture", "Tribal Art", "Photography"),
            makePlace("ntn-2", "Someswari River", "Nature",
                "A scenic blue-green river at the base of Meghalaya hills — beautiful for boat rides and photography.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.7, "37 km from Netrokona town", "River", "Photography", "Nature"),
            makePlace("ntn-3", "Kumarkanda Zamindarbari", "Heritage",
                "An impressive zamindari palace with colonial architecture, ornate gateways and peaceful gardens.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "15 km from Netrokona town", "Heritage", "Photography", "History"),
            makePlace("ntn-4", "Maghan Bihanga Haor", "Wildlife",
                "A beautiful seasonal wetland perfect for birdwatching and experiencing the haor culture of Bangladesh.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "25 km from Netrokona town", "Birdwatching", "Boat Tour", "Nature")
        ));

        // ── Barishal ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("barishal", Arrays.asList(
            makePlace("brs-1", "Floating Guava Market (Swarupkathi)", "Nature",
                "A unique floating market where guavas are sold from boats moored in the river — a rare and fascinating sight.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.7, "28 km from Barishal city", "Floating Market", "Photography", "Unique"),
            makePlace("brs-2", "Durga Sagar Lake", "Nature",
                "A large historic lake (1780 AD) in a lush park, famous for hosting thousands of migratory birds in winter.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.5, "12 km from Barishal city", "Birdwatching", "Lake", "Picnic"),
            makePlace("brs-3", "Oxford Mission Church", "Heritage",
                "A stunning Gothic church built in 1889 by the Oxford Mission — one of the finest colonial-era churches in Bangladesh.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.6, "Barishal city center", "Heritage", "Architecture", "Photography"),
            makePlace("brs-4", "Barishal River & Launch Ghat", "Nature",
                "Experience the famous 'Venice of Bangladesh' — a vast network of rivers, canals and launches.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.5, "Barishal city center", "River Cruise", "Photography", "Scenic"),
            makePlace("brs-5", "Guthia Mosque", "Religious",
                "A stunning modern mosque built on a large pond — the complex includes a beautiful garden and waterways.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "18 km from Barishal city", "Religious", "Architecture", "Photography"),
            makePlace("brs-6", "Bhati Khata (Floating Village)", "Nature",
                "A fascinating floating village in the backwaters of Barishal where people live entirely on the water.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "20 km from Barishal city", "Village Life", "Boat Tour", "Photography")
        ));

        // ── Jessore ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("jessore", Arrays.asList(
            makePlace("jsr-1", "Jessore Flower Market (Panisara)", "Nature",
                "The largest flower cultivation zone in Bangladesh — endless fields of roses, gerberas and tuberose.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "32 km from Jessore town", "Flowers", "Photography", "Unique"),
            makePlace("jsr-2", "Michael Madhusudan Birthplace", "Heritage",
                "The birthplace of Michael Madhusudan Dutt (1824), the revolutionary Bengali poet who introduced blank verse.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Keshobpur, 42 km from Jessore", "Literature", "History", "Heritage"),
            makePlace("jsr-3", "Jessore Cantonment Museum", "Heritage",
                "A military museum with artifacts from Bangladesh's independence struggle and colonial military history.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.3, "Jessore town", "Museum", "Military History", "Education"),
            makePlace("jsr-4", "Chougachha Zamindarbari", "Heritage",
                "An impressive 19th-century zamindari complex with ornate architecture and large water tanks.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.2, "30 km from Jessore town", "Heritage", "Photography", "History")
        ));

        // ── Faridpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("faridpur", Arrays.asList(
            makePlace("fdp-1", "Padma River (Goalanda)", "Nature",
                "Experience the vast and mighty Padma River at Goalanda Ghat — dramatic river views and fishing boats.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "55 km from Faridpur town", "River View", "Photography", "Scenic"),
            makePlace("fdp-2", "Rajbari Zamindarbari", "Heritage",
                "An impressive zamindar palace complex with classical European architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "Rajbari district", "Heritage", "History", "Photography"),
            makePlace("fdp-3", "Faridpur Sarkari Dighi", "Nature",
                "A large government lake in Faridpur town, popular for evening walks and relaxation.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.1, "Faridpur town center", "Lake", "Walking", "Relaxation")
        ));

        // ── Khagrachhari ──────────────────────────────────────────────────────
        LOCATION_PLACES.put("khagrachhari", Arrays.asList(
            makePlace("kgr-1", "Alutila Cave (Matai Para Tunnel)", "Nature",
                "A natural cave and dark tunnel through a hill — the most adventurous attraction in Khagrachhari.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.8, "7 km from Khagrachhari town", "Cave", "Adventure", "Trekking"),
            makePlace("kgr-2", "Richang Waterfall", "Nature",
                "A beautiful multi-tiered waterfall a short trek from Khagrachhari town, through lush forest.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "8 km from Khagrachhari town", "Waterfall", "Trekking", "Photography"),
            makePlace("kgr-3", "Sajek Valley", "Nature",
                "The most popular destination in the Chittagong Hill Tracts — a cloud-sea hilltop at 1,800 ft.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.9, "70 km from Khagrachhari town", "Cloud Sea", "Sunrise", "Hill Station"),
            makePlace("kgr-4", "Dibir Lake", "Nature",
                "A tranquil lake surrounded by hills and forest — ideal for swimming and relaxation.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.5, "40 km from Khagrachhari town", "Lake", "Swimming", "Camping")
        ));

        // ── Moulvibazar ──────────────────────────────────────────────────────
        LOCATION_PLACES.put("moulvibazar", Arrays.asList(
            makePlace("mvb-1", "Srimangal Tea Gardens", "Nature",
                "The tea capital of Bangladesh — walk through endless rows of manicured tea bushes with hills behind.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.8, "20 km from Moulvibazar town", "Tea Garden", "Photography", "Scenic"),
            makePlace("mvb-2", "Lawachara National Park", "Wildlife",
                "A tropical rainforest park — home to the critically endangered Western Hoolock Gibbon.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.8, "25 km from Moulvibazar town", "Trekking", "Wildlife", "Gibbon Spotting"),
            makePlace("mvb-3", "Madhabpur Lake", "Nature",
                "A stunning jade-green lake set in a floating tea garden — one of the most photogenic spots in Bangladesh.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.7, "12 km from Moulvibazar town", "Lake", "Tea Garden", "Photography"),
            makePlace("mvb-4", "Hakaluki Haor", "Wildlife",
                "The largest haor in Bangladesh, covering over 180 sq km — a biodiversity hotspot for birds and fish.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "30 km from Moulvibazar town", "Birdwatching", "Boat Tour", "Wildlife"),
            makePlace("mvb-5", "Seven-Layer Tea (Srimangal)", "Cultural",
                "Try the famous 7-layer tea invented in Srimangal — each layer has a different taste and color.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.7, "22 km from Moulvibazar town", "Food Experience", "Culture", "Unique")
        ));

        // ── Noakhali ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("noakhali", Arrays.asList(
            makePlace("nkh-1", "Nijhum Dwip", "Wildlife",
                "A beautiful island in the Bay of Bengal — home to the largest herd of spotted deer in Bangladesh and rich bird life.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.8, "100 km south of Noakhali town", "Deer Safari", "Birdwatching", "Island"),
            makePlace("nkh-2", "Maijdi Gandhi Ashram", "Heritage",
                "The ashram founded by Mahatma Gandhi during the 1946 Noakhali riots — preserved as a heritage and peace site.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Jayag, 30 km from Noakhali town", "Heritage", "Gandhian History", "Peace"),
            makePlace("nkh-3", "Bashbaria Beach", "Beach",
                "A quiet and beautiful beach at the southern tip of Noakhali, with mangrove forests meeting the sea.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.4, "80 km south of Noakhali town", "Beach", "Mangrove", "Nature")
        ));

        // ╔══════════════════════════════════════════════════════════════════╗
        // ║  ADDITIONAL DISTRICTS (Completing all 64 districts)              ║
        // ╚══════════════════════════════════════════════════════════════════╝

        // ── DHAKA DIVISION (Additional) ───────────────────────────────────────

        // ── Gopalganj ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("gopalganj", Arrays.asList(
            makePlace("gpg-1", "Bangabandhu Memorial Museum", "Heritage",
                "The ancestral home and museum of Father of the Nation Bangabandhu Sheikh Mujibur Rahman.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.9, "Tungipara, Gopalganj", "History", "Museum", "National Heritage"),
            makePlace("gpg-2", "Madhumati River", "Nature",
                "A scenic river perfect for boat rides and experiencing rural Bangladesh life.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Gopalganj town", "River", "Boating", "Nature"),
            makePlace("gpg-3", "Ulpur River Port", "Cultural",
                "A bustling river port with traditional wooden boats and vibrant market life.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.2, "Ulpur, Gopalganj", "River Port", "Culture", "Photography")
        ));

        // ── Madaripur ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("madaripur", Arrays.asList(
            makePlace("mdp-1", "Arial Beel", "Wildlife",
                "A large wetland and beel, home to diverse waterfowl and migratory birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "15 km from Madaripur town", "Birdwatching", "Wetland", "Nature"),
            makePlace("mdp-2", "Kumar River", "Nature",
                "A beautiful river flowing through Madaripur, perfect for scenic boat tours.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Madaripur town", "River", "Boating", "Relaxation"),
            makePlace("mdp-3", "Madaripur Museum", "Heritage",
                "District museum showcasing local history, culture, and artifacts.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.2, "Madaripur town center", "Museum", "History", "Education")
        ));

        // ── Manikganj ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("manikganj", Arrays.asList(
            makePlace("mkg-1", "Baliati Zamindar Palace", "Heritage",
                "A grand 18th-century zamindar palace complex with intricate architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.6, "20 km from Manikganj town", "Heritage", "Architecture", "History"),
            makePlace("mkgbazar-2", "Teota Zamindar Bari", "Heritage",
                "Historic zamindar mansion with beautiful terracotta work.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.4, "Teota, Manikganj", "Heritage", "Architecture", "Photography"),
            makePlace("mkg-3", "Kaliganga River", "Nature",
                "Scenic river flowing through Manikganj, ideal for boat rides.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Manikganj", "River", "Nature", "Boating")
        ));

        // ── Munshiganj ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("munshiganj", Arrays.asList(
            makePlace("msg-1", "Idrakpur Fort", "Heritage",
                "A rare 17th-century Mughal water-fort built to defend against pirates.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.7, "8 km from Munshiganj town", "Fort", "History", "Architecture"),
            makePlace("msg-2", "Baba Adam Mosque", "Religious",
                "A 15th-century sultanate mosque with beautiful architecture.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.5, "Munshiganj town", "Religious", "Heritage", "Architecture"),
            makePlace("msg-3", "Meghna River", "Nature",
                "The mighty Meghna River, perfect for river cruises and sunset viewing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Munshiganj", "River", "Boating", "Sunset")
        ));

        // ── Narsingdi ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("narsingdi", Arrays.asList(
            makePlace("nsd-1", "Wari-Bateshwar", "Heritage",
                "Ancient archaeological site dating back 2,500 years with ruins and artifacts.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.8, "30 km from Narsingdi town", "Archaeology", "History", "Ancient Ruins"),
            makePlace("nsd-2", "Madhabdi Mela Ground", "Cultural",
                "Famous fairground hosting one of Bangladesh's largest traditional fairs.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.4, "Madhabdi, Narsingdi", "Fair", "Culture", "Festival"),
            makePlace("nsd-3", "Brahmaputra River", "Nature",
                "Scenic river with beautiful sunset views and boating opportunities.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Narsingdi", "River", "Boating", "Nature")
        ));

        // ── Rajbari ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("rajbari", Arrays.asList(
            makePlace("rjb-1", "Rajbari Palace", "Heritage",
                "Historic palace of the local zamindars with colonial architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Rajbari town", "Heritage", "Architecture", "History"),
            makePlace("rjb-2", "Padma River", "Nature",
                "Majestic river offering scenic views and boat tours.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Rajbari", "River", "Boating", "Sunset"),
            makePlace("rjb-3", "Goalanda Ghat", "Cultural",
                "Historic river port connecting Rajbari to other regions, bustling with river life.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Goalanda, Rajbari", "River Port", "History", "Culture")
        ));

        // ── Shariatpur ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("shariatpur", Arrays.asList(
            makePlace("srt-1", "Padma River Char Islands", "Nature",
                "Seasonal river islands (chars) with unique rural life and agriculture.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Along Padma River", "Char Island", "Nature", "Rural Life"),
            makePlace("srt-2", "Naria Riverside", "Nature",
                "Peaceful riverside area perfect for boat rides and fishing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Naria, Shariatpur", "River", "Fishing", "Nature"),
            makePlace("srt-3", "Palong Mosque", "Religious",
                "Historic mosque with beautiful traditional architecture.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.3, "Palong, Shariatpur", "Religious", "Heritage", "Architecture")
        ));

        // ── CHITTAGONG DIVISION (Additional) ──────────────────────────────────

        // ── Brahmanbaria ──────────────────────────────────────────────────────
        LOCATION_PLACES.put("brahmanbaria", Arrays.asList(
            makePlace("brb-1", "Titas River", "Nature",
                "Famous river immortalized in Adwaita Mallabarman's novel 'Titash Ekti Nadir Naam'.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "Brahmanbaria", "River", "Literature", "Culture"),
            makePlace("brb-2", "Ashuganj River Port", "Cultural",
                "Major river port on the Meghna River with busy trade activities.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Ashuganj, Brahmanbaria", "River Port", "Industry", "Culture"),
            makePlace("brb-3", "Meghna-Gomti Bridge", "Heritage",
                "Impressive bridge offering panoramic views of river confluence.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.4, "Brahmanbaria", "Bridge", "Photography", "Engineering")
        ));

        // ── Chandpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("chandpur", Arrays.asList(
            makePlace("chp-1", "Meghna-Padma Confluence", "Nature",
                "Spectacular meeting point of two mighty rivers creating a vast water expanse.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.7, "Chandpur town", "River", "Photography", "Nature"),
            makePlace("chp-2", "Chandpur River Port", "Cultural",
                "One of Bangladesh's busiest river ports with constant ferry and boat traffic.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Chandpur town", "River Port", "Culture", "Photography"),
            makePlace("chp-3", "Meghna Bridge Area", "Heritage",
                "Engineering marvel connecting Chandpur across the mighty Meghna.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.5, "Chandpur", "Bridge", "Engineering", "Photography")
        ));

        // ── Feni ──────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("feni", Arrays.asList(
            makePlace("fni-1", "Feni River", "Nature",
                "Scenic river forming a natural boundary between Bangladesh and India.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Feni", "River", "Nature", "Border"),
            makePlace("fni-2", "Bijoy Singh Dighi", "Heritage",
                "Historic lake built in the 19th century, popular for evening walks.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.3, "Feni town", "Lake", "Heritage", "Relaxation"),
            makePlace("fni-3", "Mohipal Mausoleum", "Heritage",
                "Ancient mausoleum with historical significance.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.2, "Feni", "Heritage", "History", "Architecture")
        ));

        // ── Lakshmipur ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("lakshmipur", Arrays.asList(
            makePlace("lkp-1", "Meghna River Estuary", "Nature",
                "Where the Meghna River meets the Bay of Bengal - vast water horizons.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "Southern Lakshmipur", "River", "Nature", "Photography"),
            makePlace("lkp-2", "Char Alexander", "Nature",
                "Large river island with unique ecosystem and fishing communities.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Meghna estuary", "Char Island", "Nature", "Culture"),
            makePlace("lkp-3", "Lakshmipur Museum", "Heritage",
                "District museum showcasing local history and culture.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.2, "Lakshmipur town", "Museum", "History", "Education")
        ));

        // ── RAJSHAHI DIVISION (Additional) ────────────────────────────────────

        // ── Joypurhat ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("joypurhat", Arrays.asList(
            makePlace("jph-1", "Joypurhat Sugar Mills Area", "Cultural",
                "Historic industrial area with scenic sugarcane landscapes.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.2, "Joypurhat", "Industry", "Culture", "Agriculture"),
            makePlace("jph-2", "Panchbibi Rajbari", "Heritage",
                "Historic zamindar palace with traditional architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "Panchbibi, Joypurhat", "Heritage", "Architecture", "History"),
            makePlace("jph-3", "Atrai River", "Nature",
                "Scenic river flowing through Joypurhat, perfect for boat rides.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Joypurhat", "River", "Nature", "Boating")
        ));

        // ── Naogaon ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("naogaon", Arrays.asList(
            makePlace("ngn-1", "Somapura Mahavihara (Paharpur)", "Heritage",
                "UNESCO World Heritage Site - ancient Buddhist monastery ruins from 8th century.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.9, "Paharpur, 45 km from Naogaon", "UNESCO", "Archaeology", "Buddhism"),
            makePlace("ngn-2", "Kusumba Mosque", "Religious",
                "Beautiful 16th-century terracotta mosque with intricate designs.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "5 km from Naogaon", "Terracotta", "Heritage", "Architecture"),
            makePlace("ngn-3", "Patnitala Rajbari", "Heritage",
                "Historic zamindar palace with colonial-era architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "Patnitala, Naogaon", "Heritage", "Architecture", "History"),
            makePlace("ngn-4", "Atrai River", "Nature",
                "Peaceful river with scenic rural surroundings.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Naogaon", "River", "Nature", "Relaxation")
        ));

        // ── Natore ────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("natore", Arrays.asList(
            makePlace("ntr-1", "Uttara Ganabhaban", "Heritage",
                "Northern residence of the President of Bangladesh with beautiful gardens.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.6, "Natore town", "Heritage", "Gardens", "Government"),
            makePlace("ntr-2", "Rani Bhawani Palace", "Heritage",
                "Historic palace of the famous philanthropist Rani Bhawani.",
                "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                4.7, "Natore town", "Heritage", "History", "Architecture"),
            makePlace("ntr-3", "Natore Rajbari", "Heritage",
                "Ancient royal palace with stunning architecture and lotus ponds.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.6, "Natore town", "Heritage", "Architecture", "Photography"),
            makePlace("ntr-4", "Halti Dighi", "Nature",
                "Large historic lake in Natore town, perfect for evening walks.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.3, "Natore town", "Lake", "Relaxation", "Walking")
        ));

        // ── Chapainawabganj ───────────────────────────────────────────────────
        LOCATION_PLACES.put("chapainawabganj", Arrays.asList(
            makePlace("cnj-1", "Sona Mosque (Golden Mosque)", "Heritage",
                "A stunning 15th-century mosque covered in golden-hued stone.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.8, "25 km from Chapainawabganj", "Heritage", "Architecture", "History"),
            makePlace("cnj-2", "Chhoto Sona Mosque", "Heritage",
                "Smaller but equally beautiful 15th-century mosque near Sona Mosque.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "Near Sona Mosque", "Heritage", "Architecture", "History"),
            makePlace("cnj-3", "Tahkhana Complex", "Heritage",
                "Ancient Mughal structure with underground chambers.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Chapainawabganj", "Heritage", "Mughal", "Architecture"),
            makePlace("cnj-4", "Mahananda River", "Nature",
                "Scenic river forming border with India, known for mango orchards along banks.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Chapainawabganj", "River", "Nature", "Mangoes")
        ));

        // ── Pabna ─────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("pabna", Arrays.asList(
            makePlace("pbn-1", "Hardinge Bridge", "Heritage",
                "Historic railway bridge over Padma River, built in 1915 - engineering marvel.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.8, "Paksey, Pabna", "Bridge", "Engineering", "History"),
            makePlace("pbn-2", "Pabna Mental Hospital", "Heritage",
                "Historic mental health institution from 1957 with colonial architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.2, "Pabna town", "Heritage", "History", "Medical"),
            makePlace("pbn-3", "Radhanagar Mosque", "Religious",
                "Ancient mosque with terracotta ornamentation.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.4, "Pabna", "Religious", "Heritage", "Architecture"),
            makePlace("pbn-4", "Padma River", "Nature",
                "Mighty river offering boat rides and stunning sunset views.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Pabna", "River", "Sunset", "Boating")
        ));

        // ── Sirajganj ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("sirajganj", Arrays.asList(
            makePlace("srj-1", "Bangabandhu Bridge (Jamuna Bridge)", "Heritage",
                "One of the longest bridges in South Asia crossing the mighty Jamuna River.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.9, "20 km from Sirajganj", "Bridge", "Engineering", "Modern"),
            makePlace("srj-2", "Jamuna River", "Nature",
                "Mighty river perfect for boat rides and experiencing char island life.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "Sirajganj", "River", "Nature", "Boating"),
            makePlace("srj-3", "Kazipur River Port", "Cultural",
                "Historic river port with traditional boats and vibrant market.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Kazipur, Sirajganj", "River Port", "Culture", "Market"),
            makePlace("srj-4", "Sirajganj Circuit House", "Heritage",
                "Colonial-era government building with beautiful gardens.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.2, "Sirajganj town", "Heritage", "Colonial", "Architecture")
        ));

        // ── KHULNA DIVISION (Additional) ──────────────────────────────────────

        // ── Bagerhat ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("bagerhat", Arrays.asList(
            makePlace("bgh-1", "Sixty Dome Mosque (Shat Gombuj Masjid)", "Heritage",
                "UNESCO World Heritage Site - massive 15th-century mosque with 77 domes.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.9, "Bagerhat town", "UNESCO", "Heritage", "Architecture"),
            makePlace("bgh-2", "Nine Dome Mosque", "Heritage",
                "Beautiful example of Khan Jahan Ali's architectural legacy.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.7, "Bagerhat", "Heritage", "Architecture", "History"),
            makePlace("bgh-3", "Khan Jahan Ali Mausoleum", "Religious",
                "Sacred tomb of the 15th-century Muslim saint Khan Jahan Ali.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.8, "Bagerhat town", "Religious", "Pilgrimage", "History"),
            makePlace("bgh-4", "Mongla Port", "Cultural",
                "Bangladesh's second largest seaport and gateway to Sundarbans.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Mongla, Bagerhat", "Port", "Industry", "Sundarbans Gateway"),
            makePlace("bgh-5", "Sundarbans (Bagerhat Side)", "Wildlife",
                "Access point to the world's largest mangrove forest and Royal Bengal Tiger habitat.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.9, "Southern Bagerhat", "Wildlife", "Mangrove", "Tigers")
        ));

        // ── Chuadanga ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("chuadanga", Arrays.asList(
            makePlace("chd-1", "Damurhuda Border", "Cultural",
                "Bangladesh-India border area with cultural exchange activities.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.2, "Damurhuda, Chuadanga", "Border", "Culture", "Trade"),
            makePlace("chd-2", "Mathabhanga River", "Nature",
                "Scenic river perfect for boat rides and rural tourism.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Chuadanga", "River", "Nature", "Boating"),
            makePlace("chd-3", "Chuadanga Shilpakala Academy", "Cultural",
                "Cultural center hosting traditional performances and exhibitions.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.1, "Chuadanga town", "Culture", "Art", "Performance")
        ));

        // ── Jhenaidah ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("jhenaidah", Arrays.asList(
            makePlace("jhd-1", "Bibichini Shahi Masjid", "Heritage",
                "Historic 17th-century mosque with traditional architecture.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.5, "Jhenaidah", "Religious", "Heritage", "Architecture"),
            makePlace("jhd-2", "Kaliganj Hot Springs", "Nature",
                "Natural hot water springs believed to have medicinal properties.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.4, "Kaliganj, Jhenaidah", "Hot Springs", "Nature", "Wellness"),
            makePlace("jhd-3", "Nabaganga River", "Nature",
                "Beautiful river flowing through Jhenaidah, ideal for boat tours.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Jhenaidah", "River", "Nature", "Boating")
        ));

        // ── Kushtia ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("kushtia", Arrays.asList(
            makePlace("kst-1", "Lalon Shah's Shrine", "Cultural",
                "Sacred shrine of the legendary Baul saint Lalon Shah - center of Baul philosophy.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.9, "Cheuria, Kushtia", "Spiritual", "Culture", "Music"),
            makePlace("kst-2", "Rabindra Kuthibari (Tagore's House)", "Heritage",
                "Nobel laureate Rabindranath Tagore's ancestral home and estate.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.8, "Shelaidaha, Kushtia", "Heritage", "Literature", "Museum"),
            makePlace("kst-3", "Hardinge Bridge (Kushtia Side)", "Heritage",
                "View of the historic railway bridge from Kushtia side of Padma.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.6, "Kushtia", "Bridge", "Engineering", "Photography"),
            makePlace("kst-4", "Gorai River", "Nature",
                "Scenic distributary of the Ganges, perfect for boat rides.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Kushtia", "River", "Nature", "Boating")
        ));

        // ── Magura ────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("magura", Arrays.asList(
            makePlace("mgr-1", "Magura Co-operative Dairies", "Cultural",
                "Famous dairy cooperative known for quality milk products.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.3, "Magura town", "Industry", "Agriculture", "Culture"),
            makePlace("mgr-2", "Nabaganga River", "Nature",
                "Peaceful river flowing through Magura district.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Magura", "River", "Nature", "Boating"),
            makePlace("mgr-3", "Magura District Museum", "Heritage",
                "Local museum showcasing district history and culture.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.1, "Magura town", "Museum", "History", "Education")
        ));

        // ── Meherpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("meherpur", Arrays.asList(
            makePlace("mhp-1", "Mujibnagar Memorial Complex", "Heritage",
                "Historic site where Bangladesh's provisional government took oath in 1971.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.9, "Vaidyanathtala, Meherpur", "Liberation War", "History", "National Heritage"),
            makePlace("mhp-2", "Mujibnagar Mango Gardens", "Nature",
                "Famous mango orchards producing high-quality mangoes.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "Meherpur", "Agriculture", "Nature", "Mangoes"),
            makePlace("mhp-3", "Gangni Riverside", "Nature",
                "Peaceful riverside area ideal for picnics and relaxation.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.2, "Gangni, Meherpur", "River", "Nature", "Picnic")
        ));

        // ── Narail ────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("narail", Arrays.asList(
            makePlace("nrl-1", "Victoria Jute Mills", "Heritage",
                "Historic jute mill from British era, symbol of industrial heritage.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.4, "Narail town", "Industrial Heritage", "History", "Culture"),
            makePlace("nrl-2", "Chitra River", "Nature",
                "Beautiful river perfect for boat rides and fishing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Narail", "River", "Nature", "Boating"),
            makePlace("nrl-3", "Shekher Char", "Nature",
                "River island with unique ecosystem and traditional fishing communities.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.2, "Narail", "Char Island", "Nature", "Culture")
        ));

        // ── Satkhira ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("satkhira", Arrays.asList(
            makePlace("stk-1", "Sundarbans (Satkhira Entrances)", "Wildlife",
                "Western access point to Sundarbans mangrove forest and tiger reserve.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.9, "Southern Satkhira", "Wildlife", "Mangrove", "Tigers"),
            makePlace("stk-2", "Bhomra Land Port", "Cultural",
                "Major Bangladesh-India land port for trade and cultural exchange.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Satkhira", "Border", "Trade", "Culture"),
            makePlace("stk-3", "Hamkura White Palace", "Heritage",
                "Historic white palace with beautiful architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Hamkura, Satkhira", "Heritage", "Architecture", "History"),
            makePlace("stk-4", "Raimangal River", "Nature",
                "Border river with stunning mangrove scenery.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Satkhira", "River", "Mangrove", "Nature")
        ));

        // ── BARISAL DIVISION (Additional) ─────────────────────────────────────

        // ── Barguna ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("barguna", Arrays.asList(
            makePlace("brg-1", "Sonakata River", "Nature",
                "Scenic river with beautiful sunset views and boat tours.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Barguna", "River", "Sunset", "Boating"),
            makePlace("brg-2", "Taltali Beach Area", "Beach",
                "Coastal area near Bay of Bengal with fishing villages.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.3, "Taltali, Barguna", "Beach", "Fishing", "Coast"),
            makePlace("brg-3", "Payra River", "Nature",
                "Major river flowing through Barguna to the Bay of Bengal.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Barguna", "River", "Nature", "Boating")
        ));

        // ── Bhola ─────────────────────────────────────────────────────────────
        LOCATION_PLACES.put("bhola", Arrays.asList(
            makePlace("bhl-1", "Meghna Estuary", "Nature",
                "Where the mighty Meghna meets the Bay of Bengal - vast water horizons.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.7, "Southern Bhola", "River", "Estuary", "Nature"),
            makePlace("bhl-2", "Char Kukri Kukri", "Wildlife",
                "Island bird sanctuary with thousands of migratory birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.6, "Bhola", "Birdwatching", "Wildlife", "Island"),
            makePlace("bhl-3", "Dhal Char", "Nature",
                "Large char island with unique ecosystem and fishing communities.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Bhola", "Char Island", "Nature", "Fishing"),
            makePlace("bhl-4", "Ilisha Fish Market", "Cultural",
                "Famous market for hilsa fish - Bangladesh's national fish.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.5, "Bhola town", "Market", "Culture", "Seafood")
        ));

        // ── Jhalokati ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("jhalokati", Arrays.asList(
            makePlace("jhl-1", "Biskhali River", "Nature",
                "Scenic river with traditional boat tours and rural beauty.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Jhalokati", "River", "Boating", "Nature"),
            makePlace("jhl-2", "Gabkhan Mosque", "Religious",
                "Historic mosque with beautiful traditional architecture.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.3, "Jhalokati", "Religious", "Heritage", "Architecture"),
            makePlace("jhl-3", "Floating Guava Market", "Cultural",
                "Unique floating market where guavas are sold directly from boats.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.6, "Jhalokati", "Floating Market", "Culture", "Agriculture")
        ));

        // ── Patuakhali ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("patuakhali", Arrays.asList(
            makePlace("ptk-1", "Kuakata Sea Beach", "Beach",
                "Rare beach where you can see both sunrise and sunset over the Bay of Bengal.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                4.8, "Kuakata, Patuakhali", "Beach", "Sunrise", "Sunset"),
            makePlace("ptk-2", "Fatrar Char", "Wildlife",
                "Island sanctuary with diverse wildlife and migratory birds.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "Near Kuakata", "Wildlife", "Birdwatching", "Island"),
            makePlace("ptk-3", "Lebur Char", "Nature",
                "Scenic char island with pristine beaches and fishing villages.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Patuakhali", "Island", "Beach", "Nature"),
            makePlace("ptk-4", "Rakhine Village", "Cultural",
                "Traditional Rakhine Buddhist community with unique culture.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.6, "Kuakata area", "Culture", "Buddhism", "Ethnic")
        ));

        // ── Pirojpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("pirojpur", Arrays.asList(
            makePlace("prj-1", "Floating Guava Market", "Cultural",
                "Famous floating market where guavas are traded directly from boats.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.7, "Pirojpur", "Floating Market", "Culture", "Agriculture"),
            makePlace("prj-2", "Swarupkati River Port", "Cultural",
                "Historic river port with traditional wooden boats.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Swarupkati, Pirojpur", "River Port", "Culture", "History"),
            makePlace("prj-3", "Baleshwar River", "Nature",
                "Beautiful river perfect for boat tours and fishing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Pirojpur", "River", "Boating", "Nature"),
            makePlace("prj-4", "Rahamatpur Forest", "Nature",
                "Small forested area with diverse plant species.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.2, "Pirojpur", "Forest", "Nature", "Walking")
        ));

        // ── RANGPUR DIVISION (Additional) ─────────────────────────────────────

        // ── Gaibandha ─────────────────────────────────────────────────────────
        LOCATION_PLACES.put("gaibandha", Arrays.asList(
            makePlace("gbd-1", "Brahmaputra River", "Nature",
                "Mighty river with char islands and spectacular river views.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "Gaibandha", "River", "Nature", "Char Islands"),
            makePlace("gbd-2", "Bamondanga Shahi Mosque", "Heritage",
                "Historic 18th-century mosque with traditional architecture.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.4, "Gaibandha", "Religious", "Heritage", "Architecture"),
            makePlace("gbd-3", "Gobindaganj Hat", "Cultural",
                "Traditional rural market offering authentic local experience.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.2, "Gobindaganj, Gaibandha", "Market", "Culture", "Rural")
        ));

        // ── Kurigram ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("kurigram", Arrays.asList(
            makePlace("krg-1", "Dharla River", "Nature",
                "Beautiful river with scenic char islands and rural beauty.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Kurigram", "River", "Nature", "Char Islands"),
            makePlace("krg-2", "Dudhkumar River", "Nature",
                "Scenic river flowing from India, popular for fishing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Kurigram", "River", "Fishing", "Nature"),
            makePlace("krg-3", "Ramna Temple", "Religious",
                "Historic Hindu temple with cultural significance.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.3, "Kurigram", "Religious", "Heritage", "Temple")
        ));

        // ── Lalmonirhat ───────────────────────────────────────────────────────
        LOCATION_PLACES.put("lalmonirhat", Arrays.asList(
            makePlace("lmn-1", "Teesta River", "Nature",
                "Beautiful river with scenic views and char islands.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.5, "Lalmonirhat", "River", "Nature", "Boating"),
            makePlace("lmn-2", "Burimari Land Port", "Cultural",
                "Bangladesh-India border crossing point and trade hub.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Burimari, Lalmonirhat", "Border", "Trade", "Culture"),
            makePlace("lmn-3", "Hatibandha Tea Gardens", "Nature",
                "Small tea gardens offering scenic views of tea cultivation.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.4, "Hatibandha, Lalmonirhat", "Tea", "Agriculture", "Nature")
        ));

        // ── Nilphamari ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("nilphamari", Arrays.asList(
            makePlace("nlp-1", "Chilahati Land Port", "Cultural",
                "Major Bangladesh-India border crossing point for trade.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.3, "Chilahati, Nilphamari", "Border", "Trade", "Railway"),
            makePlace("nlp-2", "Teesta Irrigation Project", "Heritage",
                "Major irrigation project transforming northern agriculture.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.4, "Nilphamari", "Engineering", "Agriculture", "Modern"),
            makePlace("nlp-3", "Sayedpur Airfield", "Heritage",
                "Historic airfield from British era with aviation heritage.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.2, "Sayedpur, Nilphamari", "Aviation", "History", "Heritage")
        ));

        // ── Panchagarh ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("panchagarh", Arrays.asList(
            makePlace("pch-1", "Banglabandha Land Port", "Cultural",
                "Only land port connecting Bangladesh, India, and Nepal trade route.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.4, "Banglabandha, Panchagarh", "Border", "Trade", "International"),
            makePlace("pch-2", "Rocks & Boulders Area", "Nature",
                "Unique rocky landscape at the foothills of Himalayas - rare in Bangladesh.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.7, "Panchagarh", "Geology", "Nature", "Unique"),
            makePlace("pch-3", "Mirzapur Shahi Mosque", "Heritage",
                "Historic 17th-century mosque with traditional terracotta.",
                "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                4.4, "Panchagarh", "Religious", "Heritage", "Architecture"),
            makePlace("pch-4", "Mahakalganj Himalayanfoothills", "Nature",
                "Northernmost area of Bangladesh at the edge of Himalayan foothills.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.6, "Mahakalganj, Panchagarh", "Hills", "Nature", "Scenic")
        ));

        // ── Thakurgaon ────────────────────────────────────────────────────────
        LOCATION_PLACES.put("thakurgaon", Arrays.asList(
            makePlace("thk-1", "Jamini Roy's Village", "Cultural",
                "Birthplace of famous artist Yamini Roy, traditional village atmosphere.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.5, "Thakurgaon", "Art", "Culture", "Heritage"),
            makePlace("thk-2", "Ramsagar (Thakurgaon)", "Nature",
                "Large historic lake with scenic surroundings and boating.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.4, "Thakurgaon", "Lake", "Boating", "Nature"),
            makePlace("thk-3", "Tangon River", "Nature",
                "Scenic river with rural beauty and traditional boat activities.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.3, "Thakurgaon", "River", "Nature", "Rural"),
            makePlace("thk-4", "Akhanagar Buddhist Temple Ruins", "Heritage",
                "Ancient Buddhist archaeological site with historical ruins.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.5, "Thakurgaon", "Archaeology", "Buddhism", "Ancient")
        ));

        // ── MYMENSINGH DIVISION (Additional) ──────────────────────────────────

        // ── Jamalpur ──────────────────────────────────────────────────────────
        LOCATION_PLACES.put("jamalpur", Arrays.asList(
            makePlace("jmp-1", "Brahmaputra River (Jamalpur)", "Nature",
                "Mighty river with spectacular views and char island ecosystem.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.6, "Jamalpur", "River", "Nature", "Char Islands"),
            makePlace("jmp-2", "Jamalpur Synthetic Fiber Factory", "Heritage",
                "Historic industrial site representing Bangladesh's industrial growth.",
                "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                4.2, "Jamalpur town", "Industry", "Heritage", "Modern"),
            makePlace("jmp-3", "Laura Building", "Heritage",
                "Colonial-era administrative building with classic architecture.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "Jamalpur town", "Colonial", "Heritage", "Architecture"),
            makePlace("jmp-4", "Jamuna River Bridge View", "Heritage",
                "View point to observe the mighty Jamuna and Bangabandhu Bridge.",
                "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                4.4, "Near Jamalpur", "Bridge", "Photography", "River")
        ));

        // ── Sherpur ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("sherpur", Arrays.asList(
            makePlace("shp-1", "Gajni Forest", "Wildlife",
                "Protected forest with diverse wildlife and trekking trails.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.6, "Sreebardi, Sherpur", "Forest", "Wildlife", "Trekking"),
            makePlace("shp-2", "Malancha Rest House", "Heritage",
                "Colonial-era rest house with beautiful garden setting.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.3, "Sherpur", "Colonial", "Heritage", "Gardens"),
            makePlace("shp-3", "Bhogai River", "Nature",
                "Scenic river perfect for boat rides and fishing.",
                "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                4.4, "Sherpur", "River", "Boating", "Nature"),
            makePlace("shp-4", "Madhupur Garh Hills", "Nature",
                "Elevated forested area with Garo tribal communities and nature trails.",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                4.5, "Nalitabari, Sherpur", "Hills", "Tribal Culture", "Nature")
        ));

        // ── SYLHET DIVISION (Additional) ──────────────────────────────────────

        // ── Habiganj (Full district data) ─────────────────────────────────────
        LOCATION_PLACES.put("habiganj", Arrays.asList(
            makePlace("hbj-1", "Satchari National Park", "Wildlife",
                "Protected rainforest with rare wildlife, primates, and diverse birdlife.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "40 km from Habiganj", "Wildlife", "Forest", "Trekking"),
            makePlace("hbj-2", "Madhabpur Lake", "Nature",
                "Scenic lake surrounded by hills and tea gardens, famous for lotus blooms.",
                "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                4.8, "25 km from Habiganj", "Lake", "Photography", "Scenic"),
            makePlace("hbj-3", "Rema-Kalenga Wildlife Sanctuary", "Wildlife",
                "Rich biodiversity with endangered species and pristine rainforest.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.7, "50 km from Habiganj", "Wildlife", "Conservation", "Birdwatching"),
            makePlace("hbj-4", "Baikka Beel", "Wildlife",
                "Large wetland sanctuary attracting migratory birds in winter.",
                "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                4.5, "Habiganj", "Wetland", "Birdwatching", "Nature"),
            makePlace("hbj-5", "Tea Gardens of Habiganj", "Nature",
                "Sprawling tea estates with scenic beauty and tea tourism.",
                "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                4.6, "Various locations in Habiganj", "Tea", "Agriculture", "Scenic")
        ));

        // ── Aliases ───────────────────────────────────────────────────────────
        LOCATION_PLACES.put("kishoregonj",   LOCATION_PLACES.get("kishoreganj"));
        LOCATION_PLACES.put("kishorganj",    LOCATION_PLACES.get("kishoreganj"));
        LOCATION_PLACES.put("kishorgonj",    LOCATION_PLACES.get("kishoreganj"));
        LOCATION_PLACES.put("coxs bazar",    LOCATION_PLACES.get("cox's bazar"));
        LOCATION_PLACES.put("cox bazar",     LOCATION_PLACES.get("cox's bazar"));
        LOCATION_PLACES.put("barisal",       LOCATION_PLACES.get("barishal"));
        LOCATION_PLACES.put("bogra",         LOCATION_PLACES.get("bogura"));
        LOCATION_PLACES.put("chattogram",    LOCATION_PLACES.get("chittagong"));
        LOCATION_PLACES.put("cumilla",       LOCATION_PLACES.get("comilla"));
        LOCATION_PLACES.put("sonargaon",     LOCATION_PLACES.get("narayanganj"));
        LOCATION_PLACES.put("moulvibari",    LOCATION_PLACES.get("moulvibazar"));
        LOCATION_PLACES.put("sreemangal",    LOCATION_PLACES.get("moulvibazar"));
        LOCATION_PLACES.put("srimangal",     LOCATION_PLACES.get("moulvibazar"));
        LOCATION_PLACES.put("sylhet division", LOCATION_PLACES.get("sylhet"));
        LOCATION_PLACES.put("jashore",       LOCATION_PLACES.get("jessore"));
        // Additional aliases for new districts
        LOCATION_PLACES.put("chapai nawabganj", LOCATION_PLACES.get("chapainawabganj"));
        LOCATION_PLACES.put("nawabganj",     LOCATION_PLACES.get("chapainawabganj"));
        LOCATION_PLACES.put("bagerhat district", LOCATION_PLACES.get("bagerhat"));
        LOCATION_PLACES.put("sreemongol",    LOCATION_PLACES.get("moulvibazar"));
        LOCATION_PLACES.put("patuakali",     LOCATION_PLACES.get("patuakhali"));
        LOCATION_PLACES.put("lalmonir hat",  LOCATION_PLACES.get("lalmonirhat"));
        LOCATION_PLACES.put("thakur gaon",   LOCATION_PLACES.get("thakurgaon"));
        LOCATION_PLACES.put("kushtia district", LOCATION_PLACES.get("kushtia"));
    }

    /**
     * Return recommended places for the given location name.
     * The lookup is case-insensitive and tolerates extra spaces.
     */
    public PlaceRecommendationResponse getRecommendations(String location) {
        String key = location == null ? "" : location.trim().toLowerCase();
        List<Place> places = LOCATION_PLACES.get(key);

        if (places == null || places.isEmpty()) {
            // Fallback: partial match on any stored key
            final String finalKey = key;
            places = LOCATION_PLACES.entrySet().stream()
                    .filter(e -> e.getKey().contains(finalKey) || finalKey.contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(Collections.emptyList());
        }

        String displayLocation = location == null ? "Unknown" : location.trim();
        return new PlaceRecommendationResponse(displayLocation, places);
    }

    /**
     * Returns all supported location names (sorted).
     */
    public List<String> getSupportedLocations() {
        return LOCATION_PLACES.keySet().stream()
                .filter(k -> LOCATION_PLACES.get(k) != null && !LOCATION_PLACES.get(k).isEmpty())
                .map(k -> k.substring(0, 1).toUpperCase() + k.substring(1))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get recommendations with sorting options
     * @param location Location name
     * @param sortBy Sort criteria: "rating", "name", "category"
     * @param category Optional category filter
     * @param searchQuery Optional search query for name/description/highlights
     * @return Filtered and sorted places
     */
    public PlaceRecommendationResponse getRecommendationsFiltered(
            String location, String sortBy, String category, String searchQuery) {
        
        PlaceRecommendationResponse response = getRecommendations(location);
        List<Place> places = response.getPlaces();

        if (places == null || places.isEmpty()) {
            return response;
        }

        // Apply category filter
        if (category != null && !category.trim().isEmpty() && !"all".equalsIgnoreCase(category)) {
            places = places.stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
        }

        // Apply search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            places = places.stream()
                    .filter(p -> 
                        (p.getName() != null && p.getName().toLowerCase().contains(query)) ||
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(query)) ||
                        (p.getHighlights() != null && p.getHighlights().stream()
                                .anyMatch(h -> h.toLowerCase().contains(query)))
                    )
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "rating":
                    places.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                    break;
                case "name":
                    places.sort(Comparator.comparing(Place::getName));
                    break;
                case "category":
                    places.sort(Comparator.comparing(Place::getCategory)
                            .thenComparing((a, b) -> Double.compare(b.getRating(), a.getRating())));
                    break;
                default:
                    // Default: sort by rating
                    places.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                    break;
            }
        }

        response.setPlaces(places);
        return response;
    }

    /**
     * Get top-rated places across all locations
     * @param limit Number of places to return
     * @return Top-rated places
     */
    public List<Place> getPopularPlaces(int limit) {
        return LOCATION_PLACES.values().stream()
                .flatMap(List::stream)
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit > 0 ? limit : 10)
                .collect(Collectors.toList());
    }

    /**
     * Get all available categories
     * @return List of unique categories
     */
    public List<String> getCategories() {
        return LOCATION_PLACES.values().stream()
                .flatMap(List::stream)
                .map(Place::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get places by category across all locations
     * @param category Category name
     * @param limit Maximum number of results
     * @return Places matching the category
     */
    public List<Place> getPlacesByCategory(String category, int limit) {
        return LOCATION_PLACES.values().stream()
                .flatMap(List::stream)
                .filter(p -> category != null && category.equalsIgnoreCase(p.getCategory()))
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit > 0 ? limit : 20)
                .collect(Collectors.toList());
    }

    /**
     * Search places across all locations
     * @param query Search query
     * @param limit Maximum number of results
     * @return Matching places
     */
    public List<Place> searchPlaces(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchTerm = query.toLowerCase().trim();
        return LOCATION_PLACES.values().stream()
                .flatMap(List::stream)
                .filter(p -> 
                    (p.getName() != null && p.getName().toLowerCase().contains(searchTerm)) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchTerm)) ||
                    (p.getCategory() != null && p.getCategory().toLowerCase().contains(searchTerm)) ||
                    (p.getHighlights() != null && p.getHighlights().stream()
                            .anyMatch(h -> h.toLowerCase().contains(searchTerm)))
                )
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit > 0 ? limit : 50)
                .collect(Collectors.toList());
    }
}
