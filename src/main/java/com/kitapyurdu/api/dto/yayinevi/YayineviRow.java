package com.kitapyurdu.api.dto.yayinevi;

public class YayineviRow {
    private Integer yayineviId;
    private String ad;
    private String aciklama;
    private int kitapSayisi;

    public YayineviRow(Integer yayineviId, String ad, String aciklama, int kitapSayisi) {
        this.yayineviId = yayineviId;
        this.ad = ad;
        this.aciklama = aciklama;
        this.kitapSayisi = kitapSayisi;
    }

    public Integer getYayineviId() { return yayineviId; }
    public String getAd() { return ad; }
    public String getAciklama() { return aciklama; }
    public int getKitapSayisi() { return kitapSayisi; }
}
