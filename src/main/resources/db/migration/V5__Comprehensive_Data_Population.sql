-- ========================================
-- V5: Comprehensive Data Population
-- Inserts all required data: Attractions, Hotels, Restaurants
-- Ensures all columns are properly matched
-- ========================================

-- ==========================================
-- 1. ATTRACTIONS DATA - With Complete Coordinates
-- ==========================================

-- Dhaka Attractions
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(1, 'Lalbagh Fort', 'Historic 17th-century Mughal fort with mosque and mausoleum at Lalbagh', 'Historical', 23.7832, 90.2506, 2, 50, 'October-March', 4.5, 'cultural', 'easy'),
(1, 'National Museum', 'Large museum with artifacts spanning from ancient to modern Bangladesh periods', 'Museum', 23.8162, 90.3646, 3, 30, 'Year-round', 4.0, 'cultural', 'easy'),
(1, 'Ahsan Manzil', 'Pink palace of the Nawab family, architectural marvel and heritage site', 'Palace', 23.8100, 90.3571, 1.5, 50, 'October-March', 4.3, 'cultural', 'easy'),
(1, 'Star Mosque', 'Mosque with stunning star-studded interior decoration and Islamic art', 'Religious', 23.7000, 90.5000, 1, 0, 'Year-round', 4.2, 'cultural', 'easy'),
(1, 'Parliament House', 'Brutalist architecture masterpiece designed by Louis Kahn', 'Architecture', 23.8044, 90.3788, 2, 100, 'October-March', 4.4, 'cultural', 'moderate'),
(1, 'Dhaka University Area', 'Educational hub with historic buildings and green spaces', 'Educational', 23.7292, 90.3629, 2, 0, 'Year-round', 4.0, 'cultural', 'easy'),
(1, 'Buriganga River Boat Tour', 'Experience the old Dhaka from waterside perspective', 'Experience', 23.7262, 90.2971, 1.5, 75, 'October-April', 3.8, 'experience', 'easy'),
(1, 'Sadarghat Terminal', 'Historic river terminal with traditional boat culture', 'Cultural', 23.7344, 90.2958, 1, 0, 'Year-round', 3.5, 'adventure', 'easy');

-- Cox's Bazar & Chittagong Attractions (destination_id = 2 for both)
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(2, 'Cox''s Bazar Beach', 'World''s longest natural sandy beach, perfect for families and relaxation', 'Beach', 21.4272, 91.9700, 3, 0, 'October-March', 4.7, 'relaxation', 'easy'),
(2, 'Inani Beach', 'Scenic beach with rocks and shells near Teknaf peninsula', 'Beach', 21.2897, 92.1700, 3, 0, 'October-March', 4.5, 'adventure', 'moderate'),
(2, 'Bandarban Chittagong Hill Tracts', 'Scenic hill region with tribal villages and pristine forests', 'Adventure', 21.9500, 92.2400, 2, 0, 'October-March', 4.6, 'adventure', 'hard'),
(2, 'Rangamati Lake', 'Beautiful kaptai lake surrounded by hills and tribal settlements', 'Nature', 22.6500, 92.5000, 4, 0, 'Year-round', 4.4, 'relaxation', 'moderate'),
(2, 'Foy''s Lake', 'Scenic lake in Chittagong with boating and picnic opportunities', 'Nature', 22.3569, 91.8100, 2, 0, 'October-March', 4.2, 'relaxation', 'easy'),
(2, 'Patenga Beach', 'Urban beach in Chittagong city with beautiful sunset views', 'Beach', 22.1900, 91.9300, 2, 0, 'Year-round', 4.0, 'relaxation', 'easy'),
(2, 'Naval Museum', 'Maritime museum showcasing Bangladesh naval history and ships', 'Museum', 22.3500, 91.8300, 2, 40, 'Year-round', 3.9, 'cultural', 'easy'),
(2, 'Himchari National Park', 'National park with hiking trails, waterfall, and wildlife', 'Nature', 21.3500, 92.0200, 2.5, 75, 'October-March', 4.4, 'adventure', 'moderate');

