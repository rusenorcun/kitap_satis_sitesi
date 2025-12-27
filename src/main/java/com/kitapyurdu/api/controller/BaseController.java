package com.kitapyurdu.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.kitapyurdu.api.repository.SepetRepository; // Paket yolunu kendine göre düzelt

// Abstract yapıyoruz ki tek başına kullanılamasın, diğerleri extend etsin
public abstract class BaseController {

    @Autowired
    protected SepetRepository SepetRepository;

    // Bu metod, bu sınıfı miras alan TÜM controller'larda, her istekten önce çalışır.
    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Integer sepetId = (Integer) session.getAttribute("sepetId");

        if (sepetId != null) {
            // Sepet varsa veritabanından sayıyı çek
            int count = SepetRepository.getSepetUrunAdedi(sepetId);
            model.addAttribute("SepetCount", count);
        } else {
            // Sepet yoksa (login olmamış veya sepeti boş) 0 göster
            model.addAttribute("SepetCount", 0);
        }
    }
}