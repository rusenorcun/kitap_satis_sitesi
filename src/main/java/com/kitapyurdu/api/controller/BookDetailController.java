package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.service.BookDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
public class BookDetailController extends BaseController{

	private final BookDetailService service;

	public BookDetailController(BookDetailService service) {
		this.service = service;
	}

	@GetMapping("/kitap/{kitapId}")
	public String bookDetail(@PathVariable int kitapId, Authentication auth, Model model) {
		String username = (auth == null ? null : auth.getName());
		Integer kullaniciId = service.resolveUserId(username);

		model.addAttribute("book", service.getDetail(kitapId, kullaniciId));
		model.addAttribute("images", service.images(kitapId));
		model.addAttribute("authors", service.authors(kitapId));
		model.addAttribute("categories", service.categories(kitapId));
		model.addAttribute("reviews", service.reviews(kitapId));

		return "kitap-detay";
	}

	@PostMapping("/kitap/{kitapId}/favori")
	public String toggleFavorite(@PathVariable int kitapId, Authentication auth) {
		if (auth == null)
			return "redirect:/login";

		Integer kullaniciId = service.resolveUserId(auth.getName());
		if (kullaniciId == null)
			return "redirect:/login";

		service.toggleFavorite(kullaniciId, kitapId);
		return "redirect:/kitap/" + kitapId;
	}

	@PostMapping("/kitap/{kitapId}/sepete-ekle")
	public String addToCart(@PathVariable int kitapId,
							@RequestParam(name = "adet", defaultValue = "1") int adet,
							Authentication auth) {
		if (auth == null)
			return "redirect:/login";

		Integer kullaniciId = service.resolveUserId(auth.getName());
		if (kullaniciId == null)
			return "redirect:/login";

		service.addToCart(kullaniciId, kitapId, adet);
		return "redirect:/sepet";
	}
}