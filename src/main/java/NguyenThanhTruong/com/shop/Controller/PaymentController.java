package NguyenThanhTruong.com.shop.Controller;

import NguyenThanhTruong.com.shop.DTO.PaymentResDTO;
import NguyenThanhTruong.com.shop.config.Config;
import NguyenThanhTruong.com.shop.model.CartItem;
import NguyenThanhTruong.com.shop.service.OrderService;
import NguyenThanhTruong.com.shop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static NguyenThanhTruong.com.shop.config.Config.hmacSHA512;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private HttpSession session;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/create_payment")
    public ModelAndView createPayment(@RequestParam String customerName,
                                      @RequestParam String Diachi,
                                      @RequestParam String SDT,
                                      @RequestParam String Email,
                                      @RequestParam String Ghichu,
                                      @RequestParam String PTTT,
                                      HttpServletRequest request) throws UnsupportedEncodingException {
        session.setAttribute("customerName", customerName);
        session.setAttribute("Diachi", Diachi);
        session.setAttribute("SDT", SDT);
        session.setAttribute("Email", Email);
        session.setAttribute("Ghichu", Ghichu);
        session.setAttribute("PTTT", PTTT);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        List<CartItem> cartItems = cartService.getCartItems();

        long totalAmount = cartItems.stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity()) // Multiply price by quantity
                .sum();
        long amount = totalAmount * 100;

        String bankCode = "NCB";

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        return new ModelAndView("redirect:" + paymentUrl);
    }

    @GetMapping("/payment_return")
    public ModelAndView paymentReturn(@RequestParam Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");

        ModelAndView mav = new ModelAndView();

        if ("00".equals(vnp_ResponseCode)) {
            // Payment was successful, retrieve and save order details from session
            String customerName = (String) session.getAttribute("customerName");
            String diachi = (String) session.getAttribute("Diachi");
            String sdt = (String) session.getAttribute("SDT");
            String email = (String) session.getAttribute("Email");
            String ghichu = (String) session.getAttribute("Ghichu");
            String pttt = (String) session.getAttribute("PTTT");

            List<CartItem> cartItems = cartService.getCartItems();
            orderService.createOrder(customerName, diachi, sdt, email, ghichu, pttt, cartItems);

            mav.setViewName("redirect:/orders/confirmation"); // Redirect to confirmation page
        } else {
            mav.setViewName("redirect:/cart"); // Redirect to cart page
        }

        return mav;
    }

}
