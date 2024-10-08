package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, UserService userService, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Product handleSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public Optional<Product> fetchProductById(long id) {
        return this.productRepository.findById(id);
    }

    // private Specification<Product> nameLike(String name) {
    // return (root, query, criteriaBuilder) ->
    // criteriaBuilder.like(root.get(Product_.NAME), "%" + name + "%");
    // }

    public Page<Product> getAllProducts(Pageable page) {
        return this.productRepository.findAll(page);
    }

    public void deleteProductById(long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity) {
        User user = this.userService.getUserByEmail(email);

        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);
                cart = this.cartRepository.save(newCart);
            }
            Optional<Product> productOptional = this.productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product realProduct = productOptional.get();

                CartDetail oldCart = this.cartDetailRepository.findByCartAndProduct(cart, realProduct);

                if (oldCart == null) {
                    CartDetail cartDetail = new CartDetail();
                    cartDetail.setCart(cart);
                    cartDetail.setPrice(realProduct.getPrice());
                    cartDetail.setProduct(realProduct);
                    cartDetail.setQuantity(quantity);

                    this.cartDetailRepository.save(cartDetail);

                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    session.setAttribute("sum", s);
                    this.cartRepository.save(cart);
                } else {
                    oldCart.setQuantity(oldCart.getQuantity() + quantity);
                    this.cartDetailRepository.save(oldCart);
                }

            }
        }

    }

    public Cart getCartByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handleDeleteCartDetail(long cartDetailId, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional != null) {
            CartDetail cartDetail = cartDetailOptional.get();

            Cart cart = cartDetail.getCart();
            this.cartDetailRepository.deleteById(cartDetailId);

            if (cart.getSum() > 1) {
                int s = cart.getSum() - 1;
                cart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(cart);
            } else {
                session.setAttribute("sum", 0);
                this.cartRepository.deleteById(cart.getId());
            }

        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session,
            String receiverName, String receiverAddress, String receiverPhone) {

        Cart cart = this.cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            if (cartDetails != null) {

                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");
                double sum = 0;
                for (CartDetail cd : cartDetails) {
                    sum += cd.getQuantity() * cd.getPrice();
                }
                order.setTotalPrice(sum);
                order = this.orderRepository.save(order);
                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getProduct().getPrice());
                    orderDetail.setQuantity(cd.getQuantity());

                    this.orderDetailRepository.save(orderDetail);
                }

                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.delete(cd);
                }

                this.cartRepository.deleteById(cart.getId());

                session.setAttribute("sum", 0);

            }
        }
    }

}
