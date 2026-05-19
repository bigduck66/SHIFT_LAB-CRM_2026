package com.example.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSellerRequest {
    
    @NotBlank(message = "seller name is required")
    private String name;
    
    private String contactInfo;
}