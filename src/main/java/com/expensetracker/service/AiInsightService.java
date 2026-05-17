package com.expensetracker.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Rule-based AI Insight Engine.
 * Generates smart financial recommendations without any external AI API.
 */
@Service
public class AiInsightService {

    public List<String> generateInsights(
            BigDecimal totalSpent,
            BigDecimal budget,
            Map<String, BigDecimal> categoryBreakdown,
            int expenseCount,
            double budgetUsedPercent) {

        List<String> insights = new ArrayList<>();

        // Budget health
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            if (budgetUsedPercent >= 100) {
                insights.add("🚨 You have exceeded your monthly budget! Consider cutting discretionary spending.");
            } else if (budgetUsedPercent >= 85) {
                insights.add(String.format("⚠️ You have used %.1f%% of your budget. Be cautious with remaining spending.", budgetUsedPercent));
            } else if (budgetUsedPercent >= 60) {
                insights.add(String.format("📊 Budget usage at %.1f%%. You're on track — keep it up!", budgetUsedPercent));
            } else {
                insights.add(String.format("✅ Great job! Only %.1f%% of your budget used so far.", budgetUsedPercent));
            }
        }

        // Top spending category
        if (!categoryBreakdown.isEmpty()) {
            Map.Entry<String, BigDecimal> topCategory = categoryBreakdown.entrySet().iterator().next();
            insights.add(String.format("🏆 Your highest spending category is '%s' (₹%.2f).",
                    topCategory.getKey(), topCategory.getValue()));

            // Flag if single category > 50% of spending
            if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
                double topPercent = topCategory.getValue()
                        .divide(totalSpent, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                if (topPercent > 50) {
                    insights.add(String.format("💡 '%s' accounts for %.1f%% of your spending. Consider diversifying.",
                            topCategory.getKey(), topPercent));
                }
            }
        }

        // Expense frequency
        if (expenseCount == 0) {
            insights.add("📝 No expenses recorded this month. Start tracking to get insights!");
        } else if (expenseCount > 30) {
            insights.add("📌 You have many small transactions. Consolidating purchases can help reduce impulse spending.");
        }

        // Food & dining alert
        BigDecimal foodSpend = categoryBreakdown.getOrDefault("Food", BigDecimal.ZERO)
                .add(categoryBreakdown.getOrDefault("Dining", BigDecimal.ZERO))
                .add(categoryBreakdown.getOrDefault("Restaurants", BigDecimal.ZERO));
        if (foodSpend.compareTo(BigDecimal.valueOf(3000)) > 0) {
            insights.add(String.format("🍔 Food spending (₹%.2f) is high. Cooking at home could save significantly.", foodSpend));
        }

        // Shopping alert
        BigDecimal shoppingSpend = categoryBreakdown.getOrDefault("Shopping", BigDecimal.ZERO);
        if (shoppingSpend.compareTo(BigDecimal.valueOf(5000)) > 0) {
            insights.add("🛍️ Shopping expenses are elevated. Review recent purchases for non-essentials.");
        }

        // Savings suggestion
        if (budget.compareTo(BigDecimal.ZERO) > 0 && budgetUsedPercent < 70) {
            BigDecimal savings = budget.subtract(totalSpent);
            insights.add(String.format("💰 You could save ₹%.2f this month. Consider investing the surplus!", savings));
        }

        return insights;
    }
}
