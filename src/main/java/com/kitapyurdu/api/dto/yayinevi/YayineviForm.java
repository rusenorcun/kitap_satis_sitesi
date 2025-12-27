package com.kitapyurdu.api.dto.yayinevi;

public class YayineviForm {
    private Integer yayineviId;
    private String ad;
    private String aciklama;

    public Integer getYayineviId() { return yayineviId; }
    public void setYayineviId(Integer yayineviId) { this.yayineviId = yayineviId; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public static YayineviForm empty() {
        YayineviForm f = new YayineviForm();
        f.setYayineviId(null);
        f.setAd("");
        f.setAciklama(null);
        return f;
    }
}
