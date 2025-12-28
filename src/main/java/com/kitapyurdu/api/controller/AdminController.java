package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.repository.AdminRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/**
 * Admin Denetleyicisi
 * Admin paneli sayfası ve hızlı işlemler (silme, güncelleme) için kontroller sağlar
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController{

	private final AdminRepository adminRepo;

	public AdminController(AdminRepository adminRepo) {
		this.adminRepo = adminRepo;
	}

	/**
	 * Admin panel mesaj yönlendirme yardımcı metodu
	 * Mesajı URL parametresi olarak kodlayıp panele geri yönlendirir
	 */
	private String redirectPanelMsg(String msg) {
		String safe = (msg == null) ? "" : msg;
		return "redirect:/admin/panel?mesaj=" + UriUtils.encodeQueryParam(safe, StandardCharsets.UTF_8);
	}

	// ==================== ADMIN PANEL SAYFASI ====================
	/**
	 * Admin panel ana sayfası
	 * Kitaplar, yayınevler ve kullanıcı listesini gösterir
	 */
	@GetMapping("/panel")
	public String panel(@RequestParam(required = false) String q,
						@RequestParam(required = false) Integer yayineviId,
						@RequestParam(required = false) String mesaj,
						Model model) {
		// Arama ve filtre parametrelerini ekle
		model.addAttribute("mesaj", mesaj);
		model.addAttribute("q", q);
		model.addAttribute("seciliYayineviId", yayineviId);

		// İstatistik verilerini ekle
		model.addAttribute("toplamStok", adminRepo.toplamStok());
		model.addAttribute("toplamKitap", adminRepo.toplamKitap());
		model.addAttribute("yayineviSayisi", adminRepo.yayineviSayisi());
		model.addAttribute("bekleyenIs", 0);

		// Filtre seçeneklerini ve listeleri ekle
		model.addAttribute("yayinevleri", adminRepo.yayineviOptions());
		model.addAttribute("stokRows", adminRepo.stokRows(q, yayineviId));

		// Kullanıcı listesini ekle
		model.addAttribute("users", adminRepo.listUsers());
		model.addAttribute("adminName", "Admin");

		return "admin_panel";
	}

	// ==================== ROL İŞLEMLERİ ====================
	/**
	 * Kullanıcı rolü güncelle
	 * Seçilen kullanıcının rolünü değiştirir
	 */
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

	// ==================== STOK VE DURUM GÜNCELLEMESİ ====================
	/**
	 * Kitap stok ve durumunu güncelle
	 * Seçilen kitabın stok sayısını ve aktif/pasif durumunu değiştirir
	 */
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

	// ==================== KİTAP SİLME ====================
	/**
	 * Kitabı sil
	 * Seçilen kitabı veritabanından kaldırır (paneldeki hızlı işlem)
	 */
	@PostMapping("/kitap/delete")
	public String kitapDelete(@RequestParam Integer kitapId) {
		try {
			adminRepo.kitapSil(kitapId);
			return redirectPanelMsg("Kitap silindi");
		} catch (Exception e) {
			return redirectPanelMsg("Hata: Kitap silinemedi");
		}
	}

	// ==================== ADMIN ANA SAYFA ====================
	/**
	 * Admin ana sayfası
	 * İstatistikler, düşük stoklu kitaplar ve boş yayınevleri gösterir
	 */
	@GetMapping
	public String adminHome(@RequestParam(required = false) String mesaj, Model model) {
		model.addAttribute("mesaj", mesaj);
	
		// Genel istatistikler
		model.addAttribute("toplamStok", adminRepo.toplamStok());
		model.addAttribute("toplamKitap", adminRepo.toplamKitap());
		model.addAttribute("aktifKitap", adminRepo.aktifKitap());
		model.addAttribute("yayineviSayisi", adminRepo.yayineviSayisi());
		model.addAttribute("bekleyenIs", 0);
	
		// Uyarı verileri - yönetici dikkatini gerektiren öğeler
		model.addAttribute("lowStock", adminRepo.lowStockRows(5, 8));  // Stok <= 5, en fazla 8 satır
		model.addAttribute("pasifKitapSayisi", adminRepo.pasifKitapSayisi());
		model.addAttribute("emptyPublishers", adminRepo.emptyPublishers(8)); // Kitapsız yayınevler, en fazla 8 satır
	
		return "admin_home";
	}
}
