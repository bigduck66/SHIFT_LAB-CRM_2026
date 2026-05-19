package com.example.crm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellerResponse {
    private Long sellerId;
    private String sellerName;
    private BigDecimal totalAmount;
    private Long transactionCount;
}