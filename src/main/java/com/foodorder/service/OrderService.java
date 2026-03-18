package com.foodorder.service;

import com.foodorder.dto.*;
import com.foodorder.entity.*;
import com.foodorder.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public OrderDTO createOrder(Long userId, OrderRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPhone(request.getPhone());
        order.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));
            totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        for (OrderItemDTO itemDTO : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItemRepository.save(orderItem);
        }

        // Send email with PDF receipt
        sendOrderEmailWithPDF(savedOrder);

        return convertToDTO(savedOrder);
    }

    private void sendOrderEmailWithPDF(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("FoodHub - Order Confirmation #" + order.getId());

            String html = "<h2>Thank you for ordering from FoodHub!</h2>"
                    + "<p>Our delivery executive will reach your doorstep in 30 minutes.</p>"
                    + "<p>Order ID: " + order.getId() + "</p>"
                    + "<p>Delivery Address: " + order.getDeliveryAddress() + "</p>";

            helper.setText(html, true);

            ByteArrayResource pdf = generatePDFReceipt(order);
            helper.addAttachment("OrderReceipt_" + order.getId() + ".pdf", pdf);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating PDF/email: " + e.getMessage());
        }
    }

    private ByteArrayResource generatePDFReceipt(Order order) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("FoodHub Order Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Order ID: " + order.getId(), normalFont));
            document.add(new Paragraph("Customer: " + order.getUser().getFullName(), normalFont));
            document.add(new Paragraph("Delivery Address: " + order.getDeliveryAddress(), normalFont));
            document.add(new Paragraph("Phone: " + order.getPhone(), normalFont));
            document.add(new Paragraph("Order Date: " + order.getOrderDate(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 1, 2, 2});

            String[] headers = {"Item", "Qty", "Price", "Total"};
            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, headFont));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(header);
            }

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            for (OrderItem item : items) {
                table.addCell(new PdfPCell(new Phrase(item.getMenuItem().getName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + item.getPrice(), normalFont)));
                table.addCell(new PdfPCell(new Phrase("$" + item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())), normalFont)));
            }

            PdfPCell totalCell = new PdfPCell(new Phrase("Total", headFont));
            totalCell.setColspan(3);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(totalCell);
            table.addCell(new PdfPCell(new Phrase("$" + order.getTotalAmount(), headFont)));

            document.add(table);
            document.close();

            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    // --- Remaining methods ---
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.Status.valueOf(status));
        return convertToDTO(orderRepository.save(order));
    }

    public OrderDTO getOrderById(Long orderId) {
        return convertToDTO(orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }

    @Transactional
    public OrderDTO cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUser().getId().equals(userId))
            throw new RuntimeException("You can only cancel your own orders");
        if (order.getStatus() == Order.Status.DELIVERED || order.getStatus() == Order.Status.CANCELLED)
            throw new RuntimeException("This order cannot be cancelled");
        order.setStatus(Order.Status.CANCELLED);
        return convertToDTO(orderRepository.save(order));
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setPhone(order.getPhone());
        dto.setNotes(order.getNotes());

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        dto.setOrderItems(items.stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getMenuItem().getId(),
                        item.getMenuItem().getName(),
                        item.getMenuItem().getImageUrl(),
                        item.getQuantity(),
                        item.getPrice()
                )).collect(Collectors.toList()));
        return dto;
    }
}