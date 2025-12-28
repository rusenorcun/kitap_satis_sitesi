package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.auth.CustomUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Kullanıcı Deposu
 * Veritabanında kullanıcı bilgileriyle ilgili tüm sorguları gerçekleştirir
 */
@Repository
public class UserRepository {

	private final JdbcTemplate jdbc;

	public UserRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Giriş için kullanıcıyı kullanıcı adı veya e-posta ile bul
	 * Sadece aktif (Durum=1) kullanıcılar giriş yapabilir
	 */
	public CustomUserDetails findForLogin(String usernameOrEmail) {
		String sql = """
			SELECT TOP(1) KullaniciId, KullaniciAdi, Eposta, SifreHash, Rol, Durum
			FROM dbo.Kullanici
			WHERE Durum = 1
				AND (KullaniciAdi = ? OR Eposta = ?)
			""";

		return jdbc.query(sql, rs -> {
			if (!rs.next()) return null;

			return new CustomUserDetails(
					rs.getInt("KullaniciId"),
					rs.getString("KullaniciAdi"),
					rs.getString("Eposta"),
					rs.getString("SifreHash"),
					rs.getString("Rol"),      // Veritabanı: ADMIN / USER
					rs.getBoolean("Durum")
			);
		}, usernameOrEmail, usernameOrEmail);
	}

	/**
	 * En az bir admin kullanıcısı var mı kontrol et
	 */
	public boolean existsAdmin() {
		Integer cnt = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE Rol = N'ADMIN' AND Durum = 1",
				Integer.class
		);
		return cnt != null && cnt > 0;
	}

	/**
	 * Belirtilen kullanıcı adı zaten var mı
	 */
	public boolean existsByKullaniciAdi(String kullaniciAdi) {
		Integer c = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE KullaniciAdi = ?",
				Integer.class,
				kullaniciAdi
		);
		return c != null && c > 0;
	}

	/**
	 * Belirtilen e-posta zaten var mı
	 */
	public boolean existsByEposta(String eposta) {
		Integer c = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE Eposta = ?",
				Integer.class,
				eposta
		);
		return c != null && c > 0;
	}

	/**
	 * Kaydı (register) için yeni kullanıcı ekle
	 * Durum ve KayitTarihi veritabanında otomatik dolar
	 * Rol otomatik olarak USER olarak atanır
	 */
	public void insertUser(String kullaniciAdi, String eposta, String sifreHash,
		String ad, String soyad, String telefon) {

		// Telefon boş gelirse null olarak kaydet
		String tel = (telefon == null || telefon.isBlank()) ? null : telefon.trim();

		jdbc.update("""
			INSERT INTO dbo.Kullanici
				(KullaniciAdi, Eposta, SifreHash, Ad, Soyad, Telefon, Rol)
			VALUES
				(?, ?, ?, ?, ?, ?, N'USER')
			""", kullaniciAdi, eposta, sifreHash, ad, soyad, tel);
	}
}
