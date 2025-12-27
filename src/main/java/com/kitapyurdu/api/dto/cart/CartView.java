package com.kitapyurdu.api.dto.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartView {
	private List<CartItem> items = new ArrayList<>();
	private int totalQty;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private Integer sepetId;

	public List<CartItem> getItems() { return items; }
	public void setItems(List<CartItem> items) { this.items = items; }

	public int getTotalQty() { return totalQty; }
	public void setTotalQty(int totalQty) { this.totalQty = totalQty; }

	public BigDecimal getTotalPrice() { return totalPrice; }
	public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

	public Integer getSepetId() { return sepetId; }
	public void setSepetId(Integer sepetId) { this.sepetId = sepetId; }
}
