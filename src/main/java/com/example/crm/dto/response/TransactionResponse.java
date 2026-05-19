package com.example.crm.dto.response;

import com.example.crm.model.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private BigDecimal amount;
    private PaymentType paymentType;
    private LocalDateTime transactionDate;
}