package com.book.model;

import com.book.model.Book;

public class CartItem {
    private Book book;
    private Integer quantity;

    public CartItem() {}

    public CartItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getAmount() {
        if (book == null || book.getPrice() == null) return 0.0;
        return book.getPrice() * quantity;
    }
}
