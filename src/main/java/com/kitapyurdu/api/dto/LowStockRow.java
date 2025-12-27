package com.kitapyurdu.api.dto;

public class LowStockRow {
    private final int kitapId;
    private final String kitapAdi;
    private final int stok;
    private final String yayineviAdi;

    public LowStockRow(int kitapId, String kitapAdi, int stok, String yayineviAdi) {
        this.kitapId = kitapId;
        this.kitapAdi = kitapAdi;
        this.stok = stok;
        this.yayineviAdi = yayineviAdi;
    }

    public int getKitapId() { return kitapId; }
    public String getKitapAdi() { return kitapAdi; }
    public int getStok() { return stok; }
    public String getYayineviAdi() { return yayineviAdi; }
}
