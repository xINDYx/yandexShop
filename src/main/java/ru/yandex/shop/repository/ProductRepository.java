package ru.yandex.shop.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.shop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByTitleContaining(String name, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.count = p.count + 1 WHERE p.id = :id")
    void increaseCountByOne(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.count = p.count - 1 WHERE p.id = :id")
    void decreaseCountByOne(@Param("id") Long id);
}
