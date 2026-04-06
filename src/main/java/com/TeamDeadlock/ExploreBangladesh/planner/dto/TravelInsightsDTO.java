package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelInsightsDTO {
    private List<String> highlights;
    private List<String> recommendations;
    private String visaInformation;
    private String currencyInfo;
    private String bestTimeToVisit;
    private String localLanguage;
    private String culturalTips;
    private String safetyInformation;
    private String transportationTips;
}
