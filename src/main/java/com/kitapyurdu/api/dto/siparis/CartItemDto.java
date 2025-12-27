package com.kitapyurdu.api.dto.siparis;

import java.math.BigDecimal;

public class CartItemDto {

	private int kitapId;
	private String title;
	private BigDecimal price;
	private int quantity;
	private BigDecimal lineTotal;

	public CartItemDto() { }

	public CartItemDto(int kitapId, String title, BigDecimal price, int quantity, BigDecimal lineTotal) {
		this.kitapId = kitapId;
		this.title = title;
		this.price = price;
		this.quantity = quantity;
		this.lineTotal = lineTotal;
	}
	// Getters and Setters
	public int getKitapId() { return kitapId; }
	public void setKitapId(int kitapId) { this.kitapId = kitapId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }

	public int getQuantity() { return quantity; }
	public void setQuantity(int quantity) { this.quantity = quantity; }

	public BigDecimal getLineTotal() { return lineTotal; }
	public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
