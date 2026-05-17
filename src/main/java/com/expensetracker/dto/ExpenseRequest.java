package com.expensetracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** DTO for creating/updating an expense — used in REST requests */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {
    private String title;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate expenseDate;
}

