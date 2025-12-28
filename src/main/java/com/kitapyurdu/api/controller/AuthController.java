package com.kitapyurdu.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Kimlik Doğrulama Denetleyicisi
 * Giriş, giriş başarısı ve çıkış sayfalarını yönetir
 */
@Controller
public class AuthController extends BaseController{

    /**
     * Giriş sayfasını göster
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String hata,
                        @RequestParam(required = false) String cikis,
                        Model model) {

        // Hata parametresi varsa hata mesajını göster
        model.addAttribute("hata", hata != null);
        // Çıkış parametresi varsa çıkış mesajını göster
        model.addAttribute("cikis", cikis != null);
        return "login";
    }

    /**
     * Giriş başarılı oldu sayfasını göster
     * Toast mesajı gösterdikten sonra hedef sayfaya yönlendir
     */
    @GetMapping("/login/success")
    public String loginSuccess(@RequestParam String to, Model model) {
        model.addAttribute("to", to);
        return "login_success";
    }

    /**
     * Çıkış başarılı oldu sayfasını göster
     */
	@GetMapping("/logout/success")
	public String logoutSuccess() {
		return "logout_success";
	}
}
