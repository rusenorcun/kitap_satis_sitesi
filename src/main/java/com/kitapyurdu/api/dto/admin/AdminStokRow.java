package com.kitapyurdu.api.dto.admin;

import java.math.BigDecimal;

public class AdminStokRow {
    private Integer kitapId;
    private String kitapAdi;
    private String isbn;
    private String yayineviAdi;
    private BigDecimal fiyat;
    private Integer stok;
    private boolean durum;

    public AdminStokRow(Integer kitapId, String kitapAdi, String isbn, String yayineviAdi,
                        BigDecimal fiyat, Integer stok, boolean durum) {
        this.kitapId = kitapId;
        this.kitapAdi = kitapAdi;
        this.isbn = isbn;
        this.yayineviAdi = yayineviAdi;
        this.fiyat = fiyat;
        this.stok = stok;
        this.durum = durum;
    }

    public Integer getKitapId() { return kitapId; }
    public String getKitapAdi() { return kitapAdi; }
    public String getIsbn() { return isbn; }
    public String getYayineviAdi() { return yayineviAdi; }
    public BigDecimal getFiyat() { return fiyat; }
    public Integer getStok() { return stok; }
    public boolean isDurum() { return durum; }
}
