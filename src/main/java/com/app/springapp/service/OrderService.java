package com.app.springapp.service;

import com.app.springapp.dto.OrderAdminDto;
import com.app.springapp.dto.OrderDto;
import com.app.springapp.entity.Customer;
import com.app.springapp.entity.Order;
import com.app.springapp.entity.Delivery;
import com.app.springapp.repository.CustomerRepository;
import com.app.springapp.repository.OrderRepository;
import com.app.springapp.repository.DeliveryRepository;
import com.app.springapp.repository.OrderItemRepository;
import com.app.springapp.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /* ---------- CRUD ---------- */

    public Order createOrder(Order order) {
        Long customerId = null;
        if (order.getCustomer() != null && order.getCustomer().getCustomerId() != null) {
            customerId = order.getCustomer().getCustomerId();
        } else if (order.getCustomer() == null && order.getOrderId() == null) {
            // Try to get customerId from another field if needed (future-proofing)
        }
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            order.setCustomer(customer);
        } else {
            throw new RuntimeException("CustomerId is required to create an order");
        }
        Order savedOrder = orderRepository.save(order);
        // Associate all cart items with this order
        List<OrderItem> cartItems = orderItemRepository.findByCustomer_CustomerIdAndOrderIsNull(customerId);
        for (OrderItem item : cartItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        return savedOrder;
    }

    /** Paginated & sorted list */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /** Paginated & sorted list by status */
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatusIgnoreCase(status, pageable);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existing = getOrderById(id);
        existing.setOrderDate(updatedOrder.getOrderDate());
        existing.setTotalAmount(updatedOrder.getTotalAmount());
        existing.setPaymentMethod(updatedOrder.getPaymentMethod());
        existing.setStatus(updatedOrder.getStatus());
        existing.setCustomer(updatedOrder.getCustomer());
        Order savedOrder = orderRepository.save(existing);
        // Update or create delivery info if present in updatedOrder
        if (updatedOrder.getOrderId() != null) {
            Delivery delivery = deliveryRepository.findByOrder_OrderId(updatedOrder.getOrderId());
            if (delivery == null) {
                // Create new delivery if it doesn't exist
                delivery = new Delivery();
                delivery.setOrder(savedOrder);
            }
            if (updatedOrder.getOrderDate() != null) {
                delivery.setEstimatedDeliveryTime(updatedOrder.getOrderDate());
            }
            if (updatedOrder.getTotalAmount() != 0) {
                delivery.setTotalCost(updatedOrder.getTotalAmount());
            }
            deliveryRepository.save(delivery);
        }
        return savedOrder;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public long count() {
        return orderRepository.count();
    }

    /** Admin: Paginated & sorted list with customer and delivery info */
    public Page<OrderAdminDto> getAllOrdersAdmin(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new PageImpl<>(
            orders.getContent().stream().map(order -> {
                OrderAdminDto dto = new OrderAdminDto();
                dto.setOrderId(order.getOrderId());
                dto.setOrderDate(order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : null);
                dto.setTotalAmount(order.getTotalAmount());
                dto.setPaymentMethod(order.getPaymentMethod());
                dto.setStatus(order.getStatus());
                if (order.getCustomer() != null) {
                    dto.setCustomerId(order.getCustomer().getCustomerId());
                    dto.setCustomerName(order.getCustomer().getFullName());
                    dto.setCustomerEmail(order.getCustomer().getEmail());
                }
                Delivery delivery = deliveryRepository.findByOrder_OrderId(order.getOrderId());
                if (delivery != null) {
                    dto.setDeliveryDate(delivery.getEstimatedDeliveryTime() != null ? sdf.format(delivery.getEstimatedDeliveryTime()) : null);
                    dto.setDeliveryCost(delivery.getTotalCost());
                }
                return dto;
            }).collect(Collectors.toList()), pageable, orders.getTotalElements()
        );
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findOrdersByCustomerId(customerId);
    }

    public List<OrderDto> getOrderDtosByCustomerId(Long customerId) {
        List<Order> orders = getOrdersByCustomerId(customerId);
        return orders.stream().map(order -> {
            OrderDto dto = new OrderDto();
            dto.setOrderId(order.getOrderId());
            dto.setOrderDate(order.getOrderDate());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setStatus(order.getStatus());
            if (order.getCustomer() != null) {
                dto.setCustomerId(order.getCustomer().getCustomerId());
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}
