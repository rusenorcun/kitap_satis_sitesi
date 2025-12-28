package com.kitapyurdu.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin Kimlik Doğrulama Denetleyicisi
 * Admin giriş sayfasını yönetir
 */
@Controller
@RequestMapping("/admin")
public class AdminAuthController extends BaseController{

    /**
     * Admin giriş sayfasını göster
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin_login";
    }
}
