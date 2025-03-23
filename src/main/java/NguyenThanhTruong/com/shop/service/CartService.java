package NguyenThanhTruong.com.shop.service;

import NguyenThanhTruong.com.shop.model.CartItem;
import NguyenThanhTruong.com.shop.model.Product;
import NguyenThanhTruong.com.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.util.ArrayList;
import java.util.List;
@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    @Autowired
    private ProductRepository productRepository;


    public void addToCart(Long productId, int quantity) {
        // Find the cart item if it already exists
        CartItem existingCartItem = findCartItemByProductId(productId);
        if (existingCartItem != null) {
            // If the product is already in the cart, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            // If the product is not in the cart, add it to the cart
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
            CartItem newCartItem = new CartItem(product, quantity);
            cartItems.add(newCartItem);
        }
    }
    private CartItem findCartItemByProductId(Long productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(productId)) {
                return cartItem;
            }
        }
        return null;
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    public void updateQuantity(Long productId, int quantity) {
        CartItem item = cartItems.stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart: " + productId));
        item.setQuantity(quantity);
    }
    public void clearCart() {
        cartItems.clear();
    }
}

