package com.expensetracker.controller;

import com.expensetracker.dto.AnalyticsResponse;
import com.expensetracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * GET /api/analytics?userId=xxx&month=5&year=2026
     * Returns full analytics + AI insights for the given month.
     */
    @GetMapping
    public ResponseEntity<AnalyticsResponse> getMonthlyAnalytics(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {

        // Default to current month/year
        LocalDate now = LocalDate.now();
        int targetMonth = month > 0 ? month : now.getMonthValue();
        int targetYear  = year  > 0 ? year  : now.getYear();

        return ResponseEntity.ok(analyticsService.getMonthlyAnalytics(userId, targetMonth, targetYear));
    }
}
