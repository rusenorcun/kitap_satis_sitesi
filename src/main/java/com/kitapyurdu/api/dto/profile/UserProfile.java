package com.kitapyurdu.api.dto.profile;

import java.time.LocalDateTime;

public class UserProfile {
    private Integer kullaniciId;
    private String kullaniciAdi;
    private String eposta;
    private String ad;
    private String soyad;
    private String telefon;
    private String rol;
    private Boolean durum;
    private LocalDateTime kayitTarihi;

    public Integer getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(Integer kullaniciId) { this.kullaniciId = kullaniciId; }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getEposta() { return eposta; }
    public void setEposta(String eposta) { this.eposta = eposta; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getDurum() { return durum; }
    public void setDurum(Boolean durum) { this.durum = durum; }

    public LocalDateTime getKayitTarihi() { return kayitTarihi; }
    public void setKayitTarihi(LocalDateTime kayitTarihi) { this.kayitTarihi = kayitTarihi; }
}
