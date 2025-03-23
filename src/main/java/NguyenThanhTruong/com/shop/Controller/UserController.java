package NguyenThanhTruong.com.shop.Controller;

import  NguyenThanhTruong.com.shop.model.User;
import  NguyenThanhTruong.com.shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public String showUserList(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "/users/user-list";
    }
    @PostMapping("/block/{username}")
    public String blockUser(@PathVariable String username) {
        userService.blockUser(username);
        return "redirect:/user"; // Redirect to user list after successful block
    }

    @PostMapping("/unblock/{username}")
    public String unblockUser(@PathVariable String username) {
        userService.unblockUser(username);
        return "redirect:/user"; // Redirect to user list after successful unblock
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào

        return "users/register";
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "users/profile";
    }
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") @Validated User updatedUser,
                                @RequestParam("imageProduct") MultipartFile imageProduct) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve user from database
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update user details
            currentUser.setPhone(updatedUser.getPhone());

            // Handle profile image upload if provided
            if (imageProduct != null && !imageProduct.isEmpty()) {
                try {
                    // Determine the directory where you want to save the uploaded file
                    File saveDir = new ClassPathResource("static/images").getFile();
                    // Generate a unique file name for the uploaded image
                    String newImageFileName = UUID.randomUUID().toString() + ".png";
                    // Construct the path to save the file
                    Path savePath = Paths.get(saveDir.getAbsolutePath() + File.separator + newImageFileName);
                    // Copy the file to the target location
                    Files.copy(imageProduct.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
                    // Set the file name to the user object
                    currentUser.setImage(newImageFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Save updated user details
            userService.save2(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception or validation errors
        }
        return "redirect:/profile";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate

                                   @NotNull BindingResult bindingResult, // Kết quả của quá

 Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho

        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }
}

