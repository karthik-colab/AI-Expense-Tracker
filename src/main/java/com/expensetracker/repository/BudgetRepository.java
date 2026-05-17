package com.expensetracker.repository;

import com.expensetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Optional<Budget> findByUserIdAndMonthAndYear(UUID userId, int month, int year);

    java.util.List<Budget> findByUserIdOrderByYearDescMonthDesc(UUID userId);
}
