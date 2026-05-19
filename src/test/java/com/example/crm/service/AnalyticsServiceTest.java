package com.example.crm.service;

import com.example.crm.dto.response.BestPeriodResponse;
import com.example.crm.dto.response.SellerBelowResponse;
import com.example.crm.dto.response.TopSellerResponse;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.model.entity.Seller;
import com.example.crm.model.entity.Transaction;
import com.example.crm.model.enums.PaymentType;
import com.example.crm.repository.SellerRepository;
import com.example.crm.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Seller seller1;
    private Seller seller2;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        seller1 = Seller.builder().id(1L).name("Иван").build();
        seller2 = Seller.builder().id(2L).name("Мария").build();

        start = LocalDateTime.of(2024, 1, 1, 0, 0);
        end = LocalDateTime.of(2024, 12, 31, 23, 59);

        transaction1 = Transaction.builder()
                .id(1L)
                .seller(seller1)
                .amount(new BigDecimal("5000.00"))
                .paymentType(PaymentType.CASH)
                .transactionDate(LocalDateTime.of(2024, 6, 15, 10, 0))
                .build();

        transaction2 = Transaction.builder()
                .id(2L)
                .seller(seller1)
                .amount(new BigDecimal("3000.00"))
                .paymentType(PaymentType.CARD)
                .transactionDate(LocalDateTime.of(2024, 6, 15, 14, 0))
                .build();

        transaction3 = Transaction.builder()
                .id(3L)
                .seller(seller2)
                .amount(new BigDecimal("1000.00"))
                .paymentType(PaymentType.CASH)
                .transactionDate(LocalDateTime.of(2024, 7, 1, 10, 0))
                .build();
    }

    @Test
    void getTopSeller_ShouldReturnTopSeller() {
        when(transactionRepository.findByDateBetween(start, end))
                .thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        TopSellerResponse result = analyticsService.getTopSeller(start, end);

        assertEquals(1L, result.getSellerId());
        assertEquals("Иван", result.getSellerName());
        assertEquals(new BigDecimal("8000.00"), result.getTotalAmount());
        assertEquals(2L, result.getTransactionCount());
    }

    @Test
    void getTopSeller_ShouldThrowException_WhenNoTransactions() {
        when(transactionRepository.findByDateBetween(start, end)).thenReturn(Arrays.asList());

        assertThrows(ResourceNotFoundException.class, () -> {
            analyticsService.getTopSeller(start, end);
        });
    }

    @Test
    void getSellersBelowAmount_ShouldReturnFilteredList() {
        when(transactionRepository.findByDateBetween(start, end))
                .thenReturn(Arrays.asList(transaction1, transaction2, transaction3));

        List<SellerBelowResponse> result = analyticsService.getSellersBelowAmount(start, end, new BigDecimal("5000"));

        assertEquals(1, result.size());
        assertEquals("Мария", result.get(0).getSellerName());
        assertEquals(new BigDecimal("1000.00"), result.get(0).getTotalAmount());
    }

    @Test
    void getBestPeriod_ShouldReturnBestPeriod() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller1));
        when(transactionRepository.findBySellerId(1L))
                .thenReturn(Arrays.asList(transaction1, transaction2));

        BestPeriodResponse result = analyticsService.getBestPeriod(1L);

        assertEquals(1L, result.getSellerId());
        assertEquals("Иван", result.getSellerName());
        assertEquals(new BigDecimal("8000.00"), result.getTotalAmount());
        assertEquals(2L, result.getTransactionCount());
    }

    @Test
    void getBestPeriod_ShouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            analyticsService.getBestPeriod(99L);
        });
    }

    @Test
    void getBestPeriod_ShouldThrowException_WhenNoTransactions() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller1));
        when(transactionRepository.findBySellerId(1L)).thenReturn(Arrays.asList());

        assertThrows(ResourceNotFoundException.class, () -> {
            analyticsService.getBestPeriod(1L);
        });
    }
}