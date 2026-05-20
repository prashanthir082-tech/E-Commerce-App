package com.app.ecommerce_app.service;

import com.app.ecommerce_app.dto.OrderResponse;
import com.app.ecommerce_app.dto.ProductResponse;
import com.app.ecommerce_app.model.*;
import com.app.ecommerce_app.repository.OrderRepository;
import com.app.ecommerce_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<OrderResponse> createOrder(String userId) {
        //validate valid user
        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
        if(userOpt.isEmpty()){
            return Optional.empty();
        }

        //validate cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

        User user = userOpt.get();
        //calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        //create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItem(
                        null,
                        cartItem.getProduct(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())),
                        order))
                .toList();
        order.setItems(orderItems);

        //Save order
        Order savedOrder = orderRepository.save(order);

        //clear the cart
        cartService.clearCart(userId);


        return Optional.ofNullable(modelMapper.map(savedOrder, OrderResponse.class));
    }
}
