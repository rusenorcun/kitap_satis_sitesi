package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.siparis.*;
import com.kitapyurdu.api.repository.CheckoutRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CheckoutController extends BaseController{

    private CheckoutRepository checkoutRepo;

    public CheckoutController(CheckoutRepository checkoutRepo) {
        this.checkoutRepo = checkoutRepo;
    }

    @GetMapping("/siparis/tamamla")
    public String checkoutPage() {
        return "siparis-tamamla";
    }

    @GetMapping("/api/checkout/summary")
    @ResponseBody
    public ResponseEntity<CheckoutSummaryResponse> summary(Authentication auth) {
        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());
        CartSummaryDto totals = checkoutRepo.getSepetOzet(kullaniciId);
        List<CartItemDto> items = checkoutRepo.getSepetKalemler(kullaniciId);
        return ResponseEntity.ok(new CheckoutSummaryResponse(items, totals));
    }

    @GetMapping("/api/checkout/addresses")
    @ResponseBody
    public ResponseEntity<List<AddressDto>> addresses(Authentication auth) {
        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());
        return ResponseEntity.ok(checkoutRepo.getAdresler(kullaniciId));
    }   
    @GetMapping("/api/checkout/payment-methods")
    @ResponseBody
    public ResponseEntity<List<String>> paymentMethods() {
        return ResponseEntity.ok(checkoutRepo.getOdemeYontemleri());
    }

    @PostMapping("/api/checkout/place")
    @ResponseBody
    public ResponseEntity<PlaceOrderResponse> place(@RequestBody PlaceOrderRequest req, Authentication auth) {

        int kullaniciId = checkoutRepo.getKullaniciIdByAuthName(auth.getName());

        if (req == null || req.getAdresId() == null) {
            return ResponseEntity.badRequest().body(new PlaceOrderResponse(null, "Adres seçilmedi."));
        }

        String odemeYontemi = (req.getOdemeYontemi() == null) ? "" : req.getOdemeYontemi().trim();
        if (!odemeYontemi.equalsIgnoreCase("KapidaOdeme") && !odemeYontemi.equalsIgnoreCase("Havale")) {//kredi kardı sayfası bulunmamasından dolayı yalnızca kapıda ödeme ve havale kabul ediliyor
            return ResponseEntity.badRequest().body(new PlaceOrderResponse(null, "Geçersiz ödeme yöntemi. (Lütfen KapidaOdeme veya Havale seçin)"));
        }

        // 1) Sipariş oluştur
        int siparisId = checkoutRepo.siparisOlustur(kullaniciId, req.getAdresId(), null);

        // 2) Toplam tutar (SP içinde Fonksiyon kullanılıyor)
        BigDecimal genelToplam = checkoutRepo.getSiparisGenelToplam(siparisId);

        // 3) Ödeme Kaydet
        checkoutRepo.odemeKaydet(
            siparisId,
            "Beklemede",// admin panelinden onaylanacak(çalışıyor.)
            odemeYontemi,
            genelToplam,
            "Checkout - " + odemeYontemi
        );

        return ResponseEntity.ok(new PlaceOrderResponse(siparisId, null));
    }
}