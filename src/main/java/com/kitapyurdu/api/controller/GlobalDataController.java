package com.kitapyurdu.api.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kitapyurdu.api.repository.SepetRepository;

@ControllerAdvice // Bu anotasyon, tüm Controller'ları kapsamasını sağlar
public class GlobalDataController extends BaseController{

    @Autowired
    private SepetRepository SepetRepository; // Repository adın neyse onu yaz

    // Bu metod her istekte çalışır ve model'e "SepetCount" ekler
    @ModelAttribute("SepetCount")
    public int addSepetCountToModel(HttpSession session) {
        
        // 1. Session'dan sepet ID'yi al (login logic'ine göre değişebilir)
        Integer sepetId = (Integer) session.getAttribute("sepetId");

        // 2. Eğer session'da sepet yoksa 0 döndür
        if (sepetId == null) {
            return 0;
        }

        // 3. Varsa veritabanından güncel sayıyı çek
        return SepetRepository.getSepetUrunAdedi(sepetId);
    }
}// yapay zeka yapımı