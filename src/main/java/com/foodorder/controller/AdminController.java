package com.foodorder.controller;

import com.foodorder.dto.MenuItemDTO;
import com.foodorder.dto.OrderDTO;
import com.foodorder.service.MenuItemService;
import com.foodorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
// ❌ REMOVED @CrossOrigin (handled globally)
public class AdminController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllItems());
    }

    @PostMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMenuItem(@RequestBody MenuItemDTO dto) {
        try {
            return ResponseEntity.ok(menuItemService.createItem(dto));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PutMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO dto) {
        try {
            return ResponseEntity.ok(menuItemService.updateItem(id, dto));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @DeleteMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        try {
            menuItemService.deleteItem(id);
            return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully"));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> error(String msg) {
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }
}