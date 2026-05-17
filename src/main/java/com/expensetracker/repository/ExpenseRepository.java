package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    // All expenses for a user, newest first
    List<Expense> findByUserIdOrderByExpenseDateDesc(UUID userId);

    // Expenses filtered by month and year
    @Query(value = "SELECT * FROM expenses e WHERE e.user_id = :userId " +
           "AND EXTRACT(MONTH FROM e.expense_date) = :month " +
           "AND EXTRACT(YEAR FROM e.expense_date) = :year " +
           "ORDER BY e.expense_date DESC", nativeQuery = true)
    List<Expense> findByUserIdAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("month")  int month,
            @Param("year")   int year);

    // Total spent for a user in a month/year
    @Query(value = "SELECT COALESCE(SUM(e.amount), 0) FROM expenses e WHERE e.user_id = :userId " +
           "AND EXTRACT(MONTH FROM e.expense_date) = :month " +
           "AND EXTRACT(YEAR FROM e.expense_date) = :year", nativeQuery = true)
    BigDecimal sumAmountByUserIdAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("month")  int month,
            @Param("year")   int year);

    // Category totals for a specific month
    @Query(value = "SELECT e.category, SUM(e.amount) FROM expenses e WHERE e.user_id = :userId " +
           "AND EXTRACT(MONTH FROM e.expense_date) = :month " +
           "AND EXTRACT(YEAR FROM e.expense_date) = :year " +
           "GROUP BY e.category ORDER BY SUM(e.amount) DESC", nativeQuery = true)
    List<Object[]> findCategoryTotals(
            @Param("userId") UUID userId,
            @Param("month")  int month,
            @Param("year")   int year);
}
