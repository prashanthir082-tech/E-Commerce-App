package com.app.ecommerce_app.controller;

import com.app.ecommerce_app.dto.CartItemRequest;
import com.app.ecommerce_app.model.CartItem;
import com.app.ecommerce_app.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-USER-ID") String userId,
                                            @RequestBody CartItemRequest request){
        if(!cartService.addToCart(userId,request)){
            return ResponseEntity.badRequest().body("Product Out of stock or user not found or Product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(@RequestHeader("X-USER-ID") String userId,@PathVariable String productId) {
        boolean isDeleted = cartService.deleteItemFromCart(userId,productId);
        return  isDeleted ? ResponseEntity.noContent().build():
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("X-USER-ID") String userId) {
        List<CartItem> result =  cartService.getCart(userId);

        return  result.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(result);
    }
}
