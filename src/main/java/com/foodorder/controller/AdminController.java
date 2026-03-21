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
@CrossOrigin(origins = "*")
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
            MenuItemDTO created = menuItemService.createItem(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemDTO dto) {
        try {
            MenuItemDTO updated = menuItemService.updateItem(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        try {
            menuItemService.deleteItem(id);
            Map<String, String> success = new HashMap<>();
            success.put("message", "Menu item deleted successfully");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            OrderDTO updated = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
