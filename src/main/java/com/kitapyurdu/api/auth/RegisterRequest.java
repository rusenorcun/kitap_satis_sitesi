package com.kitapyurdu.api.auth;

/**
 * Kayıt İsteği DTO
 * Kullanıcı kaydı sırasında gönderilen verileri tutar
 */
public class RegisterRequest {
	private String ad;
	private String soyad;
	private String username;
	private String email;
	private String password;
	private String password2;
	private String telefon;

	// ============ Getter ve Setter Metotları ============

	public String getAd() { return ad; }
	public void setAd(String ad) { this.ad = ad; }

	public String getSoyad() { return soyad; }
	public void setSoyad(String soyad) { this.soyad = soyad; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getPassword2() { return password2; }
	public void setPassword2(String password2) { this.password2 = password2; }

	public String getTelefon() { return telefon; }
	public void setTelefon(String telefon) { this.telefon = telefon; }
}
