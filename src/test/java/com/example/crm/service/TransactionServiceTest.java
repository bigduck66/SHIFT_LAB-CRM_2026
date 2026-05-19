package com.example.crm.service;

import com.example.crm.dto.request.CreateTransactionRequest;
import com.example.crm.dto.response.TransactionResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Seller seller;
    private Transaction transaction;
    private CreateTransactionRequest request;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L)
                .name("Иван Петров")
                .active(true)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .seller(seller)
                .amount(new BigDecimal("1500.00"))
                .paymentType(PaymentType.CASH)
                .build();

        request = new CreateTransactionRequest();
        request.setSellerId(1L);
        request.setAmount(new BigDecimal("1500.00"));
        request.setPaymentType(PaymentType.CASH);
    }

    @Test
    void getAllTransactions_ShouldReturnList() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction));

        List<TransactionResponse> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("1500.00"), result.get(0).getAmount());
        verify(transactionRepository).findAll();
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionResponse result = transactionService.getTransactionById(1L);

        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_ShouldThrowException_WhenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(99L);
        });
    }

    @Test
    void createTransaction_ShouldReturnSavedTransaction() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse result = transactionService.createTransaction(request);

        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        verify(sellerRepository).findById(1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());
        request.setSellerId(99L);

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.createTransaction(request);
        });
    }

    @Test
    void getTransactionsBySellerId_ShouldReturnList() {
        when(sellerRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findBySellerId(1L)).thenReturn(Arrays.asList(transaction));

        List<TransactionResponse> result = transactionService.getTransactionsBySellerId(1L);

        assertEquals(1, result.size());
        verify(transactionRepository).findBySellerId(1L);
    }
}