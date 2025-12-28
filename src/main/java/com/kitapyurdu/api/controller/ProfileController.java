package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.profile.*;
import com.kitapyurdu.api.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/**
 * Kullanıcı Profili Denetleyicisi
 * Kullanıcı profili, adres, şifre ve siparişleri yönetir
 */
@Controller
public class ProfileController extends BaseController{

    private final ProfileService profile;

    public ProfileController(ProfileService profile) {
        this.profile = profile;
    }

    /**
     * Kullanıcı profili sayfasını göster
     * Kişisel bilgiler, adresler, favoriler ve siparişleri ekle
     */
    @GetMapping("/profil")
    public String profil(Model model,
                        Principal principal,
                        @RequestParam(required = false) String toast,
                        @RequestParam(required = false) String error) {

        // Giriş yapılmamış ise login sayfasına yönlendir
        if (principal == null) return "redirect:/login";

        // Geçerli kullanıcının kimliğini al
        String username = principal.getName();
        int userId = profile.requireUserId(username);

        // Kullanıcı profil bilgisini al
        UserProfile u = profile.profile(userId);

        // Tüm bilgileri modele ekle
        model.addAttribute("user", u);
        model.addAttribute("addresses", profile.addresses(userId));
        model.addAttribute("favorites", profile.favorites(userId));
        model.addAttribute("orders", profile.orders(userId));

        // Hata ayıklama: Giriş yapan kullanıcı
        System.out.println(">>> HATA AYIKLAMA [Denetleyici]: Giriş yapan: " + username + " (ID: " + userId + ")");

        // Kullanıcı siparişlerini al
        List<UserOrder> siparisler = profile.orders(userId);
        model.addAttribute("orders", siparisler);

        // Hata ayıklama: Modele eklenen verileri kontrol et
        if (siparisler == null) {
            System.out.println(">>> HATA AYIKLAMA [Denetleyici]: HATA! Sipariş listesi NULL döndü!");
        } else {
            System.out.println(">>> HATA AYIKLAMA [Denetleyici]: HTML'e gönderilen sipariş sayısı: " + siparisler.size());
        }

        // Güncelleme formunu hazırla
        ProfileUpdateForm pf = new ProfileUpdateForm();
        pf.setKullaniciAdi(u.getKullaniciAdi());
        pf.setEposta(u.getEposta());
        pf.setAd(u.getAd());
        pf.setSoyad(u.getSoyad());
        pf.setTelefon(u.getTelefon());

        model.addAttribute("profileForm", pf);
        model.addAttribute("passForm", new PasswordChangeForm());
        model.addAttribute("addressForm", new AddressCreateForm());

        // Toast veya hata mesajları göster
        if (toast != null) model.addAttribute("toast", toast);
        if (error != null) model.addAttribute("error", error);

        return "profile";
    }

    /**
     * Hesap bilgilerini güncelle
     * Ad, soyad, e-posta ve telefon bilgilerini kaydeder
     */
    @PostMapping("/profil/hesap")
    public String hesapGuncelle(@ModelAttribute("profileForm") ProfileUpdateForm form,
                                Principal principal,
                                RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            profile.updateProfile(userId, form);
            ra.addAttribute("toast", "Hesap bilgileri güncellendi");
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }

    /**
     * Kullanıcı şifresini değiştir
     * Eski şifreyi doğruladıktan sonra yeni şifreyi kaydeder
     */
    @PostMapping("/profil/sifre")
    public String sifreDegistir(@ModelAttribute("passForm") PasswordChangeForm form,
                                Principal principal,
                                RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            profile.changePassword(userId, form);
            ra.addAttribute("toast", "Şifre güncellendi");
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }

    /**
     * Kullanıcı profiline yeni adres ekle
     */
    @PostMapping("/profil/adres")
    public String adresEkle(@ModelAttribute("addressForm") AddressCreateForm form,
                            Principal principal,
                            RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            profile.addAddress(userId, form);
            ra.addAttribute("toast", "Adres eklendi");
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }

    /**
     * Belirlenen adresi varsayılan adres olarak ayarla
     */
    @PostMapping("/profil/adres/{adresId}/varsayilan")
    public String adresVarsayilan(@PathVariable int adresId,
                                Principal principal,
                                RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            profile.setDefaultAddress(userId, adresId);
            ra.addAttribute("toast", "Varsayılan adres güncellendi");
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }

    /**
     * Kullanıcı adresini profilinden sil
     */
    @PostMapping("/profil/adres/{adresId}/sil")
    public String adresSil(@PathVariable int adresId,
                            Principal principal,
                            RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            profile.deleteAddress(userId, adresId);
            ra.addAttribute("toast", "Adres silindi");
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }

    /**
     * Kitabı favorilere ekle/çıkar
     */
    @PostMapping("/profil/favori/{kitapId}/toggle")
    public String favoriToggle(@PathVariable int kitapId,
                                Principal principal,
                                RedirectAttributes ra) {
        try {
            int userId = profile.requireUserId(principal.getName());
            String sonuc = profile.toggleFavorite(userId, kitapId);
            ra.addAttribute("toast", "Favori: " + sonuc);
        } catch (Exception ex) {
            ra.addAttribute("error", ex.getMessage());
        }
        return "redirect:/profil";
    }
}
