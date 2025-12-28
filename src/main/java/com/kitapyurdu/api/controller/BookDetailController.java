package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.service.BookDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

/**
 * Kitap Detay Denetleyicisi
 * Seçilen kitabın ayrıntılarını gösterir ve ilgili işlemler yapar
 */
@Controller
public class BookDetailController extends BaseController{

	private final BookDetailService service;

	public BookDetailController(BookDetailService service) {
		this.service = service;
	}

	/**
	 * Kitabın ayrıntı sayfasını göster
	 * Resimler, yazarlar, kategoriler ve yorumları ekle
	 */
	@GetMapping("/kitap/{kitapId}")
	public String bookDetail(@PathVariable int kitapId, Authentication auth, Model model) {
		// Giriş yapılan kullanıcı adından kimliği al
		String username = (auth == null ? null : auth.getName());
		Integer kullaniciId = service.resolveUserId(username);

		// Kitap bilgisini ve ilişkili verileri ekle
		model.addAttribute("book", service.getDetail(kitapId, kullaniciId));
		model.addAttribute("images", service.images(kitapId));
		model.addAttribute("authors", service.authors(kitapId));
		model.addAttribute("categories", service.categories(kitapId));
		model.addAttribute("reviews", service.reviews(kitapId));

		return "kitap-detay";
	}

	/**
	 * Kitabı favorilere ekle/çıkar
	 */
	@PostMapping("/kitap/{kitapId}/favori")
	public String toggleFavorite(@PathVariable int kitapId, Authentication auth) {
		// Giriş yapılmamışsa login sayfasına yönlendir
		if (auth == null)
			return "redirect:/login";

		Integer kullaniciId = service.resolveUserId(auth.getName());
		if (kullaniciId == null)
			return "redirect:/login";

		// Favori durumunu değiştir
		service.toggleFavorite(kullaniciId, kitapId);
		return "redirect:/kitap/" + kitapId;
	}

	/**
	 * Kitabı alışveriş sepetine ekle
	 */
	@PostMapping("/kitap/{kitapId}/sepete-ekle")
	public String addToCart(@PathVariable int kitapId,
							@RequestParam(name = "adet", defaultValue = "1") int adet,
							Authentication auth) {
		// Giriş yapılmamışsa login sayfasına yönlendir
		if (auth == null)
			return "redirect:/login";

		Integer kullaniciId = service.resolveUserId(auth.getName());
		if (kullaniciId == null)
			return "redirect:/login";

		// Sepete ekle
		service.addToCart(kullaniciId, kitapId, adet);
		return "redirect:/sepet";
	}
}