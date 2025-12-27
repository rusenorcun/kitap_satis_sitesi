package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.dto.profile.*;
import com.kitapyurdu.api.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class ProfileController extends BaseController{

    private final ProfileService profile;

    public ProfileController(ProfileService profile) {
        this.profile = profile;
    }

    @GetMapping("/profil")
    public String profil(Model model,
                        Principal principal,
                        @RequestParam(required = false) String toast,
                        @RequestParam(required = false) String error) {

        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        int userId = profile.requireUserId(username);

        UserProfile u = profile.profile(userId);

        model.addAttribute("user", u);
        model.addAttribute("addresses", profile.addresses(userId));
        model.addAttribute("favorites", profile.favorites(userId));
        model.addAttribute("orders", profile.orders(userId));

        // KONSOL LOG 3: Kullanıcı kimliği
        System.out.println(">>> DEBUG [Controller]: Giriş yapan: " + username + " (ID: " + userId + ")");

        List<UserOrder> siparisler = profile.orders(userId);
        model.addAttribute("orders", siparisler);

        // KONSOL LOG 4: Modele ne eklendi?
        if (siparisler == null) {
            System.out.println(">>> DEBUG [Controller]: HATA! Sipariş listesi NULL döndü!");
        } else {
            System.out.println(">>> DEBUG [Controller]: HTML'e gönderilen sipariş sayısı: " + siparisler.size());
        }


        ProfileUpdateForm pf = new ProfileUpdateForm();
        pf.setKullaniciAdi(u.getKullaniciAdi());
        pf.setEposta(u.getEposta());
        pf.setAd(u.getAd());
        pf.setSoyad(u.getSoyad());
        pf.setTelefon(u.getTelefon());

        model.addAttribute("profileForm", pf);
        model.addAttribute("passForm", new PasswordChangeForm());
        model.addAttribute("addressForm", new AddressCreateForm());

        if (toast != null) model.addAttribute("toast", toast);
        if (error != null) model.addAttribute("error", error);

        return "profile";
    }

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
