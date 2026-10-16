package com.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.book.model.Book;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByCategory_Id(Integer categoryId);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String titleKeyword, String authorKeyword);
    List<Book> findTop8ByOrderByIdDesc();
    List<Book> findTop5ByCategory_IdOrderByIdDesc(Integer categoryId);
}