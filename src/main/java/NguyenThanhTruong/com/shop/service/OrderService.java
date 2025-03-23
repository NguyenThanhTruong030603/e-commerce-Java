package NguyenThanhTruong.com.shop.service;

import NguyenThanhTruong.com.shop.model.CartItem;
import NguyenThanhTruong.com.shop.model.Order;
import NguyenThanhTruong.com.shop.model.OrderDetail;
import NguyenThanhTruong.com.shop.model.Product;
import NguyenThanhTruong.com.shop.repository.OrderDetailRepository;
import NguyenThanhTruong.com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService; // Assuming you have a CartService
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public long getTotalRevenue() {
        return orderRepository.sumTotalAmount();
    }
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public List<Order> findOrdersByCustomerName(String username) {
        return orderRepository.findOrdersByCustomerName(username);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
    @Transactional
    public Order createOrder(String customerName, String Diachi, String SDT, String Email, String Ghichu, String PTTT, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setEmail(Email);
        order.setDiachi(Diachi);
        order.setSDT(SDT);
        order.setGhichu(Ghichu);
        order.setPTTT(PTTT);

        // Create order details for each cart item
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }

        // Calculate and set the total amount from cart items
        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
        order.setTotalamount(totalAmount);

        // Save order to persist totalAmount
        order = orderRepository.save(order);

        // Optionally clear the cart after order placement
        cartService.clearCart();

        return order;
    }

}

