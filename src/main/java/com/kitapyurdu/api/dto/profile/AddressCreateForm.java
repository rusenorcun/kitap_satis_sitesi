package com.kitapyurdu.api.dto.profile;

public class AddressCreateForm {
    private String baslik;
    private String il;
    private String ilce;
    private String mahalle;
    private String adresDetay;
    private String postaKodu;
    private Boolean varsayilan;

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
}
