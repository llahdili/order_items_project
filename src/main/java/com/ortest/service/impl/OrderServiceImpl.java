package com.ortest.service.impl;
import com.ortest.common.patherns.IReferenceStrategy;
import com.ortest.common.utils.Statu;
import com.ortest.exceptions.InvalidCommandeException;
import com.ortest.exceptions.ResourceNotFoundException;
import com.ortest.mapstruct.OrderDTO;
import com.ortest.mapstruct.mappers.OrderMapper;
import com.ortest.model.Orders;
import com.ortest.model.OrderItems;
import com.ortest.model.Products;
import com.ortest.repository.OrderRepo;
import com.ortest.service.OrderItemService;
import com.ortest.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final OrderItemService orderItemService;
    private final OrderRepo orderRepo;
    private final IReferenceStrategy iReferenceStrategy;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemService orderItemService, OrderRepo orderRepo, IReferenceStrategy iReferenceStrategy) {
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.orderRepo = orderRepo;
        this.iReferenceStrategy = iReferenceStrategy;
    }

    @Override
    public List<Orders> findAll() {
        List<Orders> filterOrders = orderRepo.findAll();
        List<Orders> filtredOrders = filterOrders.stream().filter(orders -> orders.isActive() == true)
                .collect(Collectors.toList());
        return filtredOrders;
    }

    @Override
    public Orders saveOrder(Orders order) throws InvalidCommandeException {

        order.setDateCreatedOrder(LocalDateTime.now());
        order.setActive(true);
        order.setValidation(false);
        order.setStatu(Statu.PROCESSING);
        order.setReference(iReferenceStrategy.generateReceiptNumber());

        Orders res = this.orderRepo.save(order);
        for (OrderItems orderItem : res.getOrderItems()) {
            orderItem.setOrders(res);
            this.orderItemService.save(orderItem);
        }
        return orderRepo.save(order);
    }

    @Override
    public Optional<Orders> findById(Long id) throws InvalidCommandeException {

        Optional<Orders> res = this.orderRepo.findById(id);
        if (res.isPresent()) {
            if (res.get().isActive() == false) {
                throw new InvalidCommandeException("Order is deleted");
            } else {
                for (OrderItems orderItem : res.get().getOrderItems()) {
                    orderItem.getOrders();
                }
                return orderRepo.findById(id);
            }
        } else throw new InvalidCommandeException("Order not found with this id" + res.get().getId());
    }

    public Orders findMaxId() {
        return this.orderRepo.findMaxId();
    }


    @Override
    public void enable(Long id) {
        Optional<Orders> orders = orderRepo.findById(id);
        for (OrderItems orderItems : orders.get().getOrderItems()) {
            orderItemService.deleteById(orderItems.getId());
        }
        orderRepo.desable(id);
    }


    public Orders updateOrder(Orders orders, Long id){
            return orderRepo.findById(id)
             .map(ord -> {
                ord.setValidation(orders.getValidation());
                ord.setStatu(orders.getStatu());
                ord.setOrderItems(orders.getOrderItems());
                ord.setActive(orders.isActive());
                return orderRepo.save(ord);
            }).orElseThrow(() -> new ResourceNotFoundException(Orders.class, "id", String.valueOf(id)));
      }
}

