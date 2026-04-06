-- Phase 2: Intelligent Itinerary Generation
-- ===========================================

-- Create Attractions table
CREATE TABLE IF NOT EXISTS attractions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    category VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    estimated_duration_hours INT DEFAULT 2,
    entry_fee_bdt INT DEFAULT 0,
    best_time_to_visit VARCHAR(100),
    rating DECIMAL(3, 1) DEFAULT 0.0,
    visit_count INT DEFAULT 0,
    travel_style VARCHAR(50),
    difficulty_level VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (destination_id) REFERENCES destination_info(destination_id),
    INDEX idx_destination (destination_id),
    INDEX idx_category (category),
    INDEX idx_rating (rating DESC)
);

-- Create Hotels table
CREATE TABLE IF NOT EXISTS hotels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    address VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    star_rating INT,
    phone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(255),
    economy_price_bdt INT,
    midrange_price_bdt INT,
    luxury_price_bdt INT,
    amenities VARCHAR(500),
    average_rating DECIMAL(3, 1) DEFAULT 0.0,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (destination_id) REFERENCES destination_info(destination_id),
    INDEX idx_destination (destination_id),
    INDEX idx_star_rating (star_rating DESC),
    INDEX idx_price_economy (economy_price_bdt)
);

-- Create Restaurants table
CREATE TABLE IF NOT EXISTS restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    cuisine_type VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    price_range VARCHAR(20),
    average_meal_cost_bdt INT,
    operating_hours VARCHAR(100),
    phone VARCHAR(20),
    average_rating DECIMAL(3, 1) DEFAULT 0.0,
    review_count INT DEFAULT 0,
    specialties VARCHAR(500),
    vegetarian_options BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (destination_id) REFERENCES destination_info(destination_id),
    INDEX idx_destination (destination_id),
    INDEX idx_cuisine (cuisine_type),
    INDEX idx_price_range (price_range)
);

-- Create Transport Routes table
CREATE TABLE IF NOT EXISTS transport_routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_destination_id BIGINT NOT NULL,
    target_destination_id BIGINT NOT NULL,
    transport_type VARCHAR(50) NOT NULL,
    distance_km INT,
    travel_time_hours DECIMAL(5, 2),
    cost_economy_bdt INT,
    cost_midrange_bdt INT,
    cost_luxury_bdt INT,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (source_destination_id) REFERENCES destination_info(destination_id),
    FOREIGN KEY (target_destination_id) REFERENCES destination_info(destination_id),
    INDEX idx_route (source_destination_id, target_destination_id),
    INDEX idx_transport (transport_type)
);

-- Create Activity Recommendations table
CREATE TABLE IF NOT EXISTS activity_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attraction_id BIGINT NOT NULL,
    travel_style VARCHAR(50) NOT NULL,
    duration_hours INT,
    recommended_time_slot VARCHAR(50),
    cost_estimation_bdt INT,
    suitable_for_age_group VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attraction_id) REFERENCES attractions(id),
    INDEX idx_attraction (attraction_id),
    INDEX idx_travel_style (travel_style)
);

-- ==========================================
-- Phase 2 Data Insertion: Attractions for Major Bangladesh Destinations
-- ==========================================

