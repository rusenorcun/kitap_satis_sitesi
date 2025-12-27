package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.service.SepetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

@Controller
@RequestMapping("/sepet")
public class SepetController extends BaseController{

	private final SepetService sepetService;

	public SepetController(SepetService sepetService) {
		this.sepetService = sepetService;
	}

	// Geçerli kullanıcının ID'sini Authentication objesinden yansıma ile alır
	private int currentKullaniciId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) throw new IllegalStateException("Auth yok");
		Object principal = auth.getPrincipal();

		// Yansıma ile farklı isimlerdeki getter metotlarını dene
		for (String m : new String[]{"getKullaniciId", "getUserId", "getId"}) {
			try {
				Method mm = principal.getClass().getMethod(m);
				Object v = mm.invoke(principal);
				if (v instanceof Integer) return (Integer) v;
				if (v instanceof Long) return ((Long) v).intValue();
			} catch (Exception ignored) {}
		}

		throw new IllegalStateException("KullaniciId principal'dan okunamadı (CustomUserDetails getter kontrol et).");
	}

	@GetMapping
	public String sepet(Model model) {
		int kullaniciId = currentKullaniciId();
		var cart = sepetService.view(kullaniciId);

		model.addAttribute("cart", cart);
		model.addAttribute("cartCount", sepetService.cartCount(kullaniciId));
		return "sepet";
	}

	@PostMapping("/ekle")
	public String ekle(@RequestParam int bookId,
						@RequestParam(defaultValue = "1") int qty,
						@RequestHeader(value = "Referer", required = false) String referer) {

		int kullaniciId = currentKullaniciId();
		sepetService.add(kullaniciId, bookId, qty);
		return "redirect:" + (referer != null ? referer : "/katalog");
	}

	@PostMapping("/guncelle")
	public String guncelle(@RequestParam int bookId,
							@RequestParam int qty) {

		int kullaniciId = currentKullaniciId();
		sepetService.setQty(kullaniciId, bookId, qty);
		return "redirect:/sepet";
	}

	@PostMapping("/sil")
	public String sil(@RequestParam int bookId) {
		int kullaniciId = currentKullaniciId();
		sepetService.remove(kullaniciId, bookId);
		return "redirect:/sepet";
	}

	@PostMapping("/temizle")
	public String temizle() {
		int kullaniciId = currentKullaniciId();
		sepetService.clear(kullaniciId);
		return "redirect:/sepet";
	}
	
}
