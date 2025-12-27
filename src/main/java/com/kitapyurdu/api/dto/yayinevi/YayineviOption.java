package com.kitapyurdu.api.dto.yayinevi;

public class YayineviOption {
    private Integer id;
    private String ad;

    public YayineviOption(Integer id, String ad) {
        this.id = id;
        this.ad = ad;
    }

    public Integer getId() { return id; }
    public String getAd() { return ad; }
}
