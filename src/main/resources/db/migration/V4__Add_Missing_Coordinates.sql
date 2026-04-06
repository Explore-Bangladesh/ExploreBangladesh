-- ========================================
-- V4: Add Missing Coordinates to Hotels, Attractions, and Restaurants
-- Purpose: Fix location-based routing by adding exact coordinates
-- ========================================

-- ==========================================
-- Update Hotels with Coordinates
-- ==========================================

-- Dhaka Hotels
UPDATE hotels SET latitude = 23.8103, longitude = 90.4125 WHERE name = 'Hotel Grand Dhaka';
UPDATE hotels SET latitude = 23.7645, longitude = 90.3636 WHERE name = 'Dhaka Palace Hotel';
UPDATE hotels SET latitude = 23.8050, longitude = 90.3563 WHERE name = 'Budget Inn Dhaka';
UPDATE hotels SET latitude = 23.8300, longitude = 90.4200 WHERE name = 'Radisson Blu Dhaka Water Garden';

-- Cox's Bazar Hotels
UPDATE hotels SET latitude = 21.4272, longitude = 91.9700 WHERE name = 'Cox''s Bazar Resort Paradise';
UPDATE hotels SET latitude = 21.4300, longitude = 91.9720 WHERE name = 'Seagull Hotel Cox''s Bazar';
UPDATE hotels SET latitude = 21.9500, longitude = 92.2400 WHERE name = 'Chittagong Hilltop Resort';

-- Sylhet Hotels
UPDATE hotels SET latitude = 24.3000, longitude = 91.7200 WHERE name = 'Sylhet Green Valley Hotel';
UPDATE hotels SET latitude = 24.8949, longitude = 91.8734 WHERE name = 'Sylhet City Hotel';

-- Khulna Hotels
UPDATE hotels SET latitude = 22.8456, longitude = 89.5644 WHERE name = 'Khulna Sundarbans Lodge';
UPDATE hotels SET latitude = 22.8456, longitude = 89.5644 WHERE name = 'Khulna City Hotel';

-- ==========================================
-- Update Attractions with Coordinates
-- ==========================================

-- Dhaka Attractions
UPDATE attractions SET latitude = 23.7832, longitude = 90.2506 WHERE name = 'Lalbagh Fort' AND destination_id = 1;
UPDATE attractions SET latitude = 23.8162, longitude = 90.3646 WHERE name = 'National Museum' AND destination_id = 1;
UPDATE attractions SET latitude = 23.8100, longitude = 90.3571 WHERE name = 'Ahsan Manzil' AND destination_id = 1;
UPDATE attractions SET latitude = 23.7000, longitude = 90.5000 WHERE name = 'Star Mosque' AND destination_id = 1;
UPDATE attractions SET latitude = 23.8044, longitude = 90.3788 WHERE name = 'Parliament House' AND destination_id = 1;
UPDATE attractions SET latitude = 23.7292, longitude = 90.3629 WHERE name = 'Dhaka University Area' AND destination_id = 1;
UPDATE attractions SET latitude = 23.7262, longitude = 90.2971 WHERE name = 'Buriganga River Boat Tour' AND destination_id = 1;
UPDATE attractions SET latitude = 23.7344, longitude = 90.2958 WHERE name = 'Sadarghat Terminal' AND destination_id = 1;

-- Cox's Bazar Beach Attractions  
UPDATE attractions SET latitude = 21.4272, longitude = 91.9700 WHERE name = 'Cox''s Bazar Beach' AND destination_id = 2;
UPDATE attractions SET latitude = 21.2897, longitude = 92.1700 WHERE name = 'Inani Beach' AND destination_id = 2;
UPDATE attractions SET latitude = 21.9500, longitude = 92.2400 WHERE name = 'Bandarban Chittagong Hill Tracts' AND destination_id = 2;
UPDATE attractions SET latitude = 22.6500, longitude = 92.5000 WHERE name = 'Rangamati Lake' AND destination_id = 2;
UPDATE attractions SET latitude = 22.3569, longitude = 91.8100 WHERE name = 'Foy''s Lake' AND destination_id = 2;
UPDATE attractions SET latitude = 22.1900, longitude = 91.9300 WHERE name = 'Patenga Beach' AND destination_id = 2;
UPDATE attractions SET latitude = 22.3500, longitude = 91.8300 WHERE name = 'Naval Museum' AND destination_id = 2;

