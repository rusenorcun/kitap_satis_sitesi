package com.kitapyurdu.api.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Özel Kullanıcı Detayları
 * Spring Security'nin UserDetails arayüzünü uygular
 * Veritabanından gelen kullanıcı bilgilerini tutar
 */
public class CustomUserDetails implements UserDetails {

	private final int kullaniciId;
	private final String kullaniciAdi;
	private final String eposta;
	private final String sifreHash;
	// Veritabanından tutulan rol değeri: ADMIN / USER
	private final String rol;
	private final boolean durum;

	public CustomUserDetails(int kullaniciId, String kullaniciAdi, String eposta, String sifreHash, String rol, boolean durum) {
		this.kullaniciId = kullaniciId;
		this.kullaniciAdi = kullaniciAdi;
		this.eposta = eposta;
		this.sifreHash = sifreHash;
		this.rol = rol;
		this.durum = durum;
	}

	public int getKullaniciId() { return kullaniciId; }
	public String getEposta() { return eposta; }
	public String getRol() { return rol; }

	/**
	 * Kullanıcının yetkilerini döndür
	 * Veritabanından gelen rolü Spring Security formatına dönüştür
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Rol değerini oku: ADMIN / USER
		String r = (rol == null ? "USER" : rol.trim());

		// Veritabanından ROLE_ ön eki olmadan geliyor, Spring Security için ekle
		if (!r.startsWith("ROLE_")) {
			r = "ROLE_" + r.toUpperCase(); // ADMIN -> ROLE_ADMIN, USER -> ROLE_USER
		}

		// Geçersiz roller için varsayılan olarak ROLE_USER atandır
		if (!r.equals("ROLE_ADMIN") && !r.equals("ROLE_USER")) {
			r = "ROLE_USER";
		}

		return List.of(new SimpleGrantedAuthority(r));
	}

	@Override
	public String getPassword() { return sifreHash; }

	@Override
	public String getUsername() { return kullaniciAdi; }

	// Hesap süresi bitmiş mi? -> Hayır
	@Override public boolean isAccountNonExpired() { return true; }
	// Hesap kilitli mi? -> Hayır
	@Override public boolean isAccountNonLocked() { return true; }
	// Kimlik bilgileri süresi bitmiş mi? -> Hayır
	@Override public boolean isCredentialsNonExpired() { return true; }
	// Hesap etkinleştirilmiş mi? -> Durum değerine göre
	@Override public boolean isEnabled() { return durum; }
}
