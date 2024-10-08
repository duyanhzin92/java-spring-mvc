package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.service.OrderService;

@Controller
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getDashboard(Model model, @RequestParam("page") Optional<String> PageOptional) {
        int page = 1;
        try {
            if (PageOptional.isPresent())
                page = Integer.parseInt(PageOptional.get());

        } catch (Exception e) {

        }
        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Order> prs = this.orderService.getAllOrder(pageable);
        List<Order> listOrders = prs.getContent();
        model.addAttribute("orders", listOrders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prs.getTotalPages());
        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id) {
        Optional<Order> order = this.orderService.getOrderById(id);
        Order currentOrder = new Order();
        if (order != null) {
            currentOrder = order.get();

        }
        model.addAttribute("id", currentOrder.getId());
        model.addAttribute("orderDetails", currentOrder.getOrderDetails());
        return "admin/order/detail";
    }

    @GetMapping("/admin/order/update/{id}")
    public String getOrderUpdatePage(Model model, @PathVariable long id) {
        Optional<Order> order = this.orderService.getOrderById(id);
        model.addAttribute("orderUpdate", order.get());
        return "admin/order/update";
    }

    @PostMapping("/admin/order/update")
    public String orderUpdate(Model model, @ModelAttribute("orderUpdate") Order od) {
        Order order = this.orderService.getOrderById(od.getId()).get();

        if (order != null) {
            order.setStatus(od.getStatus());

            this.orderService.updateOrderStatus(order);
        }
        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String getOrderDeletePage(Model model, @PathVariable long id) {
        Optional<Order> order = this.orderService.getOrderById(id);
        model.addAttribute("orderDelete", order.get());
        return "admin/order/delete";
    }

    @PostMapping("/admin/order/delete")
    public String deleteOrder(Model model, @ModelAttribute("orderDelete") Order od) {
        this.orderService.deleteOrderById(od.getId());
        return "redirect:/admin/order";
    }
}
