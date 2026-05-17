package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    /** GET /api/expenses?userId=xxx */
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(@RequestParam UUID userId) {
        return ResponseEntity.ok(expenseService.getAllExpenses(userId));
    }

    /** GET /api/expenses/monthly?userId=xxx&month=5&year=2026 */
    @GetMapping("/monthly")
    public ResponseEntity<List<Expense>> getMonthlyExpenses(
            @RequestParam UUID userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(expenseService.getMonthlyExpenses(userId, month, year));
    }

    /** POST /api/expenses */
    @PostMapping
    public ResponseEntity<Expense> addExpense(
            @RequestParam UUID userId,
            @RequestBody ExpenseRequest request) {
        Expense created = expenseService.addExpense(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PATCH /api/expenses/{id} */
    @PatchMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(userId, id, request));
    }

    /** DELETE /api/expenses/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        expenseService.deleteExpense(userId, id);
        return ResponseEntity.noContent().build();
    }
}
