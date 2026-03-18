package com.foodorder.service;

import com.foodorder.dto.MenuItemDTO;
import com.foodorder.entity.MenuItem;
import com.foodorder.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    public List<MenuItemDTO> getAllAvailableItems() {
        return menuItemRepository.findByAvailableTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDTO> getItemsByCategory(String category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemDTO> getAllItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MenuItemDTO getItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        return convertToDTO(item);
    }
    
    public MenuItemDTO createItem(MenuItemDTO dto) {
        MenuItem item = new MenuItem();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setCategory(dto.getCategory());
        item.setImageUrl(dto.getImageUrl());
        item.setAvailable(dto.isAvailable());
        
        MenuItem saved = menuItemRepository.save(item);
        return convertToDTO(saved);
    }
    
    public MenuItemDTO updateItem(Long id, MenuItemDTO dto) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setCategory(dto.getCategory());
        item.setImageUrl(dto.getImageUrl());
        item.setAvailable(dto.isAvailable());
        
        MenuItem saved = menuItemRepository.save(item);
        return convertToDTO(saved);
    }
    
    public void deleteItem(Long id) {
        menuItemRepository.deleteById(id);
    }
    
    private MenuItemDTO convertToDTO(MenuItem item) {
        return new MenuItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.getImageUrl(),
                item.isAvailable()
        );
    }
}
