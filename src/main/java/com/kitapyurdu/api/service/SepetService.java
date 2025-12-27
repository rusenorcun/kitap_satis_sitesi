package com.kitapyurdu.api.service;

import com.kitapyurdu.api.dto.cart.CartView;
import com.kitapyurdu.api.repository.SepetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SepetService {

	private final SepetRepository repo;

	public SepetService(SepetRepository repo) {
		this.repo = repo;
	}

	public void add(int kullaniciId, int kitapId, int adet) {
		repo.spSepeteEkle(kullaniciId, kitapId, adet);
	}

	public void setQty(int kullaniciId, int kitapId, int qty) {
		Integer sepetId = repo.findActiveSepetId(kullaniciId);
		if (sepetId == null) return;
		repo.spSepetKalemGuncelle(sepetId, kitapId, Math.max(0, qty));
	}

	public void remove(int kullaniciId, int kitapId) {
		setQty(kullaniciId, kitapId, 0);
	}

	public void clear(int kullaniciId) {
		Integer sepetId = repo.findActiveSepetId(kullaniciId);
		if (sepetId == null) return;
		repo.clearSepet(sepetId);
	}

	public CartView view(int kullaniciId) {
		CartView v = new CartView();
		Integer sepetId = repo.findActiveSepetId(kullaniciId);
		v.setSepetId(sepetId);

		if (sepetId == null) {
			v.setTotalQty(0);
			v.setTotalPrice(BigDecimal.ZERO);
			return v;
		}

		var items = repo.listSepetItems(sepetId);
		v.setItems(items);

		int totalQty = 0;
		BigDecimal total = BigDecimal.ZERO;

		for (var it : items) {
			totalQty += it.getQty();
			if (it.getLineTotal() != null) total = total.add(it.getLineTotal());
		}

		v.setTotalQty(totalQty);
		v.setTotalPrice(total);
		return v;
	}

	public int cartCount(int kullaniciId) {
		return repo.getActiveCartCount(kullaniciId);
	}
}
