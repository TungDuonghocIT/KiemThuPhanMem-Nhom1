package com.book.service;

import com.book.model.Book;
import com.book.model.CartItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    @SuppressWarnings("unchecked")
    public Map<Integer, CartItem> getCart(HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Book book, int quantity) {
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.containsKey(book.getId())) {
            CartItem item = cart.get(book.getId());
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cart.put(book.getId(), new CartItem(book, quantity));
        }
        updateSessionCartStats(session);
    }

    public void updateQuantity(HttpSession session, Integer bookId, int quantity) {
        Map<Integer, CartItem> cart = getCart(session);
        if (cart.containsKey(bookId)) {
            if (quantity <= 0) {
                cart.remove(bookId);
            } else {
                cart.get(bookId).setQuantity(quantity);
            }
        }
        updateSessionCartStats(session);
    }

    public void removeFromCart(HttpSession session, Integer bookId) {
        Map<Integer, CartItem> cart = getCart(session);
        cart.remove(bookId);
        updateSessionCartStats(session);
    }

    public void clearCart(HttpSession session) {
        Map<Integer, CartItem> cart = getCart(session);
        cart.clear();
        updateSessionCartStats(session);
    }

    public Collection<CartItem> getCartItems(HttpSession session) {
        return getCart(session).values();
    }

    public Double getCartTotal(HttpSession session) {
        return getCartItems(session).stream()
                .mapToDouble(CartItem::getAmount)
                .sum();
    }

    public void updateSessionCartStats(HttpSession session) {
        Collection<CartItem> items = getCartItems(session);
        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        double totalAmount = getCartTotal(session);
        session.setAttribute("cartCount", totalItems);
        session.setAttribute("cartTotal", totalAmount);
    }
}