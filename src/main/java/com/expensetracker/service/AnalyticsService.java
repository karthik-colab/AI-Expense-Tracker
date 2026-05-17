package com.expensetracker.service;

import com.expensetracker.dto.AnalyticsResponse;
import com.expensetracker.model.Budget;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository  budgetRepository;
    private final AiInsightService  aiInsightService;

    /**
     * Build full analytics for a user in a given month/year.
     */
    public AnalyticsResponse getMonthlyAnalytics(UUID userId, int month, int year) {

        // 1. Total spent
        BigDecimal totalSpent = expenseRepository
                .sumAmountByUserIdAndMonthAndYear(userId, month, year);

        // 2. Expense list for count & avg
        List<Expense> expenses = expenseRepository
                .findByUserIdAndMonthAndYear(userId, month, year);

        int count = expenses.size();
        BigDecimal avg = count > 0
                ? totalSpent.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 3. Budget
        Optional<Budget> budgetOpt = budgetRepository
                .findByUserIdAndMonthAndYear(userId, month, year);

        BigDecimal monthlyBudget   = budgetOpt.map(Budget::getMonthlyBudget).orElse(BigDecimal.ZERO);
        BigDecimal budgetRemaining = monthlyBudget.subtract(totalSpent);
        double usedPercent = monthlyBudget.compareTo(BigDecimal.ZERO) > 0
                ? totalSpent.divide(monthlyBudget, 4, RoundingMode.HALF_UP)
                             .multiply(BigDecimal.valueOf(100))
                             .doubleValue()
                : 0.0;

        // 4. Category breakdown
        List<Object[]> rawCats = expenseRepository.findCategoryTotals(userId, month, year);
        Map<String, BigDecimal> categoryBreakdown = new LinkedHashMap<>();
        for (Object[] row : rawCats) {
            categoryBreakdown.put((String) row[0], (BigDecimal) row[1]);
        }

        // 5. AI Insights
        List<String> insights = aiInsightService.generateInsights(
                totalSpent, monthlyBudget, categoryBreakdown, count, usedPercent);

        return AnalyticsResponse.builder()
                .month(month)
                .year(year)
                .totalSpent(totalSpent)
                .monthlyBudget(monthlyBudget)
                .budgetRemaining(budgetRemaining)
                .budgetUsedPercent(Math.round(usedPercent * 10.0) / 10.0)
                .expenseCount(count)
                .averageExpense(avg)
                .categoryBreakdown(categoryBreakdown)
                .aiInsights(insights)
                .build();
    }
}
