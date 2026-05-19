package com.example.crm.repository;

import com.example.crm.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findBySellerId(Long sellerId);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findByDateBetween(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId " +
           "AND t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findBySellerIdAndDateBetween(
        @Param("sellerId") Long sellerId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}