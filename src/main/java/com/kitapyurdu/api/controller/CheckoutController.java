package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.siparis.*;
import com.kitapyurdu.api.repository.CheckoutRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ödeme Denetleyicisi
 * Sipariş oluşturma ve ödeme işlemlerini yönetir
 */
@Controller
public class CheckoutController extends BaseController{

    private CheckoutRepository checkoutRepo;

    public CheckoutController(CheckoutRepository checkoutRepo) {
        this.checkoutRepo = checkoutRepo;
    }

    /**
     * Ödeme sayfasını göster
     */
    @GetMapping("/siparis/tamamla")
    public String checkoutPage() {
        return "siparis-tamamla";
    }

    /**
     * Sepet özetini getir
     * Toplam tutar, vergi ve ürün detaylarını JSON olarak döndür
     */
    @GetMapping("/api/checkout/summary")
    @ResponseBody
    public ResponseEntity<CheckoutSummaryResponse> summary(Authentication auth) {
        // Giriş yapan kullanıcının kimliğini al
        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());
        // Sepet özet bilgilerini al
        CartSummaryDto totals = checkoutRepo.getSepetOzet(kullaniciId);
        // Sepet kalemlerini al
        List<CartItemDto> items = checkoutRepo.getSepetKalemler(kullaniciId);
        return ResponseEntity.ok(new CheckoutSummaryResponse(items, totals));
    }

    /**
     * Kullanıcının kaydedilmiş adreslerini getir
     * JSON olarak dönüş yap
     */
    @GetMapping("/api/checkout/addresses")
    @ResponseBody
    public ResponseEntity<List<AddressDto>> addresses(Authentication auth) {
        // Giriş yapan kullanıcının kimliğini al
        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());
        return ResponseEntity.ok(checkoutRepo.getAdresler(kullaniciId));
    }

    /**
     * Ödeme yöntemlerini getir
     * Mevcut ödeme seçeneklerini JSON olarak döndür
     */
    @GetMapping("/api/checkout/payment-methods")
    @ResponseBody
    public ResponseEntity<List<String>> paymentMethods() {
        return ResponseEntity.ok(checkoutRepo.getOdemeYontemleri());
    }

    /**
     * Sipariş oluştur ve kaydet
     * Kullanıcının sepetini sipariş talebine dönüştür
     */
    @PostMapping("/api/checkout/place")
    @ResponseBody
    public ResponseEntity<PlaceOrderResponse> place(@RequestBody PlaceOrderRequest req, Authentication auth) {

        // Giriş yapan kullanıcının kimliğini al
        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());

        // Adres seçimi kontrol
        if (req == null || req.getAdresId() == null) {
            return ResponseEntity.badRequest().body(new PlaceOrderResponse(null, "Adres seçilmedi."));
        }

        // Ödeme yöntemi doğrulaması
        String odemeYontemi = (req.getOdemeYontemi() == null) ? "" : req.getOdemeYontemi().trim();
        if (!odemeYontemi.equalsIgnoreCase("KapidaOdeme") && !odemeYontemi.equalsIgnoreCase("Havale")) {
            // Kredi kartı sayfası henüz geliştirilmediğinden, yalnızca kapıda ödeme ve havale kabul ediliyor
            return ResponseEntity.badRequest().body(new PlaceOrderResponse(null, "Geçersiz ödeme yöntemi. (Lütfen KapidaOdeme veya Havale seçin)"));
        }

        // 1) Sipariş oluştur
        int siparisId = checkoutRepo.siparisOlustur(kullaniciId, req.getAdresId(), null);

        // 2) Toplam tutarı hesapla (Veritabanında Fonksiyon kullanılıyor)
        BigDecimal genelToplam = checkoutRepo.getSiparisGenelToplam(siparisId);

        // 3) Ödeme kaydını ekle
        checkoutRepo.odemeKaydet(
            siparisId,
            "Beklemede",  // Admin panelinden onaylanacak
            odemeYontemi,
            genelToplam,
            "Ödeme - " + odemeYontemi
        );

        return ResponseEntity.ok(new PlaceOrderResponse(siparisId, null));
    }
}