package com.kitapyurdu.api.dto;

public class EmptyPublisherRow {
    private final int yayineviId;
    private final String ad;

    public EmptyPublisherRow(int yayineviId, String ad) {
        this.yayineviId = yayineviId;
        this.ad = ad;
    }

    public int getYayineviId() { return yayineviId; }
    public String getAd() { return ad; }
}
