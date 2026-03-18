package com.foodorder.dto;

import java.util.List;

public class OrderRequestDTO {
    private String deliveryAddress;
    private String phone;
    private String notes;
    private List<OrderItemDTO> items;
    
    public OrderRequestDTO() {}
    
    public OrderRequestDTO(String deliveryAddress, String phone, String notes, List<OrderItemDTO> items) {
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
        this.notes = notes;
        this.items = items;
    }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
