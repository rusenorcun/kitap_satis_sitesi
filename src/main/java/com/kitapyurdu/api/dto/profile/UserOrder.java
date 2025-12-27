package com.kitapyurdu.api.dto.profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserOrder {
    private int siparisId;
    private LocalDateTime siparisTarihi;
    private BigDecimal genelToplam;
    private String durumAdi;
    private int urunAdedi;

    public int getSiparisId() { return siparisId; }
    public void setSiparisId(int siparisId) { this.siparisId = siparisId; }

    public LocalDateTime getSiparisTarihi() { return siparisTarihi; }
    public void setSiparisTarihi(LocalDateTime siparisTarihi) { this.siparisTarihi = siparisTarihi; }

    public BigDecimal getGenelToplam() { return genelToplam; }
    public void setGenelToplam(BigDecimal genelToplam) { this.genelToplam = genelToplam; }

    public String getDurumAdi() { return durumAdi; }
    public void setDurumAdi(String durumAdi) { this.durumAdi = durumAdi; }

    public int getUrunAdedi() { return urunAdedi; }
    public void setUrunAdedi(int urunAdedi) { this.urunAdedi = urunAdedi; }
}