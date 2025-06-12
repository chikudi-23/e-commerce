package com.ecommerce.app.service;

import com.ecommerce.app.dto.order.OrderItemRequestDTO;
import com.ecommerce.app.dto.order.OrderItemResponseDTO;
import com.ecommerce.app.dto.order.OrderRequestDTO;
import com.ecommerce.app.dto.order.OrderResponseDTO;
import com.ecommerce.app.model.*; // Import Order, OrderItem, Product, User, OrderStatus
import com.ecommerce.app.repository.OrderItemRepository;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ProductRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository; // Will be managed by Order entity cascade

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO placeNewOrder(Long userId, OrderRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Initial status
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtOrder(product.getPrice()); // Capture price at the time of order

            // Add to order's set of items; the addOrderItem method links it back to order
            order.addOrderItem(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // Decrease product stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product); // Save updated product stock
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return convertToOrderDTO(savedOrder);
    }

    public OrderResponseDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToOrderDTO(order);
    }

    public Page<OrderResponseDTO> getUserOrderHistory(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);
        List<OrderResponseDTO> orderResponseDTOs = ordersPage.getContent().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(orderResponseDTOs, pageable, ordersPage.getTotalElements());
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        try {
            OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(statusEnum);
            Order updatedOrder = orderRepository.save(order);
            return convertToOrderDTO(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + newStatus);
        }
    }

    // Helper method to convert Order entity to OrderResponseDTO
    public OrderResponseDTO convertToOrderDTO(Order order) { // Made public for controller access
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPriceAtOrder(item.getPriceAtOrder());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);
        return dto;
    }
}