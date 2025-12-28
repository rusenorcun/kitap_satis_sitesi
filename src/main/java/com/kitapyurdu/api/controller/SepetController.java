package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.service.SepetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * Alışveriş Sepeti Denetleyicisi
 * Sepete ürün ekleme, çıkarma, güncelleme ve temizleme işlemlerini yönetir
 */
@Controller
@RequestMapping("/sepet")
public class SepetController extends BaseController{

	private final SepetService sepetService;

	public SepetController(SepetService sepetService) {
		this.sepetService = sepetService;
	}

	/**
	 * Mevcut kullanıcının kimliğini Güvenlik objesinden al
	 * Yansıma (reflection) kullanarak farklı metod adlarını dener
	 */
	private int currentKullaniciId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) throw new IllegalStateException("Kimlik doğrulama bulunamadı");
		Object principal = auth.getPrincipal();

		// Farklı isimlerdeki getter metotlarını yansıma ile dene
		for (String m : new String[]{"getKullaniciId", "getUserId", "getId"}) {
			try {
				Method mm = principal.getClass().getMethod(m);
				Object v = mm.invoke(principal);
				if (v instanceof Integer) return (Integer) v;
				if (v instanceof Long) return ((Long) v).intValue();
			} catch (Exception ignored) {}
		}

		throw new IllegalStateException("Kullanıcı kimliği principal'dan okunamadı (CustomUserDetails getter'ını kontrol et).");
	}

	/**
	 * Alışveriş sepeti sayfasını göster
	 */
	@GetMapping
	public String sepet(Model model) {
		int kullaniciId = currentKullaniciId();
		var cart = sepetService.view(kullaniciId);

		model.addAttribute("cart", cart);
		model.addAttribute("cartCount", sepetService.cartCount(kullaniciId));
		return "sepet";
	}

	/**
	 * Sepete ürün ekle
	 */
	@PostMapping("/ekle")
	public String ekle(@RequestParam int bookId,
						@RequestParam(defaultValue = "1") int qty,
						@RequestHeader(value = "Referer", required = false) String referer) {

		int kullaniciId = currentKullaniciId();
		sepetService.add(kullaniciId, bookId, qty);
		return "redirect:" + (referer != null ? referer : "/katalog");
	}

	/**
	 * Sepetteki ürün miktarını güncelle
	 */
	@PostMapping("/guncelle")
	public String guncelle(@RequestParam int bookId,
							@RequestParam int qty) {

		int kullaniciId = currentKullaniciId();
		sepetService.setQty(kullaniciId, bookId, qty);
		return "redirect:/sepet";
	}

	/**
	 * Sepetten ürün çıkar
	 */
	@PostMapping("/sil")
	public String sil(@RequestParam int bookId) {
		int kullaniciId = currentKullaniciId();
		sepetService.remove(kullaniciId, bookId);
		return "redirect:/sepet";
	}

	/**
	 * Sepeti tamamen temizle
	 */
	@PostMapping("/temizle")
	public String temizle() {
		int kullaniciId = currentKullaniciId();
		sepetService.clear(kullaniciId);
		return "redirect:/sepet";
	}
}
