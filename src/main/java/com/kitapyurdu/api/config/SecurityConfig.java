package com.kitapyurdu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() { //Varsayılan Spring Security şifreleyicisi. Veritabanında bcrypt ile saklanır.
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RoleBasedDelayedSuccessHandler roleBasedDelayedSuccessHandler() {
		return new RoleBasedDelayedSuccessHandler();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.authorizeHttpRequests(auth -> auth//requestMatchers ile erişim kontrolü yapıldığı yer. .permitAll() ile kullanıcı tanımlanmadan erişim sağlanır.
				.requestMatchers("/", "/login", "/login/success", "/css/**", "/js/**", "/images/**", "/uploads/**", "/logout/success", "/auth/register", "/register", "/error").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")//ADMIN rolü gerekli sayfalar tanımlanır.
				.anyRequest().authenticated()//diğer tüm sayfalar için kimlik doğrulaması gerekli.
			)
			.formLogin(form -> form
				.loginPage("/login")//Özel login sayfası tanımlanır.
				.loginProcessingUrl("/login")//Form action URL'si
				.usernameParameter("kullanici")
				.passwordParameter("sifre")
				.successHandler(roleBasedDelayedSuccessHandler()) // toast.js ile mesaj göstermek için gecikmeli yönlendirme.
				.failureUrl("/login?hata=1")//Giriş hatası durumunda yönlendirme URL'si
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")//Çıkış URL'si
				.logoutSuccessUrl("/logout/success")//Çıkış sonrası yönlendirme URL'si
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
			)
			.csrf(Customizer.withDefaults()); // thymeleaf form'larda _csrf gizli alanı otomatik eklenir.

		return http.build();
	}
}
