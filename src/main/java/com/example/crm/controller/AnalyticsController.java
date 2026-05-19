package com.example.crm.controller;

import com.example.crm.dto.response.BestPeriodResponse;
import com.example.crm.dto.response.SellerBelowResponse;
import com.example.crm.dto.response.TopSellerResponse;
import com.example.crm.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-seller")
    public ResponseEntity<TopSellerResponse> getTopSeller(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(analyticsService.getTopSeller(start, end));
    }

    @GetMapping("/sellers-below")
    public ResponseEntity<List<SellerBelowResponse>> getSellersBelow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam BigDecimal maxAmount) {
        return ResponseEntity.ok(analyticsService.getSellersBelowAmount(start, end, maxAmount));
    }
    
    @GetMapping("/sellers/{sellerId}/best-period")
    public ResponseEntity<BestPeriodResponse> getBestPeriod(@PathVariable Long sellerId) {
        return ResponseEntity.ok(analyticsService.getBestPeriod(sellerId));
    }
}