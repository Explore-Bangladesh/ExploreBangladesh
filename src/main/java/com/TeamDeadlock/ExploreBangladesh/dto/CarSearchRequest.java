package com.TeamDeadlock.ExploreBangladesh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchRequest {
    private String location;
    private String pickupDate;   // Format: YYYY-MM-DD
    private String dropoffDate;  // Format: YYYY-MM-DD
}
