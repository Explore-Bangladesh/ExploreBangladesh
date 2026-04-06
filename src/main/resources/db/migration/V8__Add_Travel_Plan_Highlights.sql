-- ========================================
-- V8: Add Travel Plan Highlights Table
-- Purpose: Store highlights for legacy travel plans (ElementCollection)
-- ========================================

-- Table: travel_plan_highlights
-- Purpose: Store travel plan highlights as a collection for TravelPlanEntity
CREATE TABLE IF NOT EXISTS travel_plan_highlights (
    plan_id VARCHAR(255) NOT NULL,
    highlight VARCHAR(500) NOT NULL,
    PRIMARY KEY (plan_id, highlight),
    FOREIGN KEY (plan_id) REFERENCES travel_plans_legacy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- V8 Migration Complete
-- travel_plan_highlights table created for legacy system highlights
-- ==========================================
