package com.book.service;

import com.book.model.Book;
import com.book.model.CartItem;
import com.book.model.Order;
import com.book.model.OrderItem;
import com.book.model.User;
import com.book.repository.BookRepository;
import com.book.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final com.book.repository.OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, CartService cartService, com.book.repository.OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public Order createOrder(HttpSession session, User user, String fullname, String phone, String address) {
        Collection<CartItem> cartItems = cartService.getCartItems(session);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setFullname(fullname);
        order.setPhone(phone);
        order.setAddress(address);
        order.setTotalAmount(cartService.getCartTotal(session));

        for (CartItem item : cartItems) {
            Book book = item.getBook();
            // Optional: update quantity
            if (book.getQuantity() != null) {
                int newQty = book.getQuantity() - item.getQuantity();
                if (newQty < 0) {
                    throw new RuntimeException("Sách '" + book.getTitle() + "' đã hết hàng hoặc không đủ số lượng!");
                }
                book.setQuantity(newQty);
                bookRepository.save(book);
            }

            OrderItem orderItem = new OrderItem(order, book, item.getQuantity(), book.getPrice());
            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(session);
        return savedOrder;
    }

    public List<Order> getOrdersByUser(Integer userId) {
        return orderRepository.findByUser_IdOrderByOrderDateDesc(userId);
    }

    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    public org.springframework.data.domain.Page<Order> getAllOrdersPaged(org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public List<Object[]> getRevenueByMonth(int year) {
        return orderRepository.getRevenueByMonth(year);
    }

    public List<Object[]> getOrderCountByStatus() {
        return orderRepository.getOrderCountByStatus();
    }

    public Double getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    public Integer getTotalBooksSold() {
        return orderItemRepository.getTotalBooksSold();
    }
}
