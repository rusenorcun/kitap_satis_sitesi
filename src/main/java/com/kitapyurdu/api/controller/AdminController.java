package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.repository.AdminRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController{

	private final AdminRepository adminRepo;

	public AdminController(AdminRepository adminRepo) {
		this.adminRepo = adminRepo;
	}

	// panel mesaj yönlendirme yardımcı metodu
	private String redirectPanelMsg(String msg) {
		String safe = (msg == null) ? "" : msg;
		return "redirect:/admin/panel?mesaj=" + UriUtils.encodeQueryParam(safe, StandardCharsets.UTF_8);
	}

	// ----------------- Admin Panel  -----------------
	@GetMapping("/panel")
	public String panel(@RequestParam(required = false) String q,
						@RequestParam(required = false) Integer yayineviId,
						@RequestParam(required = false) String mesaj,
						Model model) {
		// Parametreleri modele ekleme
		model.addAttribute("mesaj", mesaj);
		model.addAttribute("q", q);
		model.addAttribute("seciliYayineviId", yayineviId);

		model.addAttribute("toplamStok", adminRepo.toplamStok());
		model.addAttribute("toplamKitap", adminRepo.toplamKitap());
		model.addAttribute("yayineviSayisi", adminRepo.yayineviSayisi());
		model.addAttribute("bekleyenIs", 0);

		model.addAttribute("yayinevleri", adminRepo.yayineviOptions());
		model.addAttribute("stokRows", adminRepo.stokRows(q, yayineviId));

		model.addAttribute("users", adminRepo.listUsers());
		model.addAttribute("adminName", "Admin");

		return "admin_panel";
	}

	// ----------------- Rol işlemleri -----------------
	@PostMapping("/users/role/set")
	public String roleSet(@RequestParam String username,
						  @RequestParam String role) {
		try {
			adminRepo.rolVer(username == null ? "" : username.trim(),
					role == null ? "" : role.trim());
			return redirectPanelMsg("Rol güncellendi");
		} catch (Exception e) {
			return redirectPanelMsg("Hata: Rol güncellenemedi");
		}
	}

	// ----------------- Stok/Durum -----------------
	@PostMapping("/stok/update")
	public String stokUpdate(@RequestParam Integer kitapId,
							 @RequestParam Integer stok,
							 @RequestParam boolean durum) {
		try {
			if (stok == null || stok < 0) stok = 0;
			adminRepo.stokDurumGuncelle(kitapId, stok, durum);
			return redirectPanelMsg("Stok güncellendi");
		} catch (Exception e) {
			return redirectPanelMsg("Hata: Stok güncellenemedi");
		}
	}

	// ----------------- Kitap sil (paneldeki hızlı işlemler) -----------------
	@PostMapping("/kitap/delete")
	public String kitapDelete(@RequestParam Integer kitapId) {
		try {
			// sp_KitapSil artık stabil
			adminRepo.kitapSil(kitapId);
			return redirectPanelMsg("Kitap silindi");
		} catch (Exception e) {
			return redirectPanelMsg("Hata: Kitap silinemedi");
		}
	}
	@GetMapping
	public String adminHome(@RequestParam(required = false) String mesaj, Model model) {
		model.addAttribute("mesaj", mesaj);
	
		model.addAttribute("toplamStok", adminRepo.toplamStok());
		model.addAttribute("toplamKitap", adminRepo.toplamKitap());
		model.addAttribute("aktifKitap", adminRepo.aktifKitap());
		model.addAttribute("yayineviSayisi", adminRepo.yayineviSayisi());
		model.addAttribute("bekleyenIs", 0);
	
		// Operasyon verileri
		model.addAttribute("lowStock", adminRepo.lowStockRows(5, 8));  // stok <= 5, top 8
		model.addAttribute("pasifKitapSayisi", adminRepo.pasifKitapSayisi());
		model.addAttribute("emptyPublishers", adminRepo.emptyPublishers(8)); // top 8
	
		return "admin_home";
	}
}
