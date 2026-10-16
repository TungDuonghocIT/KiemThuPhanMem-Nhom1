package com.book.controller;
import com.book.model.Book;
import com.book.model.Category;
import com.book.service.BookService;
import com.book.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class BookController {


    private final BookService bookService;
    private final CategoryService categoryService;

    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + id));
        
        model.addAttribute("pageTitle", book.getTitle() + " - Chi tiết sách");
        model.addAttribute("book", book);
        model.addAttribute("relatedBooks", bookService.getBooksByCategory(book.getCategory().getId()));
        model.addAttribute("contentTemplate", "pages/book-detail");
        model.addAttribute("contentFragment", "content");
        return "layout";
    }
}
