package com.TeamDeadlock.ExploreBangladesh.config;

import com.TeamDeadlock.ExploreBangladesh.entity.*;
import com.TeamDeadlock.ExploreBangladesh.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final CityCoordinateRepository cityCoordinateRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final CarRepository carRepository;
    private final GuideRepository guideRepository;
    private final PlaceRepository placeRepository;

    public DataInitializer(CityCoordinateRepository cityCoordinateRepository,
                           AirportRepository airportRepository,
                           AirlineRepository airlineRepository,
                           CarRepository carRepository,
                           GuideRepository guideRepository,
                           PlaceRepository placeRepository) {
        this.cityCoordinateRepository = cityCoordinateRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.carRepository = carRepository;
        this.guideRepository = guideRepository;
        this.placeRepository = placeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedCityCoordinates();
        seedAirports();
        seedAirlines();
        seedCars();
        seedGuides();
        seedPlaces();
    }

    // ─────────────────────────────────────────────
    // City Coordinates (used by Hotels & Cars search)
    // ─────────────────────────────────────────────
    private void seedCityCoordinates() {
        if (cityCoordinateRepository.count() > 0) return;

        List<CityCoordinate> coords = Arrays.asList(
                new CityCoordinate("dhaka",        90.4125, 23.8103),
                new CityCoordinate("cox's bazar",  91.9670, 21.4272),
                new CityCoordinate("coxsbazar",    91.9670, 21.4272),
                new CityCoordinate("chittagong",   91.7832, 22.3569),
                new CityCoordinate("sylhet",       91.8687, 24.8949),
                new CityCoordinate("khulna",       89.5690, 22.8456),
                new CityCoordinate("rajshahi",     88.6042, 24.3745),
                new CityCoordinate("rangpur",      89.2517, 25.7439),
                new CityCoordinate("barishal",     90.3667, 22.7010),
                new CityCoordinate("mymensingh",   90.4066, 24.7471),
                new CityCoordinate("rangamati",    92.1821, 22.6372),
                new CityCoordinate("bandarban",    92.2184, 22.1953),
                new CityCoordinate("tangail",      89.9168, 24.2513)
        );
        cityCoordinateRepository.saveAll(coords);
        System.out.println("[DataInitializer] Seeded " + coords.size() + " city coordinates.");
    }

    // ─────────────────────────────────────────────
    // Airports (IATA codes used by Flights search)
    // ─────────────────────────────────────────────
    private void seedAirports() {
        if (airportRepository.count() > 0) return;

        List<Airport> airports = Arrays.asList(
                // Domestic
                new Airport("DAC", "Dhaka (Hazrat Shahjalal)"),
                new Airport("CGP", "Chittagong (Shah Amanat)"),
                new Airport("CXB", "Cox's Bazar"),
                new Airport("ZYL", "Sylhet (Osmani)"),
                new Airport("RJH", "Rajshahi (Shah Makhdum)"),
                new Airport("JSR", "Jessore"),
                new Airport("SPD", "Saidpur"),
                new Airport("BZL", "Barishal"),
                // International
                new Airport("DXB", "Dubai"),
                new Airport("SIN", "Singapore"),
                new Airport("BKK", "Bangkok"),
                new Airport("KUL", "Kuala Lumpur"),
                new Airport("DEL", "Delhi"),
                new Airport("CCU", "Kolkata"),
                new Airport("DOH", "Doha"),
                new Airport("JED", "Jeddah"),
                new Airport("LHR", "London Heathrow"),
                new Airport("JFK", "New York JFK")
        );
        airportRepository.saveAll(airports);
        System.out.println("[DataInitializer] Seeded " + airports.size() + " airports.");
    }

    // ─────────────────────────────────────────────
    // Airlines (carrier codes used by Flights search)
    // ─────────────────────────────────────────────
    private void seedAirlines() {
        if (airlineRepository.count() > 0) return;

        List<Airline> airlines = Arrays.asList(
                new Airline("BG", "Biman Bangladesh Airlines"),
                new Airline("BS", "US-Bangla Airlines"),
                new Airline("VQ", "Novoair"),
                new Airline("EK", "Emirates"),
                new Airline("QR", "Qatar Airways"),
                new Airline("SQ", "Singapore Airlines"),
                new Airline("TG", "Thai Airways"),
                new Airline("MH", "Malaysia Airlines"),
                new Airline("AI", "Air India"),
                new Airline("6E", "IndiGo"),
                new Airline("SV", "Saudia"),
                new Airline("BA", "British Airways"),
                new Airline("TK", "Turkish Airlines")
        );
        airlineRepository.saveAll(airlines);
        System.out.println("[DataInitializer] Seeded " + airlines.size() + " airlines.");
    }

    // ─────────────────────────────────────────────
    // Cars (per-city rental inventory)
    // ─────────────────────────────────────────────
    private void seedCars() {
        if (carRepository.count() > 0) return;

        List<CarEntity> cars = Arrays.asList(
                // Dhaka
                car("dhk-1", "Toyota Corolla",          "Sedan", 4,  3500, "photo-1623869675781-80aa31012a5a", "dhaka"),
                car("dhk-2", "Toyota Noah",             "Micro", 7,  4500, "photo-1570125909232-eb263c188f7e", "dhaka"),
                car("dhk-3", "Toyota Allion",           "Sedan", 4,  3000, "photo-1549399542-7e3f8b79c341",   "dhaka"),
                car("dhk-4", "Mitsubishi Pajero",       "SUV",   7,  7000, "photo-1519641471654-76ce0107ad1b", "dhaka"),
                car("dhk-5", "Toyota Hiace",            "Van",   12, 6000, "photo-1609520505218-7421df70e621", "dhaka"),
                car("dhk-6", "Honda City",              "Sedan", 4,  3200, "photo-1583121274602-3e2820c69888", "dhaka"),
                car("dhk-7", "Toyota Land Cruiser",     "SUV",   7, 12000, "photo-1533473359331-0135ef1b58bf", "dhaka"),
                car("dhk-8", "Nissan Sunny",            "Sedan", 4,  2800, "photo-1502877338535-766e1452684a", "dhaka"),
                // Chittagong
                car("ctg-1", "Toyota Axio",             "Sedan", 4,  3000, "photo-1549399542-7e3f8b79c341",   "chittagong"),
                car("ctg-2", "Toyota Hiace",            "Van",   12, 5500, "photo-1609520505218-7421df70e621", "chittagong"),
                car("ctg-3", "Mitsubishi Pajero Sport", "SUV",   7,  7500, "photo-1519641471654-76ce0107ad1b", "chittagong"),
                car("ctg-4", "Toyota Probox",           "Micro", 5,  2500, "photo-1570125909232-eb263c188f7e", "chittagong"),
                car("ctg-5", "Honda Civic",             "Sedan", 4,  4000, "photo-1583121274602-3e2820c69888", "chittagong"),
                // Cox's Bazar
                car("cxb-1", "Toyota Land Cruiser Prado", "SUV", 7,  8000, "photo-1533473359331-0135ef1b58bf", "cox's bazar"),
                car("cxb-2", "Mitsubishi Pajero",       "SUV",   7,  7000, "photo-1519641471654-76ce0107ad1b", "cox's bazar"),
                car("cxb-3", "Toyota Hiace",            "Van",   12, 5000, "photo-1609520505218-7421df70e621", "cox's bazar"),
                car("cxb-4", "Toyota Noah",             "Micro", 7,  4000, "photo-1570125909232-eb263c188f7e", "cox's bazar"),
                // Sylhet
                car("syl-1", "Toyota Corolla",          "Sedan", 4,  3200, "photo-1623869675781-80aa31012a5a", "sylhet"),
                car("syl-2", "Mitsubishi Pajero",       "SUV",   7,  6500, "photo-1519641471654-76ce0107ad1b", "sylhet"),
                car("syl-3", "Toyota Noah",             "Micro", 7,  4200, "photo-1570125909232-eb263c188f7e", "sylhet"),
                car("syl-4", "Toyota Hiace",            "Van",   12, 5500, "photo-1609520505218-7421df70e621", "sylhet"),
                // Rajshahi
                car("raj-1", "Toyota Allion",           "Sedan", 4,  2800, "photo-1549399542-7e3f8b79c341",   "rajshahi"),
                car("raj-2", "Toyota Noah",             "Micro", 7,  3800, "photo-1570125909232-eb263c188f7e", "rajshahi"),
                car("raj-3", "Nissan Sunny",            "Sedan", 4,  2500, "photo-1502877338535-766e1452684a", "rajshahi"),
                // Khulna
                car("khl-1", "Toyota Corolla",          "Sedan", 4,  3000, "photo-1623869675781-80aa31012a5a", "khulna"),
                car("khl-2", "Toyota Hiace",            "Van",   12, 5000, "photo-1609520505218-7421df70e621", "khulna"),
                car("khl-3", "Mitsubishi Pajero",       "SUV",   7,  6000, "photo-1519641471654-76ce0107ad1b", "khulna"),
                // Rangpur
                car("rng-1", "Toyota Axio",             "Sedan", 4,  2800, "photo-1549399542-7e3f8b79c341",   "rangpur"),
                car("rng-2", "Toyota Noah",             "Micro", 7,  3500, "photo-1570125909232-eb263c188f7e", "rangpur"),
                // Barishal
                car("bar-1", "Toyota Corolla",          "Sedan", 4,  2800, "photo-1623869675781-80aa31012a5a", "barishal"),
                car("bar-2", "Toyota Noah",             "Micro", 7,  3500, "photo-1570125909232-eb263c188f7e", "barishal"),
                // Bandarban
                car("bdn-1", "Mitsubishi Pajero",       "SUV",   7,  7500, "photo-1519641471654-76ce0107ad1b", "bandarban"),
                car("bdn-2", "Toyota Land Cruiser",     "SUV",   7, 10000, "photo-1533473359331-0135ef1b58bf", "bandarban"),
                car("bdn-3", "Toyota Hiace",            "Van",   12, 6000, "photo-1609520505218-7421df70e621", "bandarban"),
                // Rangamati
                car("rgm-1", "Mitsubishi Pajero",            "SUV", 7, 7000, "photo-1519641471654-76ce0107ad1b", "rangamati"),
                car("rgm-2", "Toyota Land Cruiser Prado",    "SUV", 7, 9000, "photo-1533473359331-0135ef1b58bf", "rangamati"),
                // Mymensingh
                car("mym-1", "Toyota Allion",           "Sedan", 4,  2700, "photo-1549399542-7e3f8b79c341",   "mymensingh"),
                car("mym-2", "Toyota Noah",             "Micro", 7,  3500, "photo-1570125909232-eb263c188f7e", "mymensingh")
        );
        carRepository.saveAll(cars);
        System.out.println("[DataInitializer] Seeded " + cars.size() + " cars.");
    }

    private CarEntity car(String id, String name, String type, int seats, double price,
                          String unsplashId, String city) {
        return new CarEntity(id, name, type, seats, price,
                "https://images.unsplash.com/" + unsplashId + "?w=600", city);
    }

    // ─────────────────────────────────────────────
    // Guides
    // ─────────────────────────────────────────────
    private void seedGuides() {
        if (guideRepository.count() > 0) return;

        List<GuideEntity> guides = Arrays.asList(
                // Dhaka
                guide("g-1",  "Rahim Uddin",     "Dhaka",       8,  4.8, "photo-1507003211169-0a1dd7228f2d", "Bangla", "English"),
                guide("g-2",  "Kamal Hossain",   "Dhaka",      12,  4.9, "photo-1500648767791-00dcc994a43e", "Bangla", "English", "Hindi"),
                guide("g-3",  "Fatema Begum",    "Dhaka",       5,  4.5, "photo-1494790108377-be9c29b29330", "Bangla", "English"),
                guide("g-4",  "Arif Ahmed",      "Dhaka",      10,  4.7, "photo-1472099645785-5658abf4ff4e", "Bangla", "English", "Hindi"),
                // Chittagong
                guide("g-5",  "Nusrat Jahan",    "Chittagong",  6,  4.6, "photo-1438761681033-6461ffad8d80", "Bangla", "English"),
                guide("g-6",  "Mizanur Rahman",  "Chittagong", 15,  4.9, "photo-1506794778202-cad84cf45f1d", "Bangla", "English"),
                // Cox's Bazar
                guide("g-7",  "Shahid Alam",     "Cox's Bazar", 10, 4.8, "photo-1507003211169-0a1dd7228f2d", "Bangla", "English"),
                guide("g-8",  "Tasnim Akter",    "Cox's Bazar",  4, 4.3, "photo-1494790108377-be9c29b29330", "Bangla", "English"),
                guide("g-9",  "Jamal Uddin",     "Cox's Bazar",  7, 4.5, "photo-1500648767791-00dcc994a43e", "Bangla", "English", "Hindi"),
                // Sylhet
                guide("g-10", "Sumon Mia",       "Sylhet",      9,  4.7, "photo-1472099645785-5658abf4ff4e", "Bangla", "English"),
                guide("g-11", "Rupa Das",        "Sylhet",      6,  4.4, "photo-1438761681033-6461ffad8d80", "Bangla", "English", "Hindi"),
                // Rajshahi
                guide("g-12", "Habibur Rahman",  "Rajshahi",   11,  4.6, "photo-1506794778202-cad84cf45f1d", "Bangla", "English"),
                // Khulna
                guide("g-13", "Anisur Rahman",   "Khulna",      8,  4.5, "photo-1507003211169-0a1dd7228f2d", "Bangla", "English"),
                guide("g-14", "Shamima Nasrin",  "Khulna",      5,  4.2, "photo-1494790108377-be9c29b29330", "Bangla"),
                // Rangpur
                guide("g-15", "Belal Hossain",   "Rangpur",     7,  4.3, "photo-1500648767791-00dcc994a43e", "Bangla", "English"),
                // Bandarban
                guide("g-16", "Maung Thein",     "Bandarban",  13,  4.9, "photo-1472099645785-5658abf4ff4e", "Bangla", "English"),
                guide("g-17", "Ching Marma",     "Bandarban",   9,  4.6, "photo-1506794778202-cad84cf45f1d", "Bangla"),
                // Rangamati
                guide("g-18", "Dipak Chakma",    "Rangamati",  10,  4.7, "photo-1507003211169-0a1dd7228f2d", "Bangla", "English"),
                // Barishal
                guide("g-19", "Mostafa Kamal",   "Barishal",    6,  4.4, "photo-1500648767791-00dcc994a43e", "Bangla", "English"),
                // Mymensingh
                guide("g-20", "Sharmin Sultana", "Mymensingh",  4,  4.1, "photo-1438761681033-6461ffad8d80", "Bangla", "English")
        );
        guideRepository.saveAll(guides);
        System.out.println("[DataInitializer] Seeded " + guides.size() + " guides.");
    }

    private GuideEntity guide(String id, String name, String city, int expYears, double rating,
                              String unsplashId, String... languages) {
        return new GuideEntity(id, name, city, expYears, rating,
                "https://images.unsplash.com/" + unsplashId + "?w=120",
                Arrays.asList(languages));
    }

    // ─────────────────────────────────────────────
    // Places (Tourist attractions & recommendations)
    // ─────────────────────────────────────────────
    private void seedPlaces() {
        if (placeRepository.count() > 0) return;

        List<PlaceEntity> places = Arrays.asList(
                // Note: This is a comprehensive seed from the service layer
                // The full data migration is being performed from PlacesRecommendationService
                
                // Dhaka
                place("dhk-1", "Lalbagh Fort", "Heritage", 
                    "A 17th-century Mughal fort on the Buriganga River featuring the tomb of Pari Bibi and the Diwan-i-Aam — a must-visit landmark of Old Dhaka.",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600", 
                    4.8, "Old Dhaka", "dhaka", "History", "Photography", "Architecture"),
                place("dhk-2", "Ahsan Manzil (Pink Palace)", "Heritage",
                    "The iconic Pink Palace on the Buriganga riverfront, home to the Nawabs of Dhaka and now a national museum packed with history.",
                    "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                    4.8, "Old Dhaka", "dhaka", "Museum", "History", "Architecture"),
                place("dhk-3", "Star Mosque (Tara Masjid)", "Religious",
                    "An exquisitely decorated mosque adorned with Chinese porcelain tiles forming star patterns — a unique gem of Old Dhaka.",
                    "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                    4.7, "Old Dhaka", "dhaka", "Architecture", "Religious", "Photography"),
                place("dhk-4", "National Parliament House", "Heritage",
                    "Louis Kahn's masterpiece of 20th-century architecture — arguably the most important building in Bangladesh.",
                    "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                    4.9, "Sher-e-Bangla Nagar", "dhaka", "Architecture", "National Landmark", "Photography"),
                place("dhk-5", "National Museum of Bangladesh", "Heritage",
                    "The largest museum in Bangladesh with over 83,000 artifacts spanning art, history, natural history and ethnography.",
                    "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                    4.6, "Shahbagh", "dhaka", "Museum", "Education", "Art"),
                place("dhk-6", "Shaheed Minar", "Heritage",
                    "Bangladesh's national language movement monument — a symbol of cultural pride and the struggle for the Bangla language.",
                    "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                    4.9, "Dhaka University area", "dhaka", "History", "National Symbol", "Monument"),
                place("dhk-7", "Liberation War Museum", "Heritage",
                    "A powerful museum documenting the 1971 Liberation War through artifacts, photographs and testimonies.",
                    "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                    4.8, "Segunbagicha", "dhaka", "Museum", "History", "Education"),
                
                // Cox's Bazar
                place("cxb-1", "Cox's Bazar Sea Beach", "Beach",
                    "The world's longest natural sea beach stretching 120 km with golden sands and crashing Bay of Bengal waves.",
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                    4.9, "Cox's Bazar town", "cox's bazar", "Swimming", "Sunset", "Surfing"),
                place("cxb-2", "Saint Martin's Island", "Beach",
                    "Bangladesh's only coral island — famous for its crystal-clear blue water, coral reefs, coconut palms and seafood.",
                    "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=600",
                    4.9, "120 km south of Cox's Bazar", "cox's bazar", "Snorkeling", "Coral Reef", "Island"),
                place("cxb-3", "Inani Beach", "Beach",
                    "A serene coral-strewn beach 27 km south of Cox's Bazar with crystal-clear water and colorful pebbles.",
                    "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=600",
                    4.8, "27 km from Cox's Bazar", "cox's bazar", "Snorkeling", "Photography", "Coral"),
                place("cxb-4", "Himchari National Park", "Wildlife",
                    "A forested hill area with a stunning waterfall right beside the sea, home to diverse wildlife.",
                    "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                    4.7, "12 km from Cox's Bazar", "cox's bazar", "Waterfall", "Trekking", "Wildlife"),
                
                // Sylhet
                place("syl-1", "Ratargul Swamp Forest", "Wildlife",
                    "Bangladesh's only freshwater swamp forest — called the Amazon of Bangladesh. Take a boat through the eerie green canopy.",
                    "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                    4.9, "26 km from Sylhet city", "sylhet", "Boat Tour", "Photography", "Wildlife"),
                place("syl-2", "Jaflong", "Nature",
                    "A scenic area at the foothills of the Meghalaya hills with the Piyain River, stone collection, and views into India.",
                    "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                    4.8, "60 km from Sylhet city", "sylhet", "River", "Photography", "Scenic"),
                place("syl-3", "Lalakhal", "Nature",
                    "Famous for its strikingly blue-green river water — boat rides here are an unmissable Sylhet experience.",
                    "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                    4.8, "35 km from Sylhet city", "sylhet", "Boat Ride", "Photography", "Nature"),
                
                // Chittagong
                place("ctg-1", "Patenga Beach", "Beach",
                    "A popular sea beach at the mouth of the Karnaphuli River with views of ships and the busy Chittagong port.",
                    "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
                    4.5, "22 km from Chittagong city", "chittagong", "Sunset", "Beach", "Photography"),
                place("ctg-2", "Foy's Lake", "Nature",
                    "A man-made lake in forested hills with an amusement park and cable car — perfect for families.",
                    "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                    4.4, "8 km from Chittagong city", "chittagong", "Boat Ride", "Amusement Park", "Cable Car"),
                
                // Bandarban
                place("bdb-1", "Nilgiri Hill Resort", "Nature",
                    "The highest tourist spot in Bangladesh at 3,000 ft — clouds float beneath your feet and sunrises are phenomenal.",
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600",
                    4.9, "47 km from Bandarban town", "bandarban", "Sunrise", "Cloud Sea", "Trekking"),
                place("bdb-2", "Golden Temple (Buddha Dhatu Jadi)", "Religious",
                    "The largest Buddhist temple complex in Bangladesh on a hilltop, adorned in stunning golden splendor.",
                    "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                    4.8, "4 km from Bandarban town", "bandarban", "Buddhist Temple", "Photography", "Cultural"),
                place("bdb-3", "Nafakhum Waterfall", "Nature",
                    "The largest waterfall in Bangladesh — a multi-day jungle trek to witness this magnificent cascade.",
                    "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                    4.9, "79 km from Bandarban town", "bandarban", "Waterfall", "Trekking", "Adventure"),
                
                // Rangamati
                place("rmt-1", "Kaptai Lake", "Nature",
                    "The largest man-made lake in South Asia surrounded by lush hills — boat rides offer breathtaking scenery.",
                    "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                    4.9, "Rangamati town", "rangamati", "Boat Ride", "Fishing", "Photography"),
                place("rmt-2", "Hanging Bridge of Rangamati", "Heritage",
                    "A famous 335-meter suspension bridge over the Kaptai Lake — an iconic symbol of Rangamati.",
                    "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                    4.7, "Rangamati town", "rangamati", "Photography", "Scenic", "Iconic"),
                
                // Khulna
                place("khl-1", "Sundarbans Mangrove Forest", "Wildlife",
                    "The world's largest mangrove forest and UNESCO World Heritage Site — home to the Royal Bengal Tiger.",
                    "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                    5.0, "87 km from Khulna city", "khulna", "Tiger Safari", "Boat Tour", "Wildlife"),
                place("khl-2", "Sixty Dome Mosque (Bagerhat)", "Heritage",
                    "A 15th-century UNESCO World Heritage Site mosque with 60 stone pillars — the most impressive medieval mosque in Bangladesh.",
                    "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                    4.9, "50 km from Khulna city", "khulna", "Architecture", "History", "UNESCO Site"),
                
                // Tangail
                place("tng-1", "Atia Mosque", "Heritage",
                    "A magnificent 16th-century Mughal mosque with intricate terracotta ornamentation — one of the finest pre-Mughal architectural gems in Bangladesh.",
                    "https://images.unsplash.com/photo-1585211969224-3e992986159d?w=600",
                    4.7, "12 km from Tangail town", "tangail", "Photography", "History", "Architecture"),
                place("tng-2", "Madhupur National Park", "Wildlife",
                    "A protected sal forest and national park home to diverse wildlife, tribal Garo communities, and beautiful nature trails.",
                    "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600",
                    4.5, "45 km from Tangail town", "tangail", "Trekking", "Wildlife", "Tribal Culture"),
                
                // Rajshahi
                place("rjh-1", "Paharpur Vihara", "Heritage",
                    "The ruins of an 8th-century Buddhist monastery — a UNESCO World Heritage Site and the second-largest single Buddhist monastery south of the Himalayas.",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                    4.8, "67 km from Rajshahi", "rajshahi", "Archaeology", "History", "UNESCO Site"),
                place("rjh-2", "Varendra Research Museum", "Heritage",
                    "The oldest museum in Bangladesh (1910) with a rich collection of archaeological artifacts from the region.",
                    "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                    4.5, "Rajshahi city", "rajshahi", "Museum", "History", "Archaeology"),
                
                // Mymensingh
                place("mym-1", "Shashi Lodge", "Heritage",
                    "A beautiful 19th-century Greek-revival palace set within a garden, built by the Maharajas of Mymensingh.",
                    "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                    4.6, "Mymensingh town", "mymensingh", "Architecture", "History", "Gardens"),
                place("mym-2", "Alexander Castle", "Heritage",
                    "A magnificent castle built by Raja Shashi Kanta Acharya in 1905 — now housing the Teachers Training College.",
                    "https://images.unsplash.com/photo-1545558014-8692077e9b5c?w=600",
                    4.5, "Mymensingh town", "mymensingh", "History", "Architecture", "Photography"),
                place("mym-3", "BAU Botanical Garden", "Nature",
                    "The largest botanical garden in Bangladesh inside Bangladesh Agricultural University.",
                    "https://images.unsplash.com/photo-1501854140801-50d01698950b?w=600",
                    4.5, "Mymensingh town", "mymensingh", "Nature Walk", "Photography", "Education"),
                place("mym-4", "Brahmaputra River Embankment", "Nature",
                    "A lovely riverside walk along the old Brahmaputra — a popular evening stroll with great views and breeze.",
                    "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=600",
                    4.3, "Mymensingh town center", "mymensingh", "River Walk", "Relaxation", "Photography"),
                place("mym-5", "Muktagachha Palace", "Heritage",
                    "An ornate 19th-century zamindari palace in Muktagachha, famous for its terracotta artwork.",
                    "https://images.unsplash.com/photo-1587474260584-136574528ed5?w=600",
                    4.3, "19 km from Mymensingh town", "mymensingh", "History", "Architecture", "Photography"),
                place("mym-6", "Mymensingh Museum", "Heritage",
                    "A district museum housing artifacts reflecting the history and culture of the Mymensingh region.",
                    "https://images.unsplash.com/photo-1525926193424-3f2af69c96cd?w=600",
                    4.1, "Mymensingh town", "mymensingh", "Museum", "History", "Culture"),
                place("mym-7", "Gauripur Palace", "Heritage",
                    "A hauntingly beautiful ruined zamindari palace in Gauripur with grand rooms and overgrown courtyards.",
                    "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=600",
                    4.4, "20 km from Mymensingh town", "mymensingh", "Ruins", "Photography", "History")
        );
        
        placeRepository.saveAll(places);
        System.out.println("[DataInitializer] Seeded " + places.size() + " places.");
    }

    private PlaceEntity place(String id, String name, String category, String description,
                             String imageUrl, double rating, String distanceNote, String location,
                             String... highlights) {
        return new PlaceEntity(id, name, category, description, imageUrl, rating, 
                             distanceNote, location.toLowerCase(), Arrays.asList(highlights));
    }
}
