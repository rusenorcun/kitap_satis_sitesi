package com.kitapyurdu.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.kitapyurdu.api.repository.SepetRepository;

/**
 * Tüm denetleyicilerin temel sınıfı
 * Alışveriş sepeti bilgilerini tüm istek öncesinde modele ekler
 * Soyut sınıf olarak tasarlanmış - doğrudan kullanılamaz, sadece miras alınır
 */
public abstract class BaseController {

    @Autowired
    protected SepetRepository SepetRepository;

    /**
     * Tüm denetleyicide çalışan genel öznitelik ekleme metodu
     * Her istekten ÖNCE çalıştırılır
     * Alışveriş sepetindeki ürün sayısını hesaplar ve sayfaya gönderir
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Integer sepetId = (Integer) session.getAttribute("sepetId");

        if (sepetId != null) {
            // Sepet varsa veritabanından ürün sayısını çek
            int count = SepetRepository.getSepetUrunAdedi(sepetId);
            model.addAttribute("SepetCount", count);
        } else {
            // Sepet yoksa (giriş yapılmamış veya sepet boş) sıfır göster
            model.addAttribute("SepetCount", 0);
        }
    }
}