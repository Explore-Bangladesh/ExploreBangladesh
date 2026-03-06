package com.TeamDeadlock.ExploreBangladesh.config;

import com.TeamDeadlock.ExploreBangladesh.entity.*;
import com.TeamDeadlock.ExploreBangladesh.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Seeds all reference data into the database on first startup.
 * Each block is guarded by a count-check so re-runs are idempotent.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private final CityCoordinateRepository cityCoordinateRepository;
    private final AirportRepository airportRepository;
    private final AirlineRepository airlineRepository;
    private final CarRepository carRepository;
    private final GuideRepository guideRepository;

    public DataInitializer(CityCoordinateRepository cityCoordinateRepository,
                           AirportRepository airportRepository,
                           AirlineRepository airlineRepository,
                           CarRepository carRepository,
                           GuideRepository guideRepository) {
        this.cityCoordinateRepository = cityCoordinateRepository;
        this.airportRepository = airportRepository;
        this.airlineRepository = airlineRepository;
        this.carRepository = carRepository;
        this.guideRepository = guideRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedCityCoordinates();
        seedAirports();
        seedAirlines();
        seedCars();
        seedGuides();
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
}
