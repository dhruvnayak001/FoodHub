package com.foodorder.dto;

import java.math.BigDecimal;

public class OrderItemResponseDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String menuItemImage;
    private Integer quantity;
    private BigDecimal price;
    
    public OrderItemResponseDTO() {}
    
    public OrderItemResponseDTO(Long id, Long menuItemId, String menuItemName, String menuItemImage, Integer quantity, BigDecimal price) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.menuItemImage = menuItemImage;
        this.quantity = quantity;
        this.price = price;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
    public String getMenuItemImage() { return menuItemImage; }
    public void setMenuItemImage(String menuItemImage) { this.menuItemImage = menuItemImage; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
