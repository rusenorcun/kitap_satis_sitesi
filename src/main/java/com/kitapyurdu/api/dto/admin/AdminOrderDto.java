package com.kitapyurdu.api.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderDto {
    private int siparisId;
    private LocalDateTime siparisTarihi;
    private String kullaniciAdi;
    private String siparisDurumu;
    private String odemeYontemi;
    private BigDecimal genelToplam;

    public AdminOrderDto() {}

    public AdminOrderDto(int siparisId, LocalDateTime siparisTarihi, String kullaniciAdi, String siparisDurumu, String odemeYontemi, BigDecimal genelToplam) {
        this.siparisId = siparisId;
        this.siparisTarihi = siparisTarihi;
        this.kullaniciAdi = kullaniciAdi;
        this.siparisDurumu = siparisDurumu;
        this.odemeYontemi = odemeYontemi;
        this.genelToplam = genelToplam;
    }

    // Getter ve Setter'lar
    public int getSiparisId() { return siparisId; }
    public void setSiparisId(int siparisId) { this.siparisId = siparisId; }

    public LocalDateTime getSiparisTarihi() { return siparisTarihi; }
    public void setSiparisTarihi(LocalDateTime siparisTarihi) { this.siparisTarihi = siparisTarihi; }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getSiparisDurumu() { return siparisDurumu; }
    public void setSiparisDurumu(String siparisDurumu) { this.siparisDurumu = siparisDurumu; }

    public String getOdemeYontemi() { return odemeYontemi; }
    public void setOdemeYontemi(String odemeYontemi) { this.odemeYontemi = odemeYontemi; }

    public BigDecimal getGenelToplam() { return genelToplam; }
    public void setGenelToplam(BigDecimal genelToplam) { this.genelToplam = genelToplam; }
}