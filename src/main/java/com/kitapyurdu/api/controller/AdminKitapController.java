package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.admin.AdminKitapForm;
import com.kitapyurdu.api.repository.AdminRepository;
import com.kitapyurdu.api.repository.KitapRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

/**
 * Admin Kitap Denetleyicisi
 * Kitapların listelenme, ekleme, güncelleme ve yönetimi işlemlerini sağlar
 */
@Controller
@RequestMapping("/admin/kitap")
public class AdminKitapController extends BaseController{

    private final KitapRepository kitapRepo;
    private final AdminRepository adminRepo;

    public AdminKitapController(KitapRepository kitapRepo, AdminRepository adminRepo) {
        this.kitapRepo = kitapRepo;
        this.adminRepo = adminRepo;
    }

    /**
     * Arama yapıldı mesajını ekleyip sayfaya yönlendir
     */
    private String redirectMsg(String msg, String returnScroll) {
        String safe = (msg == null) ? "" : msg;
        String scroll = (returnScroll == null || returnScroll.isBlank()) ? "" : ("&returnScroll=" + UriUtils.encodeQueryParam(returnScroll, StandardCharsets.UTF_8));
        return "redirect:/admin/kitap?mesaj=" + UriUtils.encodeQueryParam(safe, StandardCharsets.UTF_8) + scroll;
    }

    /**
     * Düzenleme modunda mesajı ekleyip yönlendir
     */
    private String redirectMsgEdit(String msg, Long editId) {
        return "redirect:/admin/kitap?editId=" + editId + "&mesaj=" + UriUtils.encodeQueryParam(msg, StandardCharsets.UTF_8);
    }

    /**
     * Kitap listesi sayfasını göster
     * Arama, filtreleme ve düzenleme formunu destekler
     */
    @GetMapping
    public String page(@RequestParam(required = false) String q,
                    @RequestParam(required = false) Integer yayineviId,
                    @RequestParam(required = false) Long editId,
                    @RequestParam(required = false) String mesaj,
                    @RequestParam(required = false) String scroll,
                    Model model) {

        // Arama ve filtre parametrelerini ekle
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("q", q);
        model.addAttribute("seciliYayineviId", yayineviId);

        // Filtrelenmiş kitap listesini ekle
        model.addAttribute("kitaplar", kitapRepo.list(q, yayineviId));
        model.addAttribute("yayinevleri", adminRepo.yayineviOptions());

        // Düzenleme formu - mevcut kitap verilerini veya boş form döndür
        AdminKitapForm form = (editId != null) ? kitapRepo.findForm(editId) : AdminKitapForm.empty();
        model.addAttribute("form", form);

        // Sayfa kaydırma konumunu koru - buton tıklandığında aynı yere geri dön
        model.addAttribute("scroll", scroll);

        return "admin_kitap";
    }

    /**
     * Kitabı ekle veya güncelle
     * Tüm kitap bilgilerini ve stok detaylarını kaydeder
     */
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long kitapId,
                        @RequestParam(required = false) String isbn,
                        @RequestParam String ad,
                        @RequestParam BigDecimal fiyat,
                        @RequestParam Integer stok,
                        @RequestParam(required = false) Integer sayfaSayisi,
                        @RequestParam(required = false) Integer basimYili,
                        @RequestParam(required = false) String aciklama,
                        @RequestParam(required = false) Integer yayineviId,
                        @RequestParam(defaultValue = "true") boolean durum,
                        @RequestParam(required = false) String returnScroll) {

        try {
            // Kitapı veritabanına ekle veya güncelle
            Integer id = kitapRepo.kitapEkleGuncelle(
                    kitapId, isbn, ad, fiyat, stok, sayfaSayisi, basimYili, aciklama, yayineviId, durum
            );

            // Başarı mesajı ile düzenleme sayfasına dön
            if (kitapId == null) return redirectMsgEdit("Kitap eklendi (ID: " + id + ")", id.longValue());
            return redirectMsgEdit("Kitap güncellendi (ID: " + id + ")", id.longValue());

        } catch (Exception e) {
            e.printStackTrace();
            // Hata mesajı ile dön
            if (kitapId != null) return redirectMsgEdit("Hata: Kitap kaydedilemedi", kitapId);
            return redirectMsg("Hata: Kitap kaydedilemedi", returnScroll);
        }
    }

    /**
     * Yeni kitap formu
     * Boş formla /admin/kitap'a yönlendir
     */
    @GetMapping("/new")
    public String newForm() {
        return "redirect:/admin/kitap";
    }
}
