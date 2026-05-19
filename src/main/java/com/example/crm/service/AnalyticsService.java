package com.example.crm.service;

import com.example.crm.dto.response.BestPeriodResponse;
import com.example.crm.dto.response.SellerBelowResponse;
import com.example.crm.dto.response.TopSellerResponse;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.model.entity.Seller;
import com.example.crm.model.entity.Transaction;
import com.example.crm.repository.SellerRepository;
import com.example.crm.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    
    public TopSellerResponse getTopSeller(LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(start, end);
    
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("Нет транзакций за указанный период");
        }
    
        Map<Long, BigDecimal> sellerSums = new HashMap<>();
        Map<Long, String> sellerNames = new HashMap<>();
        Map<Long, Long> sellerCounts = new HashMap<>();
    
        for (Transaction t : transactions) {
            Seller seller = t.getSeller();
            if (seller == null) continue;
            
            Long sellerId = seller.getId();
            String sellerName = seller.getName();
            
            sellerSums.merge(sellerId, t.getAmount(), BigDecimal::add);
            sellerCounts.merge(sellerId, 1L, Long::sum);
            sellerNames.put(sellerId, sellerName);
        }
    
        if (sellerSums.isEmpty()) {
            throw new ResourceNotFoundException("Нет транзакций с продавцами");
        }
    
        Map.Entry<Long, BigDecimal> topSeller = sellerSums.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();
    
        return TopSellerResponse.builder()
                .sellerId(topSeller.getKey())
                .sellerName(sellerNames.get(topSeller.getKey()))
                .totalAmount(topSeller.getValue())
                .transactionCount(sellerCounts.get(topSeller.getKey()))
                .build();
    }

    public List<SellerBelowResponse> getSellersBelowAmount(LocalDateTime start, LocalDateTime end, BigDecimal maxAmount) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(start, end);

        Map<Long, BigDecimal> sellerSums = new HashMap<>();
        Map<Long, String> sellerNames = new HashMap<>();

        for (Transaction t : transactions) {
            Long sellerId = t.getSeller().getId();
            sellerSums.merge(sellerId, t.getAmount(), BigDecimal::add);
            sellerNames.putIfAbsent(sellerId, t.getSeller().getName());
        }

        return sellerSums.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(maxAmount) < 0)
                .map(entry -> SellerBelowResponse.builder()
                        .sellerId(entry.getKey())
                        .sellerName(sellerNames.get(entry.getKey()))
                        .totalAmount(entry.getValue())
                        .build())
                .sorted((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()))
                .collect(Collectors.toList());
    }

    public BestPeriodResponse getBestPeriod(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Продавец не найден"));

        List<Transaction> transactions = transactionRepository.findBySellerId(sellerId);

        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("У продавца нет транзакций");
        }

        transactions.sort(Comparator.comparing(Transaction::getTransactionDate));

        LocalDateTime bestStart = transactions.get(0).getTransactionDate();
        LocalDateTime bestEnd = transactions.get(0).getTransactionDate();
        BigDecimal maxSum = transactions.get(0).getAmount();

        LocalDateTime currentStart = transactions.get(0).getTransactionDate();
        BigDecimal currentSum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            BigDecimal amount = t.getAmount();

            if (currentSum.compareTo(BigDecimal.ZERO) <= 0) {
                currentSum = amount;
                currentStart = t.getTransactionDate();
            } else {
                currentSum = currentSum.add(amount);
            }

            if (currentSum.compareTo(maxSum) > 0) {
                maxSum = currentSum;
                bestStart = currentStart;
                bestEnd = t.getTransactionDate();
            }
        }

        LocalDateTime finalStart = bestStart;
        LocalDateTime finalEnd = bestEnd;
        long count = transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(finalStart) &&
                        !t.getTransactionDate().isAfter(finalEnd))
                .count();

        return BestPeriodResponse.builder()
                .sellerId(seller.getId())
                .sellerName(seller.getName())
                .periodStart(bestStart)
                .periodEnd(bestEnd)
                .transactionCount(count)
                .totalAmount(maxSum)
                .build();
    }
}