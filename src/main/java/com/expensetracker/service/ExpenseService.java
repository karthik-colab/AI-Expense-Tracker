package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses(UUID userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }

    public List<Expense> getMonthlyExpenses(UUID userId, int month, int year) {
        return expenseRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }

    public Expense addExpense(UUID userId, ExpenseRequest req) {
        Expense expense = Expense.builder()
                .userId(userId)
                .title(req.getTitle())
                .amount(req.getAmount())
                .category(req.getCategory() != null ? req.getCategory() : "Other")
                .description(req.getDescription())
                .expenseDate(req.getExpenseDate() != null ? req.getExpenseDate() : LocalDate.now())
                .build();
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(UUID userId, UUID expenseId, ExpenseRequest req) {
        Expense expense = expenseRepository.findById(expenseId)
                .filter(e -> e.getUserId().equals(userId))  // ownership check
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));

        if (req.getTitle()       != null) expense.setTitle(req.getTitle());
        if (req.getAmount()      != null) expense.setAmount(req.getAmount());
        if (req.getCategory()    != null) expense.setCategory(req.getCategory());
        if (req.getDescription() != null) expense.setDescription(req.getDescription());
        if (req.getExpenseDate() != null) expense.setExpenseDate(req.getExpenseDate());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(UUID userId, UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .filter(e -> e.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Expense not found or access denied"));
        expenseRepository.delete(expense);
    }
}
