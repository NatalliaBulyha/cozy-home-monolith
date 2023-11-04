package com.cozyhome.onlineshop.orderservice.service.impl;

import com.cozyhome.onlineshop.dto.order.OrderDto;
import com.cozyhome.onlineshop.dto.order.OrderNumberDto;
import com.cozyhome.onlineshop.orderservice.model.Delivery;
import com.cozyhome.onlineshop.orderservice.model.Order;
import com.cozyhome.onlineshop.orderservice.model.OrderItem;
import com.cozyhome.onlineshop.orderservice.model.enums.OrderStatus;
import com.cozyhome.onlineshop.orderservice.repository.OrderRepository;
import com.cozyhome.onlineshop.orderservice.service.OrderService;
import com.cozyhome.onlineshop.orderservice.service.builder.DeliveryBuilder;
import com.cozyhome.onlineshop.orderservice.service.builder.OrderItemBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemBuilder orderItemBuilder;
    private final DeliveryBuilder deliveryBuilder;


    @Override
    public OrderNumberDto createOrder(OrderDto orderDto, String userId) {

        List<OrderItem> orderItems = orderItemBuilder.buildOrderItems(orderDto.getOrderItems());

        Order order = Order.builder()
                .firstName(orderDto.getFirstName())
                .lastName(orderDto.getLastName())
                .phoneNumber(orderDto.getPhoneNumber())
                .orderItem(orderItems)
                .orderStatus(OrderStatus.NEW)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        Delivery delivery;
        if (orderDto.getDelivery().getDeliveryCompanyName() == null) {
            delivery = deliveryBuilder.buildPostalDelivery(orderDto.getDelivery());
        } else {
            delivery = deliveryBuilder.buildAddressDelivery(orderDto.getDelivery());

        }
        order.setDelivery(delivery);

        if (!orderDto.getEmail().isEmpty()) {
            order.setEmail(orderDto.getEmail());
        }
        if (userId != null) {
            order.setUserId(userId);
        }
        order.setOrderNumber(getOrderNumber());
        orderRepository.save(order);
        return new OrderNumberDto(order.getOrderNumber());
    }

    private Integer getOrderNumber() {
        int orderNumber;
        Optional<Order> order = orderRepository.findFirstByOrderByOrderNumberDesc();
        if (order.isPresent()) {
            orderNumber = order.get().getOrderNumber();
            orderNumber++;
            orderNumber = checkOrderNumberExists(orderNumber);
        } else {
            orderNumber = getRandomOrderNumber();
            orderNumber = checkOrderNumberExists(orderNumber);
        }
        return orderNumber;
    }

    private Integer getRandomOrderNumber() {
        Random rand = new Random();
        int maxValue = 10000;
        int minValue = 1000;
        return rand.nextInt(maxValue - minValue + 1) + minValue;
    }

    private int checkOrderNumberExists(int orderNumber) {
        boolean isExist = orderRepository.existsByOrderNumber(orderNumber);
        while (isExist) {
            orderNumber++;
            isExist = orderRepository.existsByOrderNumber(orderNumber);
        }
        return orderNumber;
    }
}
