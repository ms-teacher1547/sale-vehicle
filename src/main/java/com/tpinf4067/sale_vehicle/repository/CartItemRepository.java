package com.tpinf4067.sale_vehicle.repository;

import com.tpinf4067.sale_vehicle.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
