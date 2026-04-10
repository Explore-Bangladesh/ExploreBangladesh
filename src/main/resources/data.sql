-- ========================================
-- V10: COMPLETE DATA POPULATION
-- Purpose: Insert all necessary data for Smart Planner
-- Tables are created by Hibernate entities automatically
-- This migration ONLY inserts data - NO CREATE TABLE statements
-- ========================================

-- ==========================================
-- 1. DESTINATION INFO (6 major destinations)
-- ==========================================

INSERT INTO destination_info 
(city_name, country, latitude, longitude, best_month_from, best_month_to, suggested_duration_days, safety_rating, language, description, created_at, updated_at) 
VALUES
('Dhaka', 'Bangladesh', 23.8103, 90.4125, 10, 3, 3, 4.2, 'Bengali', 'Capital city rich in history and culture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cox''s Bazar', 'Bangladesh', 21.4272, 91.9700, 10, 3, 4, 4.5, 'Bengali', 'World''s longest natural beach, relaxation paradise', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sylhet', 'Bangladesh', 24.8949, 91.8734, 10, 4, 3, 4.1, 'Bengali', 'Green tea gardens and adventure destination', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chittagong', 'Bangladesh', 22.3569, 91.8100, 10, 3, 2, 4.0, 'Bengali', 'Port city with marine culture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Khulna', 'Bangladesh', 22.8456, 89.5644, 9, 3, 3, 4.0, 'Bengali', 'Gateway to Sundarbans tiger reserve', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bandarban', 'Bangladesh', 21.9500, 92.2400, 10, 4, 3, 4.1, 'Bengali', 'Hill tracts with tribal culture and adventure', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- 2. ATTRACTIONS (25+ per destination, all travel styles)
-- ==========================================

-- ========== DHAKA ATTRACTIONS (28 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- CULTURAL (8)
(1, 'Lalbagh Fort', '17th-century Mughal fort with mosque and mausoleum. Historic fortress built in 1678', 'Historical', 23.7832, 90.2506, 2, 50, 'October-March', 4.5, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Ahsan Manzil Palace', 'Pink palace architectural marvel of Nawab family, 52-room structure', 'Palace', 23.8100, 90.3571, 1.5, 50, 'October-March', 4.3, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'National Museum Dhaka', 'Largest museum with Buddhist sculptures, paintings, artifacts from ancient to modern Bangladesh', 'Museum', 23.8162, 90.3646, 3, 30, 'Year-round', 4.0, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Parliament House Dhaka', 'Brutalist architectural masterpiece designed by Louis Kahn in 1982', 'Architecture', 23.8044, 90.3788, 2, 100, 'October-March', 4.4, 'cultural', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Star Mosque Dhaka', 'Historic mosque with stunning star-studded interior decoration and Islamic calligraphy', 'Religious', 23.7000, 90.5000, 1, 0, 'Year-round', 4.2, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Bangabandhu Museum', 'Historic residence of Sheikh Mujibur Rahman, founder of Bangladesh', 'Historical', 23.7500, 90.3800, 1.5, 20, 'Year-round', 4.2, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Army Museum Dhaka', 'Military history and artifacts showcasing Bangladesh armed forces heritage', 'Museum', 23.7800, 90.4000, 2, 50, 'Year-round', 4.1, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Dhaka University Campus', 'Historic buildings, green spaces, and educational hub established in 1921', 'Educational', 23.7292, 90.3629, 2, 0, 'Year-round', 4.0, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- ADVENTURE (7)
(1, 'Sadarghat Boat Tour', 'Explore old Dhaka from waterside perspective with traditional wooden boats', 'Adventure', 23.7344, 90.2958, 2, 100, 'October-April', 3.8, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Buriganga River Cruise', 'Sunset cruise on historic river with views of old Dhaka and industrial landscape', 'Adventure', 23.7262, 90.2971, 1.5, 150, 'November-February', 3.9, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Old Dhaka Walking Heritage Tour', 'Walking tour through narrow streets, bazaars, colonial buildings and heritage sites', 'Walking Tour', 23.7200, 90.3000, 3, 200, 'October-March', 4.0, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Rickshaw Heritage Ride', 'Traditional hand-pulled rickshaw ride through city with cultural experiences', 'Experience', 23.7300, 90.3100, 2, 100, 'Year-round', 3.7, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Baldha Garden Nature Walk', 'Historic garden with rare medicinal plants, trees and botanical specimens', 'Nature', 23.7400, 90.3900, 1.5, 20, 'December-February', 3.8, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Street Food Walking Adventure', 'Guided tour of famous street food stalls in Old Dhaka and Gulshan', 'Experience', 23.7250, 90.3050, 2.5, 250, 'Year-round', 4.1, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Cycling Around Dhanmondi Lake', 'Bicycle tour around scenic Dhanmondi Lake with stops at local spots', 'Adventure', 23.7500, 90.3700, 1.5, 150, 'Early Morning', 3.9, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- RELAXATION (8)
(1, 'Ramna Park Dhaka', 'Large urban park with green spaces, walking trails and local gatherings', 'Park', 23.7500, 90.3900, 2, 0, 'Year-round', 4.1, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Hatirjheel Lake Scenic Walk', 'Scenic urban lake with walking paths, bridges and evening strolls', 'Lake', 23.7600, 90.4200, 2, 0, 'October-March', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Botanical Garden Dhaka', 'Garden with diverse plant species, flower beds and peaceful atmosphere', 'Garden', 23.7700, 90.3800, 2, 20, 'November-February', 4.0, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Suhrawardy Udyan Historic Park', 'Historic park with open spaces, monuments and cultural significance', 'Park', 23.7400, 90.4000, 1.5, 0, 'Year-round', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Dhanmondi Lake Promenade', 'Urban lake with walking paths, water views and relaxation spaces', 'Lake', 23.7500, 90.3700, 1.5, 0, 'Year-round', 4.0, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Crescent Lake Park', 'Modern urban park with open green areas and recreational facilities', 'Park', 23.8000, 90.4000, 1.5, 0, 'Year-round', 3.8, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Gulshan Lake Jogging Track', 'Jogging and walking track around scenic Gulshan Lake with cafes nearby', 'Park', 23.8000, 90.4200, 1, 0, 'Evening', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Banani Spa & Wellness', 'Wellness centers and spa for relaxation therapy treatments', 'Wellness', 23.8300, 90.4200, 1.5, 400, 'Year-round', 4.3, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (5)
(1, 'Bangladesh National Zoo', 'Zoo with diverse animal species, birds, reptiles and family entertainment', 'Zoo', 23.7800, 90.3500, 3, 100, 'Year-round', 4.0, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Fantasy Kingdom Theme Park', 'Amusement park with rides, games and entertainment for all ages', 'Amusement Park', 23.8200, 90.3300, 4, 500, 'November-February', 4.2, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Bangladesh Science Museum', 'Interactive science exhibits, planetarium and educational displays for children', 'Museum', 23.7700, 90.3800, 2.5, 30, 'Year-round', 4.1, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Children''s Park Playground', 'Dedicated children''s playground with equipment and recreational activities', 'Park', 23.7500, 90.3900, 2, 50, 'Year-round', 3.9, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Aquatic Center Dhaka', 'Swimming pools, water sports and aquatic entertainment for families', 'Recreation', 23.8000, 90.4100, 2, 200, 'Year-round', 4.0, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- SOLO (4)
(1, 'Kawran Bazar Fresh Market', 'Authentic traditional market experience with local produce and vendors', 'Market', 23.7600, 90.4000, 1.5, 0, 'Morning', 3.7, 'solo', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'New Market Shopping District', 'Traditional shopping experience with boutiques, fashion and local goods', 'Shopping', 23.7400, 90.3800, 2, 0, 'Afternoon', 3.8, 'solo', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Bangladesh Shilpakala Academy', 'Cultural performances, art exhibitions and traditional performances venue', 'Cultural', 23.7600, 90.3900, 2, 100, 'Evening', 4.1, 'solo', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Gulshan Artisan Cafes', 'Cafe culture with local artists, solo-friendly atmosphere and good coffee', 'Cafe', 23.8000, 90.4100, 1.5, 200, 'Anytime', 4.2, 'solo', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== COX'S BAZAR ATTRACTIONS (26 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- RELAXATION (10)
(2, 'Cox''s Bazar Main Beach', 'World''s longest natural sandy beach, 120 km stretch perfect for relaxation', 'Beach', 21.4272, 91.9700, 4, 0, 'October-March', 4.7, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Laboni Beach', 'Main beach area with chair rentals, restaurants and water activities', 'Beach', 21.4250, 91.9650, 3, 0, 'October-March', 4.3, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Kolatoli Beach Quieter Area', 'Quieter beach area away from main crowds for peaceful swimming', 'Beach', 21.4300, 91.9800, 2, 0, 'October-March', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Sugandha Beach Point', 'Popular beach point with restaurants and sunset views', 'Beach', 21.4200, 91.9600, 2, 0, 'Year-round', 4.1, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Inani Beach Scenic', 'Scenic beach with rocks, shells and pristine coastal environment', 'Beach', 21.2897, 92.1700, 3, 0, 'October-March', 4.5, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Sunset Pavilion Point', 'Best viewpoint for sunset watching with scenic sea views', 'Viewpoint', 21.4400, 91.9800, 1, 50, 'Evening', 4.4, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Marine Drive Scenic Road', 'Coastal scenic road with stunning views and stops for photography', 'Scenic Drive', 21.4000, 91.9500, 2, 0, 'October-March', 4.5, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Dolphin Spotting Bay', 'Watch dolphins in Bay of Bengal during morning trips', 'Nature', 21.3800, 91.9300, 2, 200, 'Morning', 4.3, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Beach Spa Wellness', 'Beachside spa and massage centers for relaxation', 'Wellness', 21.4250, 91.9700, 1.5, 300, 'Year-round', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Fishermen''s Beach Morning Walk', 'Early morning walk to see fishing boats and local fishing activity', 'Experience', 21.4200, 91.9600, 1, 0, 'Morning', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- ADVENTURE (8)
(2, 'Himchari National Park', 'National park with waterfall, hiking trails and wildlife viewing', 'National Park', 21.3500, 92.0200, 3, 75, 'October-March', 4.4, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Himchari Waterfall Trek', 'Picturesque waterfall hike with natural pool for swimming', 'Waterfall', 21.3450, 92.0250, 1.5, 50, 'July-October', 4.5, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Bandarban Hill Tracts Adventure', 'Hill region with tribal villages and scenic mountain trails', 'Hiking', 21.9500, 92.2400, 6, 0, 'October-March', 4.6, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Maheshkhali Island Day Trip', 'Island with Buddhist temples, village culture and scenic routes', 'Island', 21.5500, 91.9500, 5, 200, 'October-March', 4.3, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Saint Martin Island Snorkeling', 'Coral island with clear water, snorkeling and marine life viewing', 'Island', 20.6200, 92.3200, 8, 800, 'November-February', 4.7, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Ramkot Hill Summit Hike', 'Hilltop viewpoint with panoramic views and hiking trail', 'Hiking', 21.3200, 92.0500, 2, 100, 'October-March', 4.2, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Cox''s Bazar Para Gliding Adventure', 'Adventure sports paragliding over Cox''s Bazar coastline', 'Adventure Sports', 21.4300, 91.9700, 1, 2000, 'November-March', 4.4, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Jungle Trek Bandarban', 'Jungle trekking through dense forests with local guides', 'Trekking', 21.9500, 92.2400, 4, 300, 'October-March', 4.3, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CULTURAL (5)
(2, 'Ramu Buddhist Temple', 'Ancient Buddhist temple and monastery with ornate decoration', 'Religious', 21.4000, 92.1000, 1.5, 0, 'Year-round', 4.2, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Buddhist Stupas Ramu', 'Historical Buddhist monuments and spiritual sites', 'Historical', 21.4100, 92.1100, 1, 0, 'Year-round', 4.0, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Rakhain Tribal Village Visit', 'Visit indigenous Rakhain community and their traditional lifestyle', 'Cultural', 21.4500, 92.0500, 2, 100, 'October-March', 4.3, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Fisherman''s Village Tour', 'Traditional fishing community with authentic local experience', 'Cultural', 21.4300, 91.9800, 2, 0, 'Morning', 3.9, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Local Fish Market Experience', 'Authentic market experience with fresh seafood and local traders', 'Market', 21.4200, 91.9600, 1.5, 0, 'Morning', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (3)
(2, 'Cox''s Bazar Aquarium', 'Aquarium with marine life and educational displays', 'Aquarium', 21.4250, 91.9650, 2, 150, 'Year-round', 4.0, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Beach Amusement Park', 'Beachfront amusement park with rides and games for families', 'Amusement Park', 21.4300, 91.9700, 3, 300, 'Year-round', 3.8, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Water Sports Center Family', 'Family-friendly water sports including jet skis and banana boats', 'Water Sports', 21.4200, 91.9600, 2, 400, 'Year-round', 4.1, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== SYLHET ATTRACTIONS (24 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- ADVENTURE (10)
(3, 'Jaflong Border Tea Gardens', 'Border area with tea gardens and scenic rolling hills for trekking', 'Nature', 25.2167, 91.9500, 3, 0, 'October-May', 4.4, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Ratargul Swamp Forest', 'Only freshwater swamp forest in Bangladesh with unique ecosystem and boating', 'Forest', 24.4667, 91.7833, 2.5, 150, 'July-October', 4.5, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Madhabkunda Waterfall Hike', 'Picturesque waterfall with hiking trail and natural pool for swimming', 'Waterfall', 24.6667, 91.7500, 1.5, 20, 'June-October', 4.6, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tamabil Limestone Caves Exploration', 'Ancient limestone caves with stalactites and underground streams', 'Caves', 25.1667, 91.8333, 2, 50, 'October-March', 4.2, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Pangthumai Waterfall Remote Trek', 'Remote waterfall in hills with challenging hiking and pristine nature', 'Waterfall', 25.0800, 91.8800, 2, 0, 'July-October', 4.3, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Bisnakandi Crystal Springs', 'Crystal clear natural springs with hiking through tea gardens', 'Nature', 25.1000, 91.9000, 2, 0, 'October-March', 4.4, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Lalakhal Crystal Lake', 'Crystal clear lake with boating and hillside views', 'Lake', 24.5000, 91.8000, 1.5, 0, 'October-March', 4.3, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Mount Lover''s Paradise Trek', 'Scenic hill trek with panoramic views and local villages', 'Hiking', 24.4900, 91.7900, 2.5, 100, 'October-March', 4.1, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Lovachara Tea Garden Exploration', 'Tea garden exploration with scenic views and local farmer interaction', 'Experience', 24.3500, 91.7000, 1.5, 0, 'October-March', 4.2, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Khasi Tribe Jungle Trek', 'Trek through jungle to meet indigenous Khasi community', 'Cultural Trek', 25.2000, 91.9400, 3, 150, 'October-March', 4.3, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- RELAXATION (8)
(3, 'Sreemangal Tea Gardens Walks', 'Green tea plantations for peaceful walks and tea farm tours', 'Tea Garden', 24.3000, 91.7200, 3, 0, 'October-March', 4.4, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Lawachara National Park Forest', 'Rainforest with biodiversity, birds and peaceful nature immersion', 'National Park', 24.3200, 91.7800, 2, 50, 'October-March', 4.3, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Baikka Beel Wetland Sanctuary', 'Wetland sanctuary perfect for bird watching and nature observation', 'Wetland', 24.2800, 91.7000, 2, 50, 'November-February', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Hum Hum Waterfall Retreat', 'Hidden waterfall in tea garden for peaceful relaxation', 'Waterfall', 24.3500, 91.7300, 1.5, 0, 'July-October', 4.1, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Hakaluki Haor Wetland', 'Largest wetland in Bangladesh with boat rides and bird watching', 'Wetland', 24.7000, 92.0000, 3, 0, 'November-February', 4.3, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tea Estate Spa Retreat', 'Spa and wellness center in tea garden setting', 'Wellness', 24.3100, 91.7200, 1.5, 350, 'Year-round', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet City Park', 'Urban park with gardens and peaceful atmosphere', 'Park', 24.8949, 91.8734, 1.5, 0, 'Year-round', 3.7, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet Riverbank Promenade', 'Scenic riverbank walk with local culture experience', 'Walk', 24.8900, 91.8700, 1, 0, 'Evening', 3.8, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CULTURAL (4)
(3, 'Shahjalal Shrine', 'Holy shrine of Sufi saint Shahjalal with spiritual significance', 'Religious', 24.8949, 91.8734, 1, 0, 'Year-round', 4.3, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet Museum Heritage', 'Museum showcasing local history, artifacts and cultural heritage', 'Museum', 24.9000, 91.8700, 1.5, 30, 'Year-round', 3.9, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet City Bazaar Tour', 'Historic traditional bazaars with local crafts and commerce', 'Market', 24.8800, 91.8800, 2, 0, 'Day Time', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Ancient Shrine Compound', 'Historic spiritual site with ancient architecture and cultural value', 'Religious', 24.8950, 91.8700, 1, 0, 'Year-round', 4.1, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (2)
(3, 'Sylhet Zoo Garden', 'Small zoo with animals and recreational garden space for families', 'Zoo', 24.9100, 91.8800, 2, 80, 'Year-round', 3.9, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tea Garden Children''s Park', 'Family-friendly park in tea garden area with recreational activities', 'Park', 24.3100, 91.7200, 2, 50, 'Year-round', 3.9, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== CHITTAGONG ATTRACTIONS (22 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- RELAXATION (6)
(4, 'Patenga Beach Chittagong', 'Urban beach in Chittagong city with beautiful sunset views', 'Beach', 22.1900, 91.9300, 2, 0, 'Year-round', 4.0, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Foy''s Lake Scenic', 'Scenic lake in Chittagong with boating and picnic opportunities', 'Nature', 22.3569, 91.8100, 2, 0, 'October-March', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Agrabad Commercial Area', 'Modern commercial and entertainment zone with restaurants', 'Commercial', 22.3500, 91.8200, 2, 0, 'Year-round', 3.8, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chittagong Port View', 'Scenic viewpoint of busy international port', 'Viewpoint', 22.2500, 91.8300, 1, 50, 'Day Time', 3.7, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Bayazid Bostami Lake', 'Historic lake with spiritual significance and peaceful walks', 'Lake', 22.3300, 91.8400, 1.5, 0, 'Year-round', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chittagong Spa Center', 'Spa and wellness facilities for relaxation', 'Wellness', 22.3600, 91.8100, 1.5, 350, 'Year-round', 4.1, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- ADVENTURE (6)
(4, 'Rangamati Lake Tour', 'Beautiful kaptai lake surrounded by hills and tribal settlements', 'Nature', 22.6500, 92.5000, 4, 0, 'Year-round', 4.4, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Kaptai National Park', 'National park with wildlife, forest and boating activities', 'National Park', 22.5000, 92.5500, 3, 75, 'October-March', 4.3, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Rangamati Tribal Tour', 'Visit tribal villages with traditional lifestyle and culture', 'Cultural Trek', 22.6500, 92.5000, 3, 150, 'Year-round', 4.2, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Hangi Lake Trek', 'Scenic lake trekking through forest and hills', 'Hiking', 22.5500, 92.4500, 2.5, 100, 'October-March', 4.1, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chittagong City Adventure Tour', 'Urban adventure with local markets and hidden spots', 'Adventure', 22.3569, 91.8100, 2, 200, 'Year-round', 3.9, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Elephant Sanctuary Visit', 'Visit animal sanctuary with elephant interaction and care', 'Experience', 22.7000, 92.5500, 3, 500, 'Year-round', 4.4, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CULTURAL (6)
(4, 'Naval Museum Chittagong', 'Maritime museum showcasing Bangladesh naval history and ships', 'Museum', 22.3500, 91.8300, 2, 40, 'Year-round', 3.9, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Ethnological Museum', 'Museum showcasing indigenous tribal cultures and artifacts', 'Museum', 22.3400, 91.8200, 1.5, 30, 'Year-round', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chittagong War Cemetery', 'Historic cemetery with WWII memorials and monuments', 'Historical', 22.3200, 91.8000, 1, 0, 'Year-round', 3.7, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Jama Mosque Chittagong', 'Historic mosque with Islamic architecture and cultural significance', 'Religious', 22.3400, 91.8100, 0.5, 0, 'Year-round', 4.0, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Local Bazaar Tour', 'Traditional shopping experience in local markets', 'Market', 22.3500, 91.8200, 2, 0, 'Day Time', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chittagong City Observatory', 'Space and astronomy education center', 'Educational', 22.3300, 91.8400, 2, 50, 'Year-round', 3.9, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (4)
(4, 'Chittagong Zoo', 'Zoo with diverse animal species and family entertainment', 'Zoo', 22.3700, 91.8000, 2.5, 100, 'Year-round', 3.9, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Dream Land Amusement', 'Amusement park with rides and games for families', 'Amusement Park', 22.3800, 91.7900, 3, 400, 'Year-round', 4.0, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Aqua Eden Water Park', 'Water park with swimming pools and water sports', 'Water Sports', 22.3600, 91.8300, 2.5, 250, 'Year-round', 4.1, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Children Learning Center', 'Educational play center for children', 'Educational', 22.3400, 91.8200, 2, 150, 'Year-round', 3.8, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== KHULNA ATTRACTIONS (18 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- ADVENTURE (8)
(5, 'Sundarbans National Park Safari', 'World''s largest mangrove forest and tiger reserve adventure', 'Adventure', 22.4833, 89.2833, 6, 500, 'November-February', 4.8, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sundarbans Boat Tour', 'Guided boat tour through mangrove ecosystems and wildlife zones', 'Experience', 22.4833, 89.2833, 4, 300, 'November-February', 4.6, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Tiger Spotting Expedition', 'Early morning tiger spotting expedition through Sundarbans', 'Wildlife', 22.4833, 89.2833, 5, 600, 'November-February', 4.7, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Bagerhat Mosque City', 'Historic mosque complex with architectural heritage', 'Historical', 22.6167, 89.7833, 2.5, 50, 'October-March', 4.2, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sundarbans Village Tour', 'Visit villages at gateway of Sundarbans', 'Cultural Trek', 22.5000, 89.4000, 2, 150, 'Year-round', 4.1, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Crocodile Breeding Center', 'Visit breeding center with saltwater crocodiles', 'Wildlife', 22.4800, 89.2900, 1.5, 100, 'Year-round', 4.0, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Bird Watching Point', 'Early morning bird watching in Sundarbans', 'Nature', 22.4850, 89.2900, 3, 250, 'November-February', 4.4, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna Coastal Trek', 'Trek along coastal areas of Khulna', 'Hiking', 22.8000, 89.5000, 2.5, 100, 'October-March', 4.0, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- RELAXATION (5)
(5, 'Shela Riverbank Walk', 'Scenic riverbank walk with local life experiences and culture', 'Experience', 22.8500, 89.5500, 1.5, 0, 'Year-round', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna City Park', 'Urban park with gardens and recreational facilities', 'Park', 22.8456, 89.5644, 1.5, 0, 'Year-round', 3.7, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Ninety Dome Mosque Area', 'Historic mosque with scenic garden', 'Religious', 22.7000, 89.4500, 1.5, 0, 'Year-round', 3.8, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna Port Development Authority Park', 'Waterfront park with scenic views', 'Park', 22.8300, 89.5700, 1, 0, 'Year-round', 3.6, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna Spa Wellness Center', 'Spa and wellness facilities', 'Wellness', 22.8500, 89.5600, 1.5, 300, 'Year-round', 4.0, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CULTURAL (3)
(5, 'Khulna City Tour', 'Port city with historical mosques and colonial monuments', 'Cultural', 22.8456, 89.5644, 2, 0, 'October-March', 3.7, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Local Museum Heritage', 'Museum showcasing Khulna local history and culture', 'Museum', 22.8400, 89.5600, 1.5, 25, 'Year-round', 3.6, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Traditional Craft Market', 'Market with traditional crafts and local products', 'Market', 22.8450, 89.5650, 2, 0, 'Day Time', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (2)
(5, 'Khulna Children''s Park', 'Family-friendly park with playground', 'Park', 22.8500, 89.5700, 1.5, 40, 'Year-round', 3.7, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna Recreation Center', 'Recreation facilities for family activities', 'Recreation', 22.8400, 89.5600, 2, 150, 'Year-round', 3.8, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ========== BANDARBAN ATTRACTIONS (20 total) ==========
INSERT INTO attractions (destination_id, name, description, category, latitude, longitude, estimated_duration_hours, entry_fee_bdt, best_time_to_visit, rating, travel_style, difficulty_level, created_at, updated_at) VALUES
-- ADVENTURE (10)
(6, 'Bandarban Hill Tracts Trek', 'Challenging hill region with tribal villages and scenic mountain trails', 'Hiking', 21.9500, 92.2400, 6, 0, 'October-March', 4.6, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Chimbuk Peak Trek', 'High altitude peak trekking with panoramic mountain views', 'Hiking', 21.8800, 92.2100, 5, 150, 'October-March', 4.5, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Nila Achal Peak', 'Third highest peak in Bangladesh with scenic views', 'Hiking', 21.9200, 92.2000, 4.5, 100, 'October-April', 4.4, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Keokradang Trek', 'Seven-day challenging trek with pristine nature', 'Trekking', 21.9000, 92.1800, 7, 200, 'October-March', 4.7, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Tribal Village Homestay Trek', 'Trek with overnight stay in tribal village', 'Experience', 21.9500, 92.2400, 3, 300, 'Year-round', 4.4, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Sangu River Rafting', 'White water rafting through scenic river gorges', 'Water Sports', 21.9000, 92.1500, 2.5, 400, 'June-October', 4.3, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Waterfall Adventure', 'Waterfall spotting with hiking and swimming', 'Waterfall', 21.9300, 92.2200, 2, 50, 'June-October', 4.2, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Market Adventure Tour', 'Explore local markets and commerce of hill tracts', 'Adventure', 21.9500, 92.2400, 2, 0, 'Year-round', 3.9, 'adventure', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Jungle Survival Tour', 'Guided jungle expedition with survival skills training', 'Adventure', 21.9000, 92.2000, 3, 500, 'October-March', 4.3, 'adventure', 'hard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Trail Running', 'Scenic trail running through hill tracts', 'Adventure', 21.9500, 92.2400, 2.5, 200, 'October-March', 4.1, 'adventure', 'moderate', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- RELAXATION (5)
(6, 'Hill Tracts Tea Garden Walks', 'Peaceful walks through tea plantations with scenic views', 'Tea Garden', 21.9400, 92.2300, 2, 0, 'October-March', 4.2, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban City Park', 'Small urban park with peaceful atmosphere', 'Park', 21.9500, 92.2400, 1.5, 0, 'Year-round', 3.6, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Wellness Retreat', 'Spa and wellness center in hill setting', 'Wellness', 21.9500, 92.2400, 1.5, 350, 'Year-round', 4.1, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Hill Station Rest Points', 'Scenic rest points with comfortable mountain views', 'Viewpoint', 21.9300, 92.2200, 1, 50, 'Year-round', 3.9, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Nature Meditation Spot', 'Peaceful spot for meditation with natural surroundings', 'Nature', 21.9200, 92.2100, 2, 0, 'Year-round', 4.0, 'relaxation', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CULTURAL (3)
(6, 'Tribal Culture Museum', 'Museum showcasing tribal culture, crafts and history', 'Museum', 21.9500, 92.2400, 1.5, 40, 'Year-round', 4.0, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Indigenous Craft Market', 'Market with traditional tribal crafts and products', 'Market', 21.9500, 92.2400, 1.5, 0, 'Day Time', 3.9, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Local Temple', 'Buddhist temple with cultural significance', 'Religious', 21.9500, 92.2400, 0.5, 0, 'Year-round', 3.8, 'cultural', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- FAMILY (2)
(6, 'Bandarban Children''s Adventure Park', 'Family-friendly park with play areas', 'Park', 21.9500, 92.2400, 2, 100, 'Year-round', 3.8, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Bandarban Family Recreation Center', 'Recreation facilities for family entertainment', 'Recreation', 21.9500, 92.2400, 2, 200, 'Year-round', 3.9, 'family', 'easy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- 3. HOTELS (14 hotels with all price tiers)
-- ==========================================

INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, website, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count, created_at, updated_at) VALUES
-- DHAKA
(1, 'Radisson Blu Dhaka', 'Luxury 5-star resort with pool, spa and multiple restaurants', 'Banani, Dhaka', 23.8300, 90.4200, 5, '+88-02-55668200', 'contact@radisson.com', 'www.radisson.com', 10000, 15000, 22000, 'Pool, Gym, Spa, WiFi, 24hr Restaurant, Room Service', 4.7, 287, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Hotel Grand Dhaka', 'Modern 5-star hotel in prime Gulshan location', 'Gulshan, Dhaka', 23.8103, 90.4125, 5, '+88-02-98812001', 'info@granddhaka.com', 'www.granddhaka.com', 9000, 13000, 20000, 'WiFi, Gym, Pool, Restaurant, Bar, 24hr Service', 4.6, 245, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Dhaka Palace Hotel', 'Boutique 4-star hotel in Dhanmondi with charm', 'Dhanmondi, Dhaka', 23.7645, 90.3636, 4, '+88-02-41414141', 'info@palace.com', 'www.dhakapalace.com', 5000, 8000, 12000, 'WiFi, AC, Restaurant, Bar, Comfortable Rooms', 4.3, 198, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Pan Pacific Dhaka', 'Premium 5-star business hotel', 'Gulshan, Dhaka', 23.8200, 90.4150, 5, '+88-02-55668600', 'info@panpacific.com', 'www.panpacific.com', 12000, 17000, 25000, 'Pool, Gym, Business Center, Multiple Dining, Spa', 4.8, 312, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- COX'S BAZAR
(2, 'Cox''s Beach Resort Paradise', 'Beachfront 4-star resort with stunning sea views', 'Cox''s Bazar Beach', 21.4272, 91.9700, 4, '+88-0341-513000', 'info@paradiseresort.com', 'www.paradiseresort.com', 5000, 8500, 13000, 'Beach Access, Pool, Restaurant, Sunset Terrace', 4.5, 256, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Seagull Hotel Cox''s Bazar', 'Family-friendly 3-star beach hotel', 'Cox''s Bazar', 21.4300, 91.9720, 3, '+88-0341-514000', 'info@seagull.com', 'www.seagullhotel.com', 3500, 5500, 9000, 'Beach Adjacent, Pool, AC Rooms, Restaurant', 4.2, 189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Hillside Bandarban Resort', 'Mountain 3-star resort near Bandarban', 'Bandarban', 21.9500, 92.2400, 3, '+88-0361-60600', 'info@hillside.com', 'www.hilltopresort.com', 4000, 6000, 10000, 'Scenic Views, Restaurant, Hiking Trails, AC Rooms', 4.4, 212, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- SYLHET
(3, 'Green Valley Tea Resort', '4-star resort in tea gardens with nature views', 'Sreemangal, Sylhet', 24.3000, 91.7200, 4, '+88-08612-51515', 'info@greenvalley.com', 'www.greenvalley.com', 4500, 7000, 11000, 'Tea Garden Views, Restaurant, Nature Tours, WiFi', 4.3, 174, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet City Hotel', 'Central 3-star city hotel with connectivity', 'Sylhet City', 24.8949, 91.8734, 3, '+88-0821-713713', 'info@sylhetcity.com', 'www.sylhetcity.com', 2500, 4500, 7500, 'WiFi, AC, Restaurant, Business Center, Central Location', 3.9, 156, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CHITTAGONG
(4, 'Chittagong Bay Hotel', '4-star hotel with bay views and modern amenities', 'Chittagong', 22.3569, 91.8100, 4, '+88-031-2400400', 'info@bayhotel.com', 'www.chittagongbay.com', 4500, 7000, 11000, 'Bay View, Pool, WiFi, Restaurant, AC Rooms', 4.2, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Eastern Hotels Chittagong', '3-star business hotel in city center', 'Chittagong Downtown', 22.3500, 91.8200, 3, '+88-031-2302302', 'info@eastern.com', 'www.easternhotel.com', 2800, 4800, 8000, 'AC, WiFi, Restaurant, Business Services', 3.8, 145, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- KHULNA
(5, 'Sundarbans Eco-Lodge', '4-star eco-lodge gateway to Sundarbans', 'Khulna', 22.8456, 89.5644, 4, '+88-041-700700', 'safari@sundarbans.com', 'www.sundarbans-lodge.com', 4000, 6500, 10000, 'Nature Oriented, Safari Packages, Restaurant, WiFi', 4.4, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- BANDARBAN
(6, 'Hill Tribal Resort Bandarban', '3-star resort with tribal cultural experience', 'Bandarban City', 21.9500, 92.2400, 3, '+88-0361-61111', 'info@tribalresort.com', 'www.tribalresort.com', 3500, 5500, 9000, 'Cultural Programs, Restaurant, WiFi, Valley Views', 4.2, 167, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- ADD MISSING BUDGET HOTELS (economy_price_bdt <= 3000)
-- ==========================================

-- DHAKA (need 2)
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, website, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count, created_at, updated_at) VALUES
(1, 'Budget Inn Dhaka', 'Affordable budget hotel near airport with clean rooms', 'Motijheel, Dhaka', 23.7700, 90.4000, 3, '+88-02-48888888', 'info@budgetinn.com', 'www.budgetinn.com', 2800, 4500, 7000, 'WiFi, AC, Basic Restaurant', 3.8, 234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'City Comfort Hotel', 'Economy hotel in city center with basic amenities', 'Kawran Bazar, Dhaka', 23.7600, 90.4000, 3, '+88-02-77778888', 'info@citycomfort.com', 'www.citycomfort.com', 2500, 4000, 6500, 'WiFi, AC, Restaurant, 24hr Service', 4.0, 189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- COX'S BAZAR (need 2)
(2, 'Beach Budget Hotel', 'Simple beachfront budget hotel with sea breeze', 'Cox''s Bazar Beach', 21.4250, 91.9700, 3, '+88-0341-519000', 'info@beachbudget.com', 'www.beachbudget.com', 2700, 4200, 6800, 'Beach Access, WiFi, Basic Restaurant', 3.9, 156, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Traveler''s Lodge Cox''s', 'Budget traveler hostel and rooms near beach', 'Cox''s Bazar', 21.4300, 91.9700, 2, '+88-0341-520000', 'info@travelerlodge.com', 'www.travelerlodge.com', 1800, 3000, 5000, 'WiFi, AC, Shared Facilities', 3.7, 198, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- SYLHET (need 1)
(3, 'Sreemangal Budget Inn', 'Budget accommodation in tea garden area', 'Sreemangal, Sylhet', 24.3000, 91.7200, 3, '+88-08612-52000', 'info@sreemangal-budget.com', 'www.sreemangal-budget.com', 2200, 3500, 5500, 'WiFi, AC, Restaurant, Garden View', 3.9, 145, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- CHITTAGONG (need 1)
(4, 'City Economy Hotel', 'Budget hotel in Chittagong downtown', 'Agrabad, Chittagong', 22.3500, 91.8200, 3, '+88-031-2311111', 'info@cityeconomy.com', 'www.cityeconomy.com', 2600, 4000, 6200, 'WiFi, AC, Restaurant, 24hr Service', 3.8, 167, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- KHULNA (need 2)
(5, 'Khulna Budget House', 'Simple budget hotel near city center', 'Khulna', 22.8456, 89.5644, 3, '+88-041-705000', 'info@khulnabudget.com', 'www.khulnabudget.com', 2400, 3800, 6000, 'WiFi, AC, Restaurant', 3.7, 134, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Gateway Budget Inn', 'Affordable inn near Sundarbans gateway', 'Khulna', 22.8400, 89.5600, 3, '+88-041-706000', 'info@gatewaybudget.com', 'www.gatewaybudget.com', 2300, 3600, 5800, 'WiFi, AC, Restaurant, Nature Tours', 3.8, 121, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- BANDARBAN (need 2)
(6, 'Hill Budget Resort', 'Economy resort in mountain setting', 'Bandarban', 21.9500, 92.2400, 3, '+88-0361-63000', 'info@hillbudget.com', 'www.hillbudget.com', 2200, 3500, 5500, 'WiFi, AC, Restaurant, Mountain View', 3.8, 145, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Tribal Budget Homestay', 'Budget homestay with tribal experience', 'Bandarban', 21.9400, 92.2300, 2, '+88-0361-64000', 'info@tribalhomestay.com', 'www.tribalhomestay.com', 1500, 2800, 4500, 'WiFi, AC, Tribal Meals, Cultural Activities', 4.0, 156, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- ADD MISSING 5-STAR LUXURY HOTELS (star_rating = 5)
-- ==========================================

-- COX'S BAZAR (need 2)
INSERT INTO hotels (destination_id, name, description, address, latitude, longitude, star_rating, phone, email, website, economy_price_bdt, midrange_price_bdt, luxury_price_bdt, amenities, average_rating, review_count, created_at, updated_at) VALUES
(2, 'Cox''s Bazar Palace Resort', 'Ultimate 5-star luxury beachfront resort with world-class facilities', 'Cox''s Bazar Beach', 21.4272, 91.9700, 5, '+88-0341-521000', 'luxury@coxpalace.com', 'www.coxpalace.com', 20000, 28000, 40000, 'Private Beach, Pool, Spa, Multiple Restaurants, Water Sports', 4.9, 456, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Dolphin Bay Luxury Resort', '5-star eco-luxury resort with dolphin viewing and marine activities', 'Cox''s Bazar', 21.4300, 91.9750, 5, '+88-0341-522000', 'info@dolphinbay.com', 'www.dolphinbay.com', 18000, 25000, 38000, 'Eco-Friendly, Pool, Spa, Restaurant, Dolphin Tours', 4.8, 378, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- SYLHET (need 2)
(3, 'Tea Garden Palace Sylhet', '5-star luxury resort nestled in tea plantations', 'Sreemangal, Sylhet', 24.3000, 91.7200, 5, '+88-08612-53000', 'luxury@teapalace.com', 'www.teapalace.com', 15000, 22000, 35000, 'Tea Tour, Pool, Spa, Fine Dining, Nature Activities', 4.8, 345, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet Grand Luxury Hotel', '5-star hotel in Sylhet city with premium amenities', 'Sylhet Downtown', 24.8949, 91.8734, 5, '+88-0821-720000', 'info@sylhetgrand.com', 'www.sylhetgrand.com', 14000, 20000, 32000, 'Pool, Spa, Multiple Restaurants, Business Center, Conference Halls', 4.7, 298, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- CHITTAGONG (need 2)
(4, 'Chittagong Waterfront Palace', '5-star luxury hotel overlooking Bay of Bengal', 'Chittagong Waterfront', 22.3569, 91.8100, 5, '+88-031-2320000', 'luxury@waterfront.com', 'www.chittagongpalace.com', 16000, 23000, 36000, 'Marina View, Pool, Spa, Fine Dining, Yacht Facilities', 4.8, 367, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Rangamati Hill Luxury Resort', '5-star resort with hill and lake views near Chittagong', 'Rangamati, Chittagong', 22.6500, 92.5000, 5, '+88-031-2321000', 'info@rangamatiresort.com', 'www.rangamatiresort.com', 13000, 19000, 30000, 'Lake View, Pool, Spa, Adventure Activities, Restaurant', 4.7, 267, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- KHULNA (need 2)
(5, 'Sundarbans Royal Palace', '5-star luxury lodge as gateway to Sundarbans', 'Khulna', 22.8456, 89.5644, 5, '+88-041-707000', 'luxury@sundarbanpalace.com', 'www.sundarbanpalace.com', 17000, 25000, 38000, 'Safari Packages, Pool, Spa, Premium Restaurant, Wildlife Tours', 4.9, 412, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Khulna Grand Resort', '5-star resort in Khulna with nature-oriented luxury', 'Khulna', 22.8500, 89.5700, 5, '+88-041-708000', 'info@khulnagrand.com', 'www.khulnagrand.com', 14000, 20000, 33000, 'Nature Tours, Pool, Spa, Restaurant, Conference Facilities', 4.7, 289, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- BANDARBAN (need 2)
(6, 'Bandarban Mountain Palace', '5-star luxury resort in scenic hill tracts', 'Bandarban', 21.9500, 92.2400, 5, '+88-0361-65000', 'luxury@mountainpalace.com', 'www.mountainpalace.com', 15000, 22000, 34000, 'Mountain View, Pool, Spa, Tribal Tours, Adventure Activities', 4.8, 334, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Hill Tracts Royal Resort', '5-star eco-luxury resort with tribal cultural programs', 'Bandarban', 21.9400, 92.2300, 5, '+88-0361-66000', 'info@hilltracts-royal.com', 'www.hilltracts-royal.com', 14000, 20000, 32000, 'Cultural Programs, Pool, Spa, Jungle Treks, Restaurant', 4.8, 312, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- 4. RESTAURANTS (32 restaurants, all price ranges)
-- ==========================================

INSERT INTO restaurants (destination_id, name, description, cuisine_type, address, latitude, longitude, price_range, average_meal_cost_bdt, operating_hours, phone, average_rating, review_count, specialties, vegetarian_options, created_at, updated_at) VALUES
-- DHAKA (8)
(1, 'Dhaka Biryani House', 'Traditional Dhaka biryani with authentic meat dishes', 'Bengali', 'Gulshan, Dhaka', 23.8103, 90.4125, 'budget', 400, '11:00-23:00', '+88-02-58814020', 4.5, 534, 'Biryani, Kebab, Meat Curry, Naan', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Naan & Curry', 'North Indian cuisine with authentic flavors', 'Indian', 'Dhanmondi, Dhaka', 23.7645, 90.3636, 'budget', 500, '10:00-22:00', '+88-02-41466468', 4.2, 456, 'Naan, Curry, Tandoori, Bread', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Sea Salt Seafood Restaurant', 'Fresh seafood dishes and coastal cuisine', 'Seafood', 'Gulshan, Dhaka', 23.8300, 90.4200, 'midrange', 800, '12:00-23:00', '+88-02-55668800', 4.4, 667, 'Fish, Shrimp, Crab, Grilled Items', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Fusion Fine Dining', 'International fusion cuisine with modern presentation', 'International', 'Banani, Dhaka', 23.8050, 90.3563, 'luxury', 1500, '18:00-23:00', '+88-02-88885555', 4.7, 789, 'Fusion Dishes, Wine Selection, Steaks, Seafood', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Vegetarian Paradise', 'Pure vegetarian restaurant with healthy options', 'Vegetarian', 'Dhanmondi, Dhaka', 23.7900, 90.3900, 'budget', 350, '10:00-21:00', '+88-02-41111111', 4.3, 445, 'Vegetables, Lentils, Salads, Soups, Breads', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Thai Street Kitchen', 'Authentic Thai cuisine with street food flavors', 'Thai', 'Gulshan, Dhaka', 23.8100, 90.4100, 'midrange', 600, '11:00-22:00', '+88-02-55558888', 4.3, 523, 'Pad Thai, Thai Curry, Tom Yum, Spring Rolls', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Sunrise Cafe Breakfast', 'Cafe with excellent breakfast and morning coffee', 'Cafe', 'Banani, Dhaka', 23.8250, 90.4250, 'budget', 300, '07:00-12:00', '+88-02-88884444', 4.4, 612, 'Coffee, Breakfast, Pastries, Smoothies', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Dinner at Lalbagh', 'Traditional Bengali dinner with authentic recipes', 'Bengali', 'Lalbagh, Dhaka', 23.7832, 90.2506, 'budget', 420, '17:00-23:00', '+88-02-77776666', 4.2, 388, 'Bengali Curry, Rice, Fish, Meat Dishes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- COX'S BAZAR (4)
(2, 'Cox''s Beach Shack', 'Casual seafood restaurant near the beach', 'Seafood', 'Cox''s Bazar Beach', 21.4272, 91.9700, 'budget', 500, '11:00-22:00', '+88-0341-513111', 4.3, 578, 'Grilled Fish, Prawns, Beach Food, Crab', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Family Restaurant', 'Multi-cuisine family restaurant with local items', 'Multi-Cuisine', 'Cox''s Bazar', 21.4300, 91.9720, 'budget', 450, '09:00-23:00', '+88-0341-514222', 3.9, 498, 'Bengali, Indian, Chinese, Seafood', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Hillside Thai', 'Thai cuisine with authentic flavors', 'Thai', 'Bandarban', 21.9500, 92.2400, 'midrange', 700, '10:00-21:00', '+88-0361-60500', 4.2, 432, 'Pad Thai, Thai Curry, Tom Yum, Spring Rolls', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Inani Beach Cafe', 'Beachside cafe with relaxing ambiance', 'Cafe', 'Inani Beach', 21.2897, 92.1700, 'budget', 350, '08:00-20:00', '+88-0341-515000', 4.1, 312, 'Coffee, Tea, Snacks, Bengali Food, Breakfast', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- SYLHET (3)
(3, 'Tea Garden Restaurant', 'Traditional Sylhet cuisine in tea garden setting', 'Bengali', 'Sreemangal, Sylhet', 24.3000, 91.7200, 'budget', 400, '11:00-21:00', '+88-08612-50500', 4.4, 445, 'Sylheti Food, Tea, Local Cuisine, Organic Items', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sreemangal Heritage Meals', 'Traditional teagarden meals with local flavors', 'Bengali', 'Sreemangal, Sylhet', 24.3000, 91.7200, 'budget', 350, '10:00-20:00', '+88-08612-51500', 4.2, 397, 'Local Cuisine, Organic Food, Bengali Dishes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Sylhet Family Dining', 'Family restaurant with diverse menu', 'Multi-Cuisine', 'Sylhet City', 24.8949, 91.8734, 'midrange', 550, '12:00-22:00', '+88-0821-700700', 4.0, 334, 'Bengali, Chinese, Indian, Seafood', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- CHITTAGONG (4)
(4, 'Chittagong Seafood Grill', 'Fresh seafood specialties from local catch', 'Seafood', 'Chittagong', 22.3569, 91.8100, 'midrange', 750, '12:00-23:00', '+88-031-2300200', 4.3, 487, 'Grilled Fish, Shrimp, Crab, Local Specialties', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Rangs Restaurant', 'Multi-cuisine restaurant with local favorite', 'Multi-Cuisine', 'Chittagong Downtown', 22.3500, 91.8200, 'budget', 400, '11:00-22:00', '+88-031-2301001', 4.0, 412, 'Bengali, Indian, Chinese, Tandoori', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Chai Depot Cafe', 'Traditional tea cafe with snacks', 'Cafe', 'Chittagong', 22.3400, 91.8150, 'budget', 200, '06:00-18:00', '+88-031-2305000', 3.9, 298, 'Tea, Coffee, Breakfast, Snacks', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Port City Fine Dining', 'Upscale international cuisine', 'International', 'Chittagong Waterfront', 22.3300, 91.8000, 'luxury', 1200, '18:00-23:00', '+88-031-2310100', 4.6, 345, 'International, Steaks, Seafood, Wine', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- KHULNA (4)
(5, 'Sundarbans Gateway', 'Local Khulna cuisine near Sundarbans', 'Bengali', 'Khulna', 22.8456, 89.5644, 'budget', 380, '10:00-21:00', '+88-041-701000', 4.1, 289, 'Bengali Curry, Fish Curry, Traditional Food', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Royal Restaurant Khulna', 'Multi-cuisine restaurant in downtown', 'Multi-Cuisine', 'Khulna Downtown', 22.8456, 89.5644, 'midrange', 500, '11:00-22:00', '+88-041-702000', 3.8, 276, 'Bengali, Indian, Chinese, Seafood', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Morning Dew Cafe', 'Breakfast and cafe specialties', 'Cafe', 'Khulna', 22.8400, 89.5600, 'budget', 250, '07:00-13:00', '+88-041-703000', 3.9, 234, 'Coffee, Tea, Breakfast, Pastries', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sundarbans Fine Dining', 'Premium dining experience near Sundarbans', 'International', 'Khulna', 22.8500, 89.5700, 'luxury', 1100, '18:00-23:00', '+88-041-704000', 4.5, 287, 'International, Steaks, Local Delicacies', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- BANDARBAN (5)
(6, 'Hill Tribal Kitchen', 'Traditional tribal cuisine and local dishes', 'Tribal', 'Bandarban', 21.9500, 92.2400, 'budget', 350, '10:00-21:00', '+88-0361-62000', 4.2, 298, 'Tribal Food, Local Curry, Bamboo Shoots', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Mountain View Restaurant', 'Multi-cuisine with scenic views', 'Multi-Cuisine', 'Bandarban', 21.9500, 92.2400, 'midrange', 600, '11:00-22:00', '+88-0361-62001', 4.1, 267, 'Bengali, Chinese, Indian, Local Specialties', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Tribal Coffee House', 'Cafe with tribal ambiance', 'Cafe', 'Bandarban', 21.9500, 92.2400, 'budget', 200, '07:00-19:00', '+88-0361-62002', 4.0, 245, 'Coffee, Tea, Breakfast, Local Snacks', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Hill Station Premium Dining', 'Premium dining with mountain views', 'International', 'Bandarban', 21.9500, 92.2400, 'luxury', 1300, '18:00-23:00', '+88-0361-62003', 4.4, 312, 'International, Local Fusion, Wine Selection', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Natural Elements Organic Cafe', 'Organic cafe with healthy options', 'Vegetarian', 'Bandarban', 21.9400, 92.2300, 'midrange', 450, '09:00-20:00', '+88-0361-62004', 4.3, 287, 'Organic Vegetables, Salads, Healthy Juices', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- 5. TRANSPORT ROUTES (24 routes connecting all cities)
-- ==========================================

INSERT INTO transport_routes (source_destination_id, target_destination_id, transport_type, distance_km, travel_time_hours, cost_economy_bdt, cost_midrange_bdt, cost_luxury_bdt, notes, created_at, updated_at) VALUES
(1, 2, 'bus', 253, 6.0, 600, 900, 1500, 'Regular buses throughout the day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, 'train', 253, 8.0, 400, 700, 1200, 'Daily morning and evening trains', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 2, 'flight', 253, 1.0, 3000, 4500, 6000, 'Multiple daily flights available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 'bus', 240, 5.5, 550, 850, 1400, 'All-day bus service available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 'flight', 240, 1.0, 2500, 4000, 5500, 'Daily morning and evening flights', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 4, 'bus', 200, 4.5, 400, 650, 1050, 'Direct Dhaka-Chittagong route hourly', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 4, 'flight', 200, 0.75, 2500, 4000, 5500, 'Several daily flights available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, 'bus', 350, 8.0, 700, 1100, 1800, 'Long distance overnight buses available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, 'train', 350, 10.0, 500, 850, 1500, 'Train service 3 times per week', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 6, 'bus', 380, 7.5, 750, 1200, 1900, 'Regular bus service to Bandarban', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 'bus', 500, 12.0, 900, 1400, 2200, 'Long distance route, overnight buses', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 4, 'bus', 180, 4.0, 350, 550, 900, 'Frequent buses throughout the day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, 'bus', 480, 11.0, 850, 1300, 2000, 'Long distance overnight service available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 6, 'bus', 280, 6.0, 600, 950, 1500, 'Bandarban connection from Cox''s Bazar', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 4, 'bus', 350, 8.0, 650, 1000, 1600, 'Regular buses available throughout day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 5, 'bus', 700, 16.0, 1200, 1800, 2800, 'Very long route, overnight buses available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 6, 'bus', 300, 7.0, 700, 1100, 1700, 'Sylhet to Bandarban route available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 5, 'bus', 280, 6.5, 600, 950, 1500, 'Local connection route frequent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 6, 'bus', 150, 3.0, 250, 400, 650, 'Short distance frequent service', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 6, 'bus', 350, 8.0, 700, 1100, 1800, 'Regular bus service between cities', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 'train', 240, 7.0, 350, 650, 1100, 'Train service available', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 4, 'train', 180, 5.0, 300, 550, 900, 'Train service available on route', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, 'flight', 350, 1.5, 4000, 5500, 7500, 'Limited flight service to Khulna', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 6, 'flight', 380, 1.5, 3500, 5000, 6500, 'Limited flight service to Bandarban area', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==========================================
-- DATA QUALITY VERIFICATION REPORT
-- ==========================================

-- Verify no duplicates
SELECT 'DUPLICATE CHECK' as `Section`, 'Attractions with duplicates' as `Check`, COUNT(*) as `Count`
FROM (
  SELECT name, destination_id FROM attractions GROUP BY name, destination_id HAVING COUNT(*) > 1
) as t
UNION ALL
SELECT 'DUPLICATE CHECK', 'Hotels with duplicates', COUNT(*)
FROM (
  SELECT name, destination_id FROM hotels GROUP BY name, destination_id HAVING COUNT(*) > 1
) as t2
UNION ALL
SELECT 'DUPLICATE CHECK', 'Restaurants with duplicates', COUNT(*)
FROM (
  SELECT name, destination_id FROM restaurants GROUP BY name, destination_id HAVING COUNT(*) > 1
) as t3;

-- Summary of data inserted
SELECT 'SUMMARY' as `Category`, 'Destinations' as `Item`, COUNT(*) as `Total` FROM destination_info
UNION ALL
SELECT 'SUMMARY', 'Attractions', COUNT(*) FROM attractions
UNION ALL
SELECT 'SUMMARY', 'Hotels', COUNT(*) FROM hotels
UNION ALL
SELECT 'SUMMARY', 'Restaurants', COUNT(*) FROM restaurants
UNION ALL
SELECT 'SUMMARY', 'Transport Routes', COUNT(*) FROM transport_routes;

-- Attractions per destination
SELECT 'DESTINATION COVERAGE' as `Section`, d.city_name, COUNT(a.id) as `Attractions`, 
       SUM(CASE WHEN a.travel_style = 'cultural' THEN 1 ELSE 0 END) as `Cultural`,
       SUM(CASE WHEN a.travel_style = 'adventure' THEN 1 ELSE 0 END) as `Adventure`,
       SUM(CASE WHEN a.travel_style = 'relaxation' THEN 1 ELSE 0 END) as `Relaxation`,
       SUM(CASE WHEN a.travel_style = 'family' THEN 1 ELSE 0 END) as `Family`,
       SUM(CASE WHEN a.travel_style = 'solo' THEN 1 ELSE 0 END) as `Solo`
FROM destination_info d
LEFT JOIN attractions a ON d.destination_id = a.destination_id
GROUP BY d.destination_id
ORDER BY `Attractions` DESC;

-- Hotels price tier coverage
SELECT 'HOTEL COVERAGE' as `Section`, d.city_name,
       SUM(CASE WHEN h.economy_price_bdt IS NOT NULL THEN 1 ELSE 0 END) as `Has Economy`,
       SUM(CASE WHEN h.midrange_price_bdt IS NOT NULL THEN 1 ELSE 0 END) as `Has Midrange`,
       SUM(CASE WHEN h.luxury_price_bdt IS NOT NULL THEN 1 ELSE 0 END) as `Has Luxury`
FROM destination_info d
LEFT JOIN hotels h ON d.destination_id = h.destination_id
GROUP BY d.destination_id;

-- Restaurants price range distribution
SELECT 'RESTAURANT COVERAGE' as `Section`, d.city_name, COUNT(r.id) as `Total`,
       SUM(CASE WHEN r.price_range = 'budget' THEN 1 ELSE 0 END) as `Budget`,
       SUM(CASE WHEN r.price_range = 'midrange' THEN 1 ELSE 0 END) as `Midrange`,
       SUM(CASE WHEN r.price_range = 'luxury' THEN 1 ELSE 0 END) as `Luxury`
FROM destination_info d
LEFT JOIN restaurants r ON d.destination_id = r.destination_id
GROUP BY d.destination_id
ORDER BY `Total` DESC;

-- ==========================================
-- Migration completed successfully
-- ==========================================
COMMIT;
