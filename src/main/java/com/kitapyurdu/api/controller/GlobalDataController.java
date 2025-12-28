package com.kitapyurdu.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kitapyurdu.api.repository.SepetRepository;

/**
 * Global Veri Denetleyicisi
 * Tüm denetleyicileri kapsar ve sepet sayısını modele ekler
 */
@ControllerAdvice
public class GlobalDataController extends BaseController{

    @Autowired
    private SepetRepository SepetRepository;

    /**
     * Her istekte çalışır ve modele "SepetCount" ekler
     * Oturumdan sepet kimliğini alır ve veritabanından güncel ürün sayısını döndürür
     */
    @ModelAttribute("SepetCount")
    public int addSepetCountToModel(HttpSession session) {
        
        // 1) Oturumdan sepet kimliğini al
        Integer sepetId = (Integer) session.getAttribute("sepetId");

        // 2) Oturumda sepet yoksa sıfır döndür
        if (sepetId == null) {
            return 0;
        }

        // 3) Varsa veritabanından güncel ürün sayısını çek ve döndür
        return SepetRepository.getSepetUrunAdedi(sepetId);
    }
}// yapay zeka yapımı