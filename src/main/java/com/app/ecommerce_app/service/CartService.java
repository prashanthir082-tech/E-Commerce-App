package com.app.ecommerce_app.service;

import com.app.ecommerce_app.dto.CartItemRequest;
import com.app.ecommerce_app.model.CartItem;
import com.app.ecommerce_app.model.Product;
import com.app.ecommerce_app.model.User;
import com.app.ecommerce_app.repository.CartItemRepository;
import com.app.ecommerce_app.repository.ProductRepository;
import com.app.ecommerce_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public boolean addToCart(String userId, CartItemRequest request) {
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if(productOpt.isEmpty())
            return false;

        Product product = productOpt.get();
        if(product.getStockQuantity() < request.getQuantity())
            return false;

        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
        if(userOpt.isEmpty())
            return false;

        User user = userOpt.get();
        CartItem existingCartitem = cartItemRepository.findByUserAndProduct(user,product);
        if(existingCartitem != null){
            //update the quantity and price
            existingCartitem.setQuantity(existingCartitem.getQuantity() + request.getQuantity());
            existingCartitem.setPrice(existingCartitem.getPrice().multiply(BigDecimal.valueOf(existingCartitem.getQuantity())));
            cartItemRepository.save(existingCartitem);
        }else{
            // create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }
    return true;
    }


    public boolean deleteItemFromCart(String userId, String productId) {
        Optional<Product> productOpt = productRepository.findById(Long.valueOf(productId));
        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));

        if(productOpt.isPresent() && userOpt.isPresent()){
            cartItemRepository.deleteItemByUserAndProduct(userOpt.get(),productOpt.get());
            return true;
        }
    return false;
    }

    public List<CartItem> getCart(String userId) {
        return userRepository.findById(Integer.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }
}
