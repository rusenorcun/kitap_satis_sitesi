package com.kitapyurdu.api.dto.admin;

import java.math.BigDecimal;

public class AdminKitapForm {
    private Long kitapId;
    private String isbn;
    private String ad;
    private BigDecimal fiyat;
    private Integer stok;
    private Integer sayfaSayisi;
    private Integer basimYili;
    private String aciklama;
    private Integer yayineviId;
    private boolean durum;

    public AdminKitapForm() {}

    public Long getKitapId() { return kitapId; }
    public void setKitapId(Long kitapId) { this.kitapId = kitapId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public BigDecimal getFiyat() { return fiyat; }
    public void setFiyat(BigDecimal fiyat) { this.fiyat = fiyat; }

    public Integer getStok() { return stok; }
    public void setStok(Integer stok) { this.stok = stok; }

    public Integer getSayfaSayisi() { return sayfaSayisi; }
    public void setSayfaSayisi(Integer sayfaSayisi) { this.sayfaSayisi = sayfaSayisi; }

    public Integer getBasimYili() { return basimYili; }
    public void setBasimYili(Integer basimYili) { this.basimYili = basimYili; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public Integer getYayineviId() { return yayineviId; }
    public void setYayineviId(Integer yayineviId) { this.yayineviId = yayineviId; }

    public boolean isDurum() { return durum; }
    public void setDurum(boolean durum) { this.durum = durum; }

    public static AdminKitapForm empty() {
        AdminKitapForm f = new AdminKitapForm();
        f.setKitapId(null);
        f.setIsbn(null);
        f.setAd("");
        f.setFiyat(new BigDecimal("0.00"));
        f.setStok(0);
        f.setSayfaSayisi(null);
        f.setBasimYili(null);
        f.setAciklama(null);
        f.setYayineviId(null);
        f.setDurum(true);
        return f;
    }
}
