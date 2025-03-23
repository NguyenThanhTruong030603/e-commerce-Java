package NguyenThanhTruong.com.shop.Controller;

import NguyenThanhTruong.com.shop.model.*;
import NguyenThanhTruong.com.shop.service.CartService;
import NguyenThanhTruong.com.shop.service.OrderService;
import NguyenThanhTruong.com.shop.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @GetMapping("/checkout")
    public String checkout(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "/cart/checkout";
    }
    @PostMapping("/submit")
    public String submitOrder(String customerName,String Diachi,String SDT,String Email,String Ghichu,String PTTT) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName,Diachi,SDT,Email,Ghichu,PTTT, cartItems);
        return "redirect:/orders/confirmation";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "order/orderDetails";
    }
    @GetMapping()
    public String listOrders(Model model) {
        long totalRevenue = orderService.getTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue);
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/order-list"; // Thymeleaf template to display orders
    }
    @GetMapping("/my-orders")
    public String listMyOrders(Model model) {
        // Lấy thông tin đăng nhập của người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Tìm người dùng từ username
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm các đơn hàng có customerName giống với username của người dùng
        List<Order> orders = orderService.findOrdersByCustomerName(username);

        // Đưa dữ liệu vào model để hiển thị trên view
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);

        return "order/Myorder-list"; // Thymeleaf template để hiển thị danh sách đơn hàng
    }


    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {

        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}


