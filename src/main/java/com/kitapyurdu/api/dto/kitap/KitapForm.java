package com.kitapyurdu.api.dto.kitap;

import java.math.BigDecimal;

public class KitapForm {
    private Long kitapId;
    private String ad;
    private String yazar;
    private String isbn;
    private Integer stok;
    private BigDecimal fiyat;
    private Integer sayfaSayisi;
    private Integer basimYili;
    private String aciklama;
    private Integer yayineviId;
    private Boolean durum;

    public Long getKitapId() { return kitapId; }
    public void setKitapId(Long kitapId) { this.kitapId = kitapId; }

	public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

	public String getYazar() { return yazar; }	
    public void setYazar(String yazar) { this.yazar = yazar; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }	

	public Integer getStok() { return stok; }
    public void setStok(Integer stok) { this.stok = stok; }

	public BigDecimal getFiyat() { return fiyat; }
    public void setFiyat(BigDecimal fiyat) { this.fiyat = fiyat; }
    
    public Integer getSayfaSayisi() { return sayfaSayisi; }
    public void setSayfaSayisi(Integer sayfaSayisi) { this.sayfaSayisi = sayfaSayisi; }

    public Integer getBasimYili() { return basimYili; }
    public void setBasimYili(Integer basimYili) { this.basimYili = basimYili; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public Integer getYayineviId() { return yayineviId; }
    public void setYayineviId(Integer yayineviId) { this.yayineviId = yayineviId; }

    public Boolean getDurum() { return durum; }
    public void setDurum(Boolean durum) { this.durum = durum; }
}