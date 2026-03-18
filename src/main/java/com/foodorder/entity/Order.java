package com.foodorder.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    private String deliveryAddress;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(length = 500)
    private String notes;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    public Order() {}
    
    public Order(Long id, User user, LocalDateTime orderDate, Status status, BigDecimal totalAmount, String deliveryAddress, String phone, String notes, List<OrderItem> orderItems) {
        this.id = id;
        this.user = user;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
        this.notes = notes;
        this.orderItems = orderItems;
    }
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    public enum Status {
        PENDING, PREPARING, READY, DELIVERED, CANCELLED
    }
}
