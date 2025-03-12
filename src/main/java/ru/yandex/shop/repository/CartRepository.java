package ru.yandex.shop.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.shop.model.Cart;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.count = c.count + 1 WHERE c.id = :id")
    void increaseCountByOne(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Cart c SET c.count = c.count - 1 WHERE c.id = :id")
    void decreaseCountByOne(@Param("id") Long id);
}
