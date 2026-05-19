package com.example.crm.service;

import com.example.crm.dto.request.CreateSellerRequest;
import com.example.crm.dto.response.SellerResponse;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.model.entity.Seller;
import com.example.crm.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;
    private CreateSellerRequest request;

    @BeforeEach
    void setUp() {
        seller = Seller.builder()
                .id(1L)
                .name("Иван Петров")
                .contactInfo("ivan@email.com")
                .active(true)
                .build();

        request = new CreateSellerRequest("Иван Петров", "ivan@email.com");
    }

    @Test
    void getAllSellers_ShouldReturnList() {
        when(sellerRepository.findByActiveTrue()).thenReturn(Arrays.asList(seller));

        List<SellerResponse> result = sellerService.getAllSellers();

        assertEquals(1, result.size());
        assertEquals("Иван Петров", result.get(0).getName());
        verify(sellerRepository).findByActiveTrue();
    }

    @Test
    void getSellerById_ShouldReturnSeller() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        SellerResponse result = sellerService.getSellerById(1L);

        assertEquals("Иван Петров", result.getName());
        verify(sellerRepository).findById(1L);
    }

    @Test
    void getSellerById_ShouldThrowException_WhenNotFound() {
        when(sellerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            sellerService.getSellerById(99L);
        });
    }

    @Test
    void createSeller_ShouldReturnSavedSeller() {
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        SellerResponse result = sellerService.createSeller(request);

        assertEquals("Иван Петров", result.getName());
        assertEquals("ivan@email.com", result.getContactInfo());
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void updateSeller_ShouldUpdateAndReturn() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        CreateSellerRequest updateRequest = new CreateSellerRequest("Иван Обновленный", "new@email.com");
        SellerResponse result = sellerService.updateSeller(1L, updateRequest);

        assertEquals("Иван Обновленный", result.getName());
        verify(sellerRepository).findById(1L);
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void deleteSeller_ShouldSetActiveFalse() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        sellerService.deleteSeller(1L);

        assertFalse(seller.isActive());
        verify(sellerRepository).findById(1L);
        verify(sellerRepository).save(seller);
    }
}