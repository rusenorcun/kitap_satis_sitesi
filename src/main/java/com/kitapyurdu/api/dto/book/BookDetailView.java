package com.kitapyurdu.api.dto.book;

import java.math.BigDecimal;

public class BookDetailView {
	private int kitapId;
	private String title;
	private BigDecimal price;
	private int stock;
	private Integer pageCount;
	private Integer publishYear;
	private String description;
	private Integer publisherId;
	private String publisherName;
	private BigDecimal avgRating;
	private int reviewCount;
	private boolean isFavorite;

	public int getKitapId() { return kitapId; }
	public void setKitapId(int kitapId) { this.kitapId = kitapId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }

	public int getStock() { return stock; }
	public void setStock(int stock) { this.stock = stock; }

	public Integer getPageCount() { return pageCount; }
	public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

	public Integer getPublishYear() { return publishYear; }
	public void setPublishYear(Integer publishYear) { this.publishYear = publishYear; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public Integer getPublisherId() { return publisherId; }
	public void setPublisherId(Integer publisherId) { this.publisherId = publisherId; }

	public String getPublisherName() { return publisherName; }
	public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

	public BigDecimal getAvgRating() { return avgRating; }
	public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }

	public int getReviewCount() { return reviewCount; }
	public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

	public boolean isFavorite() { return isFavorite; }
	public void setFavorite(boolean favorite) { isFavorite = favorite; }
}