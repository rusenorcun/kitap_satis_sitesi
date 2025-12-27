package com.kitapyurdu.api.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	private final int kullaniciId;
	private final String kullaniciAdi;
	private final String eposta;
	private final String sifreHash;
	private final String rol;   //Database Tarafından Tutulan Rol Değeridir. (ADMIN / USER)
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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Database rolü: ADMIN / USER
		String r = (rol == null ? "USER" : rol.trim());

		// Database'den ROLE_ olmadan geliyor, Spring Security için ROLE_ ekleme.
		if (!r.startsWith("ROLE_")) {
			r = "ROLE_" + r.toUpperCase(); // ADMIN -> ROLE_ADMIN, USER -> ROLE_USER dönüşür.
		}

		// Geçersiz roller için varsayılan olarak ROLE_USER atama
		if (!r.equals("ROLE_ADMIN") && !r.equals("ROLE_USER")) {
			r = "ROLE_USER";
		}

		return List.of(new SimpleGrantedAuthority(r));
	}

	@Override
	public String getPassword() { return sifreHash; }

	@Override
	public String getUsername() { return kullaniciAdi; }

	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return durum; }
}