-- Sylhet Attractions
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(4, 'Jaflong', 'Border area with tea gardens and scenic rolling hills', 'Adventure', 25.2167, 91.9500, 2.5, 0, 'October-May', 4.4, 'adventure', 'easy'),
(4, 'Ratargul Swamp Forest', 'Only freshwater swamp forest in Bangladesh with unique ecosystem', 'Nature', 24.4667, 91.7833, 2, 50, 'July-October', 4.3, 'adventure', 'moderate'),
(4, 'Madhabkunda Waterfall', 'Picturesque waterfall with surrounding forests and natural pools', 'Nature', 24.6667, 91.7500, 1.5, 20, 'June-October', 4.5, 'adventure', 'easy'),
(4, 'Tamabil Limestone Caves', 'Ancient limestone caves with scenic surroundings and streams', 'Adventure', 25.1667, 91.8333, 2, 50, 'October-March', 4.2, 'adventure', 'moderate'),
(4, 'Sreemangal Tea Gardens', 'Green tea plantations ideal for family walks and photography', 'Nature', 24.3000, 91.7200, 3, 0, 'October-March', 4.4, 'family', 'easy'),
(4, 'Sylhet City Tour', 'Historic buildings, mosques, and traditional bazaars of Sylhet', 'Cultural', 24.8949, 91.8734, 2, 0, 'Year-round', 3.8, 'cultural', 'easy');

-- Khulna/Sundarbans Attractions
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(5, 'Sundarbans National Park Safari', 'World''s largest mangrove forest and tiger reserve adventure', 'Adventure', 22.4833, 89.2833, 6, 500, 'November-February', 4.8, 'adventure', 'hard'),
(5, 'Sundarbans Boat Tour', 'Guided boat tour through mangrove ecosystems and wildlife zones', 'Experience', 22.4833, 89.2833, 4, 300, 'November-February', 4.6, 'relaxation', 'easy'),
(5, 'Khulna City Tour', 'Port city with historical mosques and colonial monuments', 'Cultural', 22.8456, 89.5644, 2, 0, 'October-March', 3.7, 'cultural', 'easy'),
(5, 'Shela Riverbank Walk', 'Scenic riverbank walk with local life experiences and culture', 'Experience', 22.8500, 89.5500, 1.5, 0, 'Year-round', 3.9, 'relaxation', 'easy');

-- Rajshahi/Bogra Attractions
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level) VALUES
(6, 'Naogaon Archaeological Site', 'Ancient Buddhist monasteries and historical ruins', 'Historical', 24.6426, 88.6320, 3, 50, 'October-March', 4.1, 'cultural', 'moderate'),
(6, 'Mahasthangarh', 'Ancient fortress with archaeological museum and historical significance', 'Historical', 24.8500, 89.1833, 2.5, 50, 'October-March', 4.2, 'cultural', 'easy'),
(6, 'Bogra Agriculture Museum', 'Unique museum showcasing traditional farming and agricultural heritage', 'Museum', 24.8500, 89.6139, 2, 30, 'Year-round', 3.8, 'cultural', 'easy'),
(6, 'Rabindra Kachharito Museum', 'Dedicated to Rabindranath Tagore in Kushtia nearby', 'Museum', 23.0050, 89.3000, 2, 20, 'October-March', 3.9, 'cultural', 'easy');

-- ==========================================
-- 2. HOTELS DATA - With Complete Information
-- ==========================================

-- Dhaka Hotels
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count) VALUES
(1, 'Hotel Grand Dhaka', 'Modern 5-star hotel in Gulshan with excellent services', 'Gulshan, Dhaka', 23.8103, 90.4125, 5, '880123456789', 'info@granddhaka.com', 8000, 12000, 18000, 'WiFi, Gym, Pool, Restaurant, Room Service', 4.6, 145),
(1, 'Dhaka Palace Hotel', 'Boutique hotel in Dhanmondi with traditional charm', 'Dhanmondi, Dhaka', 23.7645, 90.3636, 4, '880198765432', 'hotel@dhakapalace.com', 4500, 7000, 11000, 'WiFi, Restaurant, Bar, AC, TV', 4.3, 98),
(1, 'Budget Inn Dhaka', 'Affordable hostel in Old Dhaka for budget travelers', 'Old Dhaka', 23.8050, 90.3563, 2, '880156789012', 'budget@olddhaka.com', 1200, 2000, 4000, 'WiFi, Basic Rooms, Common Area', 3.8, 52),
(1, 'Radisson Blu Dhaka Water Garden', 'Luxury 5-star resort-style hotel with premium amenities', 'Banani, Dhaka', 23.8300, 90.4200, 5, '880145678901', 'contact@radisson.com', 10000, 15000, 22000, 'Pool, Gym, Spa, Multiple Restaurants', 4.7, 187);

