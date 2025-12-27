package com.kitapyurdu.api.dto.profile;

import java.time.LocalDateTime;

public class UserAddress {
    private Integer adresId;
    private Integer kullaniciId;
    private String baslik;
    private String il;
    private String ilce;
    private String mahalle;
    private String adresDetay;
    private String postaKodu;
    private Boolean varsayilan;
    private LocalDateTime olusturmaTarihi;

    public Integer getAdresId() { return adresId; }
    public void setAdresId(Integer adresId) { this.adresId = adresId; }

    public Integer getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(Integer kullaniciId) { this.kullaniciId = kullaniciId; }

    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) { this.baslik = baslik; }

    public String getIl() { return il; }
    public void setIl(String il) { this.il = il; }

    public String getIlce() { return ilce; }
    public void setIlce(String ilce) { this.ilce = ilce; }

    public String getMahalle() { return mahalle; }
    public void setMahalle(String mahalle) { this.mahalle = mahalle; }

    public String getAdresDetay() { return adresDetay; }
    public void setAdresDetay(String adresDetay) { this.adresDetay = adresDetay; }

    public String getPostaKodu() { return postaKodu; }
    public void setPostaKodu(String postaKodu) { this.postaKodu = postaKodu; }

    public Boolean getVarsayilan() { return varsayilan; }
    public void setVarsayilan(Boolean varsayilan) { this.varsayilan = varsayilan; }

    public LocalDateTime getOlusturmaTarihi() { return olusturmaTarihi; }
    public void setOlusturmaTarihi(LocalDateTime olusturmaTarihi) { this.olusturmaTarihi = olusturmaTarihi; }
}