-- Dhaka Attractions
INSERT INTO attractions (destination_id, name, description, category, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(1, 'Lalbagh Fort', 'Historic 17th-century Mughal fort with mosque and mausoleum', 'Historical', 2, 50, 'October-March', 4.5, 'cultural', 'easy'),
(1, 'National Museum', 'Large museum with artifacts spanning from ancient to modern periods', 'Museum', 3, 30, 'Year-round', 4.0, 'cultural', 'easy'),
(1, 'Ahsan Manzil', 'Pink palace of the Nawab family, architectural marvel', 'Palace', 1.5, 50, 'October-March', 4.3, 'cultural', 'easy'),
(1, 'Star Mosque', 'Mosque with stunning star-studded interior decoration', 'Religious', 1, 0, 'Year-round', 4.2, 'cultural', 'easy'),
(1, 'Parliament House', 'Brutalist architecture masterpiece designed by Louis Kahn', 'Architecture', 2, 100, 'October-March', 4.4, 'cultural', 'moderate'),
(1, 'Dhaka University Area', 'Educational hub with historic buildings and green spaces', 'Educational', 2, 0, 'Year-round', 4.0, 'cultural', 'easy'),
(1, 'Buriganga River Boat Tour', 'Experience the old Dhaka from waterside perspective', 'Experience', 1.5, 75, 'October-April', 3.8, 'experience', 'easy'),
(1, 'Sadarghat Terminal', 'Historic river terminal with traditional boat culture', 'Cultural', 1, 0, 'Year-round', 3.5, 'adventure', 'easy');

-- Chittagong Attractions
INSERT INTO attractions (destination_id, name, description, category, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(2, 'Cox\'s Bazar Beach', 'World\'s longest natural sandy beach, perfect for families', 'Beach', 3, 0, 'October-March', 4.7, 'family', 'easy'),
(2, 'Inani Beach', 'Scenic beach with rocks and shells near Teknaf', 'Beach', 3, 0, 'October-March', 4.5, 'adventure', 'moderate'),
(2, 'Bandarban Chittagong Hill Tracts', 'Scenic hill region with tribal villages and forests', 'Adventure', 2, 0, 'October-March', 4.6, 'adventure', 'hard'),
(2, 'Rangamati Lake', 'Beautiful lake surrounded by hills and tribal settlements', 'Nature', 4, 0, 'Year-round', 4.4, 'relaxation', 'moderate'),
(2, 'Foy\'s Lake', 'Scenic lake in Chittagong with boating opportunities', 'Nature', 2, 0, 'October-March', 4.2, 'relaxation', 'easy'),
(2, 'Patenga Beach', 'Urban beach in Chittagong city with sunset views', 'Beach', 2, 0, 'Year-round', 4.0, 'relaxation', 'easy'),
(2, 'Naval Museum', 'Maritime museum showcasing Bangladesh naval history', 'Museum', 2, 40, 'Year-round', 3.9, 'cultural', 'easy');

-- Sylhet Attractions
INSERT INTO attractions (destination_id, name, description, category, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(4, 'Jaflong', 'Border area with tea gardens and scenic hills', 'Adventure', 2.5, 0, 'October-May', 4.4, 'adventure', 'easy'),
(4, 'Ratargul Swamp Forest', 'Only freshwater swamp forest in Bangladesh', 'Nature', 2, 50, 'July-October', 4.3, 'adventure', 'moderate'),
(4, 'Madhabkunda Waterfall', 'Picturesque waterfall with surrounding forests', 'Nature', 1.5, 20, 'June-October', 4.5, 'adventure', 'easy'),
(4, 'Tamabil Limestone Caves', 'Ancient limestone caves with scenic surroundings', 'Adventure', 2, 50, 'October-March', 4.2, 'adventure', 'moderate'),
(4, 'Sreemangal Tea Gardens', 'Green tea plantations ideal for family walks', 'Nature', 3, 0, 'October-March', 4.4, 'family', 'easy'),
(4, 'Sylhet City Tour', 'Historic buildings, mosques, and bazaars', 'Cultural', 2, 0, 'Year-round', 3.8, 'cultural', 'easy');

-- Khulna/Sundarbans Attractions
INSERT INTO attractions (destination_id, name, description, category, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(5, 'Sundarbans National Park Safari', 'World\'s largest mangrove forest and tiger reserve', 'Adventure', 6, 500, 'November-February', 4.8, 'adventure', 'hard'),
(5, 'Sundarbans Boat Tour', 'Guided boat tour through mangrove ecosystems', 'Experience', 4, 300, 'November-February', 4.6, 'relaxation', 'easy'),
(5, 'Khulna City Tour', 'Port city with historical mosques and monuments', 'Cultural', 2, 0, 'October-March', 3.7, 'cultural', 'easy'),
(5, 'Shela Riverbank Walk', 'Scenic riverbank with local life experiences', 'Experience', 1.5, 0, 'Year-round', 3.9, 'relaxation', 'easy');

-- Rajshahi/Bogra Attractions
INSERT INTO attractions (destination_id, name, description, category, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(6, 'Naogaon Archaeological Site', 'Ancient Buddhist monasteries and ruins', 'Historical', 3, 50, 'October-March', 4.1, 'cultural', 'moderate'),
(6, 'Mahasthangarh', 'Ancient fortress with archaeological museum', 'Historical', 2.5, 50, 'October-March', 4.2, 'cultural', 'easy'),
(6, 'Bogra Agriculture Museum', 'Unique museum showcasing traditional farming', 'Museum', 2, 30, 'Year-round', 3.8, 'cultural', 'easy'),
(6, 'Rabindra Kachharito Museum', 'Dedicated to Rabindranath Tagore in Kushtia', 'Museum', 2, 20, 'October-March', 3.9, 'cultural', 'easy');

-- ==========================================
-- Hotels Data
-- ==========================================

INSERT INTO hotels (destination_id, name, description, address, star_rating, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating) VALUES
(1, 'Hotel Grand Dhaka', 'Modern 5-star hotel in Gulshan', 'Gulshan, Dhaka', 5, 8000, 12000, 18000, 'WiFi, Gym, Pool, Restaurant, Room Service', 4.6),
(1, 'Dhaka Palace Hotel', 'Boutique hotel in Dhanmondi', 'Dhanmondi, Dhaka', 4, 4500, 7000, 11000, 'WiFi, Restaurant, Bar, AC, TV', 4.3),
(1, 'Budget Inn Dhaka', 'Affordable hostel in Old Dhaka', 'Old Dhaka', 2, 1200, 2000, 4000, 'WiFi, Basic Rooms, Common Area', 3.8),
(1, 'Radisson Blu Dhaka Water Garden', 'Luxury 5-star resort-style hotel', 'Banani, Dhaka', 5, 10000, 15000, 22000, 'Pool, Gym, Spa, Multiple Restaurants', 4.7),
(2, 'Cox\'s Bazar Resort Paradise', 'Beachfront resort with sea views', 'Cox\'s Bazar Beach', 4, 4000, 7500, 12000, 'Beach Access, Pool, Restaurant, Sunset View', 4.5),
(2, 'Seagull Hotel Cox\'s Bazar', 'Family-friendly beach hotel', 'Cox\'s Bazar', 3, 2500, 4500, 8000, 'Beach Adjacent, Pool, Restaurant', 4.2),
(2, 'Chittagong Hilltop Resort', 'Mountain resort near Bandarban', 'Bandarban', 3, 2800, 5000, 9000, 'Scenic Views, Restaurant, Hiking Trails', 4.4),
(4, 'Sylhet Green Valley Hotel', 'Tea garden view hotel', 'Sreemangal, Sylhet', 4, 3500, 6000, 10000, 'Garden Views, Restaurant, Nature Tours', 4.3),
(4, 'Sylhet City Hotel', 'Central location hotel', 'Sylhet City', 3, 2000, 3500, 6500, 'WiFi, AC, Restaurant, Business Center', 3.9),
(5, 'Khulna Sundarbans Lodge', 'Sundarbans gateway eco-lodge', 'Khulna', 4, 3000, 5500, 9500, 'Nature Oriented, Restaurant, Safari Packages', 4.4),
(5, 'Khulna City Hotel', 'Downtown Khulna hotel', 'Khulna', 2, 1500, 2800, 5500, 'Basic Amenities, Restaurant, AC', 3.7);

-- ==========================================
-- Restaurants Data
-- ==========================================

INSERT INTO restaurants (destination_id, name, description, cuisine_type, average_meal_cost_bdt, price_range, average_rating, vegetarian_options) VALUES
(1, 'Dhaka Biryani House', 'Traditional Dhaka biryani and meat dishes', 'Bengali', 400, 'budget', 4.5, true),
(1, 'Naan & Curry', 'North Indian cuisine', 'Indian', 500, 'budget', 4.2, true),
(1, 'Fusion Fine Dining', 'International fusion cuisine', 'International', 1200, 'luxury', 4.7, true),
(1, 'Sea Salt Seafood Restaurant', 'Fresh seafood dishes', 'Seafood', 800, 'midrange', 4.4, false),
(1, 'Vegetarian Paradise', 'Pure vegetarian restaurant', 'Vegetarian', 350, 'budget', 4.3, true),
(2, 'Cox\'s Beach Shack', 'Casual seafood near beach', 'Seafood', 500, 'budget', 4.3, false),
(2, 'Hillside Thai', 'Thai cuisine in Bandarban', 'Thai', 600, 'midrange', 4.2, true),
(2, 'Family Restaurant Chittagong', 'Multi-cuisine family restaurant', 'Multi-Cuisine', 450, 'budget', 3.9, true),
(4, 'Tea Garden Restaurant', 'Traditional Sylhet cuisine', 'Bengali', 400, 'budget', 4.4, true),
(4, 'Sreemangal Heritage Hotel', 'Traditional teagarden meals', 'Bengali', 350, 'budget', 4.2, true),
(5, 'Sundarbans Gateway Restaurant', 'Local Khulna cuisine', 'Bengali', 380, 'budget', 4.1, true),
(5, 'Royal Restaurant Khulna', 'Multi-cuisine restaurant', 'Multi-Cuisine', 500, 'midrange', 3.8, true);

-- ==========================================
-- Transport Routes
-- ==========================================

INSERT INTO transport_routes (source_destination_id, target_destination_id, transport_type, distance_km, travel_time_hours, cost_economy_bdt, cost_midrange_bdt, cost_luxury_bdt) VALUES
(1, 2, 'bus', 253, 6, 600, 900, 1500),
(1, 2, 'train', 253, 8, 400, 700, 1200),
(1, 2, 'flight', 253, 1, 3000, 4500, 6000),
(1, 4, 'bus', 240, 5.5, 550, 850, 1400),
(1, 4, 'flight', 240, 1, 2500, 4000, 5500),
(1, 5, 'bus', 350, 8, 700, 1100, 1800),
(1, 5, 'train', 350, 10, 500, 850, 1500),
(1, 6, 'bus', 280, 6, 650, 1000, 1600),
(2, 4, 'bus', 500, 12, 900, 1400, 2200),
(2, 5, 'bus', 480, 11, 850, 1300, 2000),
(4, 5, 'bus', 700, 16, 1200, 1800, 2800),
(5, 6, 'bus', 350, 8, 700, 1100, 1800);

-- ==========================================
-- Activity Recommendations
-- ==========================================

INSERT INTO activity_recommendations (attraction_id, travel_style, duration_hours, recommended_time_slot, cost_estimation_bdt, suitable_for_age_group) VALUES
(1, 'cultural', 2, 'morning', 100, 'all ages'),
(1, 'family', 2, 'afternoon', 100, 'all ages'),
(1, 'adventure', 1.5, 'afternoon', 50, '16+'),
(2, 'cultural', 3, 'morning', 100, 'all ages'),
(3, 'cultural', 1.5, 'afternoon', 100, 'all ages'),
(5, 'cultural', 2, 'morning', 200, '18+'),
(8, 'experience', 1.5, 'evening', 150, 'all ages'),
(9, 'adventure', 3, 'morning', 100, 'all ages'),
(10, 'relaxation', 3, 'afternoon', 200, 'all ages'),
(11, 'adventure', 2.5, 'afternoon', 150, '16+'),
(15, 'adventure', 2, 'morning', 100, '18+'),
(16, 'family', 3, 'afternoon', 50, 'all ages');
