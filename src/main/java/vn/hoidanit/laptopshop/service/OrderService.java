package vn.hoidanit.laptopshop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            UserService userService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userService = userService;
    }

    public void updateOrderStatus(Order order) {
        this.orderRepository.save(order);
    }

    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    public Optional<Order> getOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    public void deleteOrderById(long id) {
        Optional<Order> order = this.orderRepository.findById(id);
        if (order != null) {
            Order orderCurent = order.get();
            List<OrderDetail> orderDetails = orderCurent.getOrderDetails();
            for (OrderDetail cd : orderDetails) {
                this.orderDetailRepository.deleteById(cd.getId());
            }
        }
        this.orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByUser(long id) {
        User user = this.userService.getUserById(id);
        return this.orderRepository.findByUser(user);
    }
}
