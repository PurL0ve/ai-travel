package com.ai.travel.product.repository;

import com.ai.travel.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(Integer status, Pageable pageable);

    Page<Product> findByType(String type, Pageable pageable);

    Page<Product> findByDestinationContaining(String destination, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:destination IS NULL OR p.destination LIKE %:destination%) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByFilters(
            @Param("status") Integer status,
            @Param("type") String type,
            @Param("destination") String destination,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    List<Product> findByStatusOrderByPriceAsc(Integer status);

    List<Product> findByStatusOrderByCreatedAtDesc(Integer status);

    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    @Modifying
    @Transactional
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    @Modifying
    @Transactional
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

}