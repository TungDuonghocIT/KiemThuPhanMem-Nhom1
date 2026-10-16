package com.book.controller;
import com.book.model.Category;
import com.book.model.Order;
import com.book.model.User;
import com.book.service.CartService;
import com.book.service.CategoryService;
import com.book.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final CategoryService categoryService;

    public OrderController(OrderService orderService, CartService cartService, CategoryService categoryService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/checkout")
    public String checkoutForm(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        if (cartService.getCartItems(session).isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("pageTitle", "Thanh toán đơn hàng - BOOKSHOP");
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        
        // Pre-fill user contact info if present
        model.addAttribute("fullname", loggedUser.getFullname());
        model.addAttribute("phone", loggedUser.getPhone());
        model.addAttribute("address", loggedUser.getAddress());

        model.addAttribute("contentTemplate", "pages/checkout");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullname,
                             @RequestParam String phone,
                             @RequestParam String address,
                             HttpSession session,
                             Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        try {
            Order order = orderService.createOrder(session, loggedUser, fullname, phone, address);
            return "redirect:/order/" + order.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cartItems", cartService.getCartItems(session));
            model.addAttribute("cartTotal", cartService.getCartTotal(session));
            model.addAttribute("fullname", fullname);
            model.addAttribute("phone", phone);
            model.addAttribute("address", address);
            model.addAttribute("contentTemplate", "pages/checkout");
            model.addAttribute("contentFragment", "content");
            return "layout";
        }
    }

    @GetMapping("/history")
    public String orderHistory(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.getOrdersByUser(loggedUser.getId());
        model.addAttribute("pageTitle", "Lịch sử mua hàng - BOOKSHOP");
        model.addAttribute("orders", orders);
        model.addAttribute("contentTemplate", "pages/order-history");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Integer id, HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/auth/login";
        }

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + id));

        // Security check: only order owner or admin can view
        if (!order.getUser().getId().equals(loggedUser.getId()) && !"ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("contentTemplate", "pages/order-detail");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }
}
