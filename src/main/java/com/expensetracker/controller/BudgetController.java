package com.expensetracker.controller;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetRepository budgetRepository;

    /** GET /api/budget?userId=xxx&month=5&year=2026 */
    @GetMapping
    public ResponseEntity<Budget> getBudget(
            @RequestParam UUID userId,
            @RequestParam int month,
            @RequestParam int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/budget */
    @PostMapping
    public ResponseEntity<Budget> setBudget(
            @RequestParam UUID userId,
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam BigDecimal amount) {

        Optional<Budget> existing = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        Budget budget = existing.orElse(Budget.builder()
                .userId(userId)
                .month(month)
                .year(year)
                .build());
        budget.setMonthlyBudget(amount);
        return ResponseEntity.ok(budgetRepository.save(budget));
    }
}
