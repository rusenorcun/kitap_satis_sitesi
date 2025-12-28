package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.admin.AdminOrderDto;
import com.kitapyurdu.api.repository.AdminOrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin Sipariş Denetleyicisi
 * Tüm siparişlerin listelenmesi ve durumlarının güncellenmesini yönetir
 */
@Controller
@RequestMapping("/admin/siparisler")
public class AdminOrderController extends BaseController{

    private final AdminOrderRepository repo;

    public AdminOrderController(AdminOrderRepository repo) {
        this.repo = repo;
    }

    /**
     * Tüm siparişleri listele
     * Durum türlerini ve aktif sipariş sayısını göster
     */
    @GetMapping
    public String listOrders(Model model) {
        // Tüm siparişleri ve durum türlerini al
        List<AdminOrderDto> siparisler = repo.tumSiparisleriGetir();
        List<String> durumlar = repo.getDurumListesi();

        model.addAttribute("orders", siparisler);
        model.addAttribute("statuses", durumlar);
        
        // Aktif sipariş sayısını hesapla (henüz tamamlanmayan siparişler)
        long aktifSayisi = siparisler.stream()
                .filter(s -> !s.getSiparisDurumu().equals("TeslimEdildi") && !s.getSiparisDurumu().equals("Iptal"))
                .count();
        model.addAttribute("aktifSayisi", aktifSayisi);

        return "admin-orders";
    }

    /**
     * Siparişin durumunu güncelle
     */
    @PostMapping("/guncelle")
    public String updateStatus(@RequestParam("siparisId") int siparisId,
                                @RequestParam("yeniDurum") String yeniDurum,
                                RedirectAttributes redir) {
        try {
            // Sipariş durumunu veritabanında güncelle
            repo.durumGuncelle(siparisId, yeniDurum);
            redir.addAttribute("mesaj", "Sipariş #" + siparisId + " durumu güncellendi: " + yeniDurum);
        } catch (Exception e) {
            redir.addAttribute("hata", "Güncelleme başarısız: " + e.getMessage());
        }
        return "redirect:/admin/siparisler";
    }
}