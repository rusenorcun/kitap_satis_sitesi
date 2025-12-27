package com.kitapyurdu.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AuthController extends BaseController{

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String hata,
                        @RequestParam(required = false) String cikis,
                        Model model) {

        model.addAttribute("hata", hata != null);
        model.addAttribute("cikis", cikis != null);
        return "login";
    }

    @GetMapping("/login/success")
    public String loginSuccess(@RequestParam String to, Model model) {
        model.addAttribute("to", to);
        return "login_success";
    }
	@GetMapping("/logout/success")
	public String logoutSuccess() {
		return "logout_success";
	}
    

}
