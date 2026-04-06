-- ========================================
-- PHASE 7: Legacy System Support
-- Create Legacy Travel Planning Tables
-- ========================================

-- Table: travel_plans_legacy
-- Purpose: Store legacy travel plans (separate from new smart planner)
CREATE TABLE IF NOT EXISTS travel_plans_legacy (
    id VARCHAR(255) PRIMARY KEY,
    destination VARCHAR(255) NOT NULL,
    destination_image LONGTEXT,
    budget_tier VARCHAR(255) NOT NULL,
    duration_days INT NOT NULL,
    duration_nights INT NOT NULL,
    total_cost DOUBLE NOT NULL,
    description VARCHAR(2000),
    best_time_to_visit VARCHAR(255),
    group_size VARCHAR(255),
    division VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_destination (destination),
    INDEX idx_division (division)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: itinerary_days_legacy
-- Purpose: Store daily breakdown for legacy travel plans
CREATE TABLE IF NOT EXISTS itinerary_days_legacy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id VARCHAR(255) NOT NULL,
    day_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id),
    UNIQUE KEY unique_plan_day (plan_id, day_number),
    FOREIGN KEY (plan_id) REFERENCES travel_plans_legacy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: itinerary_activities_legacy
-- Purpose: Store individual activities within daily itineraries
CREATE TABLE IF NOT EXISTS itinerary_activities_legacy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_id BIGINT NOT NULL,
    activity_type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    location_lat DECIMAL(10,8),
    location_lng DECIMAL(11,8),
    sort_order INT,
    start_time VARCHAR(255),
    end_time VARCHAR(255),
    location VARCHAR(255),
    estimated_cost DOUBLE,
    tips VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_day_id (day_id),
    FOREIGN KEY (day_id) REFERENCES itinerary_days_legacy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: cost_breakdown_legacy
-- Purpose: Store cost breakdown for legacy travel plans
CREATE TABLE IF NOT EXISTS cost_breakdown_legacy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id),
    FOREIGN KEY (plan_id) REFERENCES travel_plans_legacy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