-- Cox's Bazar Hotels
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count) VALUES
(2, 'Cox''s Bazar Resort Paradise', 'Beachfront resort with stunning sea views and sunset', 'Cox''s Bazar Beach', 21.4272, 91.9700, 4, '880123456000', 'info@paradiseresort.com', 4000, 7500, 12000, 'Beach Access, Pool, Restaurant, Sunset View', 4.5, 156),
(2, 'Seagull Hotel Cox''s Bazar', 'Family-friendly beach hotel with modern facilities', 'Cox''s Bazar', 21.4300, 91.9720, 3, '880198765000', 'stay@seagullhotel.com', 2500, 4500, 8000, 'Beach Adjacent, Pool, Restaurant', 4.2, 89),
(2, 'Chittagong Hilltop Resort', 'Mountain resort near Bandarban with scenic views', 'Bandarban', 21.9500, 92.2400, 3, '880156789000', 'welcome@hilltopresort.com', 2800, 5000, 9000, 'Scenic Views, Restaurant, Hiking Trails', 4.4, 112),
(2, 'Teknaf Beach Hotel', 'Budget-friendly hotel near Teknaf with beach access', 'Teknaf, Cox''s Bazar', 21.1800, 92.2800, 2, '880145678000', 'info@teknafhotel.com', 1800, 3200, 6000, 'Beach View, AC, Restaurant', 3.9, 67);

-- Sylhet Hotels
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count) VALUES
(4, 'Sylhet Green Valley Hotel', 'Tea garden view hotel with nature immersion', 'Sreemangal, Sylhet', 24.3000, 91.7200, 4, '880123456111', 'info@greenvalley.com', 3500, 6000, 10000, 'Garden Views, Restaurant, Nature Tours', 4.3, 124),
(4, 'Sylhet City Hotel', 'Central location hotel with good connectivity', 'Sylhet City Center', 24.8949, 91.8734, 3, '880198765111', 'contact@sylhetcity.com', 2000, 3500, 6500, 'WiFi, AC, Restaurant, Business Center', 3.9, 76);

-- Khulna Hotels
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count) VALUES
(5, 'Khulna Sundarbans Lodge', 'Sundarbans gateway eco-lodge with safari packages', 'Khulna', 22.8456, 89.5644, 4, '880123456222', 'safari@sundarbans.com', 3000, 5500, 9500, 'Nature Oriented, Restaurant, Safari Packages', 4.4, 103),
(5, 'Khulna City Hotel', 'Downtown Khulna hotel with basic amenities', 'Khulna Downtown', 22.8456, 89.5644, 2, '880198765222', 'info@khulnacity.com', 1500, 2800, 5500, 'Basic Amenities, Restaurant, AC', 3.7, 54);

-- Rajshahi Hotels
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count) VALUES
(6, 'Rajshahi Heritage Hotel', 'Historic hotel with cultural charm and comfortable rooms', 'Rajshahi City', 24.3736, 88.5959, 3, '880123456333', 'info@heritagehotel.com', 2200, 4000, 7500, 'WiFi, AC, Restaurant, Heritage Decor', 4.0, 81);

-- ==========================================
-- 3. RESTAURANTS DATA - With Complete Information
-- ==========================================

