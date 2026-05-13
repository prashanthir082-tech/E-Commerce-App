package com.app.ecommerce_app.repository;

import com.app.ecommerce_app.model.CartItem;
import com.app.ecommerce_app.model.Product;
import com.app.ecommerce_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    CartItem findByUserAndProduct(User user, Product product);

    void deleteItemByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);
}
