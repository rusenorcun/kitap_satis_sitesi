package com.kitapyurdu.api.dto.book;

public class AuthorView {
	private int yazarId;
	private String ad;
	private String soyad;

	public int getYazarId() { return yazarId; }
	public void setYazarId(int yazarId) { this.yazarId = yazarId; }

	public String getAd() { return ad; }
	public void setAd(String ad) { this.ad = ad; }

	public String getSoyad() { return soyad; }
	public void setSoyad(String soyad) { this.soyad = soyad; }
}
