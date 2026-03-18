package com.foodorder.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String phone;
    private String notes;
    private List<OrderItemResponseDTO> orderItems;
    
    public OrderDTO() {}
    
    public OrderDTO(Long id, Long userId, LocalDateTime orderDate, String status, BigDecimal totalAmount, String deliveryAddress, String phone, String notes, List<OrderItemResponseDTO> orderItems) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
        this.notes = notes;
        this.orderItems = orderItems;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderItemResponseDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponseDTO> orderItems) { this.orderItems = orderItems; }
}
