package com.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.book.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    
    List<User> findByRole(String role);
    
    long countByRole(String role);
    
    org.springframework.data.domain.Page<User> findByRole(String role, org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'USER' AND " +
           "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchCustomers(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE u.role = 'USER' AND " +
           "(LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<User> searchCustomers(@Param("keyword") String keyword, org.springframework.data.domain.Pageable pageable);
}