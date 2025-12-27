package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.service.SepetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.lang.reflect.Method;

@ControllerAdvice
public class GlobalModelAdvice extends BaseController{

	private final SepetService sepetService;

	public GlobalModelAdvice(SepetService sepetService) {
		this.sepetService = sepetService;
	}

	private Integer currentKullaniciIdOrNull() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) return null;
		Object principal = auth.getPrincipal();
		if (principal == null) return null;

		for (String m : new String[]{"getKullaniciId", "getUserId", "getId"}) {
			try {
				Method mm = principal.getClass().getMethod(m);
				Object v = mm.invoke(principal);
				if (v instanceof Integer) return (Integer) v;
				if (v instanceof Long) return ((Long) v).intValue();
			} catch (Exception ignored) {}
		}
		return null;
	}

	@ModelAttribute("cartCount")
	public int cartCount() {
		Integer kid = currentKullaniciIdOrNull();
		if (kid == null) return 0;
		return sepetService.cartCount(kid);
	}
}
