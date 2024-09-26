package vn.hoidanit.laptopshop.controller.client;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;

@Controller
public class ItemController {
    private final ProductService productService;

    public ItemController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping("/product/{id}")
    public String getProductPage(Model model, @PathVariable long id) {
        Optional<Product> product = this.productService.fetchProductById(id);
        model.addAttribute("productDetail", product.get());
        return "client/product/detail";
    }

}
