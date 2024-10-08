package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;
    private final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UploadService uploadService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam("page") Optional<String> PageOptional) {
        int page = 1;
        try {
            if (PageOptional.isPresent())
                page = Integer.parseInt(PageOptional.get());

        } catch (Exception e) {

        }
        Pageable pageable = PageRequest.of(page - 1, 2);
        Page<User> prs = this.userService.getAllUsers(pageable);
        List<User> listUsers = prs.getContent();
        model.addAttribute("users", listUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", prs.getTotalPages());
        return "admin/user/show";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("userDetail", user);
        return "admin/user/detail";
    }

    @GetMapping("/admin/user/update/{id}")
    public String updateUserByIdPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("newUser", user);
        return "admin/user/update";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String deleteUserByIdPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newUser", new User());
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String deleteUserById(Model model, @ModelAttribute("newUser") User duyanh) {
        this.userService.deleteById(duyanh.getId());
        return "redirect:/admin/user";
    }

    @PostMapping(value = "/admin/user/update")
    public String updateUserPage(Model model, @ModelAttribute("newUser") @Valid User duyanh,
            BindingResult newUserBindingResult,
            @RequestParam("hoidanitFile") MultipartFile file) {

        if (newUserBindingResult.hasErrors()) {
            return "/admin/user/update";
        }
        User currentUser = this.userService.getUserById(duyanh.getId());
        if (currentUser != null) {
            if (!file.isEmpty()) {
                String img = this.uploadService.handlerSaveUploadFile(file, "avatar");
                currentUser.setAvatar(img);
            }
            String hashPassword = this.passwordEncoder.encode(duyanh.getPassword());
            currentUser.setPassword(hashPassword);
            currentUser.setFullName(duyanh.getFullName());
            currentUser.setPhone(duyanh.getPhone());
            currentUser.setRole(this.userService.getRoleByName(duyanh.getRole().getName()));
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        // String test = this.userService.handleHello();
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") @Valid User duyanh,
            BindingResult newUserBindingResult,
            @RequestParam("hoidanitFile") MultipartFile file) {
        List<FieldError> errors = newUserBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error.getField() + " - " + error.getDefaultMessage());
        }
        if (newUserBindingResult.hasErrors()) {
            return "/admin/user/create";
        }
        // validate
        String avatar = this.uploadService.handlerSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(duyanh.getPassword());
        duyanh.setAvatar(avatar);
        duyanh.setPassword(hashPassword);
        duyanh.setRole(this.userService.getRoleByName(duyanh.getRole().getName()));
        this.userService.handleSaveUser(duyanh);
        return "redirect:/admin/user";
    }
}
