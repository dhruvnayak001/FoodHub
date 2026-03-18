package com.foodorder.controller;

import com.foodorder.dto.MenuItemDTO;
import com.foodorder.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {
    
    @Autowired
    private MenuItemService menuItemService;
    
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemDTO>> getAllItems() {
        return ResponseEntity.ok(menuItemService.getAllAvailableItems());
    }
    
    @GetMapping("/items/{category}")
    public ResponseEntity<List<MenuItemDTO>> getItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(menuItemService.getItemsByCategory(category));
    }
    
    @GetMapping("/item/{id}")
    public ResponseEntity<MenuItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getItemById(id));
    }
}
