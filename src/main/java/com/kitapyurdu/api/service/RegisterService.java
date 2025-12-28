package com.kitapyurdu.api.service;

import com.kitapyurdu.api.auth.RegisterRequest;
import com.kitapyurdu.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Kayıt Servisi
 * Yeni kullanıcı kaydını işler ve doğrulamaları gerçekleştirir
 */
@Service
public class RegisterService {

	private final UserRepository users;
	private final PasswordEncoder encoder;

	public RegisterService(UserRepository users, PasswordEncoder encoder) {
		this.users = users;
		this.encoder = encoder;
	}

	/**
	 * Yeni kullanıcı kaydı
	 * Form verilerini doğrular ve veritabanına ekler
	 */
	public void register(RegisterRequest r) {
		// Tüm girdileri temizle ve kırp
		String username = safe(r.getUsername());
		String email = safe(r.getEmail());
		String ad = safe(r.getAd());
		String soyad = safe(r.getSoyad());
		String telefon = (r.getTelefon() == null || r.getTelefon().isBlank()) ? null : r.getTelefon().trim();

		// Zorunlu alan doğrulaması
		if (username.isBlank()) throw new IllegalArgumentException("Kullanıcı adı zorunlu.");
		if (email.isBlank()) throw new IllegalArgumentException("E-posta zorunlu.");
		if (ad.isBlank()) throw new IllegalArgumentException("Ad zorunlu.");
		if (soyad.isBlank()) throw new IllegalArgumentException("Soyad zorunlu.");

		// Şifre doğrulaması
		if (r.getPassword() == null || r.getPassword().length() < 6)
			throw new IllegalArgumentException("Şifre en az 6 karakter olmalı.");
		if (!r.getPassword().equals(r.getPassword2()))
			throw new IllegalArgumentException("Şifreler eşleşmiyor.");

		// Benzersizlik doğrulaması
		if (users.existsByKullaniciAdi(username))
			throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor.");
		if (users.existsByEposta(email))
			throw new IllegalArgumentException("Bu e-posta zaten kullanılıyor.");

		// Şifreyi kodla
		String hash = encoder.encode(r.getPassword());

		// Kullanıcıyı veritabanına ekle (parametre sırası repository imzasıyla aynı)
		users.insertUser(username, email, hash, ad, soyad, telefon);
	}

	/**
	 * Boş veya null dizgileri güvenli şekilde işle
	 */
	private static String safe(String s) {
		return s == null ? "" : s.trim();
	}
}
