package com.kitapyurdu.api.controller;

import com.kitapyurdu.api.repository.AdminRepository;
import com.kitapyurdu.api.repository.KitapRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController{

    private final AdminRepository adminRepo;
    private final KitapRepository kitapRepo;

    public HomeController(AdminRepository adminRepo, KitapRepository kitapRepo) {
        this.adminRepo = adminRepo;
        this.kitapRepo = kitapRepo;
    }

    @GetMapping("/")
    public String home(Model model) {

        // İstatistikler
        model.addAttribute("toplamKitapSayisi", adminRepo.toplamKitap());
        model.addAttribute("aktifKitapSayisi", adminRepo.toplamKitap()); // istersen ayrı metotla "Durum=1" saydırırız
        model.addAttribute("yayineviSayisi", adminRepo.yayineviSayisi());

        // Ana sayfa vitrin
        model.addAttribute("featured", kitapRepo.featured(8));
        model.addAttribute("limited", kitapRepo.limitedStock(5, 8));

        return "index";
    }
}
