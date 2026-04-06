package com.TeamDeadlock.ExploreBangladesh.planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for trip budget breakdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBreakdownDto {

    private BigDecimal accommodation;
    private BigDecimal transport;
    private BigDecimal attractions;
    private BigDecimal food;
    private BigDecimal shopping;
    private BigDecimal miscellaneous;
    private BigDecimal total;

    public void calculateTotal() {
        total = accommodation.add(transport).add(attractions).add(food).add(shopping).add(miscellaneous);
    }
}
