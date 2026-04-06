-- ========================================
-- PHASE 1: Smart Trip Planner
-- Database Migration - V1
-- ========================================

-- Table: destination_info
-- Purpose: Store information about travel destinations
CREATE TABLE IF NOT EXISTS destination_info (
    destination_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_name VARCHAR(200) NOT NULL UNIQUE,
    country VARCHAR(100),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    best_month_from INT,
    best_month_to INT,
    suggested_duration_days INT DEFAULT 3,
    safety_rating DECIMAL(3,2),
    language VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city_name (city_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: travel_plans
-- Purpose: Store user's smart travel plans
CREATE TABLE IF NOT EXISTS travel_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    destination VARCHAR(200) NOT NULL,
    duration_days INT NOT NULL,
    budget_tier VARCHAR(20) NOT NULL,
    travel_style VARCHAR(30),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'draft',
    total_budget_estimate DECIMAL(12,2),
    plan_data LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_destination (destination)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: daily_itineraries
-- Purpose: Store daily breakdown of a travel plan
CREATE TABLE IF NOT EXISTS daily_itineraries (
    itinerary_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    day_number INT NOT NULL,
    date DATE,
    theme VARCHAR(100),
    weather_condition VARCHAR(50),
    estimated_cost DECIMAL(10,2) DEFAULT 0,
    estimated_steps INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_plan_day (plan_id, day_number),
    INDEX idx_plan_id (plan_id),
    INDEX idx_date (date),
    FOREIGN KEY (plan_id) REFERENCES travel_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: itinerary_activities
-- Purpose: Store individual activities within daily itineraries
CREATE TABLE IF NOT EXISTS itinerary_activities (
    activity_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    itinerary_id BIGINT NOT NULL,
    activity_type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time TIME,
    end_time TIME,
    duration_minutes INT,
    location_lat DECIMAL(10,8),
    location_lng DECIMAL(11,8),
    address VARCHAR(300),
    estimated_cost DECIMAL(10,2) DEFAULT 0,
    booking_required BOOLEAN DEFAULT FALSE,
    order_index INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_itinerary_id (itinerary_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_order (order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: travel_preferences
-- Purpose: Store user preferences for ML-based recommendations
CREATE TABLE IF NOT EXISTS travel_preferences (
    preference_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL UNIQUE,
    preferred_cuisine VARCHAR(50),
    activity_pace VARCHAR(20),
    morning_person BOOLEAN DEFAULT TRUE,
    nightlife_interest BOOLEAN DEFAULT FALSE,
    nature_interest BOOLEAN DEFAULT TRUE,
    history_interest BOOLEAN DEFAULT TRUE,
    adventure_interest BOOLEAN DEFAULT TRUE,
    shopping_interest BOOLEAN DEFAULT FALSE,
    dietary_restrictions VARCHAR(300),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: travel_insights
-- Purpose: Store system-generated tips and warnings for plans
CREATE TABLE IF NOT EXISTS travel_insights (
    insight_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    insight_type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    severity VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id),
    INDEX idx_insight_type (insight_type),
    FOREIGN KEY (plan_id) REFERENCES travel_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample destination data
INSERT INTO destination_info 
(city_name, country, latitude, longitude, best_month_from, best_month_to, suggested_duration_days, safety_rating, language, description) 
VALUES 
('Dhaka', 'Bangladesh', 23.8103, 90.4125, 10, 3, 3, 3.5, 'Bengali', 'Capital of Bangladesh with museums, markets, and heritage sites'),
('Cox''s Bazar', 'Bangladesh', 21.4272, 92.0058, 10, 3, 4, 4.0, 'Bengali', 'World''s longest natural sand beach with tourist attractions'),
('Sylhet', 'Bangladesh', 24.8949, 91.8734, 9, 4, 3, 4.2, 'Bengali', 'Tea gardens and natural beauty in northeastern Bangladesh'),
('Chittagong', 'Bangladesh', 22.3569, 91.7832, 10, 4, 2, 3.8, 'Bengali', 'Port city with historical significance and nearby attractions'),
('Khulna', 'Bangladesh', 22.8456, 89.5644, 11, 3, 3, 3.6, 'Bengali', 'Gateway to Sundarbans mangrove forests'),
('Bandarban', 'Bangladesh', 22.1953, 92.2183, 9, 5, 3, 4.1, 'Bengali', 'Hillside town with tribal culture and trekking opportunities'),
('Kuakata', 'Bangladesh', 22.2919, 91.9163, 10, 3, 2, 4.3, 'Bengali', 'Peaceful beach with panoramic views'),
('Tangail', 'Bangladesh', 24.2505, 89.9167, 10, 4, 1, 3.9, 'Bengali', 'Famous for muslin and historical temples');

-- Insert sample destination preferences (for Phase 2)
COMMIT;

-- ========================================
-- END OF V1 MIGRATION
-- ========================================
