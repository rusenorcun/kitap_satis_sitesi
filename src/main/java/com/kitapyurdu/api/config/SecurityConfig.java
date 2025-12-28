package com.kitapyurdu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Güvenlik Yapılandırması
 * Spring Security'nin tüm ayarlarını içerir
 */
@Configuration
public class SecurityConfig {

	/**
	 * Şifre şifreleme sağlayıcı
	 * BCrypt algoritması kullanarak şifreler veritabanında güvenli şekilde saklanır
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Rol tabanlı geciktirilmiş başarı işleyicisi
	 * Giriş başarılı olduğunda kullanıcıyı yetiştirir
	 */
	@Bean
	public RoleBasedDelayedSuccessHandler roleBasedDelayedSuccessHandler() {
		return new RoleBasedDelayedSuccessHandler();
	}

	/**
	 * Güvenlik filtresi zinciri
	 * URL'lere göre erişim kontrolü tanımlar ve giriş/çıkış ayarlarını yapılandırır
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.authorizeHttpRequests(auth -> auth
				// Herkese açık sayfalar
				.requestMatchers("/", "/login", "/login/success", "/css/**", "/js/**", "/images/**", "/uploads/**", "/logout/success", "/auth/register", "/register", "/error").permitAll()
				// Admin sayfaları - sadece ADMIN rolüne izin ver
				.requestMatchers("/admin/**").hasRole("ADMIN")
				// Diğer tüm sayfalar - giriş yapılmış olması gerekli
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				// Özel giriş sayfası tanımla
				.loginPage("/login")
				// Giriş formu göndereceği URL
				.loginProcessingUrl("/login")
				// Form parametresi adları
				.usernameParameter("kullanici")
				.passwordParameter("sifre")
				// Başarılı giriş - toast mesajı göstermek için gecikme uygula
				.successHandler(roleBasedDelayedSuccessHandler())
				// Başarısız giriş - hata mesajıyla yönlendir
				.failureUrl("/login?hata=1")
				.permitAll()
			)
			.logout(logout -> logout
				// Çıkış işleminin URL'si
				.logoutUrl("/logout")
				// Çıkış sonrasında yönlendirme sayfası
				.logoutSuccessUrl("/logout/success")
				// Oturumu geçersiz kıl
				.invalidateHttpSession(true)
				// Kimlik doğrulama bilgilerini sil
				.clearAuthentication(true)
				// JSESSIONID çerezini sil
				.deleteCookies("JSESSIONID")
			)
			// CSRF koruması - Thymeleaf form'larda _csrf gizli alanı otomatik eklenir
			.csrf(Customizer.withDefaults());

		return http.build();
	}
}
