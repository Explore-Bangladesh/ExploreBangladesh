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
                // Dhaka
                place("dhk-1", "Lalbagh Fort", "Heritage", 
                    "A 17th-century Mughal fort on the Buriganga River featuring the tomb of Pari Bibi and the Diwan-i-Aam — a must-visit landmark of Old Dhaka.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Lalbagh_Fort_-_3.jpg/800px-Lalbagh_Fort_-_3.jpg", 
                    4.8, "Old Dhaka", "dhaka", "History", "Photography", "Architecture"),
                place("dhk-2", "Ahsan Manzil (Pink Palace)", "Heritage",
                    "The iconic Pink Palace on the Buriganga riverfront, home to the Nawabs of Dhaka and now a national museum packed with history.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Ahsan_Manzil_Front.jpg/800px-Ahsan_Manzil_Front.jpg",
                    4.8, "Old Dhaka", "dhaka", "Museum", "History", "Architecture"),
                place("dhk-3", "Star Mosque (Tara Masjid)", "Religious",
                    "An exquisitely decorated mosque adorned with Chinese porcelain tiles forming star patterns — a unique gem of Old Dhaka.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Star_Mosque_%28Tara_Masjid%29.jpg/800px-Star_Mosque_%28Tara_Masjid%29.jpg",
                    4.7, "Old Dhaka", "dhaka", "Architecture", "Religious", "Photography"),
                place("dhk-4", "National Parliament House", "Heritage",
                    "Louis Kahn's masterpiece of 20th-century architecture — arguably the most important building in Bangladesh.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Jatiyo_Sangsad_Bhaban_%28National_Parliament_House%29.jpg/800px-Jatiyo_Sangsad_Bhaban_%28National_Parliament_House%29.jpg",
                    4.9, "Sher-e-Bangla Nagar", "dhaka", "Architecture", "National Landmark", "Photography"),
                place("dhk-5", "National Museum of Bangladesh", "Heritage",
                    "The largest museum in Bangladesh with over 83,000 artifacts spanning art, history, natural history and ethnography.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Bangladesh_National_Museum.jpg/800px-Bangladesh_National_Museum.jpg",
                    4.6, "Shahbagh", "dhaka", "Museum", "Education", "Art"),
                place("dhk-6", "Shaheed Minar", "Heritage",
                    "Bangladesh's national language movement monument — a symbol of cultural pride and the struggle for the Bangla language.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Shaheed_Minar%2C_Dhaka.jpg/800px-Shaheed_Minar%2C_Dhaka.jpg",
                    4.9, "Dhaka University area", "dhaka", "History", "National Symbol", "Monument"),
                place("dhk-7", "Liberation War Museum", "Heritage",
                    "A powerful museum documenting the 1971 Liberation War through artifacts, photographs and testimonies.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Liberation_War_Museum.jpg/800px-Liberation_War_Museum.jpg",
                    4.8, "Segunbagicha", "dhaka", "Museum", "History", "Education"),
                
                // Tangail
                place("tng-1", "Atia Mosque", "Heritage",
                    "A magnificent 16th-century Mughal mosque with intricate terracotta ornamentation — one of the finest pre-Mughal architectural gems in Bangladesh.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Atia_Mosque%2C_Tangail_%286%29.jpg/800px-Atia_Mosque%2C_Tangail_%286%29.jpg",
                    4.7, "12 km from Tangail town", "tangail", "Photography", "History", "Architecture"),
                place("tng-2", "Madhupur National Park", "Wildlife",
                    "A protected sal forest and national park home to diverse wildlife, tribal Garo communities, and beautiful nature trails.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Madhupur_National_Park_%2801%29.jpg/800px-Madhupur_National_Park_%2801%29.jpg",
                    4.5, "45 km from Tangail town", "tangail", "Trekking", "Wildlife", "Tribal Culture"),
                place("tng-3", "Mohera Zamindar Bari", "Heritage",
                    "One of the most popular heritage sites in Tangail — a well-preserved colonial-era zamindar palace complex with ornate gardens, fountains, and large ponds.",
                    "https://upload.wikimedia.org/wikipedia/commons/8/8d/Mahera_Jamidar_Bari.jpg",
                    4.6, "18 km from Tangail town", "tangail", "Architecture", "Gardens", "History"),
                place("tng-4", "201 Dome Mosque", "Religious",
                    "A grand modern mosque in Gopalpur featuring 201 domes of varying sizes — one of the most visually striking mosques in Bangladesh.",
                    "https://upload.wikimedia.org/wikipedia/commons/c/c7/201_Dome_Mosque_13.jpg",
                    4.5, "30 km from Tangail town", "tangail", "Religious", "Architecture", "Photography"),
                place("tng-5", "Jamuna Bridge Corridor", "Heritage",
                    "The 4.8 km Bangabandhu Jamuna Multipurpose Bridge — the 11th longest bridge in the world when built, offering scenic views of the mighty Jamuna River.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Bangabandhu_Bridge_%28Jamuna_Multi-purpose_Bridge%29.jpg/800px-Bangabandhu_Bridge_%28Jamuna_Multi-purpose_Bridge%29.jpg",
                    4.4, "35 km from Tangail town", "tangail", "Engineering", "Scenic", "Photography"),
                
                // Rajshahi
                place("rjh-1", "Paharpur Vihara", "Heritage",
                    "The ruins of an 8th-century Buddhist monastery — a UNESCO World Heritage Site and the second-largest single Buddhist monastery south of the Himalayas.",
                    "https://upload.wikimedia.org/wikipedia/commons/4/42/Paharpur_Buddhist_Bihar.jpg",
                    4.8, "67 km from Rajshahi", "rajshahi", "Archaeology", "History", "UNESCO Site"),
                place("rjh-2", "Varendra Research Museum", "Heritage",
                    "The oldest museum in Bangladesh (1910) with a rich collection of archaeological artifacts from the region.",
                    "https://upload.wikimedia.org/wikipedia/commons/0/08/Varendra_Research_Museum_10.jpg",
                    4.5, "Rajshahi city", "rajshahi", "Museum", "History", "Archaeology"),
                place("rjh-3", "Bagha Mosque", "Heritage",
                    "A remarkable 16th-century Sultanate-period mosque built in 1523 by Sultan Nusrat Shah — famous for its ten domes and exquisite terracotta ornamentation.",
                    "https://upload.wikimedia.org/wikipedia/commons/9/9d/Bagha_Mosque_front_6.jpg",
                    4.7, "41 km from Rajshahi city", "rajshahi", "Architecture", "History", "Photography"),
                place("rjh-4", "Puthia Temple Complex", "Heritage",
                    "The densest cluster of historic Hindu temples in Bangladesh, featuring stunning terracotta work including the Pancha Ratna Govinda Temple and Shiva Temple.",
                    "https://upload.wikimedia.org/wikipedia/commons/9/91/Bara_Shiva_Temple%2C_Puthia_03.jpg",
                    4.8, "23 km from Rajshahi city", "rajshahi", "Temples", "Architecture", "Cultural Heritage"),
                place("rjh-5", "Padma River Garden", "Nature",
                    "The beautified Padma riverfront in Rajshahi with scenic sunset views, walking paths, and local street food — the city's most popular evening hangout.",
                    "https://upload.wikimedia.org/wikipedia/commons/2/2c/T-badh%2C_Padma_River_in_Rajshahi.jpg",
                    4.4, "Rajshahi city center", "rajshahi", "Sunset", "River Walk", "Relaxation"),
                
                // Khulna
                place("khl-1", "Sundarbans Mangrove Forest", "Wildlife",
                    "The world's largest mangrove forest and UNESCO World Heritage Site — home to the Royal Bengal Tiger.",
                    "https://upload.wikimedia.org/wikipedia/commons/6/6b/Save_the_sundarbans_20.jpg",
                    5.0, "87 km from Khulna city", "khulna", "Tiger Safari", "Boat Tour", "Wildlife"),
                place("khl-2", "Sixty Dome Mosque (Bagerhat)", "Heritage",
                    "A 15th-century UNESCO World Heritage Site mosque with 60 stone pillars — the most impressive medieval mosque in Bangladesh.",
                    "https://upload.wikimedia.org/wikipedia/commons/4/4f/Sixty_Dome_Mosque%2CBagerhat.jpg",
                    4.9, "50 km from Khulna city", "khulna", "Architecture", "History", "UNESCO Site"),
                place("khl-3", "Khan Jahan Ali Shrine", "Religious",
                    "The 15th-century tomb of the revered Sufi saint Khan Jahan Ali in Bagerhat — set beside the iconic Thakur Dighi lake with sacred turtles.",
                    "https://upload.wikimedia.org/wikipedia/commons/2/2b/Tomb_of_Khan_Jahan_Ali.JPG",
                    4.7, "50 km from Khulna city", "khulna", "Religious", "History", "Architecture"),
                place("khl-4", "Harbaria Ecotourism Center", "Wildlife",
                    "A popular Sundarbans entry point with a wooden boardwalk through dense mangroves, a hanging bridge, and a viewing tower for wildlife spotting.",
                    "https://upload.wikimedia.org/wikipedia/commons/3/37/Sundarban_Harbaria.jpg",
                    4.6, "100 km from Khulna city", "khulna", "Eco-Tourism", "Mangrove Walk", "Wildlife"),
                place("khl-5", "Karamjal Wildlife Breeding Center", "Wildlife",
                    "The most accessible Sundarbans day-trip spot featuring deer and crocodile breeding programs and a short mangrove trail.",
                    "https://upload.wikimedia.org/wikipedia/commons/1/16/Karamjal_Point-SUNDARBAN.jpg",
                    4.5, "45 km from Khulna city", "khulna", "Wildlife", "Crocodiles", "Day Trip"),
                
                // Rangamati
                place("rmt-1", "Kaptai Lake", "Nature",
                    "The largest man-made lake in South Asia surrounded by lush hills — boat rides offer breathtaking scenery.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Kaptai_Lake_%2801%29.jpg/800px-Kaptai_Lake_%2801%29.jpg",
                    4.9, "Rangamati town", "rangamati", "Boat Ride", "Fishing", "Photography"),
                place("rmt-2", "Hanging Bridge of Rangamati", "Heritage",
                    "A famous 335-meter suspension bridge over the Kaptai Lake — an iconic symbol of Rangamati.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Rangamati_Hanging_Bridge.jpg/800px-Rangamati_Hanging_Bridge.jpg",
                    4.7, "Rangamati town", "rangamati", "Photography", "Scenic", "Iconic"),
                place("rmt-3", "Shuvolong Waterfall", "Nature",
                    "One of the most spectacular waterfalls in Bangladesh, plunging roughly 300 feet into Kaptai Lake — best visited during monsoon season by boat.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Shuvolong_Jhorna.jpg/800px-Shuvolong_Jhorna.jpg",
                    4.8, "Barkal Upazila, boat access", "rangamati", "Waterfall", "Boat Trip", "Photography"),
                place("rmt-4", "Rajban Vihara", "Religious",
                    "The largest Buddhist monastery in Bangladesh, founded in 1977, with a seven-story tower symbolizing the seven heavens — a spiritual center of the Chakma community.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Rajban_Vihara_Rangamati.jpg/800px-Rajban_Vihara_Rangamati.jpg",
                    4.7, "7 km from Rangamati town", "rangamati", "Buddhist Monastery", "Cultural", "Photography"),
                place("rmt-5", "Tribal Cultural Museum", "Heritage",
                    "A museum run by the Tribal Cultural Institute showcasing traditional dresses, ornaments, musical instruments, and handicrafts of Chakma, Marma, and Tripura communities.",
                    "https://upload.wikimedia.org/wikipedia/commons/f/f9/Tribal_Museum_at_Araku.jpg",
                    4.5, "Rangamati town center", "rangamati", "Museum", "Indigenous Culture", "Handicrafts"),
                
                // Bandarban
                place("bdb-1", "Nilgiri Hill Resort", "Nature",
                    "The highest tourist spot in Bangladesh at 3,000 ft — clouds float beneath your feet and sunrises are phenomenal.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/Nilgiri%2C_Bandarban.jpg/800px-Nilgiri%2C_Bandarban.jpg",
                    4.9, "47 km from Bandarban town", "bandarban", "Sunrise", "Cloud Sea", "Trekking"),
                place("bdb-2", "Golden Temple (Buddha Dhatu Jadi)", "Religious",
                    "The largest Buddhist temple complex in Bangladesh on a hilltop, adorned in stunning golden splendor.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Buddha_Dhatu_Jadi_01.jpg/800px-Buddha_Dhatu_Jadi_01.jpg",
                    4.8, "4 km from Bandarban town", "bandarban", "Buddhist Temple", "Photography", "Cultural"),
                place("bdb-3", "Nafakhum Waterfall", "Nature",
                    "The largest waterfall in Bangladesh — a multi-day jungle trek to witness this magnificent horseshoe-shaped cascade.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/Nafakhum_Waterfall.jpg/800px-Nafakhum_Waterfall.jpg",
                    4.9, "79 km from Bandarban town", "bandarban", "Waterfall", "Trekking", "Adventure"),
                place("bdb-4", "Boga Lake", "Nature",
                    "A mysterious high-altitude natural lake surrounded by mountains and dense forest — a popular trekking destination with crystal-clear blue water.",
                    "https://upload.wikimedia.org/wikipedia/commons/0/07/Boga_Lake%2C_Bandarban.jpg",
                    4.8, "Ruma Upazila, trekking required", "bandarban", "Trekking", "Lake", "Camping"),
                place("bdb-5", "Meghla Tourist Spot", "Nature",
                    "A family-friendly hillside park just outside Bandarban town with an artificial lake, hanging bridge, mini-safari park, zoo, and cable car.",
                    "https://upload.wikimedia.org/wikipedia/commons/0/00/Meghla_Hanging_Bridge_I%2C_Bandarban_%289668%29.jpg",
                    4.4, "4 km from Bandarban town", "bandarban", "Family Friendly", "Cable Car", "Boating"),
                
                // Sylhet
                place("syl-1", "Ratargul Swamp Forest", "Wildlife",
                    "Bangladesh's only freshwater swamp forest — called the Amazon of Bangladesh. Take a boat through the eerie green canopy.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Ratargul_Swamp_Forest%2C_Sylhet_%2802%29.jpg/800px-Ratargul_Swamp_Forest%2C_Sylhet_%2802%29.jpg",
                    4.9, "26 km from Sylhet city", "sylhet", "Boat Tour", "Photography", "Wildlife"),
                place("syl-2", "Jaflong", "Nature",
                    "A scenic area at the foothills of the Meghalaya hills with the Piyain River, stone collection, and views into India.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Jaflong%2C_Sylhet.jpg/800px-Jaflong%2C_Sylhet.jpg",
                    4.8, "60 km from Sylhet city", "sylhet", "River", "Photography", "Scenic"),
                place("syl-3", "Lalakhal", "Nature",
                    "Famous for its strikingly blue-green river water — boat rides here are an unmissable Sylhet experience.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Lalakhal%2C_Sylhet.jpg/800px-Lalakhal%2C_Sylhet.jpg",
                    4.8, "35 km from Sylhet city", "sylhet", "Boat Ride", "Photography", "Nature"),
                place("syl-4", "Bisnakandi", "Nature",
                    "A breathtaking stone quarry landscape at the India border where mountain streams, boulders, and the Khasi hills create a dramatic river-mountain panorama.",
                    "https://upload.wikimedia.org/wikipedia/commons/5/57/Bichanakandi_sylhet.jpg",
                    4.7, "55 km from Sylhet city", "sylhet", "Stone Landscape", "Photography", "Scenic"),
                place("syl-5", "Lawachara National Park", "Wildlife",
                    "A tropical rainforest in Srimangal home to the endangered hoolock gibbon — ideal for birdwatching, nature hikes, and tea garden visits.",
                    "https://upload.wikimedia.org/wikipedia/commons/4/4f/Life_around_jungle.jpg",
                    4.7, "Srimangal, 115 km from Sylhet", "sylhet", "Rainforest", "Gibbon Spotting", "Eco-Tourism"),
                
                // Cox's Bazar
                place("cxb-1", "Cox's Bazar Sea Beach", "Beach",
                    "The world's longest natural sea beach stretching 120 km with golden sands and crashing Bay of Bengal waves.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/Coxs_Bazar_Sea_Beach_1.jpg/800px-Coxs_Bazar_Sea_Beach_1.jpg",
                    4.9, "Cox's Bazar town", "cox's bazar", "Swimming", "Sunset", "Surfing"),
                place("cxb-2", "Saint Martin's Island", "Beach",
                    "Bangladesh's only coral island — famous for its crystal-clear blue water, coral reefs, coconut palms and seafood.",
                    "https://upload.wikimedia.org/wikipedia/commons/d/db/Saint_Martins_Island_with_boats_in_foreground.jpg",
                    4.9, "120 km south of Cox's Bazar", "cox's bazar", "Snorkeling", "Coral Reef", "Island"),
                place("cxb-3", "Inani Beach", "Beach",
                    "A serene coral-strewn beach 27 km south of Cox's Bazar with crystal-clear water and colorful pebbles.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/Coxs_Bazar_Sea_Beach_1.jpg/800px-Coxs_Bazar_Sea_Beach_1.jpg",
                    4.8, "27 km from Cox's Bazar", "cox's bazar", "Snorkeling", "Photography", "Coral"),
                place("cxb-4", "Himchari National Park", "Wildlife",
                    "A forested hill area with a stunning waterfall right beside the sea, home to diverse wildlife.",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/Himchari_National_Park.jpg/800px-Himchari_National_Park.jpg",
                    4.7, "12 km from Cox's Bazar", "cox's bazar", "Waterfall", "Trekking", "Wildlife"),
                place("cxb-5", "Maheshkhali Island", "Nature",
                    "A scenic island accessible by boat from Cox's Bazar featuring the historic Adinath Hindu Temple atop a hill and unique mangrove landscapes.",
                    "https://upload.wikimedia.org/wikipedia/commons/2/26/Roads_of_Moheshkhali_island%2C_Cox%27s_Bazar.jpg",
                    4.5, "12 km from Cox's Bazar by boat", "cox's bazar", "Temple", "Island", "Boat Trip"),
                
                // Chittagong
                place("ctg-1", "Patenga Beach", "Beach",
                    "A popular sea beach at the mouth of the Karnaphuli River with views of ships and the busy Chittagong port.",
                    "https://upload.wikimedia.org/wikipedia/commons/9/9b/Sunset_at_Patenga_beach.jpg",
                    4.5, "22 km from Chittagong city", "chittagong", "Sunset", "Beach", "Photography"),
                place("ctg-2", "Foy's Lake", "Nature",
                    "A man-made lake in forested hills with an amusement park and cable car — perfect for families.",
                    "https://upload.wikimedia.org/wikipedia/commons/2/2c/Foy%27s_Lake_2_by_Rahat.jpg",
                    4.4, "8 km from Chittagong city", "chittagong", "Boat Ride", "Amusement Park", "Cable Car"),
                place("ctg-3", "Ethnological Museum", "Heritage",
                    "The only ethnological museum in Bangladesh, showcasing the cultures, artefacts, and traditions of diverse ethnic groups across the country.",
                    "https://upload.wikimedia.org/wikipedia/commons/a/ad/Ethnological_Museum_of_Chittagong..JPG",
                    4.5, "Chittagong city center", "chittagong", "Museum", "Indigenous Culture", "Education"),
                place("ctg-4", "Bayazid Bostami Shrine", "Religious",
                    "The shrine of the legendary Sufi saint Bayazid Bostami, featuring a sacred pond with ancient turtles and a peaceful hilltop mosque.",
                    "https://commons.wikimedia.org/wiki/File:Baizid_Bostami%27s_Mazar_001.JPG",
                    4.6, "6 km from Chittagong city", "chittagong", "Religious", "History", "Turtles"),
                place("ctg-5", "Commonwealth War Cemetery", "Heritage",
                    "A beautifully maintained WWII cemetery with 868 Commonwealth war graves — a solemn and serene memorial in the heart of Chittagong.",
                    "https://commons.wikimedia.org/wiki/File:Chittagong_War_Cemetery,_Chattogram,_Bangladesh_%288%29.jpg",
                    4.4, "Chittagong city", "chittagong", "War Memorial", "History", "Memorial"),
                
                // Mymensingh
                place("mym-1", "Shashi Lodge", "Heritage",
                    "A beautiful 19th-century Greek-revival palace set within a garden, built by the Maharajas of Mymensingh.",
                    "https://upload.wikimedia.org/wikipedia/commons/2/23/Shashi_Lodge_best_view.jpg",
                    4.6, "Mymensingh town", "mymensingh", "Architecture", "History", "Gardens"),
                place("mym-2", "Alexander Castle", "Heritage",
                    "A magnificent castle built by Raja Shashi Kanta Acharya in 1905 — now housing the Teachers Training College.",
                    "https://upload.wikimedia.org/wikipedia/commons/b/b6/%E0%A6%B2%E0%A7%8B%E0%A6%B9%E0%A6%BE%E0%A6%B0_%E0%A6%95%E0%A7%81%E0%A6%A0%E0%A6%BF.jpg",
                    4.5, "Mymensingh town", "mymensingh", "History", "Architecture", "Photography"),
                place("mym-3", "BAU Botanical Garden", "Nature",
                    "The largest botanical garden in Bangladesh inside Bangladesh Agricultural University.",
                    "https://upload.wikimedia.org/wikipedia/commons/e/ed/Bangladesh_Agricultural_University_%28BAU%29.jpg",
                    4.5, "Mymensingh town", "mymensingh", "Nature Walk", "Photography", "Education"),
                place("mym-4", "Brahmaputra River Embankment", "Nature",
                    "A lovely riverside walk along the old Brahmaputra — a popular evening stroll with great views and breeze.",
                    "https://upload.wikimedia.org/wikipedia/commons/0/0b/Rayhan_29_Sep_17_08.png",
                    4.3, "Mymensingh town center", "mymensingh", "River Walk", "Relaxation", "Photography"),
                place("mym-5", "Muktagachha Palace", "Heritage",
                    "An ornate 19th-century zamindari palace in Muktagachha, famous for its terracotta artwork.",
                    "https://upload.wikimedia.org/wikipedia/commons/9/93/Aat_Ani_Zamindar_Bari_%281%29.jpg",
                    4.3, "19 km from Mymensingh town", "mymensingh", "History", "Architecture", "Photography"),
                place("mym-6", "Mymensingh Museum", "Heritage",
                    "A district museum housing artifacts reflecting the history and culture of the Mymensingh region.",
                    "https://upload.wikimedia.org/wikipedia/commons/4/4f/Mymensingh_museum_%2818955693949%29.jpg",
                    4.1, "Mymensingh town", "mymensingh", "Museum", "History", "Culture"),
                place("mym-7", "Gauripur Palace", "Heritage",
                    "A hauntingly beautiful ruined zamindari palace in Gauripur with grand rooms and overgrown courtyards.",
                    "https://upload.wikimedia.org/wikipedia/commons/0/02/Ramgopalpur_Zamindar_Bari_is_a_historic_Zamindar_Bari_located_in_Gouripur_Upazila_of_Mymensingh_District%2C_Bangladesh.This_Zamindar_Bari_was_established_around_the_1850s.jpg",
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
