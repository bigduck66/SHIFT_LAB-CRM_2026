package com.example.crm.dto.request;

import com.example.crm.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
    
    @NotNull(message = "Seller ID is required")
    private Long sellerId;
    
    @NotNull(message = "Amount required")
    @Positive(message = "The amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment type required")
    private PaymentType paymentType;
    
    private LocalDateTime transactionDate;
}