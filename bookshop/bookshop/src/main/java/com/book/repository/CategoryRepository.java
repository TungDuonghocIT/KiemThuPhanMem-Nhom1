package com.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.book.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}