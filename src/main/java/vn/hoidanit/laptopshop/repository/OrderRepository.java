package vn.hoidanit.laptopshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Order save(Order order);

    Optional<Order> findById(long id);
}
