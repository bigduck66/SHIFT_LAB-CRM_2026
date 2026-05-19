package com.example.crm.service;

import com.example.crm.dto.request.CreateSellerRequest;
import com.example.crm.dto.response.SellerResponse;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.model.entity.Seller;
import com.example.crm.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {
    
    private final SellerRepository sellerRepository;
    
    public List<SellerResponse> getAllSellers() {
        return sellerRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public SellerResponse getSellerById(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("seller with ID " + id + " not found"));
        return toResponse(seller);
    }
    
    @Transactional
    public SellerResponse createSeller(CreateSellerRequest request) {
        Seller seller = Seller.builder()
                .name(request.getName())
                .contactInfo(request.getContactInfo())
                .build();
        
        seller = sellerRepository.save(seller);
        return toResponse(seller);
    }
    
    @Transactional
    public SellerResponse updateSeller(Long id, CreateSellerRequest request) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("seller with ID " + id + " not found"));
        
        seller.setName(request.getName());
        seller.setContactInfo(request.getContactInfo());
        
        seller = sellerRepository.save(seller);
        return toResponse(seller);
    }
    
    @Transactional
    public void deleteSeller(Long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("seller with ID " + id + " not found"));
        
        seller.setActive(false);
        sellerRepository.save(seller);
    }
    
    private SellerResponse toResponse(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .contactInfo(seller.getContactInfo())
                .registrationDate(seller.getRegistrationDate())
                .active(seller.isActive())
                .build();
    }
}