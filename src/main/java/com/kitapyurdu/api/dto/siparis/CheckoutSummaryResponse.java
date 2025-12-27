package com.kitapyurdu.api.dto.siparis;

import java.util.List;

public class CheckoutSummaryResponse {

	private List<CartItemDto> items;
	private CartSummaryDto totals;

	public CheckoutSummaryResponse() { }

	public CheckoutSummaryResponse(List<CartItemDto> items, CartSummaryDto totals) {
		this.items = items;
		this.totals = totals;
	}
	// Getters and Setters
	public List<CartItemDto> getItems() { return items; }
	public void setItems(List<CartItemDto> items) { this.items = items; }

	public CartSummaryDto getTotals() { return totals; }
	public void setTotals(CartSummaryDto totals) { this.totals = totals; }
}
