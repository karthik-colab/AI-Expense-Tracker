package com.expensetracker.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Analytics DTO returned by the /analytics endpoint */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private int month;
    private int year;
    private BigDecimal totalSpent;
    private BigDecimal monthlyBudget;
    private BigDecimal budgetRemaining;
    private double budgetUsedPercent;
    private int expenseCount;
    private BigDecimal averageExpense;

    /** category -> amount */
    private Map<String, BigDecimal> categoryBreakdown;

    /** AI-generated insights */
    private List<String> aiInsights;
}
