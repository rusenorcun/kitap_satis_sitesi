package com.kitapyurdu.api.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

/**
 * Rol tabanlı başarılı kimlik doğrulama işleyicisi
 * Giriş başarılı olduğunda kullanıcıyı uygun sayfaya yönlendirir
 */
public class RoleBasedSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache = new org.springframework.security.web.savedrequest.HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        // Eğer kullanıcı admin sayfasına gitmek istemişse (SavedRequest) -> oraya dönsün
        // SavedRequest yoksa rol göre yönlendilir.
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // Kullanıcı ADMIN rolüne sahip mi kontrol et
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin paneline yönlendir
            redirectStrategy.sendRedirect(request, response, "/admin");
        } else {
            // Ana sayfaya yönlendir
            redirectStrategy.sendRedirect(request, response, "/");
        }
    }
}
