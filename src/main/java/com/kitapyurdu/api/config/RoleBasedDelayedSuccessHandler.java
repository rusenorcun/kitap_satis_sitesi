package com.kitapyurdu.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/**
 * Rol tabanlı geciktirilmiş başarılı kimlik doğrulama işleyicisi
 * Giriş başarılı olduğunda toast mesajı göstermek için yönlendirmeyi geciktirir
 */
public class RoleBasedDelayedSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws java.io.IOException {

        // 1) Kaydedilmiş isteği kontrol et
        SavedRequest saved = requestCache.getRequest(request, response);
        String target;

        if (saved != null && saved.getRedirectUrl() != null) {
            // Kullanıcı giriş yapmadan önceki sayfaya git
            target = saved.getRedirectUrl();
            requestCache.removeRequest(request, response);
        } else {
            // 2) Rol tabanlı yönlendirme
            // Kullanıcı ADMIN rolüne sahip mi kontrol et
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_ADMIN"));

            // Hedef sayfayı belirle
            target = isAdmin ? "/admin" : "/";
        }

        // 3) Toast.js ile mesaj göstermek için yönlendirme URL'sine parametre ekle
        String to = UriUtils.encodeQueryParam(target, StandardCharsets.UTF_8);
        redirectStrategy.sendRedirect(request, response, "/login/success?to=" + to);
    }
}
