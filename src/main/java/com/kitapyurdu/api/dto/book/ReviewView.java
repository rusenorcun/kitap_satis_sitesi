package com.kitapyurdu.api.dto.book;

import java.time.LocalDateTime;

public class ReviewView {
	private int yorumId;
	private int kitapId;
	private int kullaniciId;
	private int puan;
	private String yorumMetni;
	private LocalDateTime yorumTarihi;

	private String kullaniciAdi;
	private String kullaniciAd;
	private String kullaniciSoyad;

	public int getYorumId() { return yorumId; }
	public void setYorumId(int yorumId) { this.yorumId = yorumId; }

	public int getKitapId() { return kitapId; }
	public void setKitapId(int kitapId) { this.kitapId = kitapId; }

	public int getKullaniciId() { return kullaniciId; }
	public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }

	public int getPuan() { return puan; }
	public void setPuan(int puan) { this.puan = puan; }

	public String getYorumMetni() { return yorumMetni; }
	public void setYorumMetni(String yorumMetni) { this.yorumMetni = yorumMetni; }

	public LocalDateTime getYorumTarihi() { return yorumTarihi; }
	public void setYorumTarihi(LocalDateTime yorumTarihi) { this.yorumTarihi = yorumTarihi; }

	public String getKullaniciAdi() { return kullaniciAdi; }
	public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

	public String getKullaniciAd() { return kullaniciAd; }
	public void setKullaniciAd(String kullaniciAd) { this.kullaniciAd = kullaniciAd; }

	public String getKullaniciSoyad() { return kullaniciSoyad; }
	public void setKullaniciSoyad(String kullaniciSoyad) { this.kullaniciSoyad = kullaniciSoyad; }
}