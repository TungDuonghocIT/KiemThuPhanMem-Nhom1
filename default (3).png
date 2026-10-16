package com.book.service;

import com.book.model.Book;
import com.book.model.Category;
import com.book.repository.BookRepository;
import com.book.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getLatestBooks() {
        return bookRepository.findTop8ByOrderByIdDesc();
    }

    public Optional<Book> getBookById(Integer id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooksByCategory(Integer categoryId) {
        return bookRepository.findByCategory_Id(categoryId);
    }

    public List<Book> getBooksByCategoryLimited(Integer categoryId) {
        return bookRepository.findTop5ByCategory_IdOrderByIdDesc(categoryId);
    }

    public Map<Category, List<Book>> getAllBooksCategorized() {
        Map<Category, List<Book>> result = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category cat : categories) {
            List<Book> books = bookRepository.findTop5ByCategory_IdOrderByIdDesc(cat.getId());
            if (!books.isEmpty()) {
                result.put(cat, books);
            }
        }
        return result;
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    public org.springframework.data.domain.Page<Book> getAllBooksPaged(org.springframework.data.domain.Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
}