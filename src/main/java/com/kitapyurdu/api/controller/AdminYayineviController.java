package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.yayinevi.YayineviForm;
import com.kitapyurdu.api.repository.AdminRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/yayinevi")
public class AdminYayineviController extends BaseController{

    private final AdminRepository adminRepo;

    public AdminYayineviController(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    private String redirectMsg(String msg) {
        return "redirect:/admin/yayinevi?mesaj=" + UriUtils.encodeQueryParam(msg, StandardCharsets.UTF_8);
    }

    private String redirectMsgEdit(String msg, Integer editId) {
        return "redirect:/admin/yayinevi?editId=" + editId + "&mesaj=" + UriUtils.encodeQueryParam(msg, StandardCharsets.UTF_8);
    }

    @GetMapping
    public String page(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer editId,
                        @RequestParam(required = false) String mesaj,
                        Model model) {

        model.addAttribute("mesaj", mesaj);
        model.addAttribute("q", q);
        model.addAttribute("yayinevleri", adminRepo.listYayinevleri(q));

        YayineviForm form = (editId != null) ? adminRepo.findYayineviForm(editId) : YayineviForm.empty();
        model.addAttribute("form", form);

        return "admin_yayinevi";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Integer yayineviId,
                        @RequestParam String ad,
                        @RequestParam(required = false) String aciklama) {

        try {
            String adTrim = (ad == null) ? "" : ad.trim();
            String acTrim = (aciklama == null || aciklama.trim().isEmpty()) ? null : aciklama.trim();

            if (adTrim.isEmpty()) return redirectMsg("Hata: Yayınevi adı boş olamaz");

            if (yayineviId == null) {
                adminRepo.yayineviEkle(adTrim, acTrim);
                return redirectMsg("Yayınevi eklendi");
            } else {
                adminRepo.yayineviGuncelle(yayineviId, adTrim, acTrim);
                return redirectMsgEdit("Yayınevi güncellendi", yayineviId);
            }
        } catch (Exception e) {
            if (yayineviId != null) return redirectMsgEdit("Hata: Yayınevi kaydedilemedi", yayineviId);
            return redirectMsg("Hata: Yayınevi kaydedilemedi");
        }
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer yayineviId) {
        try {
            int kitapSayisi = adminRepo.yayineviKitapSayisi(yayineviId);

            if (kitapSayisi > 0) {
                return redirectMsg(
                    "Bu yayınevine bağlı " + kitapSayisi + " kitap var. Önce kitapları taşı veya sil."
                );
            }

            adminRepo.yayineviSil(yayineviId); // SP
            return redirectMsg("Yayınevi silindi");

        } catch (Exception e) {
            return redirectMsg("Hata: Yayınevi silinemedi");
        }
    }
}
