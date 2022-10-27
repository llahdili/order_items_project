package com.ortest.service;

import com.ortest.exceptions.InvalidCommandeException;
import com.ortest.mapstruct.OrderDTO;
import com.ortest.model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Orders> findAll();

    Orders saveOrder(Orders order) throws InvalidCommandeException;

    Optional<Orders> findById(Long id) throws InvalidCommandeException;


    void enable(Long id);

    public Orders updateOrder(Orders orders, Long id) throws InvalidCommandeException;

}
