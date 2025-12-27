package com.kitapyurdu.api.dto.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookListItem {

    public int id;
    public String isbn;
    public String title;
    public BigDecimal price;
    public int stock;
    public Integer publisherId;
    public String publisherName;
    public String categoriesText;
    public LocalDateTime createdAt;
    public String coverUrl;


    // --- GETTERS 
    public Integer getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }

    public Integer getPublisherId() { return publisherId; }
    public String getPublisherName() { return publisherName; }

    public String getCategoriesText() { return categoriesText; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getCoverUrl() { return coverUrl; }

    // --- SETTERS 
    public void setId(Integer id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }

    public void setPublisherId(Integer publisherId) { this.publisherId = publisherId; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public void setCategoriesText(String categoriesText) { this.categoriesText = categoriesText; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}
