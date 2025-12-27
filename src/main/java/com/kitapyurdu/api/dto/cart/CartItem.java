package com.kitapyurdu.api.dto.cart;

import java.math.BigDecimal;

public class CartItem {
	private int bookId;
	private String title;
	private String isbn;
	private BigDecimal unitPrice;
	private int qty;
	private int stock;
	private BigDecimal lineTotal;

	public int getBookId() { return bookId; }
	public void setBookId(int bookId) { this.bookId = bookId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getIsbn() { return isbn; }
	public void setIsbn(String isbn) { this.isbn = isbn; }

	public BigDecimal getUnitPrice() { return unitPrice; }
	public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

	public int getQty() { return qty; }
	public void setQty(int qty) { this.qty = qty; }

	public int getStock() { return stock; }
	public void setStock(int stock) { this.stock = stock; }

	public BigDecimal getLineTotal() { return lineTotal; }
	public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
