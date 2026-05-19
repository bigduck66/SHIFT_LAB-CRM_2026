package com.example.crm.service;

import com.example.crm.dto.request.CreateTransactionRequest;
import com.example.crm.dto.response.TransactionResponse;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.model.entity.Seller;
import com.example.crm.model.entity.Transaction;
import com.example.crm.repository.SellerRepository;
import com.example.crm.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("transaction with ID " + id + " not found"));
        return toResponse(transaction);
    }
    
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Seller seller = sellerRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "seller with ID " + request.getSellerId() + " not found"));
        
        Transaction transaction = Transaction.builder()
                .seller(seller)
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .transactionDate(request.getTransactionDate())
                .build();
        
        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }
    
    public List<TransactionResponse> getTransactionsBySellerId(Long sellerId) {
        if (!sellerRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("seller with ID " + sellerId + " not found");
        }
        
        return transactionRepository.findBySellerId(sellerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .sellerId(transaction.getSeller().getId())
                .sellerName(transaction.getSeller().getName())
                .amount(transaction.getAmount())
                .paymentType(transaction.getPaymentType())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}