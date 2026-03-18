package com.foodorder.controller;

import com.foodorder.dto.MenuItemDTO;
import com.foodorder.dto.OrderDTO;
import com.foodorder.dto.OrderRequestDTO;
import com.foodorder.entity.User;
import com.foodorder.security.UserDetailsImpl;
import com.foodorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // =======================
    // Create a new order
    // =======================
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            OrderDTO order = orderService.createOrder(userDetails.getId(), request);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =======================
    // Get all orders for logged-in user
    // =======================
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<OrderDTO> orders = orderService.getUserOrders(userDetails.getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // =======================
    // Cancel an order
    // =======================
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            OrderDTO canceledOrder = orderService.cancelOrder(userDetails.getId(), orderId);
            return ResponseEntity.ok(canceledOrder);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }



}