package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> arrUsers = this.userService.getAllUsersByEmail("thientaiduyanh@gmail.com");
        System.out.println(arrUsers);
        model.addAttribute("test", "test");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("userDetail", user);
        return "admin/user/detail";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String updateUserByIdPage(Model model, @PathVariable long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("newUser", user);
        return "admin/user/update";
    }

    @RequestMapping("/admin/user/delete/{id}")
    public String deleteUserById(Model model, @PathVariable long id) {
        this.userService.deleteById(id);
        return "redirect:/admin/user";
    }

    @RequestMapping(value = "/admin/user/update", method = RequestMethod.POST)
    public String updateUserPage(Model model, @ModelAttribute("updateUser") User duyanh) {
        User currentUser = this.userService.getUserById(duyanh.getId());
        if (currentUser != null) {
            currentUser.setAddress(duyanh.getAddress());
            currentUser.setFullName(duyanh.getFullName());
            currentUser.setPhone(duyanh.getPhone());
            currentUser.setRole(this.userService.getRoleByName(duyanh.getRole().getName()));
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @RequestMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        // String test = this.userService.handleHello();
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model, @ModelAttribute("newUser") User duyanh,
            @RequestParam("hoidanitFile") MultipartFile file) {

        String avatar = this.uploadService.handlerSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(duyanh.getPassword());
        duyanh.setAvatar(avatar);
        duyanh.setPassword(hashPassword);
        duyanh.setRole(this.userService.getRoleByName(duyanh.getRole().getName()));
        this.userService.handleSaveUser(duyanh);
        return "redirect:/admin/user";
    }
}