-- Sylhet Attractions
UPDATE attractions SET latitude = 25.2167, longitude = 91.9500 WHERE name = 'Jaflong' AND destination_id = 4;
UPDATE attractions SET latitude = 24.4667, longitude = 91.7833 WHERE name = 'Ratargul Swamp Forest' AND destination_id = 4;
UPDATE attractions SET latitude = 24.6667, longitude = 91.7500 WHERE name = 'Madhabkunda Waterfall' AND destination_id = 4;
UPDATE attractions SET latitude = 25.1667, longitude = 91.8333 WHERE name = 'Tamabil Limestone Caves' AND destination_id = 4;
UPDATE attractions SET latitude = 24.3000, longitude = 91.7200 WHERE name = 'Sreemangal Tea Gardens' AND destination_id = 4;
UPDATE attractions SET latitude = 24.8949, longitude = 91.8734 WHERE name = 'Sylhet City Tour' AND destination_id = 4;

-- Khulna/Sundarbans Attractions
UPDATE attractions SET latitude = 22.4833, longitude = 89.2833 WHERE name = 'Sundarbans National Park Safari' AND destination_id = 5;
UPDATE attractions SET latitude = 22.4833, longitude = 89.2833 WHERE name = 'Sundarbans Boat Tour' AND destination_id = 5;
UPDATE attractions SET latitude = 22.8456, longitude = 89.5644 WHERE name = 'Khulna City Tour' AND destination_id = 5;
UPDATE attractions SET latitude = 22.8500, longitude = 89.5500 WHERE name = 'Shela Riverbank Walk' AND destination_id = 5;

-- Rajshahi/Bogra Attractions
UPDATE attractions SET latitude = 24.6426, longitude = 88.6320 WHERE name = 'Naogaon Archaeological Site' AND destination_id = 6;
UPDATE attractions SET latitude = 24.8500, longitude = 89.1833 WHERE name = 'Mahasthangarh' AND destination_id = 6;
UPDATE attractions SET latitude = 24.8500, longitude = 89.6139 WHERE name = 'Bogra Agriculture Museum' AND destination_id = 6;
UPDATE attractions SET latitude = 23.0050, longitude = 89.3000 WHERE name = 'Rabindra Kachharito Museum' AND destination_id = 6;

-- ==========================================
-- Update Restaurants with Coordinates
-- ==========================================

-- Dhaka Restaurants
UPDATE restaurants SET latitude = 23.8103, longitude = 90.4125 WHERE name = 'Dhaka Biryani House' AND destination_id = 1;
UPDATE restaurants SET latitude = 23.7645, longitude = 90.3636 WHERE name = 'Naan & Curry' AND destination_id = 1;
UPDATE restaurants SET latitude = 23.8050, longitude = 90.3563 WHERE name = 'Fusion Fine Dining' AND destination_id = 1;
UPDATE restaurants SET latitude = 23.8300, longitude = 90.4200 WHERE name = 'Sea Salt Seafood Restaurant' AND destination_id = 1;
UPDATE restaurants SET latitude = 23.7900, longitude = 90.3900 WHERE name = 'Vegetarian Paradise' AND destination_id = 1;

-- Cox's Bazar Restaurants
UPDATE restaurants SET latitude = 21.4272, longitude = 91.9700 WHERE name = 'Cox''s Beach Shack' AND destination_id = 2;
UPDATE restaurants SET latitude = 21.9500, longitude = 92.2400 WHERE name = 'Hillside Thai' AND destination_id = 2;
UPDATE restaurants SET latitude = 21.4300, longitude = 91.9720 WHERE name = 'Family Restaurant Chittagong' AND destination_id = 2;

-- Sylhet Restaurants
UPDATE restaurants SET latitude = 24.3000, longitude = 91.7200 WHERE name = 'Tea Garden Restaurant' AND destination_id = 4;
UPDATE restaurants SET latitude = 24.3000, longitude = 91.7200 WHERE name = 'Sreemangal Heritage Hotel' AND destination_id = 4;

-- Khulna Restaurants
UPDATE restaurants SET latitude = 22.4833, longitude = 89.2833 WHERE name = 'Sundarbans Gateway Restaurant' AND destination_id = 5;
UPDATE restaurants SET latitude = 22.8456, longitude = 89.5644 WHERE name = 'Royal Restaurant Khulna' AND destination_id = 5;

-- ==========================================
-- V4 Migration Complete
-- All coordinates have been added for location-based routing
-- ==========================================
