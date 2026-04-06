package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportRouteDTO {
    private Long id;
    private Long sourceDestinationId;
    private Long targetDestinationId;
    private String transportType;
    private Integer distanceKm;
    private BigDecimal travelTimeHours;
    private Integer costEconomyBdt;
    private Integer costMidrangeBdt;
    private Integer costLuxuryBdt;
    private String notes;
}
