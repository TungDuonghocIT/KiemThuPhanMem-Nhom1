package com.book.controller;
import com.book.model.Category;
import com.book.model.User;
import com.book.service.BookService;
import com.book.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public HomeController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && "ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("pageTitle", "Trang chủ - BOOKSHOP");
        model.addAttribute("books", bookService.getLatestBooks());
        model.addAttribute("categorizedBooks", bookService.getAllBooksCategorized());
        model.addAttribute("contentTemplate", "pages/home");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }

    @GetMapping("/category/{id}")
    public String categoryBooks(@PathVariable Integer id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại: " + id));
        
        model.addAttribute("pageTitle", category.getName() + " - BOOKSHOP");
        model.addAttribute("currentCategory", category);
        model.addAttribute("books", bookService.getBooksByCategory(id));
        model.addAttribute("contentTemplate", "pages/home");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("pageTitle", "Tìm kiếm: " + (keyword != null ? keyword : "") + " - BOOKSHOP");
        model.addAttribute("keyword", keyword);
        model.addAttribute("books", bookService.searchBooks(keyword));
        model.addAttribute("contentTemplate", "pages/search");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }
}