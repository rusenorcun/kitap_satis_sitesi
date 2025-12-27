package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.auth.RegisterRequest;
import com.kitapyurdu.api.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/auth")
public class RegisterController extends BaseController{

	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
	private final RegisterService registerService;

	public RegisterController(RegisterService registerService) {
		this.registerService = registerService;
	}

	@PostMapping("/register")
	public String register(@ModelAttribute RegisterRequest req) {
		try {
			registerService.register(req);
			return "redirect:/login?kayit=1";
		} catch (Exception ex) {
			log.error("Register failed. username={}, email={}", req.getUsername(), req.getEmail(), ex);
			String msg = ex.getMessage() == null ? "Kayıt başarısız." : ex.getMessage();
			String enc = URLEncoder.encode(msg, StandardCharsets.UTF_8);
			return "redirect:/login?tab=register&reg_hata=1&mesaj=" + enc;
		}
	}
}
