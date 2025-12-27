package com.kitapyurdu.api.repository;

import com.kitapyurdu.api.auth.CustomUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	private final JdbcTemplate jdbc;

	public UserRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Login için kullanıcıyı (username veya email) ile bulur.
	 * Durum=1 olmayanlar login olamaz.
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
					rs.getString("Rol"),      // DB: ADMIN / USER
					rs.getBoolean("Durum")
			);
		}, usernameOrEmail, usernameOrEmail);
	}

	public boolean existsAdmin() {
		Integer cnt = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE Rol = N'ADMIN' AND Durum = 1",
				Integer.class
		);
		return cnt != null && cnt > 0;
	}

	public boolean existsByKullaniciAdi(String kullaniciAdi) {
		Integer c = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE KullaniciAdi = ?",
				Integer.class,
				kullaniciAdi
		);
		return c != null && c > 0;
	}

	public boolean existsByEposta(String eposta) {
		Integer c = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Kullanici WHERE Eposta = ?",
				Integer.class,
				eposta
		);
		return c != null && c > 0;
	}

	/**
	 * Kayıt (register) için kullanıcı ekler.
	 * DB'de Durum ve KayitTarihi DEFAULT ise otomatik dolar.
	 * Rol: USER olarak atanır.
	 */
	public void insertUser(String kullaniciAdi, String eposta, String sifreHash,
		String ad, String soyad, String telefon) {

		// Telefon boş gelirse null olarak yazmak daha sağlıklı
		String tel = (telefon == null || telefon.isBlank()) ? null : telefon.trim();

		jdbc.update("""
			INSERT INTO dbo.Kullanici
				(KullaniciAdi, Eposta, SifreHash, Ad, Soyad, Telefon, Rol)
			VALUES
				(?, ?, ?, ?, ?, ?, N'USER')
			""", kullaniciAdi, eposta, sifreHash, ad, soyad, tel);
	}

	
}