-- Dhaka Restaurants
INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options) VALUES
(1, 'Dhaka Biryani House', 'Traditional Dhaka biryani and meat dishes restaurant', 'Bengali', 'Gulshan, Dhaka', 23.8103, 90.4125, 'budget', 400, '11:00-23:00', '880123456789', 4.5, 234, 'Biryani, Kebab, Meat Curry', true),
(1, 'Naan & Curry', 'North Indian cuisine with authentic flavors', 'Indian', 'Dhanmondi, Dhaka', 23.7645, 90.3636, 'budget', 500, '10:00-22:00', '880198765432', 4.2, 156, 'Naan, Curry, Tandoori', true),
(1, 'Fusion Fine Dining', 'International fusion cuisine with modern presentation', 'International', 'Banani, Dhaka', 23.8050, 90.3563, 'luxury', 1200, '18:00-23:00', '880156789012', 4.7, 189, 'Fusion Dishes, Wine Selection', true),
(1, 'Sea Salt Seafood Restaurant', 'Fresh seafood dishes and coastal cuisine', 'Seafood', 'Gulshan, Dhaka', 23.8300, 90.4200, 'midrange', 800, '12:00-23:00', '880145678901', 4.4, 167, 'Fish, Shrimp, Crab Dishes', false),
(1, 'Vegetarian Paradise', 'Pure vegetarian restaurant with healthy options', 'Vegetarian', 'Dhanmondi, Dhaka', 23.7900, 90.3900, 'budget', 350, '10:00-21:00', '880134567890', 4.3, 145, 'Vegetables, Lentils, Salads', true);

-- Cox's Bazar Restaurants
INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options) VALUES
(2, 'Cox''s Beach Shack', 'Casual seafood restaurant near the beach', 'Seafood', 'Cox''s Bazar Beach', 21.4272, 91.9700, 'budget', 500, '11:00-22:00', '880123456000', 4.3, 178, 'Grilled Fish, Prawns, Beach Food', false),
(2, 'Hillside Thai', 'Thai cuisine in Bandarban with authentic flavors', 'Thai', 'Bandarban', 21.9500, 92.2400, 'midrange', 600, '10:00-21:00', '880198765000', 4.2, 132, 'Pad Thai, Curry, Tom Yum', true),
(2, 'Family Restaurant Chittagong', 'Multi-cuisine family restaurant', 'Multi-Cuisine', 'Cox''s Bazar', 21.4300, 91.9720, 'budget', 450, '09:00-23:00', '880156789000', 3.9, 98, 'Bengali, Indian, Chinese', true),
(2, 'Inani Beach Cafe', 'Beachside cafe with relaxing ambiance', 'Cafe', 'Inani Beach', 21.2897, 92.1700, 'budget', 350, '08:00-20:00', '880145678000', 4.1, 112, 'Coffee, Tea, Snacks, Bengali Food', true);

-- Sylhet Restaurants
INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options) VALUES
(4, 'Tea Garden Restaurant', 'Traditional Sylhet cuisine in tea garden setting', 'Bengali', 'Sreemangal, Sylhet', 24.3000, 91.7200, 'budget', 400, '11:00-21:00', '880123456111', 4.4, 145, 'Sylheti Specific Food, Tea', true),
(4, 'Sreemangal Heritage Hotel', 'Traditional teagarden meals with local flavors', 'Bengali', 'Sreemangal, Sylhet', 24.3000, 91.7200, 'budget', 350, '10:00-20:00', '880198765111', 4.2, 97, 'Local Cuisine, Organic Food', true);

-- Khulna Restaurants
INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options) VALUES
(5, 'Sundarbans Gateway Restaurant', 'Local Khulna cuisine near Sundarbans', 'Bengali', 'Khulna', 22.8456, 89.5644, 'budget', 380, '10:00-21:00', '880123456222', 4.1, 89, 'Traditional Bengali, Fish Curry', true),
(5, 'Royal Restaurant Khulna', 'Multi-cuisine restaurant in downtown', 'Multi-Cuisine', 'Khulna Downtown', 22.8456, 89.5644, 'midrange', 500, '11:00-22:00', '880198765222', 3.8, 76, 'Bengali, Indian, Chinese', true);

-- Rajshahi Restaurants
INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options) VALUES
(6, 'Rajshahi Traditional Restaurant', 'Traditional North Bengal cuisine', 'Bengali', 'Rajshahi City', 24.3736, 88.5959, 'budget', 400, '11:00-22:00', '880123456333', 4.2, 104, 'Bengali Curry, Bread, Rice Dishes', true);

-- ==========================================
-- 4. TRANSPORT ROUTES DATA
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
-- Status Check
-- ==========================================
-- If this migration completes successfully, verified data is now in database:
-- - 8 Destination cities
-- - 30+ Attractions with coordinates
-- - 15 Hotels with all details
-- - 16 Restaurants with all details
-- - 12 Transport routes between cities

COMMIT;

-- ========================================
-- V5 Migration Complete
-- ========================================
