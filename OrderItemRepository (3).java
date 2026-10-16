package com.book.controller;
import com.book.model.Book;
import com.book.model.Category;
import com.book.service.BookService;
import com.book.service.CartService;
import com.book.service.CategoryService;
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
@RequestMapping("/cart")
public class CartController {

	

    private final CartService cartService;
    private final BookService bookService;
    private final CategoryService categoryService;

    public CartController(CartService cartService, BookService bookService, CategoryService categoryService) {
        this.cartService = cartService;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("pageTitle", "Giỏ hàng của bạn - BOOKSHOP");
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("contentTemplate", "pages/cart");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Integer bookId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + bookId));
        
        cartService.addToCart(session, book, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Integer bookId,
                                 @RequestParam Integer quantity,
                                 HttpSession session) {
        cartService.updateQuantity(session, bookId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{bookId}")
    public String removeFromCart(@PathVariable Integer bookId, HttpSession session) {
        cartService.removeFromCart(session, bookId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }
}
