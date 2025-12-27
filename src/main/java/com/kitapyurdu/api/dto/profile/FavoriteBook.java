package com.kitapyurdu.api.dto.profile;

import java.math.BigDecimal;

public class FavoriteBook {
    private int kitapId;
    private String kitapAdi;      
    private String yayineviAdi;   
    private BigDecimal fiyat;     
    private int stok;             
    private String coverUrl;

    public int getKitapId() { return kitapId; }
    public void setKitapId(int kitapId) { this.kitapId = kitapId; }

    public String getKitapAdi() { return kitapAdi; }
    public void setKitapAdi(String kitapAdi) { this.kitapAdi = kitapAdi; }

    public String getYayineviAdi() { return yayineviAdi; }
    public void setYayineviAdi(String yayineviAdi) { this.yayineviAdi = yayineviAdi; }

    public BigDecimal getFiyat() { return fiyat; }
    public void setFiyat(BigDecimal fiyat) { this.fiyat = fiyat; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}