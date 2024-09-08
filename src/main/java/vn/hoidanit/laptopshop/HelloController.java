package vn.hoidanit.laptopshop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Hello Spring Boot";
    }

    @GetMapping("/user")
    public String userPage() {
        return "only user can access thí page";
    }
}
