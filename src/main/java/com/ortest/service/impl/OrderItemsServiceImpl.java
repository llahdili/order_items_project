package com.ortest.service.impl;
import com.ortest.common.utils.Statu;
import com.ortest.exceptions.NotAccessibleActionException;
import com.ortest.exceptions.ResourceNotFoundException;
import com.ortest.model.OrderItems;
import com.ortest.model.Orders;
import com.ortest.model.Products;
import com.ortest.repository.OrderItemRepository;
import com.ortest.repository.OrderRepo;
import com.ortest.service.OrderItemService;
import com.ortest.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderItemsServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemsRepository;
    private final ProductService productService;

    private final OrderRepo orderRepo;

    @Autowired
    public OrderItemsServiceImpl(OrderItemRepository orderItemsRepository, ProductService productService, OrderRepo orderRepo) {
        this.orderItemsRepository = orderItemsRepository;
        this.productService = productService;
        this.orderRepo = orderRepo;
    }


    @Override
    public List<OrderItems> findAllItems() {
        return orderItemsRepository.findAll();
    }

    @Override
    public Optional<OrderItems> findById(Long id) {
        return orderItemsRepository.findById(id);
    }

    @Override
    public OrderItems save(OrderItems orderItem) throws NotAccessibleActionException{

        Orders orders = orderRepo.findById(orderItem.getOrders().getId()).orElseThrow(() ->
        new RuntimeException("Order not found with id = " + orderItem.getOrders().getId()));
        if (orders.isActive() == true && orders.getValidation() == false && orders.getStatu().equals(Statu.PROCESSING)) {

            Products products = productService.findById(orderItem.getProducts().getId()).orElseThrow(() ->
                    new RuntimeException("Product not found with id = " + orderItem.getProducts().getId()));

            if (products.getUnitsToStock() == null)
            products.setUnitsToStock(products.getUnitsToStock() + orderItem.getQuantity());
            log.info("we added " + orderItem.getQuantity() + " units to our " + products.getName() + " product stock ! ");

            orderItem.setTotalItemPriceHostTax(orderItem.getQuantity() * products.getUnitPriceHorsTax());
            orderItem.setTotalItemPriceTTC(orderItem.getQuantity() * products.getPriceTTC());

            if (orders.getTotaleOrdersPriceTTC() == null)
                orders.setTotaleOrdersPriceTTC(0.0);
            orders.setTotaleOrdersPriceTTC(orders.getTotaleOrdersPriceTTC() + orderItem.getTotalItemPriceTTC());

            if (orders.getTotaleOrdersPriceHorsTax() == null)
                orders.setTotaleOrdersPriceHorsTax(0.0);
            orders.setTotaleOrdersPriceHorsTax(orders.getTotaleOrdersPriceHorsTax() + orderItem.getTotalItemPriceHostTax());
            }else {
            log.error("can not add items to this order");
            throw new NotAccessibleActionException("can not add items");

        }
            return orderItemsRepository.save(orderItem);

    }

    @Override
    public void deleteById(Long id){
        Optional<OrderItems> orderItems = orderItemsRepository.findById(id);
        orderItems.map(orderItems1 -> {
                    orderItems1.getProducts().setUnitsToStock(orderItems.get().getProducts().getUnitsToStock() - orderItems1.getQuantity());
                    orderItems1.setTotalItemPriceHostTax(orderItems.get().getOrders().getTotaleOrdersPriceHorsTax() - orderItems1.getOrders().getTotaleOrdersPriceHorsTax());
                    return orderItemsRepository.save(orderItems1);
                });
        orderItemsRepository.deleteById(id);
    }

    @Override
    public OrderItems updateItem(OrderItems orderItem, Long id) throws NotAccessibleActionException {

         Orders orders = orderRepo.findById(orderItem.getOrders().getId()).orElseThrow(() ->new RuntimeException("Order not found " ));

        if (orders.isActive() || orders.getValidation().equals(false) || orders.getStatu() == Statu.PROCESSING){
            return orderItemsRepository.findById(id).map(orderItems -> {
                orderItems.setOrders(orderItem.getOrders());
                orderItems.setTotalItemPriceTTC(orderItem.getTotalItemPriceTTC());
                orderItems.setTotalItemPriceHostTax(orderItem.getTotalItemPriceHostTax());
                orderItems.getProducts();
                orderItems.setQuantity(orderItem.getQuantity());
                return orderItemsRepository.save(orderItems);
            }).orElseThrow(() -> new ResourceNotFoundException(Orders.class, "id ", String.valueOf(id)));
    } throw new NotAccessibleActionException("Can not Update this items to the order ");
    }

    @Override
    public List<OrderItems> findOrdersTodelete(Long id){
        return orderItemsRepository.findOrdersTodelete(id);
    }

}