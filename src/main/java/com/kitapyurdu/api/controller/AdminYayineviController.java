package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.yayinevi.YayineviForm;
import com.kitapyurdu.api.repository.AdminRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/**
 * Admin Yayınevi Denetleyicisi
 * Yayınevlerinin listelenme, ekleme, güncelleme ve silme işlemlerini yönetir
 */
@Controller
@RequestMapping("/admin/yayinevi")
public class AdminYayineviController extends BaseController{

    private final AdminRepository adminRepo;

    public AdminYayineviController(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    /**
     * Mesajı ekleyip yayınevi listesine yönlendir
     */
    private String redirectMsg(String msg) {
        return "redirect:/admin/yayinevi?mesaj=" + UriUtils.encodeQueryParam(msg, StandardCharsets.UTF_8);
    }

    /**
     * Mesajı ekleyip düzenleme sayfasına yönlendir
     */
    private String redirectMsgEdit(String msg, Integer editId) {
        return "redirect:/admin/yayinevi?editId=" + editId + "&mesaj=" + UriUtils.encodeQueryParam(msg, StandardCharsets.UTF_8);
    }

    /**
     * Yayınevi listesi sayfasını göster
     */
    @GetMapping
    public String page(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer editId,
                        @RequestParam(required = false) String mesaj,
                        Model model) {

        // Arama parametresi ve mesajı ekle
        model.addAttribute("mesaj", mesaj);
        model.addAttribute("q", q);
        
        // Yayınevi listesini ekle
        model.addAttribute("yayinevleri", adminRepo.listYayinevleri(q));

        // Düzenleme formu
        YayineviForm form = (editId != null) ? adminRepo.findYayineviForm(editId) : YayineviForm.empty();
        model.addAttribute("form", form);

        return "admin_yayinevi";
    }

    /**
     * Yayınevi ekle veya güncelle
     */
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Integer yayineviId,
                        @RequestParam String ad,
                        @RequestParam(required = false) String aciklama) {

        try {
            // Boş alanları işle
            String adTrim = (ad == null) ? "" : ad.trim();
            String acTrim = (aciklama == null || aciklama.trim().isEmpty()) ? null : aciklama.trim();

            // Yayınevi adı kontrolü
            if (adTrim.isEmpty()) return redirectMsg("Hata: Yayınevi adı boş olamaz");

            if (yayineviId == null) {
                // Yeni yayınevi ekle
                adminRepo.yayineviEkle(adTrim, acTrim);
                return redirectMsg("Yayınevi eklendi");
            } else {
                // Mevcut yayınevi güncelle
                adminRepo.yayineviGuncelle(yayineviId, adTrim, acTrim);
                return redirectMsgEdit("Yayınevi güncellendi", yayineviId);
            }
        } catch (Exception e) {
            // Hata mesajı göster
            if (yayineviId != null) return redirectMsgEdit("Hata: Yayınevi kaydedilemedi", yayineviId);
            return redirectMsg("Hata: Yayınevi kaydedilemedi");
        }
    }

    /**
     * Yayınevi sil
     * Silme işleminden önce yayınevine bağlı kitapları kontrol et
     */
    @PostMapping("/delete")
    public String delete(@RequestParam Integer yayineviId) {
        try {
            // Yayınevine bağlı kitap sayısını kontrol et
            int kitapSayisi = adminRepo.yayineviKitapSayisi(yayineviId);

            if (kitapSayisi > 0) {
                // Yayınevide kitap varsa silmeye izin verme
                return redirectMsg(
                    "Bu yayınevine bağlı " + kitapSayisi + " kitap var. Önce kitapları taşı veya sil."
                );
            }

            // Yayınevi'yi sil
            adminRepo.yayineviSil(yayineviId);
            return redirectMsg("Yayınevi silindi");

        } catch (Exception e) {
            return redirectMsg("Hata: Yayınevi silinemedi");
        }
    }
}
